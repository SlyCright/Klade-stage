package site.klade.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Renders a radial gradient background for the arena.
 * Creates depth by transitioning from a lighter center to darker edges.
 */
public class ArenaBackgroundRenderer implements Renderer {

    private final ShapeRenderer shapeRenderer;
    private final Color centerColor;
    private final Color edgeColor;
    // Gradient configuration - loaded from JSON
    // Arena radius
    private final float gradientRadius;
    // Performance tuning constants - loaded from JSON
    // Number of concentric circles (reduce to 50-80 for performance)
    private final int gradientSteps;
    // Segments per circle (reduce for smaller circles)
    private final int circleSegments;

    public ArenaBackgroundRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
        ConfigManager config = new ConfigManager();
        this.centerColor = config.getColor("ArenaBackground", "center");
        this.edgeColor = config.getColor("ArenaBackground", "edge");
        this.gradientRadius = config.getFloat("ArenaBackground", "gradient", "radius");
        this.gradientSteps = (int) config.getFloat("ArenaBackground", "gradient", "steps");
        this.circleSegments = (int) config.getFloat("ArenaBackground", "gradient", "circleSegments");
    }

    // TODO: Possible performance optimizations for later implementation:
    // TODO: Cache gradient as texture (FrameBuffer) to reduce 200 draw calls to 1 per frame
    // TODO: Use fragment shader for GPU-accelerated radial gradient calculation

    @Override
    public void draw() {
        // Ensure blending is enabled for smooth gradient
        Gdx.gl.glEnable(com.badlogic.gdx.graphics.GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(com.badlogic.gdx.graphics.GL20.GL_SRC_ALPHA,
                          com.badlogic.gdx.graphics.GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Draw concentric circles to simulate radial gradient
        // We draw from largest to smallest (outside to inside)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Reuse Color object to avoid GC pressure
        Color color = new Color();

        for (int i = gradientSteps; i >= 0; i--) {
            float t = (float) i / gradientSteps; // 0.0 to 1.0
            float radius = t * gradientRadius;

            // Interpolate between center and edge colors (reuse color object)
            color.set(centerColor).lerp(edgeColor, t);
            shapeRenderer.setColor(color);

            // Draw circle at center (0, 0)
            shapeRenderer.circle(0f, 0f, radius, circleSegments);
        }

        shapeRenderer.end();
    }

    public void dispose() {
        // Placeholder for future resource disposal (e.g., cached gradient texture)
    }

}
