package site.klade.stage;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import site.klade.simulation.Arena;

public class ArenaRenderer implements Renderer {

    private final ArenaBackgroundRenderer arenaBackgroundRenderer;

    private final ArenaCenterRenderer arenaCenterRenderer;

    private final NodesRenderer nodesRenderer;

    public ArenaRenderer(ShapeRenderer shapeRenderer, Arena arena) {
        arenaBackgroundRenderer = new ArenaBackgroundRenderer(shapeRenderer);
        arenaCenterRenderer = new ArenaCenterRenderer(shapeRenderer);
        nodesRenderer = new NodesRenderer(shapeRenderer, arena);
    }

    @Override
    public void draw() {
        // Draw background first (behind everything)
        arenaBackgroundRenderer.draw();
        // Draw arena center
        arenaCenterRenderer.draw();
        // Draw specimens on top
        nodesRenderer.draw();
    }

    public void dispose() {
        arenaBackgroundRenderer.dispose();
        arenaCenterRenderer.dispose();
        nodesRenderer.dispose();
    }

}
