package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Preconditions;

import java.util.DoubleSummaryStatistics;

public final class ElevationProfile {
    private double length;
    private float[] elevationSamples;
    public ElevationProfile(double length, float[] elevationSamples) {
        Preconditions.checkArgument(length>0);
        Preconditions.checkArgument(elevationSamples.length>=2);
        this.length = length;
        this.elevationSamples = elevationSamples;
    }
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
    public double minElevation() {
        DoubleSummaryStatistics elevation = obtainStatistic();
        return elevation.getMin();
    }
    public double maxElevation() {
        DoubleSummaryStatistics elevation = obtainStatistic();
        return elevation.getMax();
    }
    public double totalAscent() {
        double elevationGain = 0;
        for(int i = 1; i<elevationSamples.length; i++) {
            if((elevationSamples[i]-elevationSamples[i-1])>0) {
                elevationGain = elevationGain + (elevationSamples[i]-elevationSamples[i-1]);
            }
        }
        return elevationGain;
    }
    public double totalDescent() {
        double elevationLoss = 0;
        for(int i = 1; i<elevationSamples.length; i++) {
            if ((elevationSamples[i] - elevationSamples[i - 1]) < 0) {
                elevationLoss = elevationLoss + (elevationSamples[i] - elevationSamples[i - 1]);
            }
        }
        return Math.abs(elevationLoss);
    }
    public double elevationAt(double position) {
        return Functions.sampled(elevationSamples, length).applyAsDouble(position);
    }
}
