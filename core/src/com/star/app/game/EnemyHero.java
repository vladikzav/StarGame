package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.helpers.Poolable;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;

public class EnemyHero implements Poolable {
    private GameController gc;
    private TextureRegion texture;
    private Vector2 position;
    private Vector2 velocity;
    private float angle;
    private int hp;
    private int hpMax;
    private float fireTimer;
    private Circle hitArea;
    private boolean tryToShoot;
    private boolean active;
    private Sound shootSound;

    public int getHp() {
        return hp;
    }

    public boolean isTryToShoot() {
        return tryToShoot;
    }

    public void setTryToShoot(boolean tryToShoot) {
        this.tryToShoot = tryToShoot;
    }

    public Circle getHitArea() {
        return hitArea;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    @Override
    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }

    public EnemyHero(GameController gc) {
        this.gc = gc;
        this.texture = Assets.getInstance().getAtlas().findRegion("ship");
        this.position = new Vector2(0,0);
        this.velocity = new Vector2(0, 0);
        this.angle = 0.0f;
        this.hpMax = 0;
        this.hp = 0;
        this.active = false;
    }

    public void activate(float x, float y, float vx, float vy, int hp) {
        this.position = new Vector2(x,y);
        this.velocity = new Vector2(vx, vy);
        this.angle = 0.0f;
        this.hpMax = hp;
        this.hp = this.hpMax;
        this.hitArea = new Circle(position, 26.0f);
        this.shootSound = Assets.getInstance().getAssetManager().get("audio/Shoot.mp3");
        this.tryToShoot = false;
        this.active = true;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x-32, position.y-32, 32, 32, 64, 64, 1, 1, angle);
    }

    public void takeDamage(int amount) {
        hp -= amount;
    }

    //Вращение прицел при стрельбе
public void targeting(float dt, float angle){
        angle = angle-180.0f;
    if (this.angle != angle) {
        float rotateSpeed = (angle - this.angle) / 2.0f;
        if (Math.abs(rotateSpeed) < 180.0f) {
            rotateSpeed = Math.signum(rotateSpeed) * 180.0f;
        }
        this.angle += rotateSpeed * dt;
        if (Math.abs(this.angle - angle) < Math.abs(rotateSpeed * dt)) {
            this.angle = angle;
        }
        if(Math.abs(this.angle-angle)<= 20 && gc.getHero().getPosition().dst(this.getPosition())<=500) {
            tryToShoot = true;
        } else
        tryToShoot = false;
//        System.out.println("TMP.angle: " + angle);
//        System.out.println("angle: " + this.angle);
//        System.out.println("RTS: " + rotateSpeed);
    }
}

    public void update(float dt) {
        if (velocity.len() > 750) {
            velocity.nor().scl(750);
        }
        fireTimer += dt;
        if (tryToShoot) {
            if (fireTimer > 0.2f) {
                fireTimer = 0.0f;
                shootSound.play();
                gc.getEnemyBulletController().setup(position.x, position.y, (float) Math.cos(Math.toRadians(angle)) * 600 + velocity.x, (float) Math.sin(Math.toRadians(angle)) * 600 + velocity.y, angle);
            }
        }


        position.mulAdd(velocity, dt);
        float stopKoef = 1.0f - 2.0f * dt;
        if (stopKoef < 0.0f) {
            stopKoef = 0.0f;
        }
        velocity.scl(stopKoef);
        if (velocity.len() > 50.0f) {
            float bx, by;
            bx = position.x - 28.0f * (float) Math.cos(Math.toRadians(angle));
            by = position.y - 28.0f * (float) Math.sin(Math.toRadians(angle));
            for (int i = 0; i < 2; i++) {
                gc.getParticleController().setup(
                        bx + MathUtils.random(-4, 4), by + MathUtils.random(-4, 4),
                        velocity.x * -0.3f + MathUtils.random(-20, 20), velocity.y * -0.3f + MathUtils.random(-20, 20),
                        0.5f,
                        1.2f, 0.2f,
                        1.0f, 0.5f, 0.0f, 1.0f,
                        1.0f, 1.0f, 1.0f, 0.0f
                );
            }
        }

        if (position.x < hitArea.radius) {
            position.x += GameController.SPACE_WIDTH;
        }
        if (position.x > GameController.SPACE_WIDTH - hitArea.radius) {
            position.x -= GameController.SPACE_WIDTH;
        }
        if (position.y < hitArea.radius) {
            position.y = GameController.SPACE_HEIGHT - hitArea.radius - 1;
        }
        if (position.y > GameController.SPACE_HEIGHT - hitArea.radius) {
            position.y = hitArea.radius + 1;
        }
        hitArea.setPosition(position);
    }

}
