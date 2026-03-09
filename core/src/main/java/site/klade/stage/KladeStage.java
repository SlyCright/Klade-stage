package site.klade.stage;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
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

    private final OrthographicCamera camera = new OrthographicCamera();

    private final Viewport viewport = new FitViewport(800, 600, camera);

    private SpriteBatch batch;

    private ShapeRenderer shapeRenderer;

    private BitmapFont font;

    private Arena arena;

    private ArenaRenderer arenaRenderer;

    private long arenaTotalTicks = 0;

    private int frameCounter = 0;

    private boolean isFetchingGenome = false;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);
        arena = new Arena(new Genome());
        arenaRenderer = new ArenaRenderer(arena);
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

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }

    private void fetchBestGenomeAndResetArena() {
        if (isFetchingGenome) return; // avoid overlapping requests
        isFetchingGenome = true;
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        Net.HttpRequest httpRequest = requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .url("/api/best-genome")
                .build();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();
                try {
                    JsonReader jsonReader = new JsonReader();
                    JsonValue root = jsonReader.parse(response);
                    // Check if the response is not null (the server may return null if no genome is evaluated yet)
                    if (root == null) {
                        Gdx.app.log("KladeStage", "No genome data received, keeping current arena.");
                        return;
                    }
                    float startX = root.getFloat("startX");
                    float startY = root.getFloat("startY");
                    float impulseX = root.getFloat("impulseX");
                    float impulseY = root.getFloat("impulseY");
                    // Create a new Genome using the constructor we added
                    Genome newGenome = new Genome(startX, startY, impulseX, impulseY);
                    // Schedule replacement on the rendering thread
                    Gdx.app.postRunnable(() -> {
                        arena = new Arena(newGenome);
                        arenaRenderer = new ArenaRenderer(arena);
                        arenaTotalTicks = 0;
                        frameCounter = 0;
                    });
                } catch (Exception e) {
                    Gdx.app.log("KladeStage", "Failed to parse genome JSON", e);
                    // Fallback: keep current arena
                } finally {
                    isFetchingGenome = false;
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("KladeStage", "HTTP request failed", t);
                isFetchingGenome = false;
            }

            @Override
            public void cancelled() {
                isFetchingGenome = false;
            }
        });
    }

    private void update() {
        // Update simulation at controlled rate
        frameCounter++;
        if (frameCounter >= FRAMES_PER_TICK) {
            arena.update();
            frameCounter = 0;
            arenaTotalTicks++;
            textLines[3] = TOTAL_TICKS_PREFIX + arenaTotalTicks;
            if (arena.isEvaluationComplete()) fetchBestGenomeAndResetArena();
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