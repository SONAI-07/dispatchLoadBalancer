package com.loadbalancer.backend.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter


public class VehicleDTO {
    private String vehicleId;
    private double currentLatitude;
    private double currentLongitude;
    private double maxCapacity;
    private String address;
}
