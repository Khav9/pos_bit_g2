package com.example.PosBit.controller;

import com.example.PosBit.dto.order.OrderResponse;
import com.example.PosBit.dto.order.SalesSummaryResponse;
import com.example.PosBit.model.Order;
import com.example.PosBit.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping({"/orders", "/sales-orders"})
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@RequestBody Order order, Authentication authentication) {
        if (authentication != null && authentication.getName() != null) {
            order.setCashierUsername(authentication.getName());
        }
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders(Authentication authentication) {
        if (isAdmin(authentication)) {
            return ResponseEntity.ok(orderService.getAllOrders());
        }
        return ResponseEntity.ok(orderService.getOrdersForUser(authentication.getName()));
    }

    @PostMapping("/all")
    public ResponseEntity<List<OrderResponse>> getAllOrdersByPost(Authentication authentication) {
        if (isAdmin(authentication)) {
            return ResponseEntity.ok(orderService.getAllOrders());
        }
        return ResponseEntity.ok(orderService.getOrdersForUser(authentication.getName()));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Integer orderId, Authentication authentication) {
        if (isAdmin(authentication)) {
            return ResponseEntity.ok(orderService.getOrderById(orderId));
        }
        return ResponseEntity.ok(orderService.getOrderByIdForUser(orderId, authentication.getName()));
    }

    @GetMapping("/summary")
    public ResponseEntity<SalesSummaryResponse> getSalesSummary(Authentication authentication) {
        if (isAdmin(authentication)) {
            return ResponseEntity.ok(orderService.getSalesSummary());
        }
        return ResponseEntity.ok(orderService.getSalesSummaryForUser(authentication.getName()));
    }

    @PostMapping("/summary")
    public ResponseEntity<SalesSummaryResponse> getSalesSummaryByPost(Authentication authentication) {
        if (isAdmin(authentication)) {
            return ResponseEntity.ok(orderService.getSalesSummary());
        }
        return ResponseEntity.ok(orderService.getSalesSummaryForUser(authentication.getName()));
    }

    private boolean isAdmin(Authentication authentication) {
        if (authentication == null || authentication.getAuthorities() == null) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if ("ROLE_ADMIN".equals(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}
