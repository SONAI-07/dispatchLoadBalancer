package com.loadbalancer.backend.service;

import com.loadbalancer.backend.dto.VehicleDTO;
import com.loadbalancer.backend.dto.VehicleStatus;
import com.loadbalancer.backend.model.Vehicle;
import com.loadbalancer.backend.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor

public class VehicleService {

    private final VehicleRepository vehicleRepository;

    @Transactional
    public void saveFleet(List<VehicleDTO> dtos) {
        List<Vehicle> vehicles = dtos.stream().map(dto -> {
            Vehicle vehicle = new Vehicle();
            vehicle.setVehicleId(dto.getVehicleId());
            vehicle.setCurrentLatitude(dto.getCurrentLatitude());
            vehicle.setCurrentLongitude(dto.getCurrentLongitude());
            vehicle.setMaxCapacity(dto.getMaxCapacity());

            // Initial state for new fleet members
            vehicle.setCurrentLoad(0.0);
            vehicle.setStatus(VehicleStatus.IDLE);

            return vehicle;
        }).collect(Collectors.toList());

        vehicleRepository.saveAll(vehicles);
    }
}
