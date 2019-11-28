package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.helpers.ObjectPool;

public class PowerUpController extends ObjectPool<PowerUp> {
    private GameController gc;


    @Override
    protected PowerUp newObject() {
        return new PowerUp(gc);
    }

    public PowerUpController(GameController gc) {
        this.gc = gc;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            PowerUp p = activeList.get(i);
            p.render(batch);
        }
    }

    public void setup(float x, float y, float vx, float vy, int type) {
        getActiveElement().activate(x, y, vx, vy, type);
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }
}
