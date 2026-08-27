package site.klade.stage;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import site.klade.simulation.Arena;

import java.util.ArrayList;
import java.util.List;

public class ArenaRenderer implements Renderer {

    private final ArenaCenterRenderer arenaCenterRenderer;
    private final List<SpecimenRenderer> specimenRenderers;

    public ArenaRenderer(Arena arena) {
        arenaCenterRenderer = new ArenaCenterRenderer();
        specimenRenderers = new ArrayList<>();
        for (var specimen : arena.getSpecimens()) {
            specimenRenderers.add(new SpecimenRenderer(specimen));
        }
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        arenaCenterRenderer.draw(shapeRenderer);
        for (SpecimenRenderer renderer : specimenRenderers) {
            renderer.draw(shapeRenderer);
        }
    }
}
