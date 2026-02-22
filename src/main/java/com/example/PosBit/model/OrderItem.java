package com.example.PosBit.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    @JsonIgnore
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    private Integer quantity;

    private Double priceAtSale;

    public OrderItem() {
    }

    public OrderItem(Integer itemId, Order order, Product product, Integer quantity, Double priceAtSale) {
        this.itemId = itemId;
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.priceAtSale = priceAtSale;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
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
}
