package com.vidara.tradecenter.admin.dto;

import com.vidara.tradecenter.product.dto.response.ProductResponse;
import java.util.List;

public class DashboardStatsResponse {
    private long totalProducts;
    private long totalCategories;
    private long totalUsers;
    private List<ProductResponse> recentProducts;

    public DashboardStatsResponse() {
    }

    public DashboardStatsResponse(long totalProducts, long totalCategories, long totalUsers,
            List<ProductResponse> recentProducts) {
        this.totalProducts = totalProducts;
        this.totalCategories = totalCategories;
        this.totalUsers = totalUsers;
        this.recentProducts = recentProducts;
    }

    public long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public long getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(long totalCategories) {
        this.totalCategories = totalCategories;
    }

    public long getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(long totalUsers) {
        this.totalUsers = totalUsers;
    }

    public List<ProductResponse> getRecentProducts() {
        return recentProducts;
    }

    public void setRecentProducts(List<ProductResponse> recentProducts) {
        this.recentProducts = recentProducts;
    }
}
