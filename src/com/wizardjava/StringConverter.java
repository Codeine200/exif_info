package com.wizardjava;

/**
 * Created by Василий on 30.05.2015.
 */
public class StringConverter {
    public static String convert(byte[] data) {
        StringBuilder sb = new StringBuilder(data.length);
        for (int i = 0; i < data.length; ++ i) {
            if(data[i] < 0) throw new IllegalArgumentException();
            if(data[i] == 0) continue;
            sb.append((char) data[i]);
        }
        return sb.toString();
    }
}
