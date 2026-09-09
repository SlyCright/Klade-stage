package site.klade.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextRenderer implements Renderer {

    private static final String TOTAL_TICKS_PREFIX = "Total ticks: ";

    private final int textX;
    private final int textY;
    private final int textSpacing;
    private final String[] textLines;
    private final SpriteBatch batch;
    private final BitmapFont font;

    public TextRenderer()  {
        ConfigManager config = new ConfigManager();
        this.textX = config.getInt("Text", "x");
        this.textY = config.getInt("Text", "y");
        this.textSpacing = config.getInt("Text", "spacing");

        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.setColor(config.getColor("Text", "text"));
        this.font.getData().setScale(1.25f);
        this.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.textLines = new String[]{
                "The graphic represents simulation visualisation and is provided by libGDX.",
                "____",
                "Visual run:",
                TOTAL_TICKS_PREFIX + "0"
        };
    }

    public void draw() {
        batch.begin();
        for (int i = 0; i < textLines.length; i++) {
            font.draw(batch, textLines[i],
                    textX, Gdx.graphics.getHeight() - textY - i * textSpacing);
        }
        batch.end();
    }

    public void updateTicks(long totalTicks) {
        textLines[3] = TOTAL_TICKS_PREFIX + totalTicks;
    }

    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
