package ch.epfl.javelo.routing;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import java.util.Arrays;

import static java.lang.Float.NaN;

public final class ElevationProfileComputer {
    public static ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength>0);
        int nbSamples = (int)Math.ceil(route.length()/maxStepLength) +1;
        double stepLength = route.length()/(nbSamples-1);
        float[] elevationProfile = new float[nbSamples];
        float[] nullArray = new float[nbSamples];
        Arrays.fill(nullArray, 0);
        fillForNormalProfile(elevationProfile, route, stepLength);
        fillFirstGape(elevationProfile);
        if(!(Arrays.equals(elevationProfile, nullArray))) {
            fillLastGape(elevationProfile);
            fillMiddleGapes(elevationProfile);
        }
        return new ElevationProfile(route.length(), elevationProfile);
    }

    private static void fillForNormalProfile(float[] elevationProfile, Route route, double stepLength) {
        for(int i = 0; i<elevationProfile.length; i++) {
            if (Float.isNaN((float) route.elevationAt(i*stepLength))) {
                elevationProfile[i] = NaN;
            } else {
                elevationProfile[i] = (float) route.elevationAt(i*stepLength);
            }
        }
    }

    private static void fillFirstGape(float[] elevationProfile) {
        int index = 0;
        while((index < elevationProfile.length-1)&&(Float.isNaN(elevationProfile[index]))) {
            ++index;
        }
        if(index == elevationProfile.length-1) {
            Arrays.fill(elevationProfile, 0);
        }else {
            for (int i = 0; i < index; i++) {
                elevationProfile[i] = elevationProfile[index];
            }
        }
    }

    private static void fillLastGape(float[] elevationProfile) {
        int index = elevationProfile.length-1;
        while(Float.isNaN(elevationProfile[index])) {
            index--;
        }
        for(int i = elevationProfile.length-1; i>index; i--) {
            elevationProfile[i] = elevationProfile[index];
        }
    }

    private static void fillMiddleGapes(float[] elevationProfile) {
        int firstIndex = 0;
        int lastIndex;
        for(int i = 0; i<elevationProfile.length; i++) {
            if (Float.isNaN(elevationProfile[i])) {
                while (!(Float.isNaN(elevationProfile[firstIndex]))) {
                    firstIndex++;
                }
                lastIndex = firstIndex+1;
                do {
                    lastIndex++;
                } while (Float.isNaN(elevationProfile[lastIndex]));
                for (int j = firstIndex; j < lastIndex; j++) {
                    elevationProfile[j] = (float) Math2.interpolate(firstIndex, lastIndex, ((double)(j - firstIndex)/(lastIndex - firstIndex)));
                }
                firstIndex = 0;
            }

        }
    }
}
