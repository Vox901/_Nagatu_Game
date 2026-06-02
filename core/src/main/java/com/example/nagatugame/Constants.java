package com.example.nagatugame;

import com.badlogic.gdx.graphics.Color;

public class Constants {
    public static final float WIDTH = 1024f;
    public static final float HEIGHT = 576f;

    // Game Colors
    public static final Color C_LAPIS = new Color(0.12f, 0.23f, 0.37f, 1f);
    public static final Color C_GOLD = new Color(0.83f, 0.69f, 0.22f, 1f);
    public static final Color C_CLAY = new Color(0.76f, 0.27f, 0.16f, 1f);
    public static final Color C_STONE = new Color(0.35f, 0.35f, 0.38f, 1f);
    public static final Color C_WHITE = new Color(0.95f, 0.95f, 0.95f, 1f);
    public static final Color C_GREEN = new Color(0.18f, 0.47f, 0.31f, 1f);
    public static final Color C_PURPLE = new Color(0.5f, 0.2f, 0.6f, 1f);
    public static final Color C_TEAL = new Color(0.1f, 0.5f, 0.5f, 1f);

    // UI Colors
    public static final Color UI_BUTTON_NORMAL = new Color(0.2f, 0.2f, 0.25f, 0.8f);

    public static final float BALL_RADIUS = 15f;
    public static final float BALL_DIAMETER = BALL_RADIUS * 2f;
    public static final float BALL_SPACING = 34f;
    public static final float BALL_SPEED_START = 65f;
    public static final float SHOT_SPEED = 1400f;
    public static final int COMBO_MIN = 3;
    public static final int CIRCLE_SEGMENTS = 32;

    public static final Color[] BALL_COLORS = {C_LAPIS, C_CLAY, C_GREEN, C_GOLD, C_PURPLE, C_TEAL};
}
