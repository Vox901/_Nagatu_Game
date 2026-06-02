package com.example.nagatugame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Ball {
    public Color color;
    public Vector2 position;
    public Vector2 velocity;
    public float pathPosition;
    public boolean isFlying;
    public boolean alive;
    public int pathIndex;

    public Ball(Color color) {
        this.color = color;
        this.position = new Vector2();
        this.velocity = new Vector2();
        this.pathPosition = 0;
        this.isFlying = false;
        this.alive = true;
        this.pathIndex = -1;
    }

    public static Ball createFlying(Color color, Vector2 startPos, float angle) {
        Ball ball = new Ball(color);
        ball.isFlying = true;
        ball.position.set(startPos);
        ball.velocity.set(
            Constants.SHOT_SPEED * (float)Math.cos(angle),
            Constants.SHOT_SPEED * (float)Math.sin(angle)
        );
        return ball;
    }

    public void update(float delta) {
        if (isFlying) {
            position.add(velocity.x * delta, velocity.y * delta);
        }
    }

    public boolean equalsColor(Ball other) {
        return other != null && java.util.Objects.equals(this.color, other.color);
    }
}
