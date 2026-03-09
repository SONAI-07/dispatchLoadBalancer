package com.loadbalancer.backend.controller;

import com.loadbalancer.backend.dto.BulkVehicleRequest;
import com.loadbalancer.backend.service.VehicleService;
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
public class VehicleController {

    private final VehicleService vehicleService;

    @PostMapping("/vehicles")
    public ResponseEntity<Map<String, String>> registerFleet(@RequestBody BulkVehicleRequest request) {
        vehicleService.saveFleet(request.getVehicles());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Fleet registration successful.");
        response.put("status", "success");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}