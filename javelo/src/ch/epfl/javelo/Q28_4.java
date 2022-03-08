package ch.epfl.javelo;

/**
 * Helper class for conversion between Q28.4 format and Java floating point
 * types.
 *
 * @author Arnaud Haizmann (329072)
 * @author Florian Kolly (328313)
 */
public final class Q28_4 {
  /**
   * Constructor of the non-instantiable Q28_4 class.
   */
  private Q28_4() {
  }

  /**
   * Converts a given integer in the Q28_4 representation of it.
   *
   * @param i the integer to reformat to the Q28.4 format
   * @return the Q28_4 representation of the given integer.
   */
  public static int ofInt(int i) {
    return i << 4;
  }

  /**
   * Converts a given Q28_4 number to its double equivalent.
   *
   * @param q28_4 the integer representation to convert to a double
   * @return the double representation of the given Q28_4.
   */
  public static double asDouble(int q28_4) {
    return (double)Math.scalb((float) q28_4, -4);
  }

  /**
   * Converts a given Q24_8 number to its float equivalent.
   *
   * @param q28_4 the integer representation to convert to a float.
   * @return the float representation of the given Q28_4.
   */
  public static float asFloat(int q28_4) {
    return Math.scalb((float) q28_4, -4);
  }
}