package com.wizardjava.exif;

import com.wizardjava.ByteOrder;
import com.wizardjava.NumberManager;
import com.wizardjava.StringConverter;
import com.wizardjava.exif.consts.Type;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by v.golovko on 24-Mar-15.
 */
public class GPS {
    private String GPSVersionID;                    // GPS tag version %s.%s.%s.%s
    private String GpsLatitudeRef;                  // North or South Latitude
    private double GpsLatitude;
    private String GpsLongitudeRef;                 // East or West
    private double GpsLongitude;
    private byte GpsAltitudeRef;                    // 0 - above see level, 1 - below see level
    private double GpsAltitude;
    private String GpsTimeStamp;
    private String GpsDateStamp;

    private ByteOrder byteOrder = ByteOrder.MM;

    private int countTag;
    private static final int SIZE_COUNT_ATTRIBUTE = 2;

    private static final int SIZE_TAG_NAME = 2;
    private static final int SIZE_TAG_TYPE = 2;
    private static final int SIZE_TAG_COUNT = 4;
    private static final int SIZE_TAG_VALUE = 4;
    private static final int SIZE_TAG_STRUCT = SIZE_TAG_NAME + SIZE_TAG_TYPE + SIZE_TAG_COUNT + SIZE_TAG_VALUE;

    HashMap<Tag, Integer> offsetToValueByTag = new HashMap<Tag, Integer>();

    private byte[] buf;
    private int offsetToGPS;
    private NumberManager numberManager = new NumberManager();

    private enum Tag {
        GPSVersionID(0x00, Type.BYTE, 4),
        GPSLatitudeRef(0x01, Type.ASCII, 2),
        GPSLatitude(0x02, Type.RATIONAL, 3),
        GPSLongitudeRef(0x03, Type.ASCII, 2),
        GPSLongitude(0x04, Type.RATIONAL, 3),
        GPSAltitudeRef(0x05, Type.BYTE, 1),
        GPSAltitude(0x06, Type.RATIONAL, 1),
        GPSTimeStamp(0x07, Type.RATIONAL, 3),
        GPSSatellites(0x08, Type.ASCII, -1), // -1 - Any
        GPSStatus(0x09, Type.ASCII, 2),
        GPSMeasureMode(0x0a, Type.ASCII, 2),
        GPSDOP(0x0b, Type.RATIONAL, 1),
        GPSSpeedRef(0x0c, Type.ASCII, 2),
        GPSSpeed(0x0d, Type.RATIONAL, 1),
        GPSTrackRef(0x0e, Type.ASCII, 2),
        GPSTrack(0x0f, Type.RATIONAL, 1),
        GPSImgDirectionRef(0x10, Type.ASCII, 2),
        GPSImgDirection(0x11, Type.RATIONAL, 1),
        GPSMapDatum(0x12, Type.ASCII, -1), // -1 - Any
        GPSDestLatitudeRef(0x13, Type.ASCII, 2),
        GPSDestLatitude(0x14, Type.RATIONAL, 3),
        GPSDestLongitudeRef(0x15, Type.ASCII, 2),
        GPSDestLongitude(0x16, Type.RATIONAL, 3),
        GPSDestBearingRef(0x17, Type.ASCII, 2),
        GPSDestBearing(0x18, Type.RATIONAL, 1),
        GPSDestDistanceRef(0x19, Type.ASCII, 2),
        GPSDestDistance(0x1a, Type.RATIONAL, 1),
        GPSProcessingMethod(0x1b, Type.UNDEFINED, -1), // -1 - Any
        GPSAreaInformation(0x1c, Type.UNDEFINED, -1), // -1 - Any
        GPSDateStamp(0x1d, Type.ASCII, 11),
        GPSDifferential(0x1e, Type.SHORT, 1);

        private int value;
        private Type type;
        private int count;
        Tag(int value, Type type, int count) {
            this.value = value;
            this.type = type;
            this.count = count;
        }
        public int getValue() { return value; }
        public Type getType() { return type; }
        public int getCount() { return count; }

        private static final Map<Integer, Tag> lookup = new HashMap<Integer, Tag>();
        static {
            for (Tag type : Tag.values())
                lookup.put(type.getValue(), type);
        }

        public static Tag get(Integer value) {
            return lookup.get(value);
        }

        public String toString() {
            return this.name() + " : " + Integer.toHexString(value);
        }
    }


    public GPS(byte[] exifdata, int offset, ByteOrder byteOrder) {
        offsetToGPS = offset;
        numberManager.setByteOrder(byteOrder);
        countTag = (int)numberManager.getChar(exifdata, offset);
        System.out.println("@GPS Count GPS Tag: " + countTag);
        buf = Arrays.copyOfRange(exifdata, 0, exifdata.length);
        //System.out.println(buf.length);
    }

    public GPS(byte[] exifdata, int offset) {
        this(exifdata, offset, ByteOrder.MM);
    }

    public String getGPSVersionID() {
        if(GPSVersionID != null) {
            return GPSVersionID;
        }

        byte[] value = getValueByTag(Tag.GPSVersionID);
        GPSVersionID = String.format("%s.%s.%s.%s", String.valueOf(value[0]), String.valueOf(value[1]), String.valueOf(value[2]), String.valueOf(value[3]));
        System.out.println("@GPS GPSVersionID: " + GPSVersionID);

        return GPSVersionID;
    }

    public String getGpsLatitudeRef() {
        if(GpsLatitudeRef != null) {
            return GpsLatitudeRef;
        }

        byte[] value = getValueByTag(Tag.GPSLatitudeRef);
        GpsLatitudeRef = StringConverter.convert(value);
        System.out.println("@GPS GpsLatitudeRef: " + GpsLatitudeRef);

        return GpsLatitudeRef;
    }

    public double getGpsLongitude() {
        if(GpsLongitude != 0) {
            return GpsLongitude;
        }

        byte[] value = getValueByTag(Tag.GPSLongitude);
        GpsLongitude = GPSCoordConverter.degreesMinutesSecondsToDecimalDegrees(
                numberManager.getInt(value, 0) / numberManager.getInt(value, 4),
                numberManager.getInt(value, 8) / numberManager.getInt(value, 12),
                numberManager.getInt(value, 16) / numberManager.getInt(value, 20)
        );
        System.out.println("@GPS GpsLongitude: " + GpsLongitude);

        return GpsLongitude;
    }

    public String getGpsLongitudeRef() {
        if(GpsLongitudeRef != null) {
            return GpsLongitudeRef;
        }

        byte[] value = getValueByTag(Tag.GPSLongitudeRef);
        GpsLongitudeRef = StringConverter.convert(value);
        System.out.println("@GPS GpsLongitudeRef: " + GpsLongitudeRef);

        return GpsLongitudeRef;
    }

    public double getGpsLatitude() {
        if(GpsLatitude != 0) {
            return GpsLatitude;
        }

        byte[] value = getValueByTag(Tag.GPSLatitude);
        GpsLatitude = GPSCoordConverter.degreesMinutesSecondsToDecimalDegrees(
                numberManager.getInt(value, 0) / numberManager.getInt(value, 4),
                numberManager.getInt(value, 8) / numberManager.getInt(value, 12),
                numberManager.getInt(value, 16) / numberManager.getInt(value, 20)
        );
        System.out.println("@GPS GpsLatitude: " + GpsLatitude);

        return GpsLatitude;
    }

    public byte getGpsAltitudeRef() {
        if(GpsAltitudeRef != 0) {
            return GpsAltitudeRef;
        }

        byte[] value = getValueByTag(Tag.GPSAltitudeRef);
        GpsAltitudeRef = value[0];
        System.out.println("@GPS GpsAltitudeRef: " + GpsAltitudeRef);

        return GpsAltitudeRef;
    }

    public double getGpsAltitude() {
        if(GpsAltitude != 0) {
            return GpsAltitude;
        }

        byte[] value = getValueByTag(Tag.GPSAltitude);
        GpsAltitude = numberManager.getInt(value, 0) / numberManager.getInt(value, 4);
        System.out.println("@GPS GpsAltitude: " + GpsAltitude);

        return GpsAltitude;
    }

    public String getGpsTimeStamp() {
        if(GpsTimeStamp != null) {
            return GpsTimeStamp;
        }


        byte[] value = getValueByTag(Tag.GPSTimeStamp);
        GpsTimeStamp = String.format("%s:%s:%s",
                numberManager.getInt(value, 0) / numberManager.getInt(value, 4),
                numberManager.getInt(value, 8) / numberManager.getInt(value, 12),
                numberManager.getInt(value, 16) / numberManager.getInt(value, 20)
                );
        System.out.println("@GPS GpsTimeStamp: " + GpsTimeStamp);

        return GpsTimeStamp;
    }

    public String getGpsDateStamp() {
        if(GpsDateStamp != null) {
            return GpsDateStamp;
        }

        byte[] value = getValueByTag(Tag.GPSDateStamp);
        GpsDateStamp = StringConverter.convert(value);
        System.out.println("@GPS GpsDateStamp: " + GpsDateStamp);

        return GpsDateStamp;
    }

    private byte[] getValueByTag(Tag findTag) {
        byte[] value;
        int from = getOffsetToValueByTag(findTag);
        int to = from;
        switch(findTag.getType()) {
            case RATIONAL:
                to += 24; // size of rational type
                break;
            case ASCII:
                to += findTag.getCount();
                break;
            default:
                to += SIZE_TAG_VALUE;
                break;
        }

        value = Arrays.copyOfRange(buf, from, to);

        return value;
    }

    private int getOffsetToValueByTag(Tag findTag) {
        if(offsetToValueByTag.get(findTag) != null)
            return offsetToValueByTag.get(findTag);

        int offsetToTag = offsetToGPS + SIZE_COUNT_ATTRIBUTE;
        Tag tag;
        Type type;
        int tagValue;

        for(int i=0; i<countTag; i++) {
            tagValue = (int)numberManager.getChar(buf, offsetToTag);
            tag = Tag.get(tagValue);
            type = tag.getType();
            if(tag != null && tag != findTag) {
                offsetToTag += SIZE_TAG_STRUCT;
                continue;
            }
            else if(tag == null) return -1;

            int offsetToValue = SIZE_TAG_NAME + SIZE_TAG_TYPE + SIZE_TAG_COUNT;
            if(type == Type.RATIONAL || findTag.getCount() > 4) {
                offsetToValue = numberManager.getInt(buf, offsetToTag + offsetToValue) + ExifInfo.IDENTIFIER.length;
            } else {
                offsetToValue = offsetToTag + offsetToValue;
            }

            offsetToValueByTag.put(findTag, offsetToValue);

            return offsetToValue;
        }

        return -1;
    }

    public static class GPSCoordConverter {
        public static double degreesMinutesSecondsToDecimalDegrees(double degrees, double minutes, double seconds) {
            return Math.abs(degrees) + (minutes / 60.0) + (seconds / 3600.0);
        }
    }
}
