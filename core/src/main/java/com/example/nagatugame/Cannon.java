package com.example.nagatugame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Cannon {
    public Vector2 position;
    public float angle;
    public Color currentBallColor;
    public Color nextBallColor;
    public float reloadTime;
    public boolean canShoot;

    private static final float RELOAD_DELAY = 0.25f;
    private static final float BASE_RADIUS = 38f;
    private static final float BARREL_LENGTH = 48f;

    public Cannon(float x, float y) {
        this.position = new Vector2(x, y);
        this.angle = 0;
        this.currentBallColor = getRandomColor();
        this.nextBallColor = getRandomColor();
        this.reloadTime = 0;
        this.canShoot = true;
    }

    public Color getRandomColor() {
        return Constants.BALL_COLORS[(int)(Math.random() * Constants.BALL_COLORS.length)];
    }

    public void aimAt(float targetX, float targetY) {
        angle = (float) Math.atan2(targetY - position.y, targetX - position.x);
    }

    public Vector2 getMuzzlePoint() {
        return new Vector2(
            position.x + BARREL_LENGTH * (float)Math.cos(angle),
            position.y + BARREL_LENGTH * (float)Math.sin(angle)
        );
    }

    public void update(float delta) {
        if (reloadTime > 0) {
            reloadTime -= delta;
            if (reloadTime <= 0) {
                canShoot = true;
            }
        }
    }

    public void swapColors() {
        Color temp = currentBallColor;
        currentBallColor = nextBallColor;
        nextBallColor = temp;
    }

    public Ball shoot() {
        if (!canShoot) return null;
        canShoot = false;
        reloadTime = RELOAD_DELAY;
        Ball ball = Ball.createFlying(currentBallColor, getMuzzlePoint(), angle);
        currentBallColor = nextBallColor;
        nextBallColor = getRandomColor();
        return ball;
    }

    public void draw(com.badlogic.gdx.graphics.glutils.ShapeRenderer sr) {
        // Aiming laser dots
        sr.setColor(1, 1, 1, 0.2f);
        Vector2 muzzle = getMuzzlePoint();
        for (int i = 1; i < 12; i++) {
            float d = i * 50;
            sr.circle(muzzle.x + (float)Math.cos(angle) * d, muzzle.y + (float)Math.sin(angle) * d, 2.5f);
        }

        // Base
        sr.setColor(Constants.C_STONE);
        sr.circle(position.x, position.y, BASE_RADIUS, Constants.CIRCLE_SEGMENTS);
        sr.setColor(Constants.C_GOLD);
        sr.circle(position.x, position.y, BASE_RADIUS + 2, Constants.CIRCLE_SEGMENTS);

        // Barrel
        sr.setColor(Constants.C_GOLD);
        sr.rectLine(position.x, position.y, muzzle.x, muzzle.y, 14);

        // Current Ball in muzzle
        sr.setColor(Constants.C_GOLD);
        sr.circle(muzzle.x, muzzle.y, Constants.BALL_RADIUS + 2, Constants.CIRCLE_SEGMENTS);
        sr.setColor(currentBallColor);
        sr.circle(muzzle.x, muzzle.y, Constants.BALL_RADIUS, Constants.CIRCLE_SEGMENTS);

        // Highlight on muzzle ball
        sr.setColor(1, 1, 1, 0.4f);
        sr.circle(muzzle.x - 4, muzzle.y + 4, Constants.BALL_RADIUS * 0.4f, Constants.CIRCLE_SEGMENTS);
    }
}
