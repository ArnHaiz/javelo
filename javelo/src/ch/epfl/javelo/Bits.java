package ch.epfl.javelo;

public final class Bits {
    public static int extractSigned (int value, int start, int length) {
        Preconditions.checkArgument(length<32);
        int leftPush = value<<(32-start-length);
        int rightPush = leftPush>>start;
        return rightPush;
    };
    public static int extractUnsigned (int value, int start, int length) {
        Preconditions.checkArgument(length<32);
        int leftPush = value<<(32-start-length);
        int rightPush = leftPush>>>start;
        return rightPush;
    };
}
