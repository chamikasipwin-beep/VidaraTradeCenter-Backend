package com.vidara.tradecenter.admin.controller;

import com.vidara.tradecenter.admin.dto.DashboardStatsResponse;
import com.vidara.tradecenter.admin.service.DashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminDashboardController {

    private final DashboardService dashboardService;

    public AdminDashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping({ "/", "/dashboard" })
    public String dashboard(Model model) {
        DashboardStatsResponse stats = dashboardService.getDashboardStats();

        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("totalProducts", stats.getTotalProducts());
        model.addAttribute("totalCategories", stats.getTotalCategories());
        model.addAttribute("totalUsers", stats.getTotalUsers());
        model.addAttribute("recentProducts", stats.getRecentProducts());

        return "admin/dashboard";
    }
}