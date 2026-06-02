package com.example.nagatugame;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;

public class Path {
    private ArrayList<Vector2> points;
    private float totalLength;
    private float[] cumulativeLengths;

    public Path() {
        this.points = new ArrayList<>();
        this.totalLength = 0;
    }

    public void generateSpiral(float centerX, float centerY, float maxRadius, float turns) {
        points.clear();
        int steps = 2000;

        for (int i = 0; i < steps; i++) {
            float t = i / (float)steps;
            // Angle goes from 0 to turns * 2pi
            float angle = t * turns * 2f * (float)Math.PI;
            // Radius goes from max down to min (hole)
            float radius = maxRadius * (1.0f - 0.75f * t);

            // Add some "organic" wobble
            float wobble = (float)Math.sin(angle * 4f) * 10f;

            float x = centerX + (radius + wobble) * (float)Math.cos(angle);
            float y = centerY + (radius + wobble) * (float)Math.sin(angle);
            points.add(new Vector2(x, y));
        }
        calculateLengths();
    }

    public void calculateLengths() {
        totalLength = 0;
        cumulativeLengths = new float[points.size()];
        for (int i = 1; i < points.size(); i++) {
            float dist = points.get(i).dst(points.get(i - 1));
            totalLength += dist;
            cumulativeLengths[i] = totalLength;
        }
    }

    public Vector2 getPositionAt(float distance) {
        if (points.isEmpty()) return new Vector2();
        if (distance <= 0) return points.get(0).cpy();
        if (distance >= totalLength) return points.get(points.size() - 1).cpy();

        int idx = 0;
        for (int i = 0; i < cumulativeLengths.length; i++) {
            if (cumulativeLengths[i] >= distance) {
                idx = i;
                break;
            }
        }

        if (idx == 0) return points.get(0).cpy();

        Vector2 p1 = points.get(idx - 1);
        Vector2 p2 = points.get(idx);
        float segStart = cumulativeLengths[idx - 1];
        float segEnd = cumulativeLengths[idx];
        float t = (distance - segStart) / (segEnd - segStart);

        return new Vector2(p1.x + (p2.x - p1.x) * t, p1.y + (p2.y - p1.y) * t);
    }

    public float getTotalLength() { return totalLength; }
    public ArrayList<Vector2> getPoints() { return points; }
    public int size() { return points.size(); }

    public void draw(com.badlogic.gdx.graphics.glutils.ShapeRenderer sr) {
        if (points.size() < 2) return;

        // Track shadow/border
        sr.setColor(0, 0, 0, 0.3f);
        for (int i = 0; i < points.size() - 1; i++) {
            Vector2 p1 = points.get(i);
            Vector2 p2 = points.get(i + 1);
            sr.rectLine(p1.x, p1.y, p2.x, p2.y, Constants.BALL_SPACING + 4);
        }

        // Track surface
        sr.setColor(Constants.C_STONE);
        for (int i = 0; i < points.size() - 1; i++) {
            Vector2 p1 = points.get(i);
            Vector2 p2 = points.get(i + 1);
            sr.rectLine(p1.x, p1.y, p2.x, p2.y, Constants.BALL_SPACING);
            sr.circle(p1.x, p1.y, Constants.BALL_SPACING / 2f, Constants.CIRCLE_SEGMENTS);
        }
        sr.circle(points.get(points.size()-1).x, points.get(points.size()-1).y, Constants.BALL_SPACING / 2f, Constants.CIRCLE_SEGMENTS);

        // Inner detail
        sr.setColor(0, 0, 0, 0.15f);
        for (int i = 0; i < points.size() - 1; i++) {
            Vector2 p1 = points.get(i);
            Vector2 p2 = points.get(i + 1);
            sr.rectLine(p1.x, p1.y, p2.x, p2.y, 2);
        }
    }
}
