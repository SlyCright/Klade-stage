package site.klade.stage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import site.klade.simulation.components.Kinematics;

import java.util.Random;

public class SpecimenRenderer implements Renderer {

    // TODO: should be taken from the simulation parameters since it determines how body physics are calculated
    public final static float SPECIMEN_RADIUS = 9f;

    private final Vector2 position;
    private final Color bodyColor;
    private final Color borderColor;

    public SpecimenRenderer(Entity specimen) {
        Kinematics kinematics = specimen.getComponent(Kinematics.class);
        position = kinematics.getPosition();

        Random random = new Random();
        bodyColor = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0.75f);
        borderColor = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1f);
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        // Draw circle
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(bodyColor);
        shapeRenderer.circle(position.x, position.y, SPECIMEN_RADIUS);
        shapeRenderer.end();
        // Draw border
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(borderColor);
        shapeRenderer.circle(position.x, position.y, SPECIMEN_RADIUS);
        shapeRenderer.end();
    }
}
