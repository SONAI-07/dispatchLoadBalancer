package com.loadbalancer.backend.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class DispatchPlanResponse {
    private List<VehicleDispatchPlan> dispatchPlan;
    private List<String> errors;
}
