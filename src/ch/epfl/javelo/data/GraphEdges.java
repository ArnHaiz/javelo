package ch.epfl.javelo.data;

import ch.epfl.javelo.Bits;
import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    public boolean isInverted(int edgeId) {
        return getPath(edgeId)<0;
    }
    public int targetNodeId(int edgeId) {
        return isInverted(edgeId) ? ~getPath(edgeId) : getPath(edgeId);
    }

    private int getPath(int edgeId){return edgesBuffer.getInt(edgeId*10);}
    public double length(int edgeId) {
        int length = Short.toUnsignedInt(edgesBuffer.getShort(edgeId*10+4));
        return Q28_4.asDouble(length);
    }
    public double elevationGain(int edgeId) {
        int elevation = Short.toUnsignedInt(edgesBuffer.getShort(edgeId*10+6));
        return Q28_4.asDouble(elevation);
    }
    public boolean hasProfile(int edgeId) {
        int profile = profileIds.get(edgeId)>>>30;
        if(profile>0) {
            return true;
        }else{
            return false;
        }
    }

    public float[] profileSamples(int edgeId) {
        switch(profileId(edgeId)){
            case 0:
                return profile0(edgeId);
            case 1:
                return profile1(edgeId);
            case 2:
                return profile2(edgeId);
            case 3:
                return profile3(edgeId);
        }
        return new float[0];
    }

    private int profileId(int edgeId) {
        return profileIds.get(edgeId)>>>30;
    }

    private float[] initSamples(int edgeId) {
        int length = Short.toUnsignedInt(edgesBuffer.getShort(edgeId*10+4));
        int divider = Q28_4.ofInt(2);
        return new float[(1+ Math2.ceilDiv(length, divider))];
    }

    private int indexProfile(int edgeId) {
        int profileId = profileIds.get(edgeId);
        int indexProfile = Bits.extractUnsigned(profileId, 0, 30);
        return indexProfile;
    }

    private float[] profile0(int edgeId) {
        float[] sample = new float[0];
        return sample;
    }

    private float[] profile1(int edgeId) {
        float[] sample = initSamples(edgeId);
        for(int i = 0; i<sample.length; i++) {
            int partOfSample = elevations.get(indexProfile(edgeId)) << (16 * i);
            float elevation = partOfSample >>> (elevations.capacity() - (16 * i));
            if(isInverted(edgeId)) {
                sample[sample.length-i-1] = elevation;
            }else {
                sample[i] = elevation;
            }
        }
        System.out.println(sample[0]);
        return sample;
    }

    private float[] profile2(int edgeId){
        float[] sample = initSamples(edgeId);
        float push = elevations.get(edgeId)<<(16);
        float elevation = ((int)push)>>>((sample.length-1)*16);
        sample[0] = elevation;
        for(int i = 1; i<sample.length; i = i+2) {
            float push1 = elevations.get(edgeId)<<(2*i);
            float leftPart = ((int)push1)>>>((2*sample.length-1)*8);
            sample[i] = leftPart;
            float push2 = elevations.get(edgeId)<<(2*i+1);
            float rightPart = ((int)push2)>>>((2*sample.length-1)*8);
            sample[i+1] = rightPart;
        }
        return sample;
    }

    private float[] profile3(int edgeId) {
        float[] sample = initSamples(edgeId);
        float push = elevations.get(edgeId)<<(16);
        float elevation = ((int)push)>>>((sample.length-1)*16);
        sample[0] = elevation;
        for(int i = 1; i< sample.length; i = i+4) {
            float push1 = elevations.get(edgeId)<<(4*i);
            float leftPart = ((int)push1)>>>((4*sample.length-1)*4);
            sample[i] = leftPart;
            float push2 = elevations.get(edgeId)<<(4*i+1);
            float leftMiddlePart = ((int)push2)>>>((4*sample.length-1)*4);
            sample[i+1] = leftMiddlePart;
            float push3 = elevations.get(edgeId)<<(4*i+2);
            float rightMiddlePart = ((int)push3)>>>((4*sample.length-1)*4);
            sample[i+2] = rightMiddlePart;
            float push4 = elevations.get(edgeId)<<(4*i+3);
            float rightPart = ((int)push4)>>>((4*sample.length-1)*4);
            sample[i+3] = rightPart;
        }
        return sample;
    }


    public int attributesIndex(int edgeId) {
        int index = Short.toUnsignedInt(edgesBuffer.getShort(edgeId*10 + 8));
        return index;
    }
}
