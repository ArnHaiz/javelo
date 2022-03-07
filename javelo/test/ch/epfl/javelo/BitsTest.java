package ch.epfl.javelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class BitsTest {
  @Test
  public void extractSignedThrows() {
    int value = 0b11001010111111101011101010111110;
    assertThrows(IllegalArgumentException.class, () -> Bits.extractSigned(value, -2, 1));
    assertThrows(IllegalArgumentException.class, () -> Bits.extractSigned(value, 0, -1));
    assertThrows(IllegalArgumentException.class, () -> Bits.extractSigned(value, 27, 6));
  }

  @Test
  public void extractSignedWorksWithExample() {
    int value = 0b11001010111111101011101010111110;
    assertEquals(0b11111111111111111111111111111010, Bits.extractSigned(value, 8, 4));
  }

  @Test
  public void extractSignedWorksWithValidValues() {
    assertEquals(0b11111111111111111111111111111110, Bits.extractSigned(0b11001010111111101011111010111110, 8, 4));
    assertEquals(0b11111111111111111111111111111010, Bits.extractSigned(0b00101111011111010111101001110011, 8, 4));
    assertEquals(0b00000000000000000000000000000101, Bits.extractSigned(0b00101111011111010111101001110011, 27, 5));
  }

  @Test
  public void extractSignedWorksOnFullSpan() {
    assertEquals(0b00101111011111010111101001110011, Bits.extractSigned(0b00101111011111010111101001110011, 0, 32));
    assertEquals(0b11111111111100111100111111111010, Bits.extractSigned(0b11111111111100111100111111111010, 0, 32));
  }

  @Test
  public void extractSignedWorksWith0() {
    assertEquals(0, Bits.extractSigned(0, 0, 32));
  }

  @Test
  public void extractSignedWorksWithMinus1() {
    assertEquals(-1, Bits.extractSigned(-1, 30, 2));
  }

  @Test
  public void extractSignedWorksWithMaxInt() {
    assertEquals(1, Bits.extractSigned(Integer.MAX_VALUE, 30, 2));
  }

  @Test
  public void extractUnsignedThrows() {
    int value = 0b11001010111111101011101010111110;
    assertThrows(IllegalArgumentException.class, () -> Bits.extractUnsigned(value, -2, 1));
    assertThrows(IllegalArgumentException.class, () -> Bits.extractUnsigned(value, 0, -1));
    assertThrows(IllegalArgumentException.class, () -> Bits.extractUnsigned(value, 27, 6));
    assertThrows(IllegalArgumentException.class, () -> Bits.extractUnsigned(value, 3, 32));
  }

  @Test
  public void extractUnsignedWorksWithExample() {
    int value = 0b11001010111111101011101010111110;
    assertEquals(0b00000000000000000000000000001010, Bits.extractUnsigned(value, 8, 4));
  }

  @Test
  public void extractUnsignedWorksWithValidValues() {
    assertEquals(0b00000000000000000000000000001010, Bits.extractUnsigned(0b11011011110011101011101010111011, 8, 4));
    assertEquals(0b00000000000000000000000000000011, Bits.extractUnsigned(0b10000110111111101011001110111010, 8, 4));
    assertEquals(0b00000000000000000000000000010010, Bits.extractUnsigned(0b10010110111111101011001110111010, 27, 5));
    assertEquals(1023, Bits.extractUnsigned(-1, 4, 10));
    assertEquals(1023, Bits.extractUnsigned(-1, 10, 10));
  }

  @Test
  public void extractUnsignedWorksOnFullSpan() {
    assertEquals(0b01001010111111101011101010111110, Bits.extractUnsigned(0b11001010111111101011101010111110, 0, 31));
  }

  @Test
  public void extractUnsignedWorksWith0() {
    assertEquals(0, Bits.extractUnsigned(0, 0, 31));
  }

  @Test
  public void extractUnsignedWorksWithMaxInt() {
    assertEquals(1, Bits.extractUnsigned(Integer.MAX_VALUE, 30, 2));
  }

  @Test
  public void extractUnsignedWorksWithMinus1() {
    assertEquals(3, Bits.extractUnsigned(-1, 30, 2));
  }
}
