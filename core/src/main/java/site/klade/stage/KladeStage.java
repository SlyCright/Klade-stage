package site.klade.stage;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import site.klade.simulation.Arena;
import site.klade.simulation.ArenaSettings;
import site.klade.simulation.Genome;

import java.util.ArrayList;

public class KladeStage extends ApplicationAdapter {

    private final OrthographicCamera camera = new OrthographicCamera();

    private final Viewport viewport = new FitViewport(800, 600, camera);

    private ShapeRenderer shapeRenderer;

    private ArenaSimulation arenaSimulation;

    private ArenaRenderer arenaRenderer;

    private GenomeFetcher genomeFetcher;

    private ArenaSettingsFetcher arenaSettingsFetcher;

    /**
     * UI layer: ONE scene2d Stage as the single UI root (screen-space viewport,
     * own camera + batch). Drawn on top of the game/world layer in screen
     * coordinates; owns the info text and the "Restart" seed button. Registered
     * in an InputMultiplexer so future game-camera input can coexist.
     */
    private UiStage uiStage;

    private boolean isFetchingGenome = false;

    private ArrayList<Genome> pendingGenomes;

    private ArenaSettings pendingSettings;

    @Override
    public void create() {
        // Enable antialiasing through OpenGL blending
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer = new ShapeRenderer();
        arenaSimulation = new ArenaSimulation();
        genomeFetcher = new GenomeFetcher();
        arenaSettingsFetcher = new ArenaSettingsFetcher();
        uiStage = new UiStage(new ConfigManager(), new UiStage.RestartAction() {
            @Override
            public void onRestart() {
                fetchGenomesAndSettings();
            }
        });
        // UI consumes what it needs; future game-camera input can be appended to the multiplexer.
        Gdx.input.setInputProcessor(new InputMultiplexer(uiStage));
        fetchGenomesAndSettings();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);  // handles screen resizing
        camera.position.set(0, 0, 0);    // world center at screen center
        shapeRenderer.setProjectionMatrix(camera.combined);
        uiStage.getViewport().update(width, height);  // UI stage is screen-space
    }

    @Override
    public void render() {
        update();
        draw();
        // UI last, on top, in screen coordinates.
        uiStage.act();
        uiStage.draw();
    }

    private void update() {
        arenaSimulation.update();
        uiStage.updateTicks(arenaSimulation.getTotalTicks());
        if (arenaSimulation.isEvaluationComplete()) {
            fetchGenomesAndSettings();
        }
    }

    private void draw() {
        // Clear with transparent black since gradient background handles the visuals
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        if (arenaRenderer != null) {
            arenaRenderer.draw();
        }
    }

    private void fetchGenomesAndSettings() {
        if (isFetchingGenome) {
            return;
        }
        isFetchingGenome = true;
        pendingGenomes = null;
        pendingSettings = null;

        genomeFetcher.fetchBestGenome(new GenomeFetchCallback() {
            @Override
            public void onSuccess(ArrayList<Genome> genomes) {
                pendingGenomes = genomes;
                completeFetch();
            }

            @Override
            public void onFailure(Throwable error) {
                completeFetchWithError();
            }
        });

        arenaSettingsFetcher.fetchArenaSettings(new ArenaSettingsCallback() {
            @Override
            public void onSuccess(ArenaSettings settings) {
                pendingSettings = settings;
                completeFetch();
            }

            @Override
            public void onFailure(Throwable error) {
                completeFetchWithError();
            }
        });
    }

    private void completeFetch() {
        if (pendingGenomes != null && pendingSettings != null) {
            // resetArena returns the arena synchronously; bind the renderer to that exact instance
            // (getArena() would still return the stale/null arena until the posted runnable runs).
            Arena newArena = arenaSimulation.resetArena(pendingGenomes, pendingSettings);
            arenaRenderer = new ArenaRenderer(shapeRenderer, newArena);
            pendingGenomes = null;
            pendingSettings = null;
            isFetchingGenome = false;
        }
    }

    private void completeFetchWithError() {
        // No fake defaults: keep the current arena (or stay empty) and retry next cycle.
        pendingGenomes = null;
        pendingSettings = null;
        isFetchingGenome = false;
    }

    @Override
    public void dispose() {
        if (arenaRenderer != null) {
            arenaRenderer.dispose();
        }
        uiStage.dispose();
        shapeRenderer.dispose();
    }
}
