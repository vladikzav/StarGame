package com.star.app.game;


import com.badlogic.gdx.math.Vector2;
import com.star.app.game.helpers.Poolable;
import com.star.app.screen.ScreenManager;

public class Asteroid implements Poolable {
    private Vector2 position;
    private Vector2 velocity;
    private boolean active;
    private int generateNext;

    public void setGenerateNext(int generateNext){
        this.generateNext = generateNext;
    }

    public int getGenerateNext() {
        return generateNext;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity(){
        return velocity;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public Asteroid() {
        this.position = new Vector2(ScreenManager.SCREEN_WIDTH+64.0f, ScreenManager.SCREEN_HEIGHT/2.0f);
        this.velocity = new Vector2(0,0);
        this.generateNext = 0;
        active = false;
    }

    public void activate(float x, float y, float vx, float vy, int genNext) {
        position.set(x, y);
        velocity.set(vx, vy);
        generateNext = genNext;
        active = true;
    }


    public void update(float dt) {
        position.x += velocity.x * dt;
        position.y += velocity.y * dt;

        if (position.x < -64.1f) {
            position.x = ScreenManager.SCREEN_WIDTH+64.0f;
        }
        if (position.x > ScreenManager.SCREEN_WIDTH+64.1f) {
            position.x = -64.0f;
        }
        if (position.y < -64.1f) {
            position.y = ScreenManager.SCREEN_HEIGHT+64.0f;
        }
        if (position.y > ScreenManager.SCREEN_HEIGHT+64.1f) {
            position.y = -64.0f;
        }

    }
}
