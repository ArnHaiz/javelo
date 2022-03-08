package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

public final class Functions {
    public static DoubleUnaryOperator constant (double y) {
        return new Constant(y);
    };
    public static DoubleUnaryOperator sampled (float[] samples, double xMax) {
        return new Sample(samples, xMax);
    };
    private static final class Constant
            implements DoubleUnaryOperator {
        public Constant(double y) {
        };
        @Override
        public double applyAsDouble(double operand) {
            return operand;
        }
    }
    private static final class Sample
            implements DoubleUnaryOperator {
        private static float[] useSamples;
        public Sample(float[] samples, double xMax) {
            Preconditions.checkArgument(samples.length>=2);
            useSamples = samples;
        };
        @Override
        public double applyAsDouble(double operand) {
            int floor = (int)(Math.floor(operand));
            double y = Math2.interpolate(useSamples[floor],useSamples[floor+1],operand);
            return y;
        };
    }
}
