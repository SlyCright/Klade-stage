package site.klade.stage;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import site.klade.simulation.Arena;
import site.klade.simulation.components.Kinematics;
import site.klade.simulation.components.Node;
import site.klade.simulation.components.NodeType;

public class NodesRenderer implements Renderer {

    private final ShapeRenderer shapeRenderer;
    private final Arena arena;

    public final static float NODE_RADIUS = 9f;
    // TODO: should be taken from the simulation parameters since it determines how body physics are calculated

    private final ConfigManager configManager;
    private final Family nodeFamily;

    public NodesRenderer(ShapeRenderer shapeRenderer, Arena arena) {
        this.shapeRenderer = shapeRenderer;
        this.arena = arena;
        this.nodeFamily = Family.all(Node.class, Kinematics.class).get();
        this.configManager = new ConfigManager();
    }

    @Override
    public void draw() {
        // Ensure blending is enabled for smooth edges
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Get all entities with Node and Kinematics components
        for (Entity entity : arena.getEntitiesFor(nodeFamily)) {
            Node node = entity.getComponent(Node.class);
            Kinematics kinematics = entity.getComponent(Kinematics.class);

            Color bodyColor = getColorForNodeType(node.nodeType);

            // Draw body circle with reduced radius to accommodate border within NODE_RADIUS
            float borderWidth = configManager.getFloat("Nodes", "borderWidth");
            float bodyRadius = NODE_RADIUS - borderWidth;
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(bodyColor);
            shapeRenderer.circle(kinematics.position.x, kinematics.position.y, bodyRadius, 32);
            shapeRenderer.end();

            // Draw thick border from body edge to full NODE_RADIUS
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(configManager.getColor("Nodes", "border"));
            for (float offset = 0; offset < borderWidth; offset += 0.25f) {
                shapeRenderer.circle(kinematics.position.x, kinematics.position.y, bodyRadius + offset, 32);
            }
            shapeRenderer.end();
        }
    }

    private Color getColorForNodeType(NodeType nodeType) {
        switch (nodeType) {
            case STEM:
                return configManager.getColor("Nodes", "stem");
            case FRICTION:
                return configManager.getColor("Nodes", "friction");
            case RHYTHM:
                return configManager.getColor("Nodes", "rhythm");
            case NEURON:
                return configManager.getColor("Nodes", "neuron");
            default:
                return Color.WHITE;
        }
    }

    public void dispose() {
        // Placeholder for future resource disposal
    }

}
