package com.labrox.iam.controller;

import com.labrox.common.dto.ApiResponse;
import com.labrox.iam.dto.MenuItemDto;
import com.labrox.iam.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/iam/menu")
@Tag(name = "Dynamic RBAC Menu Navigation")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    @GetMapping("/main-menu")
    @Operation(summary = "Get dynamic menu tree matching user designation and company")
    public ResponseEntity<ApiResponse<List<MenuItemDto>>> getMainMenu(
            @RequestParam("companyId") Integer companyId,
            @RequestParam("designationId") Integer designationId,
            @RequestParam("userId") Integer userId) {
        List<MenuItemDto> menu = menuService.getDynamicMenu(companyId, designationId, userId);
        return ResponseEntity.ok(ApiResponse.ok(menu));
    }
}

