package com.loadbalancer.backend.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkVehicleRequest {
    private List<VehicleDTO> vehicles;
}

