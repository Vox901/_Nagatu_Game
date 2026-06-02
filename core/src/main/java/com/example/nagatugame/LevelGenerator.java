package com.example.nagatugame;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import java.util.Random;

public class LevelGenerator {
    private static final Random random = new Random();

    public enum LevelType { SPIRAL, WAVE, FLOWER, SNAKE, HEART }

    public static Path generateLevel(int levelNumber) {
        Path path = new Path();
        LevelType type = getLevelTypeForNumber(levelNumber);

        float cx = Constants.WIDTH / 2f;
        float cy = Constants.HEIGHT / 2f;

        // Dynamic sizing based on screen dimensions
        float maxR = Constants.HEIGHT * 0.46f;

        switch (type) {
            case SPIRAL:
                generateSpaciousSpiral(path, cx, cy, maxR * 1.1f, 2.0f + levelNumber * 0.1f);
                break;
            case WAVE:
                generateSineWave(path, cx, cy, levelNumber);
                break;
            case FLOWER:
                generateFlower(path, cx, cy, maxR, 4 + (levelNumber % 4));
                break;
            case SNAKE:
                generateSnake(path, levelNumber);
                break;
            case HEART:
                generateHeart(path, cx, cy, maxR * 0.8f);
                break;
        }
        return path;
    }

    private static LevelType getLevelTypeForNumber(int level) {
        LevelType[] types = LevelType.values();
        return types[(level - 1) % types.length];
    }

    private static void generateSpaciousSpiral(Path path, float cx, float cy, float maxR, float turns) {
        int steps = 2500;
        for (int i = 0; i < steps; i++) {
            float t = i / (float)steps;
            float angle = t * turns * MathUtils.PI2;
            // Radius winds inwards towards a hole that isn't exactly at the center (to avoid cannon)
            float r = maxR * (1.1f - 0.85f * t);

            float x = cx + r * MathUtils.cos(angle);
            float y = cy + r * MathUtils.sin(angle);
            path.getPoints().add(new Vector2(x, y));
        }
        path.calculateLengths();
    }

    private static void generateSineWave(Path path, float cx, float cy, int level) {
        int steps = 2000;
        float width = Constants.WIDTH * 0.85f;
        float amplitude = Constants.HEIGHT * 0.35f;
        float frequency = 1.5f + (level * 0.2f);

        for (int i = 0; i < steps; i++) {
            float t = i / (float)steps;
            // Move from left to right, then curve back to center
            float x = (cx - width/2f) + width * t;
            float y = cy + MathUtils.sin(t * MathUtils.PI2 * frequency) * amplitude * (1.0f - 0.8f * t);

            // Adjust end point to be closer to center hole
            float endT = MathUtils.clamp((t - 0.8f) / 0.2f, 0, 1);
            float finalX = MathUtils.lerp(x, cx + 40, endT);
            float finalY = MathUtils.lerp(y, cy - 20, endT);

            path.getPoints().add(new Vector2(finalX, finalY));
        }
        path.calculateLengths();
    }

    private static void generateFlower(Path path, float cx, float cy, float baseR, int petals) {
        int steps = 2500;
        for (int i = 0; i < steps; i++) {
            float t = i / (float)steps;
            float angle = t * MathUtils.PI2 * 2; // Two full rotations for complexity

            float petalMod = MathUtils.sin(angle * petals) * (baseR * 0.25f);
            float r = (baseR + petalMod) * (1.0f - 0.8f * t);

            float x = cx + r * MathUtils.cos(angle);
            float y = cy + r * MathUtils.sin(angle);
            path.getPoints().add(new Vector2(x, y));
        }
        path.calculateLengths();
    }

    private static void generateSnake(Path path, int level) {
        int steps = 3000;
        float margin = 60f;
        float startX = margin;
        float endX = Constants.WIDTH - margin;
        float startY = Constants.HEIGHT - margin;

        // A path that zig-zags across the whole screen
        for (int i = 0; i < steps; i++) {
            float t = i / (float)steps;
            float x, y;

            if (t < 0.25f) { // Top row left to right
                float nt = t / 0.25f;
                x = MathUtils.lerp(startX, endX, nt);
                y = startY;
            } else if (t < 0.5f) { // Right side down
                float nt = (t - 0.25f) / 0.25f;
                x = endX;
                y = MathUtils.lerp(startY, margin, nt);
            } else if (t < 0.75f) { // Bottom row right to left
                float nt = (t - 0.5f) / 0.25f;
                x = MathUtils.lerp(endX, startX + 100, nt);
                y = margin;
            } else { // Curve to center
                float nt = (t - 0.75f) / 0.25f;
                float angle = MathUtils.PI + nt * MathUtils.PI;
                float r = 150 * (1.0f - nt);
                x = (startX + 150) + r * MathUtils.cos(angle);
                y = (margin + 150) + r * MathUtils.sin(angle);

                // Final destination is center hole
                x = MathUtils.lerp(x, Constants.WIDTH/2f + 50, nt);
                y = MathUtils.lerp(y, Constants.HEIGHT/2f - 30, nt);
            }

            path.getPoints().add(new Vector2(x, y));
        }
        path.calculateLengths();
    }

    private static void generateHeart(Path path, float cx, float cy, float size) {
        int steps = 2000;
        for (int i = 0; i < steps; i++) {
            float t = i / (float)steps;
            float angle = t * MathUtils.PI2;

            // Parametric Heart Equation
            float x = 16 * MathUtils.sin(angle) * MathUtils.sin(angle) * MathUtils.sin(angle);
            float y = 13 * MathUtils.cos(angle) - 5 * MathUtils.cos(2*angle) - 2 * MathUtils.cos(3*angle) - MathUtils.cos(4*angle);

            float scale = size / 16f * (1.1f - 0.9f * t);

            path.getPoints().add(new Vector2(cx + x * scale, cy + y * scale));
        }
        path.calculateLengths();
    }
}
