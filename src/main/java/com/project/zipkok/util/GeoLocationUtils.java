package com.project.zipkok.util;

public class GeoLocationUtils {

    private static final double KM_IN_ONE_DEGREE_LATITUDE = 110.0;
    private static final double KM_IN_ONE_DEGREE_LONGITUDE = 88.74;

    public static double[] getSquareBounds(double centerLat, double centerLon, double halfsideLengthKm) {

        double latChange = halfsideLengthKm / KM_IN_ONE_DEGREE_LATITUDE;
        double lonChange = halfsideLengthKm / KM_IN_ONE_DEGREE_LONGITUDE;

        double minLat = centerLat - latChange;
        double maxLat = centerLat + latChange;
        double minLon = centerLon - lonChange;
        double maxLon = centerLon + lonChange;

        return new double[]{minLat, maxLat, minLon, maxLon};
    }
}
