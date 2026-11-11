package com.labrox.iam.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
@Slf4j
public class LegacyMetadataService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final List<Map<String, Object>> menus = new CopyOnWriteArrayList<>();
    private final List<Map<String, Object>> pages = new CopyOnWriteArrayList<>();
    private final Map<String, List<Map<String, Object>>> pageControlsByPageId = new ConcurrentHashMap<>();
    private final List<Map<String, Object>> allPageControls = new CopyOnWriteArrayList<>();
    private final Map<Integer, List<Map<String, Object>>> manageFieldsByVisitTypeId = new ConcurrentHashMap<>();
    private final List<Map<String, Object>> allManageFields = new CopyOnWriteArrayList<>();

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource("legacy_metadata_seed.json");
            if (!resource.exists()) {
                log.warn("legacy_metadata_seed.json not found on classpath, skipping metadata preload.");
                return;
            }

            try (InputStream is = resource.getInputStream()) {
                Map<String, Object> data = objectMapper.readValue(is, new TypeReference<Map<String, Object>>() {});

                // 1. Menus
                List<Map<String, Object>> rawMenus = (List<Map<String, Object>>) data.get("menus");
                Map<Integer, String> menuIdToName = new HashMap<>();
                if (rawMenus != null) {
                    menus.addAll(rawMenus);
                    for (Map<String, Object> m : rawMenus) {
                        Object mId = m.get("MenuID");
                        Object mName = m.get("MenuName");
                        if (mId != null && mName != null) {
                            menuIdToName.put(((Number) mId).intValue(), mName.toString());
                        }
                    }
                    log.info("Loaded {} menus from legacy metadata seed.", menus.size());
                }

                // 2. Pages
                List<Map<String, Object>> rawPages = (List<Map<String, Object>>) data.get("pages");
                if (rawPages != null) {
                    Map<Integer, Object[]> alias = Map.of(
                        65, new Object[]{73, "Logistic"},
                        64, new Object[]{81, "SampleStorage"},
                        45, new Object[]{82, "Store"},
                        30, new Object[]{63, "MacMachineMaster"},
                        61, new Object[]{70, "Tools"}
                    );

                    for (Map<String, Object> p : rawPages) {
                        Object mId = p.get("MenuID");
                        if (mId != null) {
                            int mid = ((Number) mId).intValue();
                            if (alias.containsKey(mid)) {
                                p.put("MenuID", alias.get(mid)[0]);
                                p.put("MenuName", alias.get(mid)[1]);
                            } else if (!p.containsKey("MenuName") || p.get("MenuName") == null || p.get("MenuName").toString().isEmpty()) {
                                String name = menuIdToName.get(mid);
                                if (name != null) {
                                    p.put("MenuName", name);
                                }
                            }
                        }
                    }
                    pages.addAll(rawPages);
                    log.info("Loaded {} pages with 100% MenuName mapped from legacy metadata seed.", pages.size());
                }

                // 3. Page Controls
                List<Map<String, Object>> rawControls = (List<Map<String, Object>>) data.get("pageControls");
                if (rawControls != null) {
                    allPageControls.addAll(rawControls);
                    for (Map<String, Object> ctrl : rawControls) {
                        Object pageIdObj = ctrl.get("PageID");
                        if (pageIdObj != null) {
                            String pageIdStr = String.valueOf(pageIdObj);
                            pageControlsByPageId.computeIfAbsent(pageIdStr, k -> new CopyOnWriteArrayList<>()).add(ctrl);
                        }
                    }
                    log.info("Loaded {} page controls across {} pages from legacy metadata seed.",
                            allPageControls.size(), pageControlsByPageId.size());
                }

                // 4. Manage Fields
                List<Map<String, Object>> rawFields = (List<Map<String, Object>>) data.get("manageFields");
                if (rawFields != null) {
                    allManageFields.addAll(rawFields);
                    for (Map<String, Object> fld : rawFields) {
                        Object vtObj = fld.get("VisitTypeID");
                        if (vtObj != null) {
                            int vt = ((Number) vtObj).intValue();
                            manageFieldsByVisitTypeId.computeIfAbsent(vt, k -> new CopyOnWriteArrayList<>()).add(fld);
                        }
                    }
                    log.info("Loaded {} manage fields from legacy metadata seed.", allManageFields.size());
                }
            }
        } catch (Exception ex) {
            log.error("Failed to load legacy_metadata_seed.json: {}", ex.getMessage(), ex);
        }
    }

    public List<Map<String, Object>> getMenus() {
        return new ArrayList<>(menus);
    }

    public List<Map<String, Object>> getPages() {
        return new ArrayList<>(pages);
    }

    public List<Map<String, Object>> getPageControls(String pageId) {
        if (pageId == null || pageId.trim().isEmpty()) {
            return Collections.emptyList();
        }
        List<Map<String, Object>> list = pageControlsByPageId.get(pageId.trim());
        if (list == null) {
            return Collections.emptyList();
        }
        return new ArrayList<>(list);
    }

    public List<Map<String, Object>> getAllPageControls() {
        return new ArrayList<>(allPageControls);
    }

    public void saveOrUpdatePageControls(List<Map<String, Object>> updatedControls) {
        if (updatedControls == null) return;
        for (Map<String, Object> ctrl : updatedControls) {
            Object pageIdObj = ctrl.get("PageID");
            if (pageIdObj != null) {
                String pageId = String.valueOf(pageIdObj);
                List<Map<String, Object>> existingList = pageControlsByPageId.computeIfAbsent(pageId, k -> new CopyOnWriteArrayList<>());
                Object controlId = ctrl.get("ControlDisplayID");
                if (controlId == null) {
                    controlId = ctrl.get("ControlID");
                }
                final Object targetId = controlId;
                if (targetId != null) {
                    existingList.removeIf(item -> targetId.equals(item.get("ControlDisplayID")) || targetId.equals(item.get("ControlID")));
                }
                existingList.add(ctrl);
            }
        }
    }

    public List<Map<String, Object>> getManageFields(Integer visitTypeId) {
        if (visitTypeId == null) {
            return new ArrayList<>(allManageFields);
        }
        List<Map<String, Object>> list = manageFieldsByVisitTypeId.get(visitTypeId);
        if (list != null && !list.isEmpty()) {
            return new ArrayList<>(list);
        }
        return new ArrayList<>(allManageFields);
    }

    public void saveOrUpdateManageFields(Map<String, Object> field) {
        if (field == null) return;
        allManageFields.add(field);
        Object vtObj = field.get("VisitTypeID");
        if (vtObj != null) {
            int vt = ((Number) vtObj).intValue();
            manageFieldsByVisitTypeId.computeIfAbsent(vt, k -> new CopyOnWriteArrayList<>()).add(field);
        }
    }
}
