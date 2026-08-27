package site.klade.stage;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
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

import java.util.ArrayList;

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

        // Enable anti-aliasing through OpenGL blending
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

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
        if (isFetchingGenome) return;
        isFetchingGenome = true;

        Net.HttpRequest httpRequest = createGenomeRequest();
        Gdx.net.sendHttpRequest(httpRequest, new Net.HttpResponseListener() {
            @Override
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                String response = httpResponse.getResultAsString();
                try {
                    ArrayList<Genome> newGenomes = parseGenomesFromJson(response);
                    if (newGenomes != null) {
                        resetArenaWithGenomes(newGenomes);
                    }
                } catch (Exception e) {
                    Gdx.app.log("KladeStage", "Failed to parse genome JSON", e);
                } finally {
                    setFetchingGenomeFalse();
                }
            }

            @Override
            public void failed(Throwable t) {
                Gdx.app.log("KladeStage", "HTTP request failed", t);
                setFetchingGenomeFalse();
            }

            @Override
            public void cancelled() {
                setFetchingGenomeFalse();
            }
        });
    }

    private Net.HttpRequest createGenomeRequest() {
        HttpRequestBuilder requestBuilder = new HttpRequestBuilder();
        return requestBuilder.newRequest()
                .method(Net.HttpMethods.GET)
                .url("/api/best-genome")
                .build();
    }

    private ArrayList<Genome> parseGenomesFromJson(String response) {
        JsonReader jsonReader = new JsonReader();
        JsonValue root = jsonReader.parse(response);

        if (root == null) {
            Gdx.app.log("KladeStage", "No genome data received, keeping current arena.");
            return null;
        }

        JsonValue genomesArray = root.get("genomes");
        if (genomesArray == null || !genomesArray.isArray() || genomesArray.size == 0) {
            Gdx.app.log("KladeStage", "No genomes array in response, keeping current arena.");
            return null;
        }

        ArrayList<Genome> genomes = new ArrayList<Genome>();
        for (JsonValue genomeValue : genomesArray) {
            float startX = genomeValue.getFloat("startX");
            float startY = genomeValue.getFloat("startY");
            float impulseX = genomeValue.getFloat("impulseX");
            float impulseY = genomeValue.getFloat("impulseY");
            genomes.add(new Genome(startX, startY, impulseX, impulseY));
        }

        return genomes;
    }

    private void resetArenaWithGenomes(final ArrayList<Genome> genomes) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                arena = new Arena(genomes);
                arenaRenderer = new ArenaRenderer(arena);
                arenaTotalTicks = 0;
                frameCounter = 0;
            }
        });
    }

    private void setFetchingGenomeFalse() {
        isFetchingGenome = false;
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
        // Clear with transparent black since gradient background handles the visuals
        ScreenUtils.clear(0f, 0f, 0f, 1f);
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
