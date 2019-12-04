package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.StringBuilder;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;
import com.star.app.screen.utils.OptionsUtils;

public class Hero {
    private GameController gc;
    private TextureRegion texture;
    private KeysControl keysControl;
    private Vector2 position;
    private Vector2 velocity;
    private int hp;
    private float angle;
    private float enginePower;
    private float fireTimer;
    private int score;
    private int scoreView;
    private Circle hitArea;
    private Weapon currentWeapon;
    private int money;

    public float getAngle() {
        return angle;
    }

    private StringBuilder strBuilder;

    public void addScore(int amount) {
        score += amount;
    }

    public int getScore() {
        return score;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public Circle getHitArea() {
        return hitArea;
    }

    public Hero(GameController gc, String keysControlPrefix) {
        this.gc = gc;
        this.texture = Assets.getInstance().getAtlas().findRegion("ship");
        this.position = new Vector2(640, 360);
        this.velocity = new Vector2(0, 0);
        this.angle = 0.0f;
        this.enginePower = 750.0f;
        this.hp = 100;
        this.strBuilder = new StringBuilder();
        this.hitArea = new Circle(position, 26.0f);
        this.keysControl = new KeysControl(OptionsUtils.loadProperties(), keysControlPrefix);

        this.currentWeapon = new Weapon(
                gc, this, "Laser", 0.2f, 1, 600.0f, 320,
                new Vector3[]{
                        new Vector3(28, 0, 0),
                        new Vector3(28, 90, 20),
                        new Vector3(28, -90, -20)
                }
        );
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, 1, 1, angle);
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        strBuilder.clear();
        strBuilder.append("SCORE: ").append(scoreView).append("\n");
        strBuilder.append("MONEY: ").append(money).append("\n");
        strBuilder.append("HP: ").append(hp).append("\n");
        strBuilder.append("BULLETS: ").append(currentWeapon.getCurBullets()).append(" / ").append(currentWeapon.getMaxBullets()).append("\n");
        font.draw(batch, strBuilder, 20, 700);
    }

    public void update(float dt) {
        fireTimer += dt;
        updateScore(dt);

        if (Gdx.input.isKeyPressed(keysControl.fire)) {
            tryToFire();
        }
        if (Gdx.input.isKeyPressed(keysControl.left)) {
            angle += 180.0f * dt;
        }
        if (Gdx.input.isKeyPressed(keysControl.right)) {
            angle -= 180.0f * dt;
        }
        if (Gdx.input.isKeyPressed(keysControl.forward)) {
            velocity.x += (float) Math.cos(Math.toRadians(angle)) * enginePower * dt;
            velocity.y += (float) Math.sin(Math.toRadians(angle)) * enginePower * dt;
        }
        if (Gdx.input.isKeyPressed(keysControl.backward)) {
            velocity.x -= (float) Math.cos(Math.toRadians(angle)) * enginePower * dt / 2.0f;
            velocity.y -= (float) Math.sin(Math.toRadians(angle)) * enginePower * dt / 2.0f;
        }
        position.mulAdd(velocity, dt);
        hitArea.setPosition(position);
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
        checkSpaceBorders();
    }

    public void takeDamage(int amount) {
        hp -= amount;
        if(hp<=0){
            ScreenManager.getInstance().getGame().setScore(score);
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.OVER);
        }
    }

    public void tryToFire() {
        if (fireTimer > currentWeapon.getFirePeriod()) {
            fireTimer = 0.0f;
            currentWeapon.fire();
        }
    }

    public void checkSpaceBorders() {
        if (position.x < hitArea.radius) {
            position.x = hitArea.radius;
            velocity.x *= -1;
        }
        if (position.x > ScreenManager.SCREEN_WIDTH - hitArea.radius) {
            position.x = ScreenManager.SCREEN_WIDTH - hitArea.radius;
            velocity.x *= -1;
        }
        if (position.y < hitArea.radius) {
            position.y = hitArea.radius;
            velocity.y *= -1;
        }
        if (position.y > ScreenManager.SCREEN_HEIGHT - hitArea.radius) {
            position.y = ScreenManager.SCREEN_HEIGHT - hitArea.radius;
            velocity.y *= -1;
        }
    }

    public void updateScore(float dt) {
        if (scoreView < score) {
            float scoreSpeed = (score - scoreView) / 2.0f;
            if (scoreSpeed < 2000.0f) {
                scoreSpeed = 2000.0f;
            }
            scoreView += scoreSpeed * dt;
            if (scoreView > score) {
                scoreView = score;
            }
        }
    }

    public void consume(PowerUp p) {
        switch (p.getType()) {
            case MEDKIT: // todo add max hp check
                hp += p.getPower();
                break;
            case AMMOS:
                currentWeapon.addAmmos(p.getPower());
                break;
            case MONEY:
                money += p.getPower();
                break;
        }
    }
}
