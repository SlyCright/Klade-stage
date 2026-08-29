package site.klade.stage;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import site.klade.simulation.Arena;

import java.util.ArrayList;
import java.util.List;

public class ArenaRenderer implements Renderer {

    private final ArenaBackgroundRenderer arenaBackgroundRenderer;
    private final ArenaCenterRenderer arenaCenterRenderer;
    private final List<SpecimenRenderer> specimenRenderers;

    public ArenaRenderer(ShapeRenderer shapeRenderer, Arena arena) {
        arenaBackgroundRenderer = new ArenaBackgroundRenderer(shapeRenderer);
        arenaCenterRenderer = new ArenaCenterRenderer(shapeRenderer);
        specimenRenderers = new ArrayList<>();
        for (var specimen : arena.getSpecimens()) {
            specimenRenderers.add(new SpecimenRenderer(shapeRenderer, specimen));
        }
    }

    @Override
    public void draw() {
        // Draw background first (behind everything)
        arenaBackgroundRenderer.draw();
        // Draw arena center
        arenaCenterRenderer.draw();
        // Draw specimens on top
        for (SpecimenRenderer renderer : specimenRenderers) {
            renderer.draw();
        }
    }

    public void dispose() {
        // ShapeRenderer is owned by KladeStage, not disposed here
    }
}
