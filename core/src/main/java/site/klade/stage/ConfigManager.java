package site.klade.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

public class ConfigManager {

    private final JsonValue root;

    public ConfigManager() {
        this.root = new JsonReader().parse(Gdx.files.internal("arena-skin.json"));
    }

    private Color parseHSVColor(JsonValue hsv) {
        float h = hsv.getFloat("h");
        float s = hsv.getFloat("s");
        float v = hsv.getFloat("v");
        float a = hsv.getFloat("a");
        return ColorUtils.fromHSV(h, s, v, a);
    }

    public Color getColor(String section, String name) {
        JsonValue sectionValue = root.get(section);
        if (sectionValue == null) {
            return Color.WHITE;
        }
        JsonValue color = sectionValue.get(name);
        if (color == null) {
            return Color.WHITE;
        }
        return parseHSVColor(color);
    }

    public int getInt(String section, String name) {
        JsonValue sectionValue = root.get(section);
        if (sectionValue == null) {
            return 0;
        }
        JsonValue layout = sectionValue.get("layout");
        if (layout == null) {
            return 0;
        }
        JsonValue value = layout.get(name);
        if (value == null) {
            return 0;
        }
        return value.asInt();
    }

    public float getFloat(String section, String name) {
        JsonValue sectionValue = root.get(section);
        if (sectionValue == null) {
            return 0f;
        }
        JsonValue value = sectionValue.get(name);
        if (value == null) {
            return 0f;
        }
        return value.asFloat();
    }

    public float getFloat(String section, String subsection, String name) {
        JsonValue sectionValue = root.get(section);
        if (sectionValue == null) {
            return 0f;
        }
        JsonValue sub = sectionValue.get(subsection);
        if (sub == null) {
            return 0f;
        }
        JsonValue value = sub.get(name);
        if (value == null) {
            return 0f;
        }
        return value.asFloat();
    }
}
