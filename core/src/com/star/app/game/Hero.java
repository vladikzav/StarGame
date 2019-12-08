package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.StringBuilder;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;
import com.star.app.screen.utils.OptionsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hero {
    public class Skill {
        private int level;
        private int maxLevel;
        private String title;
        private Runnable[] effects;
        private int[] cost;

        public int getLevel() {
            return level;
        }

        public String getTitle() {
            return title;
        }

        public int getMaxLevel() {
            return maxLevel;
        }

        public int getCurrentLevelCost() {
            return cost[level - 1];
        }

        public Skill(String title, Runnable[] effects, int[] cost) {
            this.level = 1;
            this.title = title;
            this.effects = effects;
            this.cost = cost;
            this.maxLevel = effects.length;
            if (effects.length != cost.length) {
                throw new RuntimeException("Unable to create skill tree");
            }
        }

        public boolean isUpgradable() {
            return level < effects.length + 1;
        }

        public void upgrade() {
            effects[level - 1].run();
            level++;
        }
    }

    private TextureRegion starTexture;
    private Skill[] skills;
    private GameController gc;
    private TextureRegion texture;
    private KeysControl keysControl;
    private Vector2 position;
    private Vector2 velocity;
    private int hp;
    private int hpMax;
    private float angle;
    private float enginePower;
    private float fireTimer;
    private int score;
    private int scoreView;
    private Circle hitArea;
    private Weapon currentWeapon;
    private int money;
    private Shop shop;
    private Vector2 tmpVector;

    public Skill[] getSkills() {
        return skills;
    }

    public Shop getShop() {
        return shop;
    }

    public boolean isMoneyEnough(int amount) {
        return money >= amount;
    }

    public void decreaseMoney(int amount) {
        money -= amount;
    }

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

    public boolean isAlive() {
        return hp > 0;
    }

    public Hero(GameController gc, String keysControlPrefix) {
        this.gc = gc;
        this.starTexture = Assets.getInstance().getAtlas().findRegion("star16");
        this.texture = Assets.getInstance().getAtlas().findRegion("ship");
        this.position = new Vector2(640, 360);
        this.velocity = new Vector2(0, 0);
        this.angle = 0.0f;
        this.enginePower = 750.0f;
        this.hpMax = 100;
        this.hp = this.hpMax;
        this.money = 1000;
        this.strBuilder = new StringBuilder();
        this.hitArea = new Circle(position, 26.0f);
        this.keysControl = new KeysControl(OptionsUtils.loadProperties(), keysControlPrefix);
        this.createSkillsTable();
        this.shop = new Shop(this);
        this.tmpVector = new Vector2(0, 0);
        this.currentWeapon = new Weapon(
                gc, this, "Laser", 0.2f, 1, 500.0f, 320,
                new Vector3[]{
                        new Vector3(24, 90, 0),
                        new Vector3(24, -90, 0)
                }
//                new Vector3[]{
//                        new Vector3(28, 0, 0),
//                        new Vector3(28, 90, 20),
//                        new Vector3(28, -90, -20)
//                }
        );
    }


    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, 1, 1, angle);
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {
        strBuilder.clear();
        strBuilder.append("SCORE: ").append(scoreView).append("\n");
        strBuilder.append("MONEY: ").append(money).append("\n");
        strBuilder.append("HP: ").append(hp).append(" / ").append(hpMax).append("\n");
        strBuilder.append("BULLETS: ").append(currentWeapon.getCurBullets()).append(" / ").append(currentWeapon.getMaxBullets()).append("\n");
        font.draw(batch, strBuilder, 20, 1060);

        int mapX = 1700;
        int mapY = 800;
        batch.setColor(Color.GREEN);
        batch.draw(starTexture, mapX - 24, mapY - 24, 48, 48);
        batch.setColor(Color.RED);
        for (int i = 0; i < gc.getAsteroidController().getActiveList().size(); i++) {
            Asteroid a = gc.getAsteroidController().getActiveList().get(i);
            float dst = position.dst(a.getPosition());
            if (dst < 3000.0f) {
                tmpVector.set(a.getPosition()).sub(this.position);
                tmpVector.scl(160.0f / 3000.0f);
                batch.draw(starTexture, mapX + tmpVector.x - 16, mapY + tmpVector.y - 16, 32, 32);
            }
        }

        batch.setColor(Color.WHITE);
        for (int i = 0; i < 120; i++) {
            batch.draw(starTexture, mapX + 160.0f * MathUtils.cosDeg(360.0f / 120.0f * i) - 8, mapY + 160.0f * MathUtils.sinDeg(360.0f / 120.0f * i) - 8);
        }
    }

    public void update(float dt) {
        if (velocity.len() > 1000.0f) {
            velocity.nor().scl(1000.0f);
        }
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            shop.setVisible(true);
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
    }

    public void tryToFire() {
        if (fireTimer > currentWeapon.getFirePeriod()) {
            fireTimer = 0.0f;
            currentWeapon.fire();
        }
    }

    public void checkSpaceBorders() {
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
    }

    public void updateScore(float dt) {
        if (scoreView != score) {
            float scoreSpeed = (score - scoreView) / 2.0f;
            if (Math.abs(scoreSpeed) < 2000.0f) {
                scoreSpeed = Math.signum(scoreSpeed) * 2000.0f;
            }
            scoreView += scoreSpeed * dt;
            if (Math.abs(scoreView - score) < Math.abs(scoreSpeed * dt)) {
                scoreView = score;
            }
        }
    }

    public void consume(PowerUp p) {
        switch (p.getType()) {
            case MEDKIT:
                hp += p.getPower();
                if (hp > hpMax) {
                    hp = hpMax;
                }
                break;
            case AMMOS:
                currentWeapon.addAmmos(p.getPower());
                break;
            case MONEY:
                money += p.getPower();
                break;
        }
    }

    public void upgrade(int index) {
        int level = this.skills[index].level;
        this.skills[index].effects[level - 1].run();
        this.skills[index].level++;
    }

    public void createSkillsTable() {
        this.skills = new Skill[2];
        skills[0] = new Skill("HP",
                new Runnable[]{
                        new Runnable() {
                            @Override
                            public void run() {
                                hpMax += 10;
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                hpMax += 20;
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                hpMax += 30;
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                hpMax += 40;
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                hpMax += 50;
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                hpMax += 50;
                            }
                        }
                },
                new int[]{
                        10,
                        20,
                        30,
                        50,
                        100,
                        500
                }
        );

        skills[1] = new Skill("WX-I",
                new Runnable[]{
                        new Runnable() {
                            @Override
                            public void run() {
                                Hero.this.currentWeapon = new Weapon(
                                        gc, Hero.this, "Laser", 0.3f, 1, 600.0f, 320,
                                        new Vector3[]{
                                                new Vector3(24, 90, 10),
                                                new Vector3(24, 0, 0),
                                                new Vector3(24, -90, -10)
                                        }
                                );
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                Hero.this.currentWeapon = new Weapon(
                                        gc, Hero.this, "Laser", 0.3f, 1, 600.0f, 320,
                                        new Vector3[]{
                                                new Vector3(24, 90, 20),
                                                new Vector3(24, 20, 0),
                                                new Vector3(24, -20, 0),
                                                new Vector3(24, -90, -20)
                                        }
                                );
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                Hero.this.currentWeapon = new Weapon(
                                        gc, Hero.this, "Laser", 0.05f, 2, 600.0f, 32000,
                                        new Vector3[]{
                                                new Vector3(24, 90, 20),
                                                new Vector3(24, 20, 0),
                                                new Vector3(24, 0, 0),
                                                new Vector3(24, -20, 0),
                                                new Vector3(24, -90, -20)
                                        }
                                );
                            }
                        }
                },
                new int[]{
                        100,
                        200,
                        300
                }
        );
    }
}