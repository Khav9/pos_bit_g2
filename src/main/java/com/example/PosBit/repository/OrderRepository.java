package com.example.PosBit.repository;

import com.example.PosBit.model.Order;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findAll();

    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Order> findById(Integer orderId);

    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findByCashierUsername(String cashierUsername);

    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<Order> findByOrderIdAndCashierUsername(Integer orderId, String cashierUsername);
}
