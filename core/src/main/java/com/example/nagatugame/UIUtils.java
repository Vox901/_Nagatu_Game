package com.example.nagatugame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import java.util.HashMap;

public class UIUtils {

    private static final HashMap<String, TextureRegionDrawable> drawableCache = new HashMap<>();

    public static TextButton.TextButtonStyle createButtonStyle(BitmapFont font, Color baseColor) {
        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.downFontColor = Constants.C_GOLD;
        style.overFontColor = Constants.C_GOLD;

        style.up = getCachedDrawable(baseColor);
        style.over = getCachedDrawable(new Color(baseColor).add(0.12f, 0.12f, 0.15f, 0));
        style.down = getCachedDrawable(new Color(baseColor).sub(0.08f, 0.08f, 0.08f, 0));

        return style;
    }

    private static TextureRegionDrawable getCachedDrawable(Color color) {
        String key = color.toString();
        if (!drawableCache.containsKey(key)) {
            drawableCache.put(key, createDrawable(color));
        }
        return drawableCache.get(key);
    }

    private static TextureRegionDrawable createDrawable(Color color) {
        int w = 64, h = 64; // Increased resolution for better border scaling
        Pixmap pixmap = new Pixmap(w, h, Pixmap.Format.RGBA8888);

        // Main Background
        pixmap.setColor(color);
        pixmap.fill();

        // Gradient effect (glossy top)
        pixmap.setColor(1, 1, 1, 0.12f);
        pixmap.fillRectangle(0, 0, w, h / 2);

        // Outer Border (darker)
        pixmap.setColor(0, 0, 0, 0.4f);
        pixmap.drawRectangle(0, 0, w, h);

        // Inner Border (lighter)
        pixmap.setColor(1, 1, 1, 0.2f);
        pixmap.drawRectangle(1, 1, w - 2, h - 2);

        Texture texture = new Texture(pixmap);
        texture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        pixmap.dispose();

        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        // Add padding
        drawable.setLeftWidth(15);
        drawable.setRightWidth(15);

        return drawable;
    }

    public static void dispose() {
        for (TextureRegionDrawable d : drawableCache.values()) {
            d.getRegion().getTexture().dispose();
        }
        drawableCache.clear();
    }
}
