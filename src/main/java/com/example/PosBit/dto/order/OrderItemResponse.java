package com.example.PosBit.dto.order;

public class OrderItemResponse {
    private Integer itemId;
    private Integer productId;
    private String productName;
    private Integer quantity;
    private Double priceAtSale;
    private Double lineTotal;

    public OrderItemResponse() {
    }

    public OrderItemResponse(Integer itemId, Integer productId, String productName, Integer quantity, Double priceAtSale, Double lineTotal) {
        this.itemId = itemId;
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.priceAtSale = priceAtSale;
        this.lineTotal = lineTotal;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPriceAtSale() {
        return priceAtSale;
    }

    public void setPriceAtSale(Double priceAtSale) {
        this.priceAtSale = priceAtSale;
    }

    public Double getLineTotal() {
        return lineTotal;
    }

    public void setLineTotal(Double lineTotal) {
        this.lineTotal = lineTotal;
    }
}
