package site.klade.stage;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import site.klade.simulation.Arena;

public class ArenaRenderer implements Renderer {

    public static final float ARENA_CENTER_COLOR_R = 0.1f;

    public static final float ARENA_CENTER_COLOR_G = 0.1f;

    public static final float ARENA_CENTER_COLOR_B = 0.6f;

    public static final float ARENA_CENTER_COLOR_A = 0.75f;

    public static final float ARENA_CENTER_RADIUS = 10f;

    private final SpecimenRenderer specimenRenderer;

    public ArenaRenderer(Arena arena) {
        specimenRenderer = new SpecimenRenderer(arena.getSpecimen());
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(
                ARENA_CENTER_COLOR_R,
                ARENA_CENTER_COLOR_G,
                ARENA_CENTER_COLOR_B,
                ARENA_CENTER_COLOR_A);
        shapeRenderer.circle(0f, 0f, ARENA_CENTER_RADIUS);
        shapeRenderer.end();
        specimenRenderer.draw(shapeRenderer);
    }
}
