package site.klade.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ArenaCenterRenderer implements Renderer {

    private final ShapeRenderer shapeRenderer;
    private final Color centerColor;

    public ArenaCenterRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
        this.centerColor = new ConfigManager().getColor("ArenaCenter", "center");
    }

    public static final float ARENA_CENTER_RADIUS = 2f;

    @Override
    public void draw() {
        // Ensure blending is enabled for smooth edges
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(centerColor);
        shapeRenderer.circle(0f, 0f, ARENA_CENTER_RADIUS, 32);
        shapeRenderer.end();
    }

    public void dispose() {
        // Placeholder for future resource disposal
    }

}
