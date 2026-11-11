package com.labrox.iam.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class MenuQueryRepository {

    private final JdbcTemplate jdbcTemplate;

    public MenuQueryRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Executes dynamic menu resolution faithfully preserving legacy MenuRepository.MainMenuPageData logic.
     */
    public List<Map<String, Object>> getMainMenuData(Integer companyId, Integer designationId, Integer userId) {
        String sql = """
            SELECT MenuID, MenuName FROM (
                SELECT DISTINCT -1 AS MenuID, 'Favourite' AS MenuName, -1 AS Priority
                FROM iam_schema.page_accesses_favourite tpaf
                INNER JOIN iam_schema.page_accesses tpa ON tpa.page_id = tpaf.page_id 
                    AND tpa.designation_id = ? AND tpa.company_id = ?
                WHERE tpaf.employee_id = ? AND tpaf.company_id = ?
                
                UNION ALL
                
                SELECT mm.id AS MenuID, mm.menu_name AS MenuName, mmp.priority AS Priority
                FROM iam_schema.menumaster mm
                INNER JOIN iam_schema.menumasterpriority mmp ON mm.id = mmp.menu_id
                INNER JOIN iam_schema.page_accesses pa ON pa.menuid = mm.id
                WHERE mm.is_active = TRUE AND mmp.company_id = ? AND pa.designation_id = ?
                GROUP BY mm.id, mmp.priority
            ) t ORDER BY t.Priority, t.MenuName
        """;
        return jdbcTemplate.queryForList(sql, designationId, companyId, userId, companyId, companyId, designationId);
    }

    public List<Map<String, Object>> getPageData(Integer companyId, Integer designationId, Integer userId) {
        String sql = """
            SELECT MenuID, MenuName, PageID, PageName, PageUrl, Priority, IsFavourite FROM (
                SELECT -1 AS MenuID, 'Favourite' AS MenuName, tpaf.page_id AS PageID, t2.display AS PageName, 
                       t2.url AS PageUrl, tpaf.priority AS Priority, 1 AS IsFavourite
                FROM iam_schema.page_accesses_favourite tpaf
                INNER JOIN iam_schema.pagemasters t2 ON t2.page_id = tpaf.page_id
                INNER JOIN iam_schema.page_accesses tpa ON tpa.page_id = tpaf.page_id 
                    AND tpa.designation_id = ? AND tpa.company_id = ?
                WHERE tpaf.employee_id = ? AND tpaf.company_id = ?
                
                UNION ALL
                
                SELECT mm.id AS MenuID, mm.menu_name AS MenuName, pm.page_id AS PageID, pm.display AS PageName, 
                       pm.url AS PageUrl, pmp.priority AS Priority, 0 AS IsFavourite
                FROM iam_schema.menumaster mm
                INNER JOIN iam_schema.menumasterpriority mmp ON mm.id = mmp.menu_id
                INNER JOIN iam_schema.pagemasters pm ON pm.menu_id = mm.id
                INNER JOIN iam_schema.pagemasterspriority pmp ON pmp.page_id = pm.page_id
                INNER JOIN iam_schema.page_accesses pc ON pc.page_id = pm.page_id
                WHERE pc.designation_id = ? AND pc.company_id = ?
                  AND mmp.company_id = ? AND pmp.company_id = ?
                  AND mm.is_active = TRUE AND pmp.is_active = TRUE AND pm.is_active = TRUE
                GROUP BY pm.page_id, mm.id, pmp.priority, pm.display, pm.url, mm.menu_name
            ) t ORDER BY Priority, PageName
        """;
        return jdbcTemplate.queryForList(sql, designationId, companyId, userId, companyId, 
                                              designationId, companyId, companyId, companyId);
    }
}

