package site.klade.stage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import site.klade.simulation.components.Kinematics;

import java.util.Random;

public class SpecimenRenderer implements Renderer {

    private final ShapeRenderer shapeRenderer;

    public final static float SPECIMEN_RADIUS = 9f;
    // TODO: should be taken from the simulation parameters since it determines how body physics are calculated

    public static final float BORDER_WIDTH = 2f;
    public static final Color BODER_COLOR = new Color(0.9f, 0.9f, 1f, 1f);

    private final Vector2 position;
    private final Color bodyColor;
    private final Color borderColor;

    public SpecimenRenderer(Entity specimen) {
        this.shapeRenderer = new ShapeRenderer();
        Kinematics kinematics = specimen.getComponent(Kinematics.class);
        position = kinematics.getPosition();

        Random random = new Random();
        bodyColor = new Color(random.nextFloat(), random.nextFloat(), random.nextFloat(), 0.75f);
        borderColor = BODER_COLOR;
    }

    @Override
    public void draw() {
        // Ensure blending is enabled for smooth edges
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw body circle with reduced radius to accommodate border within SPECIMEN_RADIUS
        float bodyRadius = SPECIMEN_RADIUS - BORDER_WIDTH;
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(bodyColor);
        shapeRenderer.circle(position.x, position.y, bodyRadius, 32);
        shapeRenderer.end();

        // Draw thick border from body edge to full SPECIMEN_RADIUS
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(borderColor);
        for (float offset = 0; offset < BORDER_WIDTH; offset += 0.25f) {
            shapeRenderer.circle(position.x, position.y, bodyRadius + offset, 32);
        }
        shapeRenderer.end();
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}
