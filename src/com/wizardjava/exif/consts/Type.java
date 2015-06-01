package com.wizardjava.exif.consts;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by v.golovko on 25-Mar-15.
 */
public enum Type {
    BYTE(1),
    ASCII(2),
    SHORT(3),
    LONG(4),
    RATIONAL(5),
    UNDEFINED(7),
    SLONG(9),
    SRATIONAL(10);

    private int value;
    Type(int value) {
        this.value = value;
    }
    public int getValue() { return value; }

    private static final Map<Integer, Type> lookup = new HashMap<Integer, Type>();
    static {
        for (Type type : Type.values())
            lookup.put(type.getValue(), type);
    }

    public static Type get(Integer value) {
        return lookup.get(value);
    }

    public String toString() {
        return this.name() + " : " + Integer.toHexString(value);
    }

}
