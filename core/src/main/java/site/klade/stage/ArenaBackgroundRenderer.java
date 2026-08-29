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

    public ArenaBackgroundRenderer(ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
    }

    // Gradient colors - center to edges
    private static final Color CENTER_COLOR = new Color(0.2f, 0.25f, 0.35f, 1f);
    private static final Color EDGE_COLOR = new Color(0.0f, 0.0f, 0.0f, 1f);

    // Gradient configuration
    private static final float GRADIENT_RADIUS = 400f; // Arena radius

    // Performance tuning constants
    private static final int GRADIENT_STEPS = 200; // Number of concentric circles (reduce to 50-80 for performance)
    private static final int CIRCLE_SEGMENTS = 64; // Segments per circle (reduce for smaller circles)

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

        for (int i = GRADIENT_STEPS; i >= 0; i--) {
            float t = (float) i / GRADIENT_STEPS; // 0.0 to 1.0
            float radius = t * GRADIENT_RADIUS;

            // Interpolate between center and edge colors (reuse color object)
            color.set(CENTER_COLOR).lerp(EDGE_COLOR, t);
            shapeRenderer.setColor(color);

            // Draw circle at center (0, 0)
            shapeRenderer.circle(0f, 0f, radius, CIRCLE_SEGMENTS);
        }

        shapeRenderer.end();
    }


}
