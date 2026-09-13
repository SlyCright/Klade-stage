package site.klade.stage;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

/**
 * The single scene2d UI root: screen-space viewport with its own camera and batch.
 * Owns the info text lines (as a ui.Table of ui.Label) and the seed "Restart" button.
 */
public class UiStage extends Stage {

    /** Called when the user clicks the "Restart" button. */
    public interface RestartAction {

        void onRestart();
    }

    private static final String TOTAL_TICKS_PREFIX = "Total ticks: ";

    private static final String RESTART_BUTTON_TEXT = "Restart";

    private final BitmapFont font;

    private final Texture solidColorTexture;

    private final Label ticksLabel;

    public UiStage(ConfigManager config, final RestartAction restartAction) {
        super(new ScreenViewport());

        this.font = new BitmapFont();
        this.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.solidColorTexture = createSolidColorTexture();

        Label.LabelStyle labelStyle = new Label.LabelStyle(font, config.getColor("Text", "text"));
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle(
                tintedDrawable(config, "buttonBackground"),
                tintedDrawable(config, "buttonBackgroundPressed"),
                tintedDrawable(config, "buttonBackground"),
                font);
        buttonStyle.fontColor = config.getColor("Ui", "buttonText");

        float fontScale = config.getFloat("Text", "fontScale");
        float spacing = config.getInt("Text", "spacing");

        Table table = new Table();
        table.setFillParent(true);
        table.top().left();
        table.padLeft(config.getInt("Text", "x"));
        table.padTop(config.getInt("Text", "y"));
        // Labels must not swallow clicks meant for future game-camera input.
        table.setTouchable(Touchable.childrenOnly);

        addLabel(table, labelStyle, fontScale, spacing,
                "The graphic represents simulation visualisation and is provided by libGDX.");
        addLabel(table, labelStyle, fontScale, spacing, "____");
        addLabel(table, labelStyle, fontScale, spacing, "Visual run:");
        this.ticksLabel = addLabel(table, labelStyle, fontScale, spacing, TOTAL_TICKS_PREFIX + 0);

        TextButton restartButton = new TextButton(RESTART_BUTTON_TEXT, buttonStyle);
        restartButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                restartAction.onRestart();
            }
        });
        restartButton.pad(config.getFloat("Ui", "button", "padY"),
                config.getFloat("Ui", "button", "padX"),
                config.getFloat("Ui", "button", "padY"),
                config.getFloat("Ui", "button", "padX"));
        table.add(restartButton).left().padTop(spacing);
        table.row();

        getRoot().addActor(table);
    }

    public void updateTicks(long totalTicks) {
        ticksLabel.setText(TOTAL_TICKS_PREFIX + totalTicks);
    }

    @Override
    public void dispose() {
        font.dispose();
        solidColorTexture.dispose();
        super.dispose();
    }

    private Label addLabel(Table table, Label.LabelStyle style, float fontScale, float spacing, String text) {
        Label label = new Label(text, style);
        label.setFontScale(fontScale);
        table.add(label).left().padBottom(spacing);
        table.row();
        return label;
    }

    private Drawable tintedDrawable(ConfigManager config, String name) {
        return new TextureRegionDrawable(new TextureRegion(solidColorTexture))
                .tint(config.getColor("Ui", name));
    }

    private Texture createSolidColorTexture() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        return texture;
    }
}
