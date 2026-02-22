package com.example.PosBit.service.impl;

import com.example.PosBit.dto.order.OrderItemResponse;
import com.example.PosBit.dto.order.OrderResponse;
import com.example.PosBit.dto.order.SalesSummaryResponse;
import com.example.PosBit.model.Order;
import com.example.PosBit.model.OrderItem;
import com.example.PosBit.model.Product;
import com.example.PosBit.repository.OrderRepository;
import com.example.PosBit.repository.ProductRepository;
import com.example.PosBit.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderServiceImpl(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @Override
    @Transactional
    public OrderResponse createOrder(Order order) {
        if (order.getItems() == null || order.getItems().isEmpty()) {
            throw new RuntimeException("Order must contain at least one item");
        }
        double totalAmount = 0;

        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProduct().getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + item.getProduct().getProductId()));

            if (product.getProductQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getProductName());
            }

            // Update product quantity
            product.setProductQuantity(product.getProductQuantity() - item.getQuantity());
            productRepository.save(product);

            // Set item details
            item.setOrder(order);
            item.setPriceAtSale(product.getProductPrice());
            totalAmount += item.getQuantity() * product.getProductPrice();
        }

        order.setTotalAmount(totalAmount);
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentStatus("PAID");
        if (order.getPaymentMethod() == null || order.getPaymentMethod().isBlank()) {
            order.setPaymentMethod("CASH");
        } else {
            order.setPaymentMethod(order.getPaymentMethod().trim().toUpperCase());
        }
        if (order.getNote() != null && order.getNote().isBlank()) {
            order.setNote(null);
        }

        Order savedOrder = orderRepository.save(order);
        return toOrderResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::toOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersForUser(String username) {
        return orderRepository.findByCashierUsername(username)
                .stream()
                .map(this::toOrderResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        return toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByIdForUser(Integer orderId, String username) {
        Order order = orderRepository.findByOrderIdAndCashierUsername(orderId, username)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + orderId));
        return toOrderResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public SalesSummaryResponse getSalesSummary() {
        return buildSummary(orderRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public SalesSummaryResponse getSalesSummaryForUser(String username) {
        return buildSummary(orderRepository.findByCashierUsername(username));
    }

    private OrderResponse toOrderResponse(Order order) {
        List<OrderItemResponse> items = order.getItems() == null
                ? List.of()
                : order.getItems().stream()
                .map(item -> {
                    OrderItemResponse itemResponse = new OrderItemResponse();
                    itemResponse.setItemId(item.getItemId());
                    itemResponse.setProductId(item.getProduct() != null ? item.getProduct().getProductId() : null);
                    itemResponse.setProductName(item.getProduct() != null ? item.getProduct().getProductName() : "Unknown product");
                    itemResponse.setQuantity(item.getQuantity());
                    itemResponse.setPriceAtSale(item.getPriceAtSale());
                    itemResponse.setLineTotal((item.getPriceAtSale() == null || item.getQuantity() == null)
                            ? 0
                            : item.getPriceAtSale() * item.getQuantity());
                    return itemResponse;
                })
                .toList();

        int totalItems = items.stream()
                .map(OrderItemResponse::getQuantity)
                .filter(qty -> qty != null)
                .mapToInt(Integer::intValue)
                .sum();

        OrderResponse response = new OrderResponse();
        response.setOrderId(order.getOrderId());
        response.setOrderDate(order.getOrderDate());
        response.setTotalAmount(order.getTotalAmount());
        response.setPaymentStatus(order.getPaymentStatus());
        response.setPaymentMethod(order.getPaymentMethod());
        response.setCashierUsername(order.getCashierUsername());
        response.setNote(order.getNote());
        response.setTotalItems(totalItems);
        response.setItems(items);
        return response;
    }

    private SalesSummaryResponse buildSummary(List<Order> orders) {
        double totalRevenue = orders.stream().mapToDouble(o -> o.getTotalAmount() == null ? 0 : o.getTotalAmount()).sum();
        long totalOrders = orders.size();

        LocalDateTime startOfToday = LocalDateTime.now().toLocalDate().atStartOfDay();
        double todayRevenue = orders.stream()
                .filter(order -> order.getOrderDate() != null && !order.getOrderDate().isBefore(startOfToday))
                .mapToDouble(o -> o.getTotalAmount() == null ? 0 : o.getTotalAmount())
                .sum();
        long todayOrders = orders.stream()
                .filter(order -> order.getOrderDate() != null && !order.getOrderDate().isBefore(startOfToday))
                .count();

        long lowStockProducts = productRepository.countByProductQuantityLessThanEqual(5);

        SalesSummaryResponse summary = new SalesSummaryResponse();
        summary.setTotalOrders(totalOrders);
        summary.setTotalRevenue(totalRevenue);
        summary.setTodayOrders(todayOrders);
        summary.setTodayRevenue(todayRevenue);
        summary.setLowStockProducts(lowStockProducts);
        return summary;
    }
}
