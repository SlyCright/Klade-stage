package site.klade.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import site.klade.simulation.ArenaSettings;

/**
 * Fetches /api/arena-settings and converts the JSON response directly into the
 * shared {@link ArenaSettings} domain type. No DTO is replicated here; the JSON
 * keys match Main's ArenaSettingsController response.
 */
public class ArenaSettingsFetcher extends ApiFetcher {

    private static final String ARENA_SETTINGS_URL = "/api/arena-settings";

    public void fetchArenaSettings(final ArenaSettingsCallback callback) {
        fetch(ARENA_SETTINGS_URL, new HttpResponseCallback() {
            @Override
            public void onSuccess(String body) {
                try {
                    ArenaSettings settings = parseSettingsFromJson(body);
                    callback.onSuccess(settings);
                } catch (Exception e) {
                    Gdx.app.log("ArenaSettingsFetcher", "Failed to parse arena settings JSON", e);
                    callback.onFailure(e);
                }
            }

            @Override
            public void onFailure(Throwable error) {
                Gdx.app.log("ArenaSettingsFetcher", "HTTP request failed", error);
                callback.onFailure(error);
            }
        });
    }

    private ArenaSettings parseSettingsFromJson(String response) {
        JsonReader jsonReader = new JsonReader();
        JsonValue root = jsonReader.parse(response);

        if (root == null) {
            throw new IllegalArgumentException("No arena settings received");
        }

        return new ArenaSettings(
                root.getFloat("initialDistance"),
                root.getFloat("frictionFactor"),
                root.getFloat("nodeSize"),
                root.getFloat("repulsionFactor"),
                root.getInt("maxTicks"));
    }
}
