package com.labrox.iam.service;

import com.labrox.iam.dto.MenuItemDto;
import com.labrox.iam.repository.MenuQueryRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MenuService {

    private final MenuQueryRepository menuQueryRepository;

    public MenuService(MenuQueryRepository menuQueryRepository) {
        this.menuQueryRepository = menuQueryRepository;
    }

    public List<MenuItemDto> getDynamicMenu(Integer companyId, Integer designationId, Integer userId) {
        List<Map<String, Object>> menus = menuQueryRepository.getMainMenuData(companyId, designationId, userId);
        List<Map<String, Object>> pages = menuQueryRepository.getPageData(companyId, designationId, userId);

        Map<Integer, MenuItemDto> menuMap = new LinkedHashMap<>();

        for (Map<String, Object> m : menus) {
            Integer menuId = ((Number) m.get("MenuID")).intValue();
            String menuName = (String) m.get("MenuName");
            menuMap.put(menuId, MenuItemDto.builder()
                    .menuId(menuId)
                    .menuName(menuName)
                    .pages(new ArrayList<>())
                    .build());
        }

        for (Map<String, Object> p : pages) {
            Integer menuId = ((Number) p.get("MenuID")).intValue();
            MenuItemDto parent = menuMap.get(menuId);
            if (parent != null) {
                parent.getPages().add(MenuItemDto.PageItemDto.builder()
                        .pageId(((Number) p.get("PageID")).intValue())
                        .pageName((String) p.get("PageName"))
                        .pageUrl((String) p.get("PageUrl"))
                        .favourite(((Number) p.get("IsFavourite")).intValue() == 1)
                        .build());
            }
        }

        return new ArrayList<>(menuMap.values());
    }
}

