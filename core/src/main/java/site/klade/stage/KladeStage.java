package site.klade.stage;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
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

    /* TODO(UI MVP): Build the app's UI on the scene2d toolkit (this fork ships it).
     *
     * Goal / architecture
     *  - Introduce ONE com.badlogic.gdx.scenes.scene2d.Stage as the single UI root
     *    (screen-space viewport + OrthographicCamera + its own SpriteBatch). All future
     *    interactive UI lives in this scene graph as Actor/Group/ui-widgets.
     *  - Keep the GAME/WORLD rendering on the existing path (ArenaRenderer + ShapeRenderer,
     *    immediate mode, world coordinates). UI is a separate layer: drawn on top, in
     *    screen coordinates, by stage.draw().
     *  - Wire input via Gdx.input.setInputProcessor(stage), or a
     *    com.badlogic.gdx.InputMultiplexer so the UI stage and any future game-camera
     *    input can coexist (UI consuming what it needs, passing the rest through).
     *
     * Per-frame wiring (in KladeStage):
     *  - render(): after world rendering, call uiStage.act() then uiStage.draw() (last).
     *  - dispose(): uiStage.dispose() (clears root + disposes its batch). Everything is
     *    single-threaded + reentrant.
     *
     * scene2d classes to use (com.badlogic.gdx.scenes.scene2d):
     *  - Stage, Group, Actor (root = stage.getRoot())
     *  - ui: Label, Table (layout + built-in Debug), Button, TextButton, ImageButton,
     *    ButtonGroup, Window, Dialog, ScrollPane, SelectBox, tooltips
     *  - utils: ClickListener, ChangeListener, DragListener, FocusListener, ActorGestureListener
     *  - input.GestureDetector for pointer gestures
     *
     * MVP (smallest thing that proves the base end-to-end):
     *  1. Create the Stage (screen-space ScalingViewport; share app camera or give it its own).
     *  2. Register it in an InputMultiplexer via Gdx.input.setInputProcessor(...).
     *  3. Migrate TextRenderer's lines onto the Stage as a ui.Table of ui.Label
     *     (replace the manual g2d.BitmapFont/SpriteBatch here -- the Stage owns batch+camera).
     *  4. Add ONE Button (e.g. "Restart") with a ClickListener that calls
     *     fetchGenomesAndSettings(), proving Stage + input + click + widget in one step.
     *     This button is the seed for all later controls (pause, skip, settings, menus...).
     *
     * Styling: drive colors/layout/fonts through ConfigManager + arena-skin.json
     * (existing pattern) and/or a scene2d ui Style (LabelStyle/ButtonStyle...) built there.
     *
     * Do NOT hand-roll UI as overlay renderers -- use scene2d; it supports HTML5/GWT
     * (touch focus is "Public only for GWT", gdx_backends_gwt is inherited).
     */
    private TextRenderer textRenderer; // This renderer isn't encapsulated in ArenaRenderer because it's future UI

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
        textRenderer = new TextRenderer();
        fetchGenomesAndSettings();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);  // handles screen resizing
        camera.position.set(0, 0, 0);    // world center at screen center
        shapeRenderer.setProjectionMatrix(camera.combined);
    }

    @Override
    public void render() {
        update();
        draw();
    }

    private void update() {
        arenaSimulation.update();
        textRenderer.updateTicks(arenaSimulation.getTotalTicks());
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
        textRenderer.draw();
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
            arenaSimulation.resetArena(pendingGenomes, pendingSettings);
            arenaRenderer = new ArenaRenderer(shapeRenderer, arenaSimulation.getArena());
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
        textRenderer.dispose();
        shapeRenderer.dispose();
    }
}
