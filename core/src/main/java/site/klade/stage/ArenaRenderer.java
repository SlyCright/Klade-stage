package site.klade.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import site.klade.simulation.Arena;

public class ArenaRenderer implements Renderer {

    public static final float ARENA_CENTER_COLOR_R = 0.5f;

    public static final float ARENA_CENTER_COLOR_G = 0.1f;

    public static final float ARENA_CENTER_COLOR_B = 0.1f;

    public static final float ARENA_CENTER_COLOR_A = 1f;

    public static final float ARENA_CENTER_RADIUS = 10f;

    private final SpecimenRenderer specimenRenderer;

    private final Vector2 centerOffset;

    public ArenaRenderer(Arena arena) {
        specimenRenderer = new SpecimenRenderer(arena.getSpecimen());
        var graphics = Gdx.graphics;
        centerOffset = new Vector2(graphics.getWidth() / 2f, graphics.getHeight() / 2f);
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(
                ARENA_CENTER_COLOR_R,
                ARENA_CENTER_COLOR_G,
                ARENA_CENTER_COLOR_B,
                ARENA_CENTER_COLOR_A);
        shapeRenderer.circle(centerOffset.x, centerOffset.y, ARENA_CENTER_RADIUS);
        shapeRenderer.end();
        specimenRenderer.draw(shapeRenderer);
    }
}
