package site.klade.stage;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import site.klade.simulation.Arena;
import site.klade.simulation.Simulation;
import site.klade.simulation.SimulationDto;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation for Rhythm Node POC.
 */
public class KladeStage extends ApplicationAdapter {

    private static final int FRAMES_PER_TICK = 2; // Adjustable speed

    private SpriteBatch batch;

    private ShapeRenderer shapeRenderer;

    private BitmapFont font;

    private Simulation simulation;

    private long clientTotalTicks = 0;

    private long clientStateChanges = 0;

    private boolean lastNodeState = false;

    private Arena arena;

    private int frameCounter = 0;

    private long arenaTotalTicks = 0;

    private ArenaRenderer arenaRenderer;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
        simulation = new Simulation();
        arena = new Arena();
        arenaRenderer = new ArenaRenderer(arena);
    }

    @Override
    public void render() {
        update();
        draw();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }

    private void update() {
        // Update simulation at controlled rate
        frameCounter++;
        if (frameCounter >= FRAMES_PER_TICK) {
            simulation.update();
            frameCounter = 0;
            clientTotalTicks++;
            SimulationDto dto = simulation.getSimulationDto();
            if (dto.getIsRhymeNodeCurrentlyActive() != lastNodeState) {
                clientStateChanges++;
                lastNodeState = dto.getIsRhymeNodeCurrentlyActive();
            }
        }
    }

    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        arenaRenderer.draw(shapeRenderer);
        batch.begin();
        String description = "The graphic represents simulation visualisation and is provided by libGDX. ";
        String separator = "____";
        String headerText = "Visual run:";
        String ticksText = "Total ticks: " + clientTotalTicks;
        String changesText = "Rhyme node status changes: " + clientStateChanges;
        String statusText = "Rhyme node current status: " + ("ACTIVE" + "INACTIVE");
        int x = 20;
        int y = 300;
        int spacing = 18;
        font.draw(batch, description, x, Gdx.graphics.getHeight() - y - 0 * spacing);
        font.draw(batch, separator, x, Gdx.graphics.getHeight() - y - 1 * spacing);
        font.draw(batch, headerText, x, Gdx.graphics.getHeight() - y - 2 * spacing);
        font.draw(batch, ticksText, x, Gdx.graphics.getHeight() - y - 3 * spacing);
        font.draw(batch, changesText, x, Gdx.graphics.getHeight() - y - 4 * spacing);
        font.draw(batch, statusText, x, Gdx.graphics.getHeight() - y - 5 * spacing);
        batch.end();
    }
}