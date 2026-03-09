package site.klade.stage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import site.klade.simulation.Kinematics;

public class SpecimenRenderer implements Renderer {

    public final static float SPECIMEN_RADIUS = 9f;

    private final Vector2 position;

    public SpecimenRenderer(Entity specimen) {
        var kinematics = specimen.getComponent(Kinematics.class);
        position = kinematics.getPosition();
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        // Draw circle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.6f, 0.2f, 0.2f, 0.75f);
        shapeRenderer.circle(position.x, position.y, SPECIMEN_RADIUS);
        shapeRenderer.end();
        // Draw border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1f);
        shapeRenderer.circle(position.x, position.y, SPECIMEN_RADIUS);
        shapeRenderer.end();
    }
}
