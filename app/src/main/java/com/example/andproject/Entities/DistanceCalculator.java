package com.example.andproject.Entities;

public class DistanceCalculator {

    public static int distanceBetween(String usersLocation, String pickupLocation) {
        // We convert the strings into doubles
        double lat1, lng1, lat2, lng2;

        String[] parts = usersLocation.split(", ");
        System.out.println(parts[0] + "-" + parts[1]);
        lat1 = Double.parseDouble(parts[0]);
        lng1 = Double.parseDouble(parts[1]);

        parts = pickupLocation.split(", ");
        System.out.println(parts[0] + "-" + parts[1]);
        lat2 = Double.parseDouble(parts[0]);
        lng2 = Double.parseDouble(parts[1]);

        //returns distance in meters
        double a = (lat1 - lat2) * distPerLat(lat1);
        double b = (lng1 - lng2) * distPerLng(lng1);

        return Integer.parseInt(String.format("%.0f", (Math.sqrt(a * a + b * b))));
    }

    private static double distPerLng(double lng){
        return 0.0003121092*Math.pow(lng, 4)
                +0.0101182384*Math.pow(lng, 3)
                -17.2385140059*lng*lng
                +5.5485277537*lng+111301.967182595;
    }

    private static double distPerLat(double lat){
        return -0.000000487305676*Math.pow(lat, 4)
                -0.0033668574*Math.pow(lat, 3)
                +0.4601181791*lat*lat
                -1.4558127346*lat+110579.25662316;
    }
}
