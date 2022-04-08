package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;

/**
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 *
 * class representing the profile in length of an itinerary
 */
public final class ElevationProfile {
    private final double length;
    private final float[] elevationSamples;

    /**
     * construct the profile in length
     * @param length : distance covered by the profile
     * @param elevationSamples : elevation samples attached to the profile
     * @throws IllegalArgumentException if the length is negative or equal to zero
     * @throws IllegalArgumentException if the length of the arrays containing the samples is smaller than 2
     */
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length>0);
        Preconditions.checkArgument(elevationSamples.length>=2);
        this.length = length;
        this.elevationSamples = elevationSamples;
    }

    /**
     * return the length of the profile
     * @return the length of the profile
     */
    public double length() {
        return this.length;
    }
    private DoubleSummaryStatistics obtainStatistic() {
        DoubleSummaryStatistics elevation = new DoubleSummaryStatistics();
        for(int i = 0; i<elevationSamples.length; i++) {
            elevation.accept(elevationSamples[i]);
        }
        return elevation;
    }

    /**
     * return the minimal altitude of the profile
     * @return the minimal altitude of the profile
     */
    public double minElevation() {
        DoubleSummaryStatistics elevation = obtainStatistic();
        return elevation.getMin();
    }

    /**
     * return the maximal altitude of the profile
     * @return the maximal altitude of the profile
     */
    public double maxElevation() {
        DoubleSummaryStatistics elevation = obtainStatistic();
        return elevation.getMax();
    }

    /**
     * return the total positive height difference of the profile
     * @return the total positive height difference of the profile
     */
    public double totalAscent() {
        double elevationGain = 0;
        for(int i = 1; i<elevationSamples.length; i++) {
            if((elevationSamples[i]-elevationSamples[i-1])>0) {
                elevationGain = elevationGain + (elevationSamples[i]-elevationSamples[i-1]);
            }
        }
        return elevationGain;
    }

    /**
     * return the total positive height difference of the profile
     * @return the total positive height difference of the profile
     */
    public double totalDescent() {
        double elevationLoss = 0;
        for(int i = 1; i<elevationSamples.length; i++) {
            if ((elevationSamples[i] - elevationSamples[i - 1]) < 0) {
                elevationLoss = elevationLoss + (elevationSamples[i] - elevationSamples[i - 1]);
            }
        }
        return Math.abs(elevationLoss);
    }

    /**
     * return the altitude at a given position,
     *         if the position is negative return the first sample,
     *         if the position is greater than the length return the last sample
     * @param position : position on the profile
     * @return the altitude at a given position,
     *         if the position is negative return the first sample,
     *         if the position is greater than the length return the last sample
     */
    public double elevationAt(double position) {
        return Functions.sampled(elevationSamples, length).applyAsDouble(position);
    }
}
