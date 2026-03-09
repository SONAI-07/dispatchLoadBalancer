package com.loadbalancer.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.loadbalancer.backend.dto.VehicleStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;

import java.util.Set;
import java.util.UUID;
import org.locationtech.jts.geom.Point;



@Entity
@Table(name = "vehicles")
@Data
@EqualsAndHashCode(of = "vehicleId")
@Getter
@Setter


public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    // 2. The Business ID (from your JSON)
    @Column(name="Vehicle_id", unique = true, nullable = false)
    private String vehicleId;


    @OneToMany(fetch=FetchType.LAZY, mappedBy="assignedVehicle")
    @JsonIgnore
    private Set<Order> orders;



    private double maxCapacity;
    private double currentLoad;

    private VehicleStatus Status;

    // The single spatial column for PostGIS math
    @Column(columnDefinition = "geography(Point, 4326)")
    @JsonIgnore // We hide this from the JSON response
    private Point location;

    // These fields are NOT in the database, but used for API Input/Output
    @Transient
    private double currentLatitude;

    @Transient
    private double currentLongitude;


     // Helper method to sync Lat/Lon into the Point geometry before the entity is persisted or updated.

    @PrePersist
    @PreUpdate
    public void updateLocationPoint()
    {
        GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
        // JTS Point uses (Longitude, Latitude) order
        this.location = factory.createPoint(new Coordinate(currentLongitude, currentLatitude));

    }


     // Helper to sync data back to Lat/Lon when loading from DB

    @PostLoad
    public void updateLatLonFields() {
        if (this.location != null) {
            this.currentLongitude = this.location.getX();
            this.currentLatitude = this.location.getY();
        }
    }
}