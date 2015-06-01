package com.wizardjava;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by v.golovko on 20-Mar-15.
 */
public enum Markers {
    SOI(0xFFD8), /** Marks the start of an image. */
    COM(0xFFFE), /** Marks the start of an comment block. */
    DQT(0xFFDB), /** Marks the quantization table. */
    DHT(0xFFC4), /** Marks the Huffman table. */
    SOS(0xFFDA), /** Marks the start of scan. */
    EOI(0xFFD9), /** Marks the end of an image. */

    // App segment markers (APPn)
    APP0(0xFFE0), // "JFIF" "JFXX"
    APP1(0xFFE1), // "Exif"
    APP2(0xFFE2), // "ICC_PROFILE"
    APP3(0xFFE3),
    APP4(0xFFE4),
    APP5(0xFFE5),
    APP6(0xFFE6),
    APP7(0xFFE7),
    APP8(0xFFE8),
    APP9(0xFFE9),
    APP10(0xFFEA),
    APP11(0xFFEB),
    APP12(0xFFEC),
    APP13(0xFFED),
    APP14(0xFFEE), // "Adobe"
    APP15(0xFFEF),

    // Start of Frame segment markers (SOFn).
    SOF0(0xFFC0),
    SOF1(0xFFC1),
    SOF2(0xFFC2), /** Marks the start of a frame - progressive DCT. */
    SOF3(0xFFC3),
    SOF5(0xFFC5),
    SOF6(0xFFC6),
    SOF7(0xFFC7),
    SOF9(0xFFC9),
    SOF10(0xFFCA),
    SOF11(0xFFCB),
    SOF13(0xFFCD),
    SOF14(0xFFCE),
    SOF15(0xFFCF),

    // JPEG-LS markers
    SOF55(0xFFF7), // NOTE: Equal to a normal SOF segment
    LSE(0xFFF8); // JPEG-LS Preset Parameter marker

    private int value;
    Markers(int value) { this.value = value; }
    public int getValue() { return value; }

    private static final Map<Integer, Markers> lookup = new HashMap<Integer, Markers>();
    static {
        for (Markers marker : Markers.values())
            lookup.put(marker.getValue(), marker);
    }

    public static Markers get(Integer value) {
        return lookup.get(value);
    }

    public String toString() {
        return String.format("%s (0x%s)", this.name(), Integer.toHexString(value));
    }
}
