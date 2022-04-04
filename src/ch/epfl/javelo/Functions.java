package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;
/**
 * @author Hervé Sérandour (328233)
 *
 * class representing a single route composed of a list of edges.
 */
public final class Functions {
    /**
     * return a constant function of value y
     * @param y : value of the constant
     * @return a constant function of value y
     */
    public static DoubleUnaryOperator constant (double y) {
        return new Constant(y);
    }
    /**
     * return a function obtained by linear interpolation on the samples, regularly spaced out between 0 and xMax
     * and throw an exception if there are less than 2 samples
     * @param samples : array of samples
     * @param xMax : position of the last index of the array
     * @throws IllegalArgumentException if there are less than 2 samples
     * @return a function obtained by linear interpolation on the samples, regularly spaced out between 0 and xMax
     */
    public static DoubleUnaryOperator sampled (float[] samples, double xMax) {
        return new Sample(samples, xMax);
    }
    private static final class Constant
            implements DoubleUnaryOperator {
        private double cste;
        public Constant(double y) {
            this.cste = y;
        }
        @Override
        public double applyAsDouble(double operand) {
            return cste;
        }
    }
    private static final class Sample
            implements DoubleUnaryOperator {
        private static float[] useSamples;
        private static double useXMax;
        public Sample(float[] samples, double xMax) {
            Preconditions.checkArgument(samples.length>=2);
            useSamples = samples;
            useXMax = xMax;
        }
        @Override
        public double applyAsDouble(double operand) {

            if(operand<=0) {
                return useSamples[0];
            }else if(operand>=useXMax) {
                return useSamples[useSamples.length-1];
            }else{
                double intervalSize = useXMax/(useSamples.length-1);
                int sampleIdx = (int)Math.floor(operand/intervalSize);
                double  x =((operand-sampleIdx*intervalSize)/intervalSize);
                return Math2.interpolate(useSamples[sampleIdx], useSamples[sampleIdx + 1], x);

            }
        }
    }
}
