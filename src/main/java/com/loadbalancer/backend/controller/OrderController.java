package com.loadbalancer.backend.controller;

import com.loadbalancer.backend.dto.BulkOrderRequest;
import com.loadbalancer.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/dispatch")
@RequiredArgsConstructor


public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<Map<String, String>> createOrders(@RequestBody BulkOrderRequest request) {
        orderService.saveAllOrders(request.getOrders());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Delivery orders accepted.");
        response.put("status", "success");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}