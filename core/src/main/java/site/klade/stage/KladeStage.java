package site.klade.stage;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import site.klade.simulation.Simulation;
import site.klade.simulation.SimulationDto;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation for Rhythm Node POC.
 */
public class KladeStage extends ApplicationAdapter {

    private SpriteBatch batch;

    private ShapeRenderer shapeRenderer;

    private BitmapFont font;

    private Simulation simulation;

    private int frameCounter = 0;

    private static final int FRAMES_PER_TICK = 2; // Adjustable speed

    private long clientTotalTicks = 0;

    private long clientStateChanges = 0;

    private boolean lastNodeState = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
        simulation = new Simulation();
    }

    @Override
    public void render() {
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
        // Clear screen
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        // Render rhythm node visualization
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        float nodeRadius = 50f;
        SimulationDto dto = simulation.getSimulationDto();
        boolean isActive = dto.getIsRhymeNodeCurrentlyActive();
        // Draw node circle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        if (isActive) {
            // Active state - dirty red
            shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1f);
        } else {
            // Inactive state - dark dirty red
            shapeRenderer.setColor(0.3f, 0.1f, 0.1f, 1f);
        }
        shapeRenderer.circle(centerX, centerY, nodeRadius);
        shapeRenderer.end();
        // Draw border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.9f, 0.8f, 0.2f, 1f);
        shapeRenderer.circle(centerX, centerY, nodeRadius);
        shapeRenderer.end();
        // Draw text overlay
        batch.begin();
        String separator = "____";
        String headerText = "Visual run:";
        String ticksText = "Total ticks: " + clientTotalTicks;
        String changesText = "Rhyme node status changes: " + clientStateChanges;
        String statusText = "Rhyme node current status: " + (isActive ? "ACTIVE" : "INACTIVE");
        int x = 20;
        int y = 200;
        int spacing = 18;
        font.draw(batch, separator, x, Gdx.graphics.getHeight() - y - 0 * spacing);
        font.draw(batch, headerText, x, Gdx.graphics.getHeight() - y - 1 * spacing);
        font.draw(batch, ticksText, x, Gdx.graphics.getHeight() - y - 2 * spacing);
        font.draw(batch, changesText, x, Gdx.graphics.getHeight() - y - 3 * spacing);
        font.draw(batch, statusText, x, Gdx.graphics.getHeight() - y - 4 * spacing);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}