package com.star.app.game;

public class GameController {
    private Background background;
    private BulletController bulletController;
    private Hero hero;

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
    }

    public void update(float dt) {
        background.update(dt);
        hero.update(dt);
        bulletController.update(dt);
        checkCollisions();
    }

    // Заготовка под столкновение с астероидами (для ДЗ)
    public void checkCollisions() {
        for (int i = 0; i < bulletController.getActiveList().size(); i++) {
            Bullet b = bulletController.getActiveList().get(i);
            if (hero.getPosition().dst(b.getPosition()) < 32.0f) { // 32.0f - примерно радиус корабля
                // b.deactivate();
                // считаем что столкнулись
            }
        }
    }
}