package com.labrox.iam.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemDto {
    private Integer menuId;
    private String menuName;
    private List<PageItemDto> pages;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PageItemDto {
        private Integer pageId;
        private String pageName;
        private String pageUrl;
        private boolean favourite;
    }
}

