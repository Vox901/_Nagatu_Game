package com.example.nagatugame;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;

public class NagatuGame extends Game {
    public SpriteBatch batch;
    public BitmapFont font;
    public BitmapFont fontLarge;
    public BitmapFont fontSmall;

    private Preferences prefs;
    private int highScore;
    private int totalGames;
    private int totalShots;
    private int maxCombo;

    @Override
    public void create() {
        batch = new SpriteBatch();

        FileHandle fontFile = Gdx.files.internal("arial.ttf");
        if (!fontFile.exists()) {
            fontFile = Gdx.files.absolute("/system/fonts/DroidSans.ttf");
        }

        if (fontFile.exists()) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(fontFile);
            FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
            parameter.borderColor = Color.BLACK;
            parameter.borderWidth = 1.5f;
            parameter.minFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;
            parameter.magFilter = com.badlogic.gdx.graphics.Texture.TextureFilter.Linear;

            parameter.characters = FreeTypeFontGenerator.DEFAULT_CHARS
                + "абвгдеёжзийклмнопрстуфхцчшщъыьэюяАБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯ"
                + "▶📊✕🔄🏠🏆🎮🎯💫←||";

            parameter.size = 36;
            fontLarge = generator.generateFont(parameter);
            parameter.size = 26;
            font = generator.generateFont(parameter);
            parameter.size = 20;
            fontSmall = generator.generateFont(parameter);
            generator.dispose();
        } else {
            font = new BitmapFont();
            fontLarge = new BitmapFont();
            fontSmall = new BitmapFont();
        }

        prefs = Gdx.app.getPreferences("nagatu_stats");
        loadStats();
        setScreen(new MenuScreen(this));
    }

    private void loadStats() {
        highScore = prefs.getInteger("highScore", 0);
        totalGames = prefs.getInteger("totalGames", 0);
        totalShots = prefs.getInteger("totalShots", 0);
        maxCombo = prefs.getInteger("maxCombo", 0);
    }

    public void saveStats() {
        prefs.putInteger("highScore", highScore);
        prefs.putInteger("totalGames", totalGames);
        prefs.putInteger("totalShots", totalShots);
        prefs.putInteger("maxCombo", maxCombo);
        prefs.flush();
    }

    public void updateHighScore(int score) {
        if (score > highScore) {
            highScore = score;
            saveStats();
        }
    }

    public void recordGameEnd(int shots, int combo) {
        totalGames++;
        totalShots += shots;
        if (combo > maxCombo) maxCombo = combo;
        saveStats();
    }

    public int getHighScore() { return highScore; }
    public int getTotalGames() { return totalGames; }
    public int getTotalShots() { return totalShots; }
    public int getMaxCombo() { return maxCombo; }

    @Override
    public void dispose() {
        saveStats();
        batch.dispose();
        font.dispose();
        fontLarge.dispose();
        fontSmall.dispose();
        UIUtils.dispose();
    }
}
