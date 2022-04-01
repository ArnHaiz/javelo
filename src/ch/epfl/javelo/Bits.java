package ch.epfl.javelo;

/**
 * @author Hervé Sérandour (328233)
 *
 * class for extract a bit sequence in 32 bits vector.
 */
public final class Bits {
    /**
     * extract of a 32 bits vector the bit sequence of given length and at the start bit given by taking count of the sign
     * @param value : 32 bits vector
     * @param start : start bit
     * @param length : length wanted
     * @throws IllegalArgumentException if the bit sequence is invalid
     * @return the bit sequence extract of the 32 bits vector by taking count of the sign
     */
    public static int extractSigned (int value, int start, int length) {
        Preconditions.checkArgument(0<=length);
        Preconditions.checkArgument(0<=start);
        Preconditions.checkArgument(start+length<=32);
        int leftPush = value<<(32-(start+length));
        int rightPush = leftPush>>(32-length);
        return rightPush;
    }
    /**
     * extract of a 32 bits vector the bit sequence of given length and at the start bit given without taking count of the sign
     * @param value : 32 bits vector
     * @param start : start bit
     * @param length : length wanted
     * @throws IllegalArgumentException if the bit sequence is invalid
     * @return the bit sequence extract of the 32 bits vector without taking count of the sign
     */
    public static int extractUnsigned (int value, int start, int length) {
        Preconditions.checkArgument((0<=length)&&(length<32));
        Preconditions.checkArgument((0<=start)&&(start<32));
        Preconditions.checkArgument((start+length<=32));
        int leftPush = value<<(32-(start+length));
        int rightPush = leftPush>>>(32-length);
        return rightPush;
    }
}
