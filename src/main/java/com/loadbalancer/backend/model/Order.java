package com.loadbalancer.backend.model;

import com.loadbalancer.backend.dto.OrderStatus;
import com.loadbalancer.backend.dto.Priority;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.UUID;


@Entity
@Table(name = "orders")
@Data
@EqualsAndHashCode(of = "orderId")
@Getter
@Setter

public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // 2. The Business ID (from your JSON)
    @Column(name="order_Id", unique = true, nullable = false)
    private String orderId;


    @ManyToOne(fetch = FetchType.LAZY)
    private Vehicle assignedVehicle;

    private double packageWeight;

    @Enumerated(EnumType.STRING)
    private Priority priority; // Enum: HIGH, MEDIUM, LOW

    private String address;

    // PostGIS Spatial column
    @Column(columnDefinition = "geography(Point, 4326)")
    @JsonIgnore
    private Point dropLocation;

    // API-facing fields
    @Transient
    private double latitude;

    @Transient
    private double longitude;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.UNASSIGNED;

    @PrePersist
    @PreUpdate
    public void updateDropLocation() {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
        // Remember: X = Longitude, Y = Latitude
        this.dropLocation = factory.createPoint(new Coordinate(this.longitude, this.latitude));
    }

    @PostLoad
    public void loadLatLon() {
        if (this.dropLocation != null) {
            this.longitude = this.dropLocation.getX();
            this.latitude = this.dropLocation.getY();
        }
    }
}