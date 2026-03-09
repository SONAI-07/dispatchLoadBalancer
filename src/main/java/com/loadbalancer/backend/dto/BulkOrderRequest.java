package com.loadbalancer.backend.dto;

import lombok.Data;
import java.util.List;

@Data

public class BulkOrderRequest {
    private List<OrderDTO> orders;
}

