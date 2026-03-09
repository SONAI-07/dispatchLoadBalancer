package com.loadbalancer.backend.service;

import com.loadbalancer.backend.dto.OrderDTO;
import com.loadbalancer.backend.dto.OrderStatus;
import com.loadbalancer.backend.dto.Priority;
import com.loadbalancer.backend.model.Order;
import com.loadbalancer.backend.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public void saveAllOrders(List<OrderDTO> dtos) {
        List<Order> orders = dtos.stream().map(dto -> {
            Order order = new Order();
            order.setOrderId(dto.getOrderId().trim());
            order.setLatitude(dto.getLatitude());
            order.setLongitude(dto.getLongitude());
            order.setAddress(dto.getAddress());
            order.setPackageWeight(dto.getPackageWeight());

            // Map String priority to our Enum
            order.setPriority(Priority.valueOf(dto.getPriority().toUpperCase()));
            order.setStatus(OrderStatus.UNASSIGNED);

            return order;
        }).collect(Collectors.toList());

        orderRepository.saveAll(orders);
    }
}