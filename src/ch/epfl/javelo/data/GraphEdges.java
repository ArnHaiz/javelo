package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

/**
 * @param edgesBuffer : buffer containing attributes of the edges
 * @param profileIds  : buffer containing the profile of the edges
 * @param elevations  : buffer containing elevations of each edge
 * @author Arnaud Haizmann (329072)
 * @author Hervé Sérandour (328233)
 * <p>
 * record class representing all the edges in Switzerland.
 */
public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    /**
     * return the direction of the edge
     *
     * @param edgeId : identity of the edge
     * @return true if the edge is inverted
     */
    public boolean isInverted(int edgeId) {
        return getPath(edgeId) < 0;
    }

    /**
     * return the node targeted by the edge
     *
     * @param edgeId : identity of the edge
     * @return the node targeted by the edge
     */
    public int targetNodeId(int edgeId) {
        return isInverted(edgeId) ? ~getPath(edgeId) : getPath(edgeId);
    }

    private int getPath(int edgeId) {
        return edgesBuffer.getInt(edgeId * 10);
    }

    /**
     * return the length of the edge
     *
     * @param edgeId : identity of the edge
     * @return the length of the edge
     */
    public double length(int edgeId) {
        int length = Short.toUnsignedInt(edgesBuffer.getShort(edgeId * 10 + 4));
        return Q28_4.asDouble(length);
    }

    /**
     * return the overall gain of altitude of a given edge
     *
     * @param edgeId : identity of the edge
     * @return the overall gain of altitude the given edge
     */
    public double elevationGain(int edgeId) {
        int elevation = Short.toUnsignedInt(edgesBuffer.getShort(edgeId * 10 + 6));
        return Q28_4.asDouble(elevation);
    }

    /**
     * return true if the edge has a profile
     *
     * @param edgeId : identity of the edge
     * @return true if the edge has a profile
     */
    public boolean hasProfile(int edgeId) {
        int profile = profileIds.get(edgeId) >>> 30;
        return profile > 0;
    }

    /**
     * return the array corresponding of the elevation samples of the given edge
     *
     * @param edgeId : identity of the edge
     * @return the array corresponding of the elevation samples of the edge or 0 if the has no profile
     */
    public float[] profileSamples(int edgeId) {
        return switch (profileId(edgeId)) {
            case 0 -> profile0(edgeId);
            case 1 -> profile1(edgeId);
            case 2 -> profile2(edgeId);
            case 3 -> profile3(edgeId);
            default -> new float[0];
        };
    }

    private int profileId(int edgeId) {
        return profileIds.get(edgeId) >>> 30;
    }

    private float[] initSamples(int edgeId) {
        int length = Short.toUnsignedInt(edgesBuffer.getShort(edgeId * 10 + 4));
        int divider = Q28_4.ofInt(2);
        return new float[(1 + Math2.ceilDiv(length, divider))];
    }

    private int indexProfile(int edgeId) {
        int profileId = profileIds.get(edgeId);
        return Bits.extractUnsigned(profileId, 0, 30);
    }

    private float[] profile0(int edgeId) {
        return new float[0];
    }

    private float[] profile1(int edgeId) {
        float[] sample = initSamples(edgeId);
        for (int i = 0; i < sample.length; i++) {
            int partOfSample = Short.toUnsignedInt(elevations.get(indexProfile(edgeId) + i));
            float elevation = Q28_4.asFloat(partOfSample);
            if (isInverted(edgeId)) {
                sample[sample.length - i - 1] = elevation;
            } else {
                sample[i] = elevation;
            }
        }
        return sample;
    }

    private float[] profile2(int edgeId) {
        float[] sample = initSamples(edgeId);
        int push = Short.toUnsignedInt(elevations.get(indexProfile(edgeId)));
        float elevation = Q28_4.asFloat(push);
        sample[0] = elevation;
        for (int i = 1; i < sample.length; i = i + 2) {
            int elevationToExtract = Short.toUnsignedInt(elevations.get(indexProfile(edgeId) + Math2.ceilDiv(i, 2)));
            int push1 = Bits.extractSigned(elevationToExtract, 8, 8);
            float leftPart = Q28_4.asFloat(push1);
            sample[i] = leftPart + sample[i - 1];
            if (i + 1 < sample.length) {
                int push2 = Bits.extractSigned(elevationToExtract, 0, 8);
                float rightPart = Q28_4.asFloat(push2);
                sample[i + 1] = rightPart + sample[i];
            }
        }
        return invertedSample(sample, edgeId);
    }

    private float[] profile3(int edgeId) {
        float[] sample = initSamples(edgeId);
        int push = Short.toUnsignedInt(elevations.get(indexProfile(edgeId)));
        float elevation = Q28_4.asFloat(push);
        sample[0] = elevation;
        for (int i = 1; i < sample.length; i = i + 4) {
            int elevationToExtract = Short.toUnsignedInt(elevations.get(indexProfile(edgeId) + Math2.ceilDiv(i, 4)));
            int push1 = Bits.extractSigned(elevationToExtract, 12, 4);
            float leftPart = Q28_4.asFloat(push1);
            sample[i] = leftPart + sample[i - 1];
            if (i + 1 < sample.length) {
                int push2 = Bits.extractSigned(elevationToExtract, 8, 4);
                float leftMiddlePart = Q28_4.asFloat(push2);
                sample[i + 1] = leftMiddlePart + sample[i];

                if (i + 2 < sample.length) {
                    int push3 = Bits.extractSigned(elevationToExtract, 4, 4);
                    float rightMiddlePart = Q28_4.asFloat(push3);
                    sample[i + 2] = rightMiddlePart + sample[i + 1];

                    if (i + 3 < sample.length) {
                        int push4 = Bits.extractSigned(elevationToExtract, 0, 4);
                        float rightPart = Q28_4.asFloat(push4);
                        sample[i + 3] = rightPart + sample[i + 2];
                    }
                }
            }
        }
        return invertedSample(sample, edgeId);
    }

    private float[] invertedSample(float[] sample, int edgeId) {
        if (isInverted(edgeId)) {
            float[] invertedSample = new float[sample.length];
            for (int i = 0; i < invertedSample.length; i++) {
                invertedSample[i] = sample[sample.length - i - 1];
            }
            return invertedSample;
        } else {
            return sample;
        }
    }

    /**
     * return all the ids of the attributes of a given edge
     *
     * @param edgeId : identity of the edge
     * @return all the ids of the attributes of the edge
     */
    public int attributesIndex(int edgeId) {
        return Short.toUnsignedInt(edgesBuffer.getShort(edgeId * 10 + 8));
    }
}
