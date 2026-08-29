package site.klade.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ArenaCenterRenderer implements Renderer {

    private final ShapeRenderer shapeRenderer;

    public ArenaCenterRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
    }

    public static final float ARENA_CENTER_COLOR_R = 0.9f;
    public static final float ARENA_CENTER_COLOR_G = 0.1f;
    public static final float ARENA_CENTER_COLOR_B = 0.1f;
    public static final float ARENA_CENTER_COLOR_A = 0.8f;
    public static final float ARENA_CENTER_RADIUS = 2f;

    @Override
    public void draw() {
        // Ensure blending is enabled for smooth edges
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(
                ARENA_CENTER_COLOR_R,
                ARENA_CENTER_COLOR_G,
                ARENA_CENTER_COLOR_B,
                ARENA_CENTER_COLOR_A);
        shapeRenderer.circle(0f, 0f, ARENA_CENTER_RADIUS, 32);
        shapeRenderer.end();
    }

}
