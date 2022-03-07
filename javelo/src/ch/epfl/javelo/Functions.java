package ch.epfl.javelo;

import java.util.function.DoubleUnaryOperator;

/**
 * This class contains methods that create objects representing mathematical
 * functions (reals to reals).
 * 
 * @author Arnaud Haizmann (329072)
 * @author Florian Kolly (328313)
 */
public final class Functions {
  /**
   * Constructor of the non-instantiable Functions class.
   */
  private Functions() {
  }

  /**
   * Returns a constant function, for which the return value will always be the y
   * parameter.
   * 
   * @param y constant value that the function will always return
   * @return the constant function f(x)=y.
   */
  public static DoubleUnaryOperator constant(double y) {
    return new Constant(y);
  }

  /**
   * Returns a function obtained by linearly interpolating the samples
   * <code>samples</code>, evenly spaced from 0 to <code>xMax</code>. Before 0,
   * the function will return the value at x=0 (i.e. the first value in the array)
   * and after <code>xMax</code>, the function will return the sample at
   * <code>xMax</code>.
   * 
   * @param samples array of evenly space values between 0 and <code>xMax</code>
   * @param xMax    maximum x value for which the function has interpolation
   *                points
   * @throws IllegalArgumentException if the <code>samples</code> array contains
   *                                  less than 2 elements, or if
   *                                  <code>xMax</code> is less than or equal to
   *                                  0.
   * @return a function created by interpolating the samples between 0 and xMax.
   */
  public static DoubleUnaryOperator sampled(float[] samples, double xMax) {
    Preconditions.checkArgument(xMax > 0 && samples.length >= 2);
    return new Sampled(samples, xMax);
  }

  /**
   * Class representing a constant function.
   */
  private static final class Constant implements DoubleUnaryOperator {
    private double y;

    /**
     * Constructor for a Constant function.
     * 
     * @param y the constant value to be returned on each applyAsDouble call.
     */
    public Constant(double y) {
      this.y = y;
    }

    @Override
    public double applyAsDouble(double x) {
      return this.y;
    }
  }

  /**
   * Class representing an interpolating function using samples to determine the
   * value for any given input.
   */
  private static final class Sampled implements DoubleUnaryOperator {
    private float[] samples;
    private double xMax;

    /**
     * Constructor for a Sampled function.
     * 
     * @param samples array of evenly space values between 0 and <code>xMax</code>,
     *                must contain at least 2 elements
     * @param xMax    maximum x value for which the function has interpolation
     *                points
     * @throws IllegalArgumentException if the samples array contains less than 2
     *                                  elements or <code>xMax</code> is less than
     *                                  or equal to 0.
     */
    public Sampled(float[] samples, double xMax) {
      Preconditions.checkArgument(samples.length >= 2 && xMax > 0);
      this.samples = samples;
      this.xMax = xMax;
    }

    @Override
    public double applyAsDouble(double x) {
      if (x <= 0)
        return samples[0];
      if (x >= xMax)
        return samples[samples.length - 1];
      double deltaX = xMax / (samples.length - 1);
      int spacing = (int) (x / deltaX);
      return Math2.interpolate(samples[spacing], samples[spacing + 1], x / deltaX - spacing);
    }
  }
}
