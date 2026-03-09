package com.loadbalancer.backend.util;



public class DistanceCalculatorUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;

    public static double calculateDistance(double startLat, double startLon, double endLat, double endLon) {
        double dLat = Math.toRadians(endLat - startLat);
        double dLon = Math.toRadians(endLon - startLon);

        double lat1 = Math.toRadians(startLat);
        double lat2 = Math.toRadians(endLat);

        double a = Math.pow(Math.sin(dLat / 2), 2) +
                Math.pow(Math.sin(dLon / 2), 2) *
                        Math.cos(lat1) * Math.cos(lat2);

        double c = 2 * Math.asin(Math.sqrt(a));

        return EARTH_RADIUS_KM * c;
    }
}