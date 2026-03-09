package com.loadbalancer.backend;

import com.loadbalancer.backend.util.DistanceCalculatorUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;



class DistanceCalculatorUtilTest {

    @Test
    void testDistanceSameLocation_ShouldReturnZero() {
        // Delhi Coordinates
        double lat = 28.6139;
        double lon = 77.2090;

        double distance = DistanceCalculatorUtil.calculateDistance(lat, lon, lat, lon);

        assertEquals(0.0, distance, 0.001, "Distance to the exact same point should be 0");
    }

    @Test
    void testDistanceKnownCoordinates_ShouldReturnAccurateDistance() {
        // Point A: Connaught Place, Delhi (28.6304, 77.2177)
        // Point B: India Gate, Delhi (28.6129, 77.2295)
        // Actual distance is roughly 2.27 km

        double distance = DistanceCalculatorUtil.calculateDistance(28.6304, 77.2177, 28.6129, 77.2295);

        assertEquals(2.27, distance, 0.1, "Haversine distance should be within 100 meters of true distance");
    }

    @Test
    void testDistanceOutsideRadius_ShouldExceed10km() {
        // Point A: Central Delhi (28.6139, 77.2090)
        // Point B: Gurgaon (28.4595, 77.0266)
        // Actual distance is roughly 24 km

        double distance = DistanceCalculatorUtil.calculateDistance(28.6139, 77.2090, 28.4595, 77.0266);

        assertTrue(distance > 10.0, "Distance to Gurgaon should exceed the 10km threshold");
    }
}