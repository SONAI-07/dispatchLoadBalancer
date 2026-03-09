package com.loadbalancer.backend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderDTO {
    private String orderId;
    private double latitude;
    private double longitude;
    private String address;
    private double packageWeight;
    private String priority; // Received as String, mapped to Enum in Service
}
