package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import com.star.app.screen.utils.Assets;
import com.star.app.screen.utils.OptionsUtils;

public class Hero extends Ship {
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
    private KeysControl keysControl;
    private int score;
    private int scoreView;
    private int money;
    private Shop shop;
    private StringBuilder tmpStr;
    private float objectCaptureRadius;
    private float timeShiftTimer;

    public float getObjectCaptureRadius() {
        return objectCaptureRadius;
    }

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

    public void addScore(int amount) {
        score += amount;
    }

    public int getScore() {
        return score;
    }

    public Hero(GameController gc, String keysControlPrefix) {
        super(gc, 100);
        this.starTexture = Assets.getInstance().getAtlas().findRegion("star16");
        this.texture = Assets.getInstance().getAtlas().findRegion("ship");
        this.changePosition(640, 360);
        this.enginePower = 1500.0f;
        this.money = 1000;
        this.tmpStr = new StringBuilder();
        this.keysControl = new KeysControl(OptionsUtils.loadProperties(), keysControlPrefix);
        this.createSkillsTable();
        this.shop = new Shop(this);
        this.objectCaptureRadius = 200.0f;
        this.ownerType = OwnerType.PLAYER;
        this.currentWeapon = new Weapon(
                gc, this, "Laser", 0.2f, 1, 1, 320.0f, 500.0f, 320,
                new Vector3[]{
                        new Vector3(24, 90, 0),
                        new Vector3(24, -90, 0)
                }
        );
        this.timeShiftTimer = 0.0f;
    }

    public void renderGUI(SpriteBatch batch, BitmapFont font) {

        tmpStr.setLength(0);
        tmpStr.append("SCORE: ").append(scoreView).append("\n");
        tmpStr.append("MONEY: ").append(money).append("\n");
        tmpStr.append("HP: ").append(hp.getCurrent()).append(" / ").append(hp.getMax()).append("\n");
        tmpStr.append("BULLETS: ").append(currentWeapon.getCurBullets()).append(" / ").append(currentWeapon.getMaxBullets()).append("\n");
        font.draw(batch, tmpStr, 20, 1060);

        int mapX = 1700;
        int mapY = 800;
        batch.setColor(Color.GREEN);
        batch.draw(starTexture, mapX - 24, mapY - 24, 48, 48);

        {
            float dst = position.dst(gc.getBot().getPosition());
            if (dst < 3000.0f) {
                tmpVector.set(gc.getBot().getPosition()).sub(this.position);
                tmpVector.scl(160.0f / 3000.0f);
                batch.setColor(Color.PURPLE);
                batch.draw(starTexture, mapX + tmpVector.x - 16, mapY + tmpVector.y - 16, 32, 32);
            }
        }

        batch.setColor(Color.RED);
        for (int i = 0; i < gc.getAsteroidController().getActiveList().size(); i++) {
            Asteroid a = gc.getAsteroidController().getActiveList().get(i);
            float dst = position.dst(a.getPosition());
            if (dst < 3000.0f) {
                tmpVector.set(a.getPosition()).sub(this.position);
                tmpVector.scl(160.0f / 3000.0f);
                float pointScale = a.getScale() * 2.0f;
                batch.draw(starTexture, mapX + tmpVector.x - 8, mapY + tmpVector.y - 8, 8, 8,16,16,pointScale,pointScale,0.0f);
            }
        }

        batch.setColor(Color.WHITE);
        for (int i = 0; i < 120; i++) {
            batch.draw(starTexture, mapX + 160.0f * MathUtils.cosDeg(360.0f / 120.0f * i) - 8, mapY + 160.0f * MathUtils.sinDeg(360.0f / 120.0f * i) - 8);
        }
    }

    public void update(float dt) {
        super.update(dt);

        updateScore(dt);

        if (Gdx.input.isKeyPressed(keysControl.fire)) {
            currentWeapon.tryToFire();
        }
        if (Gdx.input.isKeyPressed(keysControl.left)) {
            rotate(180.0f, dt);
        }
        if (Gdx.input.isKeyPressed(keysControl.right)) {
            rotate(-180.0f, dt);
        }
        if (Gdx.input.isKeyPressed(keysControl.forward)) {
            accelerate(dt);
        }
        if (Gdx.input.isKeyPressed(keysControl.backward)) {
            brake(dt);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            shop.setVisible(true);
        }

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
    }

    public void updateTimeShift(float dt){
        timeShiftTimer +=dt;
        if (timeShiftTimer > 0.2 || gc.getTimeShiftProgress() > 0) {
            timeShiftTimer = 0;
            gc.setTimeShift(1.0f);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) && gc.getTimeShiftProgress() > 10.0f) {
            gc.setTimeShift(4.0f);
            gc.setTimeShiftProgress(gc.getTimeShiftProgress()-dt*10);
        }
        if(gc.getTimeShiftProgress()<0){
            gc.setTimeShiftProgress(0);
        }
        if(gc.getTimeShiftProgress()<100.0f){
            gc.setTimeShiftProgress(gc.getTimeShiftProgress()+dt*4);
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
                tmpStr.setLength(0);
                tmpStr.append("HP +").append(hp.increase(p.getPower()));
                gc.getInfoController().setup(p.getPosition().x, p.getPosition().y, tmpStr, Color.GREEN);
                break;
            case AMMOS:
                currentWeapon.addAmmos(p.getPower());
                tmpStr.setLength(0);
                tmpStr.append("AMMOS +").append(p.getPower());
                gc.getInfoController().setup(p.getPosition().x, p.getPosition().y, tmpStr, Color.ORANGE);
                break;
            case MONEY:
                money += p.getPower();
                tmpStr.setLength(0);
                tmpStr.append("MONEY +").append(p.getPower());
                gc.getInfoController().setup(p.getPosition().x, p.getPosition().y, tmpStr, Color.YELLOW);
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
                                hp.increase(10);
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                hp.increase(20);
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                hp.increase(30);
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                hp.increase(40);
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                hp.increase(50);
                            }
                        },
                        new Runnable() {
                            @Override
                            public void run() {
                                hp.increase(50);
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
                                        gc, Hero.this, "Laser", 0.3f, 1,1,200.0f, 600.0f, 320,
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
                                        gc, Hero.this, "Laser", 0.3f, 1, 3,300.0f,600.0f, 320,
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
                                        gc, Hero.this, "Laser", 0.05f, 2,4,500.0f, 600.0f, 32000,
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