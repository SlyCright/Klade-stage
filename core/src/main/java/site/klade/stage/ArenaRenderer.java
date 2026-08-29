package site.klade.stage;

import site.klade.simulation.Arena;

import java.util.ArrayList;
import java.util.List;

public class ArenaRenderer implements Renderer {

    private final ArenaBackgroundRenderer arenaBackgroundRenderer;
    private final ArenaCenterRenderer arenaCenterRenderer;
    private final List<SpecimenRenderer> specimenRenderers;

    public ArenaRenderer(Arena arena) {
        arenaBackgroundRenderer = new ArenaBackgroundRenderer();
        arenaCenterRenderer = new ArenaCenterRenderer();
        specimenRenderers = new ArrayList<>();
        for (var specimen : arena.getSpecimens()) {
            specimenRenderers.add(new SpecimenRenderer(specimen));
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
        arenaBackgroundRenderer.dispose();
        arenaCenterRenderer.dispose();
        for (SpecimenRenderer renderer : specimenRenderers) {
            renderer.dispose();
        }
    }
}
