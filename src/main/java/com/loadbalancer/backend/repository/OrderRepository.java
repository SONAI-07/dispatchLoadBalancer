package com.loadbalancer.backend.repository;

import com.loadbalancer.backend.dto.OrderStatus;
import com.loadbalancer.backend.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository <Order, UUID> {


     // Fetches orders by status, sorted by Priority (HIGH -> MEDIUM -> LOW).

    List<Order> findAllByStatusOrderByPriorityAsc(OrderStatus status);


    List<Order> findAllByStatus(OrderStatus status);

}
