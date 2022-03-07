package ch.epfl.javelo;

import static org.junit.Assert.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.function.DoubleUnaryOperator;

import org.junit.Test;

public class FunctionsTest {
  @Test
  public void constantFunctionReturnsConstantValue() {
    DoubleUnaryOperator const2 = Functions.constant(2);
    DoubleUnaryOperator const0 = Functions.constant(0);
    assertEquals(2, const2.applyAsDouble(3));
    assertNotEquals(3, const2.applyAsDouble(3));
    assertEquals(0, const0.applyAsDouble(1));
    assertNotEquals(3, const0.applyAsDouble(3));
  }

  @Test
  public void sampledFunctionThrowsOnNotLargeEnoughSamplesArray() {
    assertThrows(IllegalArgumentException.class, () -> Functions.sampled(new float[] {}, 2));
    assertThrows(IllegalArgumentException.class, () -> Functions.sampled(new float[] { 2f }, 2));
  }

  @Test
  public void sampledFunctionThrowsOnNotXMaxLessOrEqualThan0() {
    float[] samples = new float[] { 2f, 1f };
    assertThrows(IllegalArgumentException.class, () -> Functions.sampled(samples, 0));
    assertThrows(IllegalArgumentException.class, () -> Functions.sampled(samples, -1));
  }

  @Test
  public void sampledFunctionWorksOnTrivialArray() {
    float[] samples = new float[] { 2f, 1f };
    int xMax = 1;
    DoubleUnaryOperator sampled = Functions.sampled(samples, xMax);
    assertEquals(1.5, sampled.applyAsDouble(0.5));
    assertEquals(2, sampled.applyAsDouble(0));
    assertEquals(1, sampled.applyAsDouble(1));
    assertEquals(2, sampled.applyAsDouble(-10));
    assertEquals(1, sampled.applyAsDouble(10));
  }

  @Test
  public void sampledFunctionWorksOnNonTrivialArray1() {
    float[] samples = new float[] { 4f, 2f, 1f, 7f, 3f, 0f };
    int xMax = 10;
    DoubleUnaryOperator sampled = Functions.sampled(samples, xMax);
    assertEquals(4, sampled.applyAsDouble(0));
    assertEquals(3, sampled.applyAsDouble(1));
    assertEquals(2, sampled.applyAsDouble(2));
    assertEquals(1.5, sampled.applyAsDouble(3));
    assertEquals(1, sampled.applyAsDouble(4));
    assertEquals(4, sampled.applyAsDouble(5));
    assertEquals(7, sampled.applyAsDouble(6));
    assertEquals(5, sampled.applyAsDouble(7));
    assertEquals(3, sampled.applyAsDouble(8));
    assertEquals(1.5, sampled.applyAsDouble(9));
    assertEquals(0, sampled.applyAsDouble(10));
    assertEquals(4, sampled.applyAsDouble(-100));
    assertEquals(0, sampled.applyAsDouble(100));
  }

  @Test
  public void sampledFunctionWorksOnNonTrivialArray2() {
    float[] samples = new float[] { 8f, 3f, 6f, 11f };
    int xMax = 6;
    DoubleUnaryOperator sampled = Functions.sampled(samples, xMax);
    assertEquals(8, sampled.applyAsDouble(0));
    assertEquals(6.75, sampled.applyAsDouble(0.5));
    assertEquals(5.5, sampled.applyAsDouble(1));
    assertEquals(4.25, sampled.applyAsDouble(1.5));
    assertEquals(3, sampled.applyAsDouble(2));
    assertEquals(3.75, sampled.applyAsDouble(2.5));
    assertEquals(4.5, sampled.applyAsDouble(3));
    assertEquals(5.25, sampled.applyAsDouble(3.5));
    assertEquals(6, sampled.applyAsDouble(4));
    assertEquals(7.25, sampled.applyAsDouble(4.5));
    assertEquals(8.5, sampled.applyAsDouble(5));
    assertEquals(9.75, sampled.applyAsDouble(5.5));
    assertEquals(11, sampled.applyAsDouble(6));
    assertEquals(8, sampled.applyAsDouble(-100));
    assertEquals(11, sampled.applyAsDouble(100));
  }
}
