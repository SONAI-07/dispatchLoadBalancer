package com.loadbalancer.backend.dto;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleDispatchPlan {
    private String vehicleId;
    private double totalLoad;
    private String totalDistance;
    private List<AssignedOrderDTO> assignedOrders;
}
