package com.example.PosBit.service;

import com.example.PosBit.dto.order.OrderResponse;
import com.example.PosBit.dto.order.SalesSummaryResponse;
import com.example.PosBit.model.Order;
import java.util.List;

public interface OrderService {
    OrderResponse createOrder(Order order);
    List<OrderResponse> getAllOrders();
    List<OrderResponse> getOrdersForUser(String username);
    OrderResponse getOrderById(Integer orderId);
    OrderResponse getOrderByIdForUser(Integer orderId, String username);
    SalesSummaryResponse getSalesSummary();
    SalesSummaryResponse getSalesSummaryForUser(String username);
}
