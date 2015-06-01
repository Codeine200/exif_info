package com.wizardjava;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by v.golovko on 24-Mar-15.
 */
public enum ByteOrder {
    II(0x4949),
    MM(0x4d4d);

    private final int value;
    ByteOrder(int value) { this.value = value; }
    public int getValue() { return value; }

    private static final Map<Integer, ByteOrder> lookup = new HashMap<Integer, ByteOrder>();
    static {
        for (ByteOrder tag : ByteOrder.values())
            lookup.put(tag.getValue(), tag);
    }

    public static ByteOrder get(Integer value) {
        return lookup.get(value);
    }

    public String toString() {
        return String.format("%s (0x%s)", this.name(), Integer.toHexString(value));
    }
}