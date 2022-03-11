package ch.epfl.javelo.data;

import ch.epfl.javelo.Math2;
import ch.epfl.javelo.Q28_4;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public record GraphEdges(ByteBuffer edgesBuffer, IntBuffer profileIds, ShortBuffer elevations) {
    public boolean isInverted(int edgeId) {
        int intverted = edgesBuffer.getInt(edgeId*10);
        if(intverted>=0) {
            return false;
        }else{
            return true;
        }
    }
    public int targetNodeId(int edgeId) {
        int path = edgesBuffer.getInt(edgeId*10);
        if(isInverted(path)){
            return ~path;
        }else{
            return path;
        }
    }
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
        int length = Short.toUnsignedInt(edgesBuffer.getShort(edgeId*10+4));
        int divider = Q28_4.ofInt(2);
        float[] sample = new float[(1+ Math2.ceilDiv(length, divider))];
        if(hasProfile(edgeId)) {
            int profile = profileIds.get(edgeId)>>>30;
            if(profile==1) {
                for(int i = 0; i<sample.length; i++) {
                    float push = elevations.get(edgeId)<<(i*16);
                    float elevation = ((int)push)>>>((sample.length-1)*16);
                    sample[i] = elevation;
                }
            }else if(profile==2) {
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
            }else if(profile==3) {
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
            }
            return sample;
        }else{
            for(int i = 0; i<sample.length; i++) {
                sample[i] = 0;
            }
            return sample;
        }
    }
    public int attributesIndex(int edgeId) {
        int index = edgesBuffer.getInt(edgeId*10 + 8);
        return index;
    }
}
