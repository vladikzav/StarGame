package com.star.app.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.helpers.ObjectPool;

public class EnemyHeroController extends ObjectPool<EnemyHero> {
    private GameController gc;

    @Override
    protected EnemyHero newObject() {
        return new EnemyHero(gc);
    }

    public EnemyHeroController(GameController gc) {
        this.gc = gc;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            EnemyHero a = activeList.get(i);
            a.render(batch);
        }
    }

    public void setup(float x, float y, float vx, float vy, int hp) {
        getActiveElement().activate(x, y, vx, vy, hp);
    }

    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }
}
