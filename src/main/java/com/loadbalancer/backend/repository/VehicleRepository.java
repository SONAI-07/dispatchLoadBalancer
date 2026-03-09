package com.loadbalancer.backend.repository;

import com.loadbalancer.backend.dto.VehicleStatus;
import com.loadbalancer.backend.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;



public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {

    // 1. Fixed the capital 'S' in v.status to prevent your next crash!
    @Query("SELECT v FROM Vehicle v WHERE v.Status = :status AND (v.maxCapacity - v.currentLoad) >= :weight")
    List<Vehicle> findAllVehicles(
            @Param("weight") double weight,
            @Param("status") VehicleStatus status
    );

    // 2. Added the @Query annotation so Spring stops looking for an 'availableVehicles' field
    @Query("SELECT v FROM Vehicle v WHERE v.Status = :status")
    List<Vehicle> findAvailableVehicles(@Param("status") VehicleStatus status);

}
