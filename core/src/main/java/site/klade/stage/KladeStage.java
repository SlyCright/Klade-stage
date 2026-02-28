package site.klade.stage;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import site.klade.simulation.Arena;

public class KladeStage extends ApplicationAdapter {

    private static final int FRAMES_PER_TICK = 2; // Adjustable speed

    private static final int TEXT_X = 20;

    private static final int TEXT_Y = 300;

    private static final int TEXT_SPACING = 18;

    private SpriteBatch batch;

    private ShapeRenderer shapeRenderer;

    private BitmapFont font;

    private Arena arena;

    private ArenaRenderer arenaRenderer;

    private long arenaTotalTicks = 0;

    private int frameCounter = 0;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
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
            arena.update();
            frameCounter = 0;
            arenaTotalTicks++;
        }
    }

    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        arenaRenderer.draw(shapeRenderer);
        drawText();
    }

    private void drawText() {
        batch.begin();
        String[] lines = {
                "The graphic represents simulation visualisation and is provided by libGDX.",
                "____",
                "Visual run:",
                "Total ticks: " + arenaTotalTicks};
        for (int i = 0; i < lines.length; i++) {
            font.draw(batch, lines[i],
                    TEXT_X, Gdx.graphics.getHeight() - TEXT_Y - i * TEXT_SPACING);
        }
        batch.end();
    }
}