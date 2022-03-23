package ch.epfl.javelo;

/**
 * @author Arnaud Haizmann (329072)
 *
 * Helper class for preconditions.
 */
public final class Preconditions {
    /**
     * Constructor of the non-instantiable Preconditions class.
     */
    private Preconditions() {
    }

    /**
     * General method to check the argument. The validity condition is given
     * in argument as a boolean value. If the condition is not fulfilled, the
     * method throws an <code>IllegalArgumentException</code>.
     *
     * @param shouldBeTrue the condition that must be fulfilled
     * @throws IllegalArgumentException if the condition is not fulfilled.
     */
    public static void checkArgument(boolean shouldBeTrue) {
        if (!shouldBeTrue) {
            throw new IllegalArgumentException();
        }
    }
}
