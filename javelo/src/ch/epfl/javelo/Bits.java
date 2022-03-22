package ch.epfl.javelo;

public final class Bits {
    public static int extractSigned (int value, int start, int length) {
        Preconditions.checkArgument(0<=length);
        Preconditions.checkArgument(0<=start);
        Preconditions.checkArgument(start+length<=32);
        int leftPush = value<<(32-(start+length));
        int rightPush = leftPush>>(32-length);
        return rightPush;
    };
    public static int extractUnsigned (int value, int start, int length) {
        Preconditions.checkArgument((0<=length)&&(length<32));
        Preconditions.checkArgument((0<=start)&&(start<32));
        Preconditions.checkArgument((start+length<=32));
        int leftPush = value<<(32-(start+length));
        int rightPush = leftPush>>>(32-length);
        return rightPush;
    };
}
