package site.klade.stage;

import com.badlogic.gdx.graphics.Color;

public class ColorUtils {

    /**
     * Converts HSV color values to libGDX Color (RGBA).
     *
     * @param h Hue (0-360 degrees)
     * @param s Saturation (0.0-1.0)
     * @param v Value/Brightness (0.0-1.0)
     * @param a Alpha (0.0-1.0)
     * @return Color object with converted RGB values
     */
    public static Color fromHSV(float h, float s, float v, float a) {
        float c = v * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = v - c;

        float r, g, b;

        if (h < 60) {
            r = c;
            g = x;
            b = 0;
        } else if (h < 120) {
            r = x;
            g = c;
            b = 0;
        } else if (h < 180) {
            r = 0;
            g = c;
            b = x;
        } else if (h < 240) {
            r = 0;
            g = x;
            b = c;
        } else if (h < 300) {
            r = x;
            g = 0;
            b = c;
        } else {
            r = c;
            g = 0;
            b = x;
        }

        return new Color(r + m, g + m, b + m, a);
    }
}
