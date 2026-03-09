package com.loadbalancer.backend.dto;



import lombok.*;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AssignedOrderDTO {
    private String orderId;
    private double latitude;
    private double longitude;
    private String address;
    private double packageWeight;
    private Priority priority;
}