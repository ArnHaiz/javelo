package ch.epfl.javelo.data;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class AttributeSetTest {
  @Test
  public void constructorThrowsOnInvalidAttributes() {
    assertThrows(IllegalArgumentException.class,
        () -> new AttributeSet(0b1000000000000000000000000000000000000000000000000000000000000000L));
    assertThrows(IllegalArgumentException.class,
        () -> new AttributeSet(0b01000000000000000000000000000000000000000000000000000000000000000L));
    assertThrows(IllegalArgumentException.class,
        () -> new AttributeSet(0b1100000000000000000011000000010010000000000000000001110000000000L));
    assertDoesNotThrow(() -> new AttributeSet(0b0000000000000000000011000000010010000000000000000001110000000000L));
  }

  @Test
  public void ofWorksForValidAttributes() {
    AttributeSet set = AttributeSet.of(new Attribute[] {
        Attribute.HIGHWAY_TRACK, // 1
        Attribute.TRACKTYPE_GRADE1, // 17
        Attribute.SURFACE_GRASS, // 30
        Attribute.VEHICLE_PRIVATE, // 43
        Attribute.LCN_YES // 61
    });
    long bits = 0b0010000000000000000010000000000001000000000000100000000000000010L;
    assertEquals(bits, set.bits());
  }

  @Test
  public void containsWorks() {
    AttributeSet set = AttributeSet.of(new Attribute[] {
        Attribute.HIGHWAY_TRACK, // 1
        Attribute.TRACKTYPE_GRADE1, // 17
        Attribute.SURFACE_GRASS, // 30
        Attribute.VEHICLE_PRIVATE, // 43
        Attribute.LCN_YES // 61
    });
    assertTrue(set.contains(Attribute.HIGHWAY_TRACK));
    assertTrue(set.contains(Attribute.TRACKTYPE_GRADE1));
    assertTrue(set.contains(Attribute.SURFACE_GRASS));
    assertTrue(set.contains(Attribute.VEHICLE_PRIVATE));
    assertTrue(set.contains(Attribute.LCN_YES));
    assertFalse(set.contains(Attribute.HIGHWAY_CYCLEWAY));
    assertFalse(set.contains(Attribute.VEHICLE_NO));
    AttributeSet emptySet = AttributeSet.of(new Attribute[] {});
    AttributeSet fullSet = AttributeSet.of(Attribute.values());
    for (Attribute a : Attribute.ALL) {
      assertFalse(emptySet.contains(a));
      assertTrue(fullSet.contains(a));
    }
  }

  @Test
  public void intersectsWorks() {
    AttributeSet setThis = AttributeSet.of(new Attribute[] {
        Attribute.HIGHWAY_TRACK, // 1
        Attribute.TRACKTYPE_GRADE1, // 17
        Attribute.SURFACE_GRASS, // 30
        Attribute.VEHICLE_PRIVATE, // 43
        Attribute.LCN_YES // 61
    });
    AttributeSet setThat = AttributeSet.of(new Attribute[] {
        Attribute.HIGHWAY_RESIDENTIAL, // 2
        Attribute.TRACKTYPE_GRADE1, // 17
        Attribute.SURFACE_GRASS, // 30
        Attribute.VEHICLE_NO, // 42
        Attribute.LCN_YES // 61
    });
    AttributeSet emptySet = AttributeSet.of(new Attribute[] {});
    AttributeSet fullSet = AttributeSet.of(Attribute.values());
    assertTrue(setThis.intersects(setThat));
    assertTrue(setThis.intersects(setThis));
    assertFalse(emptySet.intersects(setThat));
    assertFalse(emptySet.intersects(emptySet));
    assertTrue(fullSet.intersects(setThat));
    assertTrue(fullSet.intersects(fullSet));
  }

  @Test
  public void toStringWorksWithExample() {
    AttributeSet set = AttributeSet.of(new Attribute[] {
        Attribute.HIGHWAY_TRACK, // 1
        Attribute.TRACKTYPE_GRADE1 // 17
    });
    String expected = "{highway=track,tracktype=grade1}";
    assertEquals(expected, set.toString());
  }

  @Test
  public void toStringWorksWithEmptySet() {
    AttributeSet emptySet = AttributeSet.of(new Attribute[] {});
    assertEquals("{}", emptySet.toString());
  }

  @Test
  public void toStringWorksWithValidSet() {
    AttributeSet set = AttributeSet.of(new Attribute[] {
        Attribute.HIGHWAY_RESIDENTIAL, // 2
        Attribute.TRACKTYPE_GRADE2, // 18
        Attribute.SURFACE_GRASS, // 30
        Attribute.VEHICLE_NO, // 42
        Attribute.LCN_YES // 61
    });
    String expected = "{highway=residential,tracktype=grade2,surface=grass,vehicle=no,lcn=yes}";
    assertEquals(expected, set.toString());
  }
}
