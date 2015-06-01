
package com.wizardjava;

import com.wizardjava.exif.ExifInfo;
import com.wizardjava.exif.GPS;

import java.io.IOException;
import java.util.*;

/**
 * Created by Василий on 16.03.2015.
 */
public class JPEGExtension {

    public static final byte[] IDENTIFIER = {(byte)0xff, (byte)0xd8}; // image signature;

    private final static int SIZE_MARKER = 2; // 2 byte

    private byte[] buf;
    private final static int BYTE_MASK = 0xff;

    private static final String ERR_NOT_JPG = "Data is not jpg format";
    private static final String ERR_INCORRECT_INDEX = "Index is not correct";
    private static final String ERR_NOT_DATA = "Not data";

    private Map<Markers, ArrayList<Integer>> mapOffsetMarkers = null;
    private Map<Markers, ArrayList<Byte[]>> mapMarkersData = null;

    private ExifInfo exif = null;

    JPEGExtension(byte[] image){
        if(!isJPEG(image))
            throw new IllegalArgumentException(ERR_NOT_JPG);

        buf = Arrays.copyOfRange(image, 0, image.length);
    }

    public void setData(byte[] image) {
        clear();
        buf = Arrays.copyOfRange(image, 0, image.length);
    }

    public static boolean isJPEG(byte[] image){
        if(image.length < IDENTIFIER.length)
            return false;

        byte[] id = Arrays.copyOfRange(image, 0, IDENTIFIER.length);
        return Arrays.equals(id, IDENTIFIER);
    }

    public GPS getGPS() {
        exif = (exif == null) ? getExifInfo() : exif;
        return  (exif == null) ? null : exif.getGPS() ;
    }

    // get marker by index in buf data. if data is not marker we have error
    private Markers getMarker(int index) {
        if(buf == null)
            throw new IllegalArgumentException(ERR_NOT_DATA);

        if(index > buf.length)
            throw new IllegalArgumentException(ERR_INCORRECT_INDEX);

        int markerValue = ((buf[index] & BYTE_MASK << 8) | buf[++index] & BYTE_MASK);
        return Markers.get(markerValue);
    }

    // get array of offsets by marker
    private ArrayList<Integer> getOffsetToMarker(Markers findMarker) throws IOException {
        if(mapOffsetMarkers == null)
            fillMapOffsetMarkers();

        return new ArrayList<Integer>(mapOffsetMarkers.get(findMarker));
    }

    // create array of offset by marker (marker => offset)
    private void fillMapOffsetMarkers() {
        if(mapOffsetMarkers == null)
            mapOffsetMarkers = new HashMap<Markers, ArrayList<Integer>>();
        else
            return;

        int index = 0;
        int length;
        Markers marker;
        ArrayList<Integer> listOffsetByMarker;

        System.out.println("#### JPEGExtension:fillMapOffsetMarkers");
        while(index < buf.length) {
            marker = getMarker(index);
            if(marker == null) return;

            System.out.println("Marker: " + marker);
            listOffsetByMarker = mapOffsetMarkers.get(marker);
            if(listOffsetByMarker == null)
                listOffsetByMarker = new ArrayList<Integer>();
            listOffsetByMarker.add(index);
            mapOffsetMarkers.put(marker, listOffsetByMarker);

            index += SIZE_MARKER;

            if (marker == Markers.SOS) {
                return;
            }

            if (marker == Markers.SOI || marker == Markers.EOI) {
                continue;
            }

            length = getInt(index);
            index += length;
        }
    }

    // get array of byte of data corresponding marker
    private ArrayList<Byte[]> getDataByMarkers(Markers marker) {
        if(mapMarkersData == null)
            fillMapMarkersData();

        return mapMarkersData.get(marker);
    }

    // create array of data by marker (marker => data)
    private void fillMapMarkersData() {

        fillMapOffsetMarkers();

        if(mapMarkersData == null) {
            mapMarkersData = new HashMap<Markers, ArrayList<Byte[]>>();
        } else {
            return;
        }

        int length;
        ArrayList<Byte[]> listDataByMarker;

        System.out.println("#### JPEGExtension:fillMapMarkersData");
        for(Markers marker : mapOffsetMarkers.keySet()) {
            listDataByMarker = new ArrayList<Byte[]>();

            // get offset by markers
            for(Integer offset : mapOffsetMarkers.get(marker)){
                length = getInt(offset + SIZE_MARKER);
                System.out.println("Marker: " + marker + " Size of data : " + length);
                byte[] DataByMarker = Arrays.copyOfRange(buf, offset + SIZE_MARKER + 2, offset + length - 2);
                listDataByMarker.add(NumberManager.convertToByte(DataByMarker));
            }

            mapMarkersData.put(marker, listDataByMarker);
        }
    }

    // create ExifInfo object if data has exif format
    private ExifInfo getExifInfo() {
        ArrayList<Byte[]> dataByMarker = getDataByMarkers(Markers.APP1);

        byte[] exifData;
        for(Byte[] data : dataByMarker) {
            exifData = NumberManager.convertTobyte(data);
            if(ExifInfo.isExifInfo(exifData)){
                System.out.println("#JPEGExtension Exif: true");
                return new ExifInfo(exifData);
            }
        }

        return null;
    }

    private int getInt(int offset) {
        int a = buf[offset++] & BYTE_MASK;
        int b = buf[offset] & BYTE_MASK;
        return ((a << 8) | b);
    }

    // clear all data
    private void clear() {
        buf = null;
        mapOffsetMarkers.clear();
        mapOffsetMarkers = null;
        mapMarkersData.clear();
        mapMarkersData = null;
    }
}