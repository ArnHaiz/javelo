package ch.epfl.javelo.projection;

/**
 * @author Arnaud Haizmann (329072)
 *
 * Helper class for converting coordinates between WGS 84 and the swiss system.
 */
public final class Ch1903 {
    /**
     * private constructor of the non-instantiable Ch1903 class.
     */
    private Ch1903() {
    }

    /**
     * Converts the given coordinate from WGS 84 to the corresponding east value
     * in swiss coordinates.
     *
     * @param lon the WGS 84 longitude (in radians)
     * @param lat the WGS 84 latitude (in radians)
     * @return the east value in swiss coordinates from WGS 84.
     */
    public static double e(double lon, double lat) {
        lon = Math.toDegrees(lon);
        lat = Math.toDegrees(lat);
        double lambda1 = 1e-4 * (3600 * lon - 26_782.5);
        double phi1 = 1e-4 * (3600 * lat - 169_028.66);
        return 2_600_072.37
                + 211_455.93 * lambda1
                - 10_938.51 * lambda1 * phi1
                - 0.36 * lambda1 * Math.pow(phi1, 2)
                - 44.54 * Math.pow(lambda1, 3);
    }

    /**
     * Converts the given coordinate from WGS 84 to the corresponding north value
     * in swiss coordinates.
     *
     * @param lon the WGS 84 longitude (in radians)
     * @param lat the WGS 84 latitude (in radians)
     * @return the north value in swiss coordinates from WGS 84.
     */
    public static double n(double lon, double lat) {
        lon = Math.toDegrees(lon);
        lat = Math.toDegrees(lat);
        double lambda1 = 1e-4 * (3600 * lon - 26_782.5);
        double phi1 = 1e-4 * (3600 * lat - 169_028.66);
        return 1_200_147.07
                + 308_807.95 * phi1
                + 3_745.25 * Math.pow(lambda1, 2)
                + 76.63 * Math.pow(phi1, 2)
                - 194.56 * Math.pow(lambda1, 2) * phi1
                + 119.79 * Math.pow(phi1, 3);
    }

    /**
     * Converts the given coordinate from swiss coordinates to the corresponding
     * WGS 84 longitude.
     *
     * @param e the east value
     * @param n the north value
     * @return the longitude value (in radians) in WGS 84 from swiss coordinates.
     */
    public static double lon(double e, double n) {
        double x = 1e-6 * (e - 2_600_000);
        double y = 1e-6 * (n - 1_200_000);
        double lambda0 = 2.6_779_094
                + 4.728_982 * x
                + 0.791_484 * x * y
                + 0.1_306 * x * Math.pow(y, 2)
                - 0.0_436 * Math.pow(x, 3);
        return Math.toRadians(lambda0 * 100.0 / 36.0);
    }

    /**
     * Converts the given coordinate from swiss coordinates to the corresponding
     * WGS 84 latitude.
     *
     * @param e the east value
     * @param n the north value
     * @return the latitude value (in radians) in WGS 84 from swiss coordinates.
     */
    public static double lat(double e, double n) {
        double x = 1e-6 * (e - 2_600_000);
        double y = 1e-6 * (n - 1_200_000);
        double phi0 = 16.9_023_892
                + 3.238_272 * y
                - 0.270_978 * Math.pow(x, 2)
                - 0.002_528 * Math.pow(y, 2)
                - 0.0_447 * Math.pow(x, 2) * y
                - 0.0_140 * Math.pow(y, 3);
        return Math.toRadians(phi0 * 100.0 / 36.0);
    }

}
