package com.blocketing.utils;

import net.minecraft.util.Formatting;

import java.awt.*;

/**
 * Utility class for color-related operations in the Blocketing mod.
 * Provides methods to find the nearest Minecraft formatting color to a given AWT Color.
 */
public class ColorUtil {

    /**
     * Finds the nearest Minecraft formatting color to the given AWT Color.
     *
     * @param color The AWT Color to find the nearest formatting for.
     * @return The nearest Formatting color, or Formatting.WHITE if the input is null.
     */
    public static Formatting getNearestFormatting(Color color) {
        if (color == null) return Formatting.WHITE;
        Formatting closest = Formatting.WHITE;
        double minDist = Double.MAX_VALUE;
        for (Formatting formatting : Formatting.values()) {
            if (!formatting.isColor()) continue;
            Color mcColor = new Color(formatting.getColorValue(), true);
            double dist = colorDistance(color, mcColor);
            if (dist < minDist) {
                minDist = dist;
                closest = formatting;
            }
        }
        return closest;
    }

    private static double colorDistance(Color c1, Color c2) {
        int r = c1.getRed() - c2.getRed();
        int g = c1.getGreen() - c2.getGreen();
        int b = c1.getBlue() - c2.getBlue();
        return r * r + g * g + b * b;
    }
}
