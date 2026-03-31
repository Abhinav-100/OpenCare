package com.ciphertext.opencarebackend.util;

import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;

import java.math.BigDecimal;

public class GisUtil {
    public static Point getLocation(BigDecimal lat, BigDecimal lon) throws ParseException {
        String pointWKT = String.format("POINT(%f %f)", lon, lat);
        return (Point) new org.locationtech.jts.io.WKTReader().read(pointWKT);
    }

    public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    public static double getDistance(Point p1, Point p2) {
        return p1.distance(p2) * 100000; // convert to meter
    }
}