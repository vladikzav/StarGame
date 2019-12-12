package com.star.app.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.star.app.screen.utils.Assets;

public class Weapon {
    private GameController gc;
    private Ship ship;
    private String title;
    private float firePeriod;
    private int minDamage;
    private int maxDamage;
    private float bulletSpeed;
    private int maxBullets;
    private int curBullets;
    private float radius;
    private Sound shootSound;
    private float fireTimer;

    // Когда мы описываем слот Vector3[] slots:
    //   x - это то на сколько пикселей он смещен относительно центра
    //   y - угол смещения относильно центра корабля
    //   z - угол смещения вылета пуль относительно направления корабля
    private Vector3[] slots;

    public float getFirePeriod() {
        return firePeriod;
    }

    public int getMaxBullets() {
        return maxBullets;
    }

    public int getCurBullets() {
        return curBullets;
    }

    public float getRadius() {
        return radius;
    }

    public int getRandomDamage() {
        return MathUtils.random(minDamage, maxDamage);
    }

    public void addAmmos(int amount) {
        curBullets += amount;
        if (curBullets > maxBullets) {
            curBullets = maxBullets;
        }
    }

    public Weapon(GameController gc, Ship ship, String title, float firePeriod, int minDamage, int maxDamage, float radius, float bulletSpeed, int maxBullets, Vector3[] slots) {
        this.gc = gc;
        this.ship = ship;
        this.title = title;
        this.firePeriod = firePeriod;
        this.minDamage = minDamage;
        this.maxDamage = maxDamage;
        this.radius = radius;
        this.bulletSpeed = bulletSpeed;
        this.maxBullets = maxBullets;
        this.curBullets = this.maxBullets;
        this.slots = slots;
        this.shootSound = Assets.getInstance().getAssetManager().get("audio/Shoot.mp3");
    }

    public void update(float dt) {
        fireTimer += dt;
    }

    public void tryToFire() {
        if (fireTimer > firePeriod && (curBullets > 0 || maxBullets == -1)) {
            fireTimer = 0;
            curBullets--;
            // shootSound.play();

            for (int i = 0; i < slots.length; i++) {
                float x, y, vx, vy;
                x = ship.getPosition().x + slots[i].x * MathUtils.cosDeg(ship.getAngle() + slots[i].y);
                y = ship.getPosition().y + slots[i].x * MathUtils.sinDeg(ship.getAngle() + slots[i].y);
                vx = ship.getVelocity().x + bulletSpeed * MathUtils.cosDeg(ship.getAngle() + slots[i].z);
                vy = ship.getVelocity().y + bulletSpeed * MathUtils.sinDeg(ship.getAngle() + slots[i].z);
                gc.getBulletController().setup(ship, title, x, y, vx, vy, getRandomDamage(),ship.getAngle() + slots[i].z, radius);
            }
        }
    }
}
