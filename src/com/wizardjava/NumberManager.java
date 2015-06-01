package com.wizardjava;

/**
 * Created by v.golovko on 24-Mar-15.
 */
public class NumberManager {
    private ByteOrder byteOrder = ByteOrder.MM;
    public final static int BYTE_MASK = 0xff;

    public void setByteOrder(ByteOrder byteOrder) {
        this.byteOrder = byteOrder;
    }

    public char getChar(byte[] buf, int offset) {
        int a = buf[offset++] & BYTE_MASK;
        int b = buf[offset] & BYTE_MASK;

        return (byteOrder == ByteOrder.MM) ?    (char) ((a << 8) | b) :
                                                (char) ((b << 8) | a);
    }

    public int getInt(byte[] buf, int offset) {
        int a = buf[offset++] & BYTE_MASK;
        int b = buf[offset++] & BYTE_MASK;
        int c = buf[offset++] & BYTE_MASK;
        int d = buf[offset] & BYTE_MASK;
        return (byteOrder == ByteOrder.MM) ? ((a << 24) | (b << 16) | (c << 8) | d) : // 0xabcd
                                             (a | (b << 8) | (c << 16) | (d << 24)); // 0xdcba

    }

    public static Byte[] convertToByte(byte[] bytes) {
        Byte[] byteObjects = new Byte[bytes.length];

        int i = 0;
        // Associating Byte array values with bytes. (byte[] to Byte[])
        for(byte b: bytes)
            byteObjects[i++] = b;  // Autoboxing.

        return byteObjects;
    }

    public static byte[] convertTobyte(Byte[] Bytes) {
        byte[] bytes = new byte[Bytes.length];

        int j=0;
        // Unboxing byte values. (Byte[] to byte[])
        for(Byte b: Bytes)
            bytes[j++] = b;

        return bytes;
    }
}
