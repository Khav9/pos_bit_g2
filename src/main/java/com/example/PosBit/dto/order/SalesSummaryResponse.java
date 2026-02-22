package com.example.PosBit.dto.order;

public class SalesSummaryResponse {
    private Long totalOrders;
    private Double totalRevenue;
    private Long todayOrders;
    private Double todayRevenue;
    private Long lowStockProducts;

    public SalesSummaryResponse() {
    }

    public SalesSummaryResponse(Long totalOrders, Double totalRevenue, Long todayOrders, Double todayRevenue, Long lowStockProducts) {
        this.totalOrders = totalOrders;
        this.totalRevenue = totalRevenue;
        this.todayOrders = todayOrders;
        this.todayRevenue = todayRevenue;
        this.lowStockProducts = lowStockProducts;
    }

    public Long getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(Long totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(Double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public Long getTodayOrders() {
        return todayOrders;
    }

    public void setTodayOrders(Long todayOrders) {
        this.todayOrders = todayOrders;
    }

    public Double getTodayRevenue() {
        return todayRevenue;
    }

    public void setTodayRevenue(Double todayRevenue) {
        this.todayRevenue = todayRevenue;
    }

    public Long getLowStockProducts() {
        return lowStockProducts;
    }

    public void setLowStockProducts(Long lowStockProducts) {
        this.lowStockProducts = lowStockProducts;
    }
}
