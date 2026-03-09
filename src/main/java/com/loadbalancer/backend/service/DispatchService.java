package com.loadbalancer.backend.service;

import com.loadbalancer.backend.dto.*;
import com.loadbalancer.backend.model.Order;
import com.loadbalancer.backend.model.Vehicle;
import com.loadbalancer.backend.repository.OrderRepository;
import com.loadbalancer.backend.repository.VehicleRepository;
import com.loadbalancer.backend.util.DistanceCalculatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DispatchService {

    private final OrderRepository orderRepository;
    private final VehicleRepository vehicleRepository;
    private static final double MAX_PICKUP_RADIUS_KM = 10.0;

    @Transactional
    public DispatchPlanResponse optimize() {
        List<Order> unassignedOrders = orderRepository.findAllByStatusOrderByPriorityAsc(OrderStatus.UNASSIGNED);
        List<Vehicle> availableVehicles = vehicleRepository.findAvailableVehicles(VehicleStatus.IDLE);

        Map<Vehicle, List<Order>> vehicleOrdersMap = new HashMap<>();
        Map<Vehicle, Double> vehicleDistanceMap = new HashMap<>();

        Set<Vehicle> modifiedVehicles = new HashSet<>();
        List<Order> assignedOrders = new ArrayList<>();

        // NEW: List to track orders that couldn't be fulfilled
        List<String> routingErrors = new ArrayList<>();

        for (Order order : unassignedOrders) {
            Vehicle bestVehicle = null;
            double shortestDistance = Double.MAX_VALUE;
            boolean foundCapacity = false;

            for (Vehicle vehicle : availableVehicles) {
                double remainingCapacity = vehicle.getMaxCapacity() - vehicle.getCurrentLoad();

                if (remainingCapacity >= order.getPackageWeight()) {
                    foundCapacity = true;

                    double distance = DistanceCalculatorUtil.calculateDistance(
                            vehicle.getCurrentLatitude(), vehicle.getCurrentLongitude(),
                            order.getLatitude(), order.getLongitude()
                    );

                    if (distance < shortestDistance && distance <= MAX_PICKUP_RADIUS_KM) {
                        shortestDistance = distance;
                        bestVehicle = vehicle;
                    }
                }
            }

            if (bestVehicle != null) {
                // Successful Assignment
                bestVehicle.setCurrentLoad(bestVehicle.getCurrentLoad() + order.getPackageWeight());

                order.setStatus(OrderStatus.ASSIGNED);
                order.setAssignedVehicle(bestVehicle);

                modifiedVehicles.add(bestVehicle);
                assignedOrders.add(order);

                vehicleOrdersMap.computeIfAbsent(bestVehicle, k -> new ArrayList<>()).add(order);
                vehicleDistanceMap.merge(bestVehicle, shortestDistance, Double::sum);
            } else {
                // GRACEFUL FAILURE: Log the error but keep the loop running!
                if (!foundCapacity) {
                    routingErrors.add("Order " + order.getOrderId() + " skipped: Weight (" + order.getPackageWeight() + "kg) exceeds available fleet capacity.");
                } else {
                    routingErrors.add("Order " + order.getOrderId() + " skipped: No available vehicles found within the 10km pickup radius.");
                }
            }
        }

        // Batch save the successful assignments (The transaction is safe!)
        if (!modifiedVehicles.isEmpty()) vehicleRepository.saveAll(modifiedVehicles);
        if (!assignedOrders.isEmpty()) orderRepository.saveAll(assignedOrders);

        // Build the final response
        List<VehicleDispatchPlan> planList = new ArrayList<>();

        for (Map.Entry<Vehicle, List<Order>> entry : vehicleOrdersMap.entrySet()) {
            Vehicle vehicle = entry.getKey();
            List<Order> ordersForVehicle = entry.getValue();

            List<AssignedOrderDTO> orderDTOs = ordersForVehicle.stream()
                    .map(o -> new AssignedOrderDTO(
                            o.getOrderId(), o.getLatitude(), o.getLongitude(),
                            o.getAddress(), o.getPackageWeight(), o.getPriority()
                    )).collect(Collectors.toList());

            String distanceString = String.format("%.1fkm", vehicleDistanceMap.get(vehicle));

            planList.add(new VehicleDispatchPlan(
                    vehicle.getVehicleId(),
                    vehicle.getCurrentLoad(),
                    distanceString,
                    orderDTOs
            ));
        }

        // Return both the successful plans AND the errors
        return new DispatchPlanResponse(planList, routingErrors);
    }
}