package com.loadbalancer.backend;



import com.loadbalancer.backend.dto.DispatchPlanResponse;
import com.loadbalancer.backend.dto.Priority;
import com.loadbalancer.backend.dto.VehicleStatus;
import com.loadbalancer.backend.model.Order;
import com.loadbalancer.backend.model.Vehicle;
import com.loadbalancer.backend.repository.OrderRepository;
import com.loadbalancer.backend.repository.VehicleRepository;
import com.loadbalancer.backend.service.DispatchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;







@ExtendWith(MockitoExtension.class)
class DispatchServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private VehicleRepository vehicleRepository;

    @InjectMocks
    private DispatchService dispatchService;

    private Vehicle v1, v2;
    private Order orderHigh80, orderHigh50, orderMedium35, orderLow20, orderFar;

    @BeforeEach
    void setUp() {
        // Base coordinates (e.g., a hub in Delhi)
        double baseLat = 28.6139;
        double baseLon = 77.2090;

        // Setup Vehicle 1 (100kg capacity)
        v1 = new Vehicle();
        v1.setId(UUID.randomUUID());
        v1.setVehicleId("V1");
        v1.setMaxCapacity(100.0);
        v1.setCurrentLoad(0.0);
        v1.setCurrentLatitude(baseLat);
        v1.setCurrentLongitude(baseLon);

        // Setup Vehicle 2 (80kg capacity)
        v2 = new Vehicle();
        v2.setId(UUID.randomUUID());
        v2.setVehicleId("V2");
        v2.setMaxCapacity(80.0);
        v2.setCurrentLoad(0.0);
        v2.setCurrentLatitude(baseLat);
        v2.setCurrentLongitude(baseLon);

        // Setup Orders (All close by to pass the 10km check, except orderFar)
        orderHigh80 = createOrder("ORD-1", Priority.HIGH, 80.0, baseLat + 0.01, baseLon);
        orderHigh50 = createOrder("ORD-2", Priority.HIGH, 50.0, baseLat + 0.01, baseLon);
        orderMedium35 = createOrder("ORD-3", Priority.MEDIUM, 35.0, baseLat + 0.01, baseLon);
        orderLow20 = createOrder("ORD-4", Priority.LOW, 20.0, baseLat + 0.01, baseLon);

        // Setup an order 50km away (fails radius check)
        orderFar = createOrder("ORD-FAR", Priority.HIGH, 10.0, baseLat + 0.5, baseLon + 0.5);
    }

    private Order createOrder(String orderId, Priority priority, double weight, double lat, double lon) {
        Order order = new Order();
        order.setId(UUID.randomUUID());
        order.setOrderId(orderId);
        order.setPriority(priority);
        order.setPackageWeight(weight);
        order.setLatitude(lat);
        order.setLongitude(lon);
        return order;
    }

    @Test
    void testOptimize_WithFragmentedCapacity_ShouldAssignCorrectlyAndReportErrors() {
        // Arrange
        when(orderRepository.findAllByStatusOrderByPriorityAsc(any()))
                .thenReturn(Arrays.asList(orderHigh80, orderHigh50, orderMedium35, orderLow20));
        when(vehicleRepository.findAvailableVehicles(VehicleStatus.IDLE))
                .thenReturn(Arrays.asList(v1, v2));

        // Act
        DispatchPlanResponse response = dispatchService.optimize();

        // Assert
        assertNotNull(response);
        assertEquals(2, response.getDispatchPlan().size(), "Both vehicles should be utilized");

        // V1 took the 80kg High priority and 20kg Low priority order (total 100kg)
        assertEquals(100.0, v1.getCurrentLoad());

        // V2 took the 50kg High priority order
        assertEquals(50.0, v2.getCurrentLoad());

        // Order 3 (35kg) should fail because neither V1 (0kg left) nor V2 (30kg left) can take it
        assertEquals(1, response.getErrors().size(), "There should be exactly one routing error reported");
        assertTrue(response.getErrors().get(0).contains("ORD-3"), "The error should specifically mention ORD-3");

        // Verify database saves were called
        verify(vehicleRepository, times(1)).saveAll(any());
        verify(orderRepository, times(1)).saveAll(any());
    }

    @Test
    void testOptimize_OrderOutside10kmRadius_ShouldNotAssign() {
        // Arrange
        when(orderRepository.findAllByStatusOrderByPriorityAsc(any()))
                .thenReturn(List.of(orderFar)); // Passing the order that is 50km away
        when(vehicleRepository.findAvailableVehicles(VehicleStatus.IDLE))
                .thenReturn(List.of(v1)); // V1 has 100kg capacity, so weight is fine

        // Act
        DispatchPlanResponse response = dispatchService.optimize();

        // Assert
        assertTrue(response.getDispatchPlan().isEmpty(), "No vehicles should be assigned because it violates the 10km radius constraint");
        assertEquals(1, response.getErrors().size());
        assertTrue(response.getErrors().get(0).contains("ORD-FAR"), "The error should mention the far order");
        assertTrue(response.getErrors().get(0).contains("10km pickup radius"), "The error should specify the radius violation");
    }
}