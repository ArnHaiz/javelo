package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

public final class Functions {
    public static DoubleUnaryOperator constant (double y) {
        return new Constant(y);
    }

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
