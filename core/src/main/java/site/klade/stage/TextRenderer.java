package site.klade.stage;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TextRenderer implements Renderer {

    private static final int TEXT_X = 20;
    private static final int TEXT_Y = 300;
    private static final int TEXT_SPACING = 18;
    private static final String TOTAL_TICKS_PREFIX = "Total ticks: ";

    private final String[] textLines;
    private final SpriteBatch batch;
    private final BitmapFont font;

    public TextRenderer()  {
        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
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
                    TEXT_X, Gdx.graphics.getHeight() - TEXT_Y - i * TEXT_SPACING);
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
