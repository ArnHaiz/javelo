package ch.epfl.javelo.data;

import java.util.StringJoiner;

import ch.epfl.javelo.Preconditions;

/**
 * @author Arnaud Haizmann (329072)
 *
 * Class (record) representing a set of OpenStreetMap attributes.
 */
public record AttributeSet(long bits) {
  /**
   * Constructor for a set of attributes.
   * 
   * @param bits represents the content of the set of attributes
   */
  public AttributeSet {
    Preconditions.checkArgument((bits >> Attribute.COUNT) == 0);
  }

  /**
   * Returns an attribute set containing the attributes given in argument.
   * 
   * @param attributes the attributes to add to the set
   * @return the set of attributes with a value of 1 at each comprised attribute
   *         and 0 if the attribute is not part of the set.
   */
  public static AttributeSet of(Attribute... attributes) {
    long bits = 0L;
    for (Attribute attribute : attributes) {
      bits |= (1L << attribute.ordinal());
    }
    return new AttributeSet(bits);
  }

  /**
   * Checks if the attribute set contains the given attribute.
   * 
   * @param attribute the attribute to verify if it is contained in the set
   * @return true if the attribute is in the set, false otherwise.
   */
  public boolean contains(Attribute attribute) {
    long mask = 1L << attribute.ordinal();
    return (bits & mask) == mask;
  }

  /**
   * Checks if the intersection between the current set and the given set is not
   * empty.
   * 
   * @param that the set to compare against
   * @return true if the intersection between both sets is not empty.
   */
  public boolean intersects(AttributeSet that) {
    return (this.bits & that.bits) != 0;
  }

  @Override
  public String toString() {
    StringJoiner joiner = new StringJoiner(",", "{", "}");
    for (Attribute a : Attribute.ALL) {
      if (contains(a))
        joiner.add(a.keyValue());
    }
    return joiner.toString();
  }
}
