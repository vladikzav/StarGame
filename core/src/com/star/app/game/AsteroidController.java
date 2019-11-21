package com.star.app.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.game.helpers.ObjectPool;


public class AsteroidController extends ObjectPool<Asteroid> {
    private Texture asteroidTexture;
    private GameController gc;

    @Override
    protected Asteroid newObject() {
        return new Asteroid();
    }

    public AsteroidController(GameController gc) {
        this.asteroidTexture = new Texture("Asteroid.png");
        this.gc = gc;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeList.size(); i++) {
            Asteroid a = activeList.get(i);
            batch.draw(asteroidTexture, a.getPosition().x - 64, a.getPosition().y - 64, 64, 64, 128, 128, 1, 1, 0, 0, 0, 256, 256, false, false);
        }
    }

    public void setup(float x, float y, float vx, float vy) {
        if(gc.getAsteroidController().getActiveList().size()>=gc.ASTEROID_COUNT){
            getActiveElement().activate(x, y, vx, vy,0);
        }else {
            getActiveElement().activate(x, y, vx, vy,1);
        }
    }


    public void update(float dt) {
        for (int i = 0; i < activeList.size(); i++) {
            activeList.get(i).update(dt);
        }
        checkPool();
    }
}
