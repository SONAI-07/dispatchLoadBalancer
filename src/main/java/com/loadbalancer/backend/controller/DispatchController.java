package com.loadbalancer.backend.controller;



import com.loadbalancer.backend.service.DispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.loadbalancer.backend.dto.DispatchPlanResponse;


@RestController
@RequestMapping("/api/dispatch")
@RequiredArgsConstructor
public class DispatchController {

    private final DispatchService dispatchService;

    @GetMapping("/plan")
    public ResponseEntity<DispatchPlanResponse> getOptimizedDispatchPlan() {
        DispatchPlanResponse response = dispatchService.optimize();

        if (response.getDispatchPlan() == null || response.getDispatchPlan().isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }
}
