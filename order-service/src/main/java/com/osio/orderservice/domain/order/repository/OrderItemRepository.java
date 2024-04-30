package com.osio.orderservice.domain.order.repository;

import com.osio.orderservice.domain.order.entity.Order;
import com.osio.orderservice.domain.order.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    List<OrderItem> findAllByOrder(Order order);
}
