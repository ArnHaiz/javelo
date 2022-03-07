package ch.epfl.javelo;

/**
 * Helper class to help extract sequence of bits from a 32 bits vector.
 * 
 * @author Arnaud Haizmann (329072)
 * @author Florian Kolly (328313)
 */
public final class Bits {
  /**
   * Constructor of the non-instantiable Bits class.
   */
  private Bits() {
  }

  /**
   * Extracts from the 32 bits vector <code>value</code> the span of
   * <code>length</code> bits beginning at index <code>start</code>
   * and interpreting it as a 2 complement signed value.
   * 
   * @param value  the 32 bits vector
   * @param start  the start index
   * @param length the length of the span
   * @throws IllegalArgumentException if the span is invalid. This happens if
   *                                  start or lengths strictly negative or if
   *                                  their sum strictly exceeds 32.
   * @return the extracted sequence of bits.
   */
  public static int extractSigned(int value, int start, int length) {
    Preconditions.checkArgument(start >= 0 && length >= 0 && length <= 32 && start + length <= 32);
    value = value << (32 - start - length);
    value = value >> (32 - length);
    return value;
  }

  /**
   * Extracts from the 32 bits vector <code>value</code> the span of
   * <code>length</code> bits beginning at index <code>start</code>
   * and interpreting it as an unsigned value.
   * 
   * @param value  the 32 bits vector
   * @param start  the start index
   * @param length the length of the span
   * @throws IllegalArgumentException if the span is invalid. This happens if
   *                                  start or length is strictly negative, if
   *                                  their sum strictly exceeds 32 or if length
   *                                  is greater than 32.
   * @return the extracted sequence of bits.
   */
  public static int extractUnsigned(int value, int start, int length) {
    Preconditions.checkArgument(start >= 0 && length >= 0 && length < 32 && start + length <= 32);
    value = value << (32 - start - length);
    value = value >>> (32 - length);
    return value;
  }
}
