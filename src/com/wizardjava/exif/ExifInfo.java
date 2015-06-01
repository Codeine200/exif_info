package com.wizardjava.exif;

import com.wizardjava.ByteOrder;
import com.wizardjava.NumberManager;
import com.wizardjava.exif.consts.Type;

import java.io.IOException;
import java.util.*;


/**
 * Created by v.golovko on 19-Mar-15.
 */
public class ExifInfo {

    public static final byte[] IDENTIFIER = {0x45, 0x78, 0x69, 0x66, 0x00, 0x00}; // "Exif\000\000";
    public static final int SIZE_IFD_STRUCTURE = 12;
    private byte[] buf;

    private IFD IFD_GPS = new IFD();
    private IFD IFD_Exif = new IFD();
    private IFD IFD_Interoperability = new IFD();

    private ByteOrder byteOrder;

    private static final String ERR_NOT_EXIF = "Data is not exif format";

    private enum IFDTags {
        ExifIFDPointer(0x8769),
        GPSInfoIFDPointer(0x8825),
        InteroperabilityIFDPointer(0xA005);

        private int value;
        IFDTags(int value) {
            this.value = value;
        }
        public int getValue() { return value; }

        private static final Map<Integer, IFDTags> lookup = new HashMap<Integer, IFDTags>();
        static {
            for (IFDTags tag : IFDTags.values())
                lookup.put(tag.getValue(), tag);
        }

        public static IFDTags get(Integer value) {
            return lookup.get(value);
        }

        public String toString() {
            return String.format("%s (0x%s)", this.name(), Integer.toHexString(value));
        }
    }

    private class IFD {
        private IFDTags tag;    // Each tag is assigned a unique 2-byte number to identify the field
        private Type type;
        private int count;
        private int offset;     // This tag records the offset from the start of the TIFF header to the position where the value itself is recorded

        public Type getType() {
            return type;
        }

        public void setType(Type type) {
            this.type = type;
        }

        public IFDTags getTag() {
            return tag;
        }

        public void setTag(IFDTags tag) {
            this.tag = tag;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }
    }

    private static class Image {
        private byte[] XResolution = new byte[8];
        private byte[] YResolution = new byte[8];
        private byte[] ResolutionUnit = new byte[2];
        private byte[] YCbCrPositioning = new byte[2];
    }

    public ExifInfo(byte[] data) {

        if(!isExifInfo(data))
            throw new IllegalArgumentException(ERR_NOT_EXIF);

        buf = Arrays.copyOfRange(data, 0, data.length);

        int offsetToTIFF = IDENTIFIER.length;

        TIFF tiff = new TIFF(Arrays.copyOfRange(data, IDENTIFIER.length, IDENTIFIER.length + TIFF.SIZE));
        int offsetToIFD0 = tiff.getOffsetIFD0();
        byteOrder = tiff.getByteOrder();

        NumberManager numberManager = new NumberManager();
        numberManager.setByteOrder(byteOrder);

        System.out.println("#### EXIF");
        int countIFD = numberManager.getChar(buf, offsetToTIFF + offsetToIFD0);
        System.out.println("Count of IFD structures: " + countIFD);

        if(countIFD <= 0)
            throw new IllegalArgumentException(ERR_NOT_EXIF);

        IFDTags tag;
        int offset = offsetToTIFF + offsetToIFD0 + 2; // 2b - size of count of tags
        for(int i=0; i<countIFD; i++) {
            tag = IFDTags.get((int)numberManager.getChar(buf, offset));
            offset += (2 + 2 + 4);  // 2 - size of tag field
                                    // 2 - size of type field
                                    // 4 - size of count field

            if(tag != null) { // fill only GPSInfoIFDPointer, ExifIFDPointer, InteroperabilityIFDPointer structures
                /* Setting default values and set offset */
                switch (tag) {
                    case GPSInfoIFDPointer:
                        IFD_GPS.setTag(IFDTags.GPSInfoIFDPointer);
                        IFD_GPS.setType(Type.LONG);
                        IFD_GPS.setCount(1);
                        int offsetToGPSData = numberManager.getInt(buf, offset);
                        IFD_GPS.setOffset(offsetToGPSData);
                        System.out.println("Offset to GPS data: " + offsetToGPSData + " 0x" + Integer.toHexString(offsetToGPSData));
                        break;

                    case ExifIFDPointer:
                        IFD_Exif.setTag(IFDTags.ExifIFDPointer);
                        IFD_Exif.setType(Type.LONG);
                        IFD_Exif.setCount(1);
                        IFD_Exif.setOffset(numberManager.getInt(buf, offset) + 2);
                        break;

                    case InteroperabilityIFDPointer:
                        IFD_Interoperability.setTag(IFDTags.InteroperabilityIFDPointer);
                        IFD_Interoperability.setType(Type.LONG);
                        IFD_Interoperability.setCount(1);
                        IFD_Interoperability.setOffset(numberManager.getInt(buf, offset) + 2);
                        break;
                }
            }
            offset += 4; // 4 - size of offset field

        }
    }

    public static boolean isExifInfo(byte[] data) {
        if(data.length < IDENTIFIER.length)
            return false;

        byte[] exif = Arrays.copyOfRange(data, 0, IDENTIFIER.length);
        return Arrays.equals(exif, IDENTIFIER);
    }

    public GPS getGPS() {
        return new GPS(buf, IDENTIFIER.length + IFD_GPS.getOffset(), byteOrder);
    }
}
