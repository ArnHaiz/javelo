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
            double y;
            if(operand<0) {
                y = useSamples[0];
            }else {
                if(operand>useXMax) {
                    y = useSamples[useSamples.length-1];
                }else {
                    double intervalSize = useXMax/(useSamples.length-1);
                    double x = operand/intervalSize;
                    int floor = (int)Math.floor(operand/intervalSize);
                    y = Math2.interpolate(useSamples[floor], useSamples[floor + 1], ((operand-floor*intervalSize)/intervalSize));
                }
            }
            return y;
        }
    }
}
