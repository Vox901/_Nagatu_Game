package com.example.nagatugame;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

public class Particle {
    public Vector2 position;
    public Vector2 velocity;
    public Color color;
    public float life;
    public float maxLife;
    public float size;

    public Particle(float x, float y, Color color) {
        this.position = new Vector2(x, y);
        float angle = MathUtils.random(0, MathUtils.PI2);
        float speed = MathUtils.random(80, 250);
        this.velocity = new Vector2(MathUtils.cos(angle) * speed, MathUtils.sin(angle) * speed);
        this.color = new Color(color);
        this.maxLife = MathUtils.random(0.4f, 0.8f);
        this.life = maxLife;
        this.size = MathUtils.random(4f, 10f);
    }

    public boolean update(float delta) {
        position.add(velocity.x * delta, velocity.y * delta);
        velocity.scl(0.92f); // Friction
        life -= delta;
        color.a = MathUtils.clamp(life / maxLife, 0, 1);
        return life > 0;
    }
}
