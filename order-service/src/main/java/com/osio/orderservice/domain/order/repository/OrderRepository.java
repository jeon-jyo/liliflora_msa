package com.osio.orderservice.domain.order.repository;

import com.osio.orderservice.domain.order.entity.Order;
import com.osio.orderservice.domain.order.entity.OrderStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findFirstByUserIdOrderByPurchaseDateDesc(Long userId);

    List<Order> findAllByUserIdOrderByPurchaseDateDesc(Long userId);

    List<Order> findAllByOrderStatus_StatusAndChangedDateBefore(OrderStatusEnum status, LocalDateTime localDateTime);
}
