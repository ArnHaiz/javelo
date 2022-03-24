package ch.epfl.javelo.routing;

import ch.epfl.javelo.Functions;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Preconditions;

import static java.lang.Float.NaN;

public final class ElevationProfileComputer {
    ElevationProfile elevationProfile(Route route, double maxStepLength) {
        Preconditions.checkArgument(maxStepLength>0);
        int nbSamples = (int)(route.length()/maxStepLength) +1;
        float[] elevationProfile = new float[nbSamples];
        fillForNormalProfile(elevationProfile, route);
        fillFirstGape(elevationProfile);
        fillLastGape(elevationProfile);
        fillMiddleGapes(elevationProfile);
        return new ElevationProfile(route.length(), elevationProfile);
    }

    private float[] fillForNormalProfile(float[] elevationProfile, Route route) {
        for(int i = 0; i<elevationProfile.length; i++) {
            if (Float.isNaN((float) route.elevationAt(i))) {
                elevationProfile[i] = NaN;
            } else {
                elevationProfile[i] = (float) route.elevationAt(i);
            }
        }
        return elevationProfile;
    }

    private float[] fillFirstGape(float[] elevationProfile) {
        int index = 0;
        while(Float.isNaN(elevationProfile[index])) {
            index++;
        }
        for(int i = 0; i<index; i++) {
            elevationProfile[i] = elevationProfile[index];
        }
        return elevationProfile;
    }

    private float[] fillLastGape(float[] elevationProfile) {
        int index = elevationProfile.length-1;
        while(Float.isNaN(elevationProfile[index])) {
            index--;
        }
        for(int i = elevationProfile.length-1; i>index; i++) {
            elevationProfile[i] = elevationProfile[index];
        }
        return elevationProfile;
    }

    private float[] fillMiddleGapes(float[] elevationProfile) {
        int firstIndex = 0;
        int lastIndex = 0;
        for(int i = 0; i<elevationProfile.length; i++) {
            if (Float.isNaN(elevationProfile[i])) {
                while (!(Float.isNaN(elevationProfile[firstIndex]))) {
                    firstIndex++;
                    lastIndex++;
                }
                do {
                    lastIndex++;
                } while (Float.isNaN(elevationProfile[lastIndex]));
                for (int j = firstIndex + 1; j < lastIndex; j++) {
                    elevationProfile[j] = (float) Math2.interpolate(firstIndex, lastIndex, ((j - firstIndex) / (lastIndex - firstIndex)));
                }
            }
        }
        return elevationProfile;
    }
}
