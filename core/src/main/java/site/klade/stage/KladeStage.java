package site.klade.stage;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import site.klade.simulation.Arena;
import site.klade.simulation.Genome;

public class KladeStage extends ApplicationAdapter {

    private static final int FRAMES_PER_TICK = 2; // Adjustable speed

    private static final int TEXT_X = 20;

    private static final int TEXT_Y = 300;

    private static final int TEXT_SPACING = 18;

    private static final String TOTAL_TICKS_PREFIX = "Total ticks: ";

    private final String[] textLines = {
            "The graphic represents simulation visualisation and is provided by libGDX.",
            "____",
            "Visual run:",
            TOTAL_TICKS_PREFIX + "0"};

    private OrthographicCamera camera = new OrthographicCamera();

    private Viewport viewport = new FitViewport(800, 600, camera);

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
        arena = new Arena(new Genome());
        arenaRenderer = new ArenaRenderer(arena);
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
            textLines[3] = TOTAL_TICKS_PREFIX + arenaTotalTicks;
        }
    }

    private void draw() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        shapeRenderer.setProjectionMatrix(camera.combined);
        arenaRenderer.draw(shapeRenderer);
        drawText();
    }

    // TODO: should be TextRenderer for consistency of the abstraction level of the KladeStage class
    private void drawText() {
        batch.begin();
        for (int i = 0; i < textLines.length; i++) {
            font.draw(batch, textLines[i],
                    TEXT_X, Gdx.graphics.getHeight() - TEXT_Y - i * TEXT_SPACING);
        }
        batch.end();
    }
}