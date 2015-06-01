package com.wizardjava;

import com.wizardjava.exif.GPS;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        String file = "img.jpg";
        Path path = Paths.get(file);

        byte[] data = null;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException e) {
            System.out.println("File not found");
        }

        if(JPEGExtension.isJPEG(data)) {
            try {
                JPEGExtension jpg = new JPEGExtension(data);
                GPS gps = jpg.getGPS();
                gps.getGPSVersionID();
                gps.getGpsLatitudeRef();
                gps.getGpsLatitude();
                gps.getGpsLongitudeRef();
                gps.getGpsLongitude();
                gps.getGpsAltitudeRef();
                gps.getGpsAltitude();
                gps.getGpsTimeStamp();
                gps.getGpsDateStamp();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }
}
