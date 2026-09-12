package site.klade.stage;

import site.klade.simulation.ArenaSettings;

public interface ArenaSettingsCallback {
    void onSuccess(ArenaSettings settings);
    void onFailure(Throwable error);
}
