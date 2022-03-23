package ch.epfl.javelo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class Q28_4Test {
  @Test
  public void ofIntWorks() {
    assertEquals(0, Q28_4.ofInt(0));
    assertEquals(0b00100010100101001011010011110000, Q28_4.ofInt(0b10100010001010010100101101001111));
    assertEquals(0b11111111111111111111111111110000, Q28_4.ofInt(0b11111111111111111111111111111111));
    assertEquals(0b00000000000000000000000000010000, Q28_4.ofInt(1));
    assertEquals(0b00000000000000000000000000100000, Q28_4.ofInt(2));
  }

  @Test
  public void asDoubleWorks() {
    assertEquals(0, Q28_4.asDouble(0));
    assertEquals(0.5, Q28_4.asDouble(0b00000000000000000000000000001000));
    assertEquals(0.25, Q28_4.asDouble(0b00000000000000000000000000000100));
    assertEquals(0.125, Q28_4.asDouble(0b00000000000000000000000000000010));
    assertEquals(0.0625, Q28_4.asDouble(0b00000000000000000000000000000001));
    assertEquals(-Math.pow(2, 27), Q28_4.asDouble(0b10000000000000000000000000000000));
    assertEquals(Math.pow(2, 26), Q28_4.asDouble(0b01000000000000000000000000000000));
    assertEquals(1, Q28_4.asDouble(0b00000000000000000000000000010000));
    assertEquals(2, Q28_4.asDouble(0b00000000000000000000000000100000));
    assertEquals(3, Q28_4.asDouble(0b00000000000000000000000000110000));
    assertEquals(-1, Q28_4.asDouble(0b11111111111111111111111111110000));
    assertEquals(3.625, Q28_4.asDouble(0b00000000000000000000000000111010));
    assertEquals(-3.625, Q28_4.asDouble(0b11111111111111111111111111000110));
    assertEquals(134217727.9375, Q28_4.asDouble(2147483647));
  }

  @Test
  public void asFloatWorks() {
    assertEquals(0f, Q28_4.asFloat(0));
    assertEquals(0.5f, Q28_4.asFloat(0b00000000000000000000000000001000));
    assertEquals(0.25f, Q28_4.asFloat(0b00000000000000000000000000000100));
    assertEquals(0.125f, Q28_4.asFloat(0b00000000000000000000000000000010));
    assertEquals(0.0625f, Q28_4.asFloat(0b00000000000000000000000000000001));
    assertEquals((float) -Math.pow(2, 27), Q28_4.asFloat(0b10000000000000000000000000000000));
    assertEquals((float) Math.pow(2, 26), Q28_4.asFloat(0b01000000000000000000000000000000));
    assertEquals(1f, Q28_4.asFloat(0b00000000000000000000000000010000));
    assertEquals(2f, Q28_4.asFloat(0b00000000000000000000000000100000));
    assertEquals(3f, Q28_4.asFloat(0b00000000000000000000000000110000));
    assertEquals(-1f, Q28_4.asFloat(0b11111111111111111111111111110000));
    assertEquals(3.625f, Q28_4.asFloat(0b00000000000000000000000000111010));
    assertEquals(-3.625f, Q28_4.asFloat(0b11111111111111111111111111000110));
  }
}
