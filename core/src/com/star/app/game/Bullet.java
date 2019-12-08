package com.star.app.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.helpers.Poolable;
import com.star.app.screen.ScreenManager;

public class Bullet implements Poolable {
    private GameController gc;
    private Vector2 position;
    private Vector2 velocity;
    private float angle;
    private boolean active;

    public Vector2 getPosition() {
        return position;
    }

    public float getAngle() {
        return angle;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public void deactivate() {
        active = false;
    }

    public Bullet(GameController gc) {
        this.gc = gc;
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.active = false;
    }

    public void activate(float x, float y, float vx, float vy, float angle) {
        this.position.set(x, y);
        this.velocity.set(vx, vy);
        this.active = true;
        this.angle = angle;
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);
        gc.getParticleController().setup(
                position.x + MathUtils.random(-4, 4), position.y + MathUtils.random(-4, 4),
                velocity.x * -0.3f + MathUtils.random(-20, 20), velocity.y * -0.3f + MathUtils.random(-20, 20),
                0.2f,
                1.2f, 0.2f,
                1.0f, 0.0f, 0.0f, 1.0f,
                1.0f, 1.0f, 0.0f, 0.0f
        );
        if (position.x < 0.0f || position.x > GameController.SPACE_WIDTH || position.y < 0.0f || position.y > GameController.SPACE_HEIGHT) {
            deactivate();
        }
    }
}