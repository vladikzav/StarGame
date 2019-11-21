package com.star.app.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.screen.ScreenManager;

public class GameController {
    private Background background;
    private BulletController bulletController;
    private Hero hero;
    private AsteroidController asteroidController;
    public final int ASTEROID_COUNT = 5;

    public AsteroidController getAsteroidController() {
        return asteroidController;
    }

    public BulletController getBulletController() {
        return bulletController;
    }

    public Background getBackground() {
        return background;
    }

    public Hero getHero() {
        return hero;
    }

    public GameController() {
        this.background = new Background(this);
        this.hero = new Hero(this);
        this.bulletController = new BulletController();
        this.asteroidController = new AsteroidController(this);
        asteroidController.setup(ScreenManager.SCREEN_WIDTH+64.0f, ScreenManager.SCREEN_HEIGHT/2.0f, MathUtils.random(-100, -40), MathUtils.random(-100, 100));
    }

    public void update(float dt) {
        background.update(dt);
        hero.update(dt);
        bulletController.update(dt);
        asteroidController.update(dt);

        checkCollisions();
    }

    // Заготовка под столкновение с астероидами (для ДЗ)
    public void checkCollisions() {
        for (int i = 0; i < bulletController.getActiveList().size(); i++) {
            Bullet b = bulletController.getActiveList().get(i);
            for (int j = 0; j < asteroidController.getActiveList().size(); j++) {
                Asteroid a = asteroidController.getActiveList().get(j);
                if (a.getPosition().dst(b.getPosition()) < 64.0f) { // 64.0f - примерно радиус астероида
                    if(a.getGenerateNext()==1) {
                        newAsteroids(a.getPosition().x, a.getPosition().y, a.getVelocity());
                    }
                    b.deactivate();
                    a.deactivate();
                    break;
                    // считаем что столкнулись
                 }

            }
        }


    }

    public void newAsteroids(float x, float y, Vector2 vector){
        vector.rotate(50f);
        asteroidController.setup(x, y, vector.x, vector.y);
        vector.rotate(-100f);
        asteroidController.setup(x, y, vector.x, vector.y);
    }

}