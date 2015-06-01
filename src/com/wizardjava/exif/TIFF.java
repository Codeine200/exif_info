package com.wizardjava.exif;

import com.wizardjava.ByteOrder;
import com.wizardjava.NumberManager;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by v.golovko on 25-Mar-15.
 */
public class TIFF {
    private ByteOrder byteOrder;   // 'II' (little-endian) or 'MM' (big-endian)
    public static final byte[] IDENTIFIER = {0x00, 0x2a};
    private int IFD0_Pointer;   // offset of 0th IFD (usually 8), not stored
    // 0th IFD offset. If the TIFF header is followed immediately by the 0th IFD, it is written as 00000008

    private static final String ERR_NOT_TIFF = "Data is not tiff format";

    public static final int SIZE = 8;

    public TIFF(byte[] data)  {
        System.out.println("#### TIFF");
        int offset = 0;
        byteOrder = ByteOrder.get(((data[offset++] & NumberManager.BYTE_MASK) << 8) | (data[offset++] & NumberManager.BYTE_MASK));
        if(byteOrder == null)
            throw new IllegalArgumentException(ERR_NOT_TIFF);

        NumberManager numberManager = new NumberManager();
        numberManager.setByteOrder(byteOrder);
        System.out.println("byteOrder: " + byteOrder);
        char signature = numberManager.getChar(data, offset);

        if(!isTIFF(new byte[] {(byte)(signature >> NumberManager.BYTE_MASK), (byte)signature} ))
            throw new IllegalArgumentException(ERR_NOT_TIFF);

        offset += IDENTIFIER.length;

        IFD0_Pointer = numberManager.getInt(data, offset);
        System.out.println("offset IFD0: " + IFD0_Pointer);
    }

    public ByteOrder getByteOrder() {
        return byteOrder;
    }
    public int getOffsetIFD0() {
        return IFD0_Pointer;
    }

    public static boolean isTIFF(byte[] data) {
        if(data.length < IDENTIFIER.length)
            return false;

        byte[] tiff = Arrays.copyOfRange(data, 0, IDENTIFIER.length);
        return Arrays.equals(tiff, IDENTIFIER);
    }
}
