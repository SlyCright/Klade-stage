package site.klade.stage;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import site.klade.simulation.Genome;

import java.util.ArrayList;

public class KladeStage extends ApplicationAdapter {

    private final OrthographicCamera camera = new OrthographicCamera();

    private final Viewport viewport = new FitViewport(800, 600, camera);

    private SimulationController simulationController;

    private GenomeFetcher genomeFetcher;

    private TextRenderer textRenderer;

    private boolean isFetchingGenome = false;

    @Override
    public void create() {
        // Enable anti-aliasing through OpenGL blending
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        simulationController = new SimulationController();
        genomeFetcher = new GenomeFetcher();
        textRenderer = new TextRenderer();
        fetchBestGenomeAndResetArena();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);  // handles screen resizing
        camera.position.set(0, 0, 0);    // world centre at screen centre
    }

    @Override
    public void render() {
        update();
        draw();
    }

    private void update() {
        simulationController.update();
        textRenderer.updateTicks(simulationController.getTotalTicks());
        if (simulationController.isEvaluationComplete()) {
            fetchBestGenomeAndResetArena();
        }
    }

    private void draw() {
        // Clear with transparent black since gradient background handles the visuals
        ScreenUtils.clear(0f, 0f, 0f, 1f);
        simulationController.getArenaRenderer().draw();
        textRenderer.draw();
    }

    private void fetchBestGenomeAndResetArena() {
        if (isFetchingGenome) return;
        isFetchingGenome = true;

        genomeFetcher.fetchBestGenome(new GenomeFetchCallback() {
            @Override
            public void onSuccess(ArrayList<Genome> genomes) {
                simulationController.resetArena(genomes);
                isFetchingGenome = false;
            }

            @Override
            public void onFailure(Throwable error) {
                isFetchingGenome = false;
            }
        });
    }

    @Override
    public void dispose() {
        simulationController.dispose();
        textRenderer.dispose();
    }
}
