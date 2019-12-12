package com.star.app.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.helpers.Poolable;
import com.star.app.screen.ScreenManager;

public class Bullet implements Poolable {
    private GameController gc;
    private String weaponTitle;
    private Vector2 position;
    private Vector2 velocity;
    private float angle;
    private boolean active;
    private int damage;
    private float lifetimeDistance;
    private Ship owner;

    public int getDamage() {
        return damage;
    }

    public Ship getOwner() {
        return owner;
    }

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

    public void activate(Ship owner, String weaponTitle, float x, float y, float vx, float vy, int damage, float angle, float lifetimeDistance) {
        this.weaponTitle = weaponTitle;
        this.position.set(x, y);
        this.velocity.set(vx, vy);
        this.active = true;
        this.angle = angle;
        this.damage = damage;
        this.lifetimeDistance = lifetimeDistance;
        this.owner = owner;
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);
        lifetimeDistance -= velocity.len() * dt;
        gc.getParticleController().getEffectBuilder().createBulletTrace(weaponTitle, position, velocity);
        if (lifetimeDistance < 0.0f || position.x < -ScreenManager.HALF_SCREEN_WIDTH || position.x > GameController.SPACE_WIDTH+ScreenManager.HALF_SCREEN_WIDTH || position.y < -ScreenManager.HALF_SCREEN_HEIGHT || position.y > GameController.SPACE_HEIGHT+ScreenManager.HALF_SCREEN_HEIGHT) {
            deactivate();
        }
    }
}