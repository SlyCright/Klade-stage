package site.klade.stage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class SpecimenRenderer implements Renderer {

    public final static float SPECIMEN_RADIUS = 15f;

    private final Vector2 position = new Vector2(0f, 0f);

    public SpecimenRenderer(Entity specimen) {
        // var physics = specimen.getComponent(SpecimenPhysics.class);
        // position = physics.getPositon();
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        // Draw circle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.8f, 0.2f, 0.2f, 1f);
        shapeRenderer.circle(position.x, position.y, SPECIMEN_RADIUS);
        shapeRenderer.end();
        // Draw border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0.9f, 0.2f, 0.2f, 1f);
        shapeRenderer.circle(position.x, position.y, SPECIMEN_RADIUS);
        shapeRenderer.end();
    }
}
