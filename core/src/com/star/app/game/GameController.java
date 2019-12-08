package com.star.app.game;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;


public class GameController {
    ///////////////////////////////////////////////////////////////////////////////////////
    //Constants
    ///////////////////////////////////////////////////////////////////////////////////////

    public static final int SPACE_WIDTH = 9600;
    public static final int SPACE_HEIGHT = 5400;

    ///////////////////////////////////////////////////////////////////////////////////////
    //Initialization
    ///////////////////////////////////////////////////////////////////////////////////////

    private Music music;
    private int level;
    private Background background;
    private AsteroidController asteroidController;
    private BulletController bulletController;
    private BulletController enemyBulletController;
    private ParticleController particleController;
    private PowerUpsController powerUpsController;
    private EnemyHeroController enemyHeroController;
    private Hero hero;
    private Vector2 tmpVec;
    private Stage stage;
    private boolean isNewLevel;
    private float showLevel;

    ///////////////////////////////////////////////////////////////////////////////////////
    //Getters
    ///////////////////////////////////////////////////////////////////////////////////////


    public BulletController getEnemyBulletController() {
        return enemyBulletController;
    }

    public EnemyHeroController getEnemyHeroController() {
        return enemyHeroController;
    }

    public int getLevel() {
        return level;
    }

    public boolean isNewLevel() {
        return isNewLevel;
    }

    public AsteroidController getAsteroidController() {
        return asteroidController;
    }

    public BulletController getBulletController() {
        return bulletController;
    }

    public Background getBackground() {
        return background;
    }

    public PowerUpsController getPowerUpsController() {
        return powerUpsController;
    }

    public ParticleController getParticleController() {
        return particleController;
    }

    public Hero getHero() {
        return hero;
    }

    public Stage getStage(){
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    ///////////////////////////////////////////////////////////////////////////////////////
    //Methods
    ///////////////////////////////////////////////////////////////////////////////////////

    public GameController() {
        this.background = new Background(this);
        this.hero = new Hero(this, "PLAYER1");
        this.enemyHeroController = new EnemyHeroController(this);
        this.asteroidController = new AsteroidController(this);
        this.bulletController = new BulletController(this);
        this.enemyBulletController = new BulletController(this);
        this.particleController = new ParticleController();
        this.powerUpsController = new PowerUpsController(this);
        this.tmpVec = new Vector2(0.0f, 0.0f);
        this.level = 0;
        this.isNewLevel = false;
		this.music = Assets.getInstance().getAssetManager().get("audio/Music.mp3");
        this.music.setLooping(true);
        this.music.play();
    }

    public void loseCheck(){
        if(!hero.isAlive()){
            ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.OVER);
        }
    }

    public void update(float dt) {
        background.update(dt);
        hero.update(dt);
        enemyHeroController.update(dt);
        asteroidController.update(dt);
        enemyBulletController.update(dt);
        bulletController.update(dt);
        particleController.update(dt);
        powerUpsController.update(dt);
        loseCheck();
        showLevelTimer(dt);
        gameLevelUpdate();
        checkCollisions();
        powerUpsGetter();
        enemyScanner(dt);
        stage.act(dt);
    }

    public void updatePause(float dt) {
        background.update(dt);
        stage.act(dt);
    }

    public void showLevelTimer(float dt){
        showLevel+=dt;
        if (showLevel > 1.5f) {
            showLevel = 0.0f;
            isNewLevel=false;
        }
    }

    public void gameLevelUpdate(){
        if(asteroidController.getActiveList().size()==0 && enemyHeroController.getActiveList().size()==0){
            isNewLevel=true;
            level++;
            for (int i = 0; i < 2; i++) {
                this.asteroidController.setup(MathUtils.random(0, ScreenManager.SCREEN_WIDTH), MathUtils.random(0, ScreenManager.SCREEN_HEIGHT),
                        MathUtils.random(-150.0f, 150.0f), MathUtils.random(-150.0f, 150.0f), 1.0f);
            }
            enemyHeroSpawner(level);
        }
    }

    public void enemyHeroSpawner(int count){
        for (int i = 0; i < count; i++) {
            this.enemyHeroController.setup(MathUtils.random(0, ScreenManager.SCREEN_WIDTH)-32, MathUtils.random(0, ScreenManager.SCREEN_HEIGHT)-32,0,0, 50);
        }
    }

    public void powerUpsGetter(){
        for (int i = 0; i < powerUpsController.getActiveList().size(); i++) {
            PowerUp p = powerUpsController.getActiveList().get(i);
            hero.getHitArea().radius = hero.getHitArea().radius*8;
            if(hero.getHitArea().contains(p.getPosition())){
                tmpVec.set(hero.getPosition()).sub(p.getPosition()).nor();
                p.getVelocity().mulAdd(tmpVec, 50.0f);
            }
            hero.getHitArea().radius = hero.getHitArea().radius/8;
        }
    }

    public void enemyScanner(float dt){
        for (int i = 0; i < enemyHeroController.getActiveList().size(); i++) {
            EnemyHero e = enemyHeroController.getActiveList().get(i);

            hero.getHitArea().radius = hero.getHitArea().radius*300;
            if(hero.getHitArea().contains(e.getPosition()) && hero.getPosition().dst(e.getPosition())>250){
                tmpVec.set(hero.getPosition()).sub(e.getPosition()).nor();
                e.getVelocity().mulAdd(tmpVec, 25.0f);
            }
            hero.getHitArea().radius = hero.getHitArea().radius/300;

            tmpVec.set(e.getPosition().x-32,e.getPosition().y -32).sub(hero.getPosition().x-32, hero.getPosition().y -32).nor();
            e.targeting(dt, tmpVec.angle());

        }

        for (int i = 0; i < enemyBulletController.getActiveList().size(); i++) {
            Bullet eb = enemyBulletController.getActiveList().get(i);
            if (hero.getHitArea().contains(eb.getPosition())) {

                particleController.setup(
                        eb.getPosition().x + MathUtils.random(-4, 4), eb.getPosition().y + MathUtils.random(-4, 4),
                        eb.getVelocity().x * -0.3f + MathUtils.random(-30, 30), eb.getVelocity().y * -0.3f + MathUtils.random(-30, 30),
                        0.2f,
                        2.2f, 1.7f,
                        1.0f, 1.0f, 1.0f, 1.0f,
                        0.0f, 0.0f, 1.0f, 0.0f
                );

                eb.deactivate();
                hero.takeDamage(1);
                break;
            }
        }


    }

    public void checkCollisions() {
        for (int i = 0; i < asteroidController.getActiveList().size(); i++) {
            Asteroid a = asteroidController.getActiveList().get(i);
            if (a.getHitArea().overlaps(hero.getHitArea())) {
                float dst = a.getPosition().dst(hero.getPosition());
                float halfOverLen = (a.getHitArea().radius + hero.getHitArea().radius - dst) / 2.0f;
                tmpVec.set(hero.getPosition()).sub(a.getPosition()).nor();
                hero.getPosition().mulAdd(tmpVec, halfOverLen);
                a.getPosition().mulAdd(tmpVec, -halfOverLen);

                float sumScl = hero.getHitArea().radius * 2 + a.getHitArea().radius;

                hero.getVelocity().mulAdd(tmpVec, 400.0f * halfOverLen * a.getHitArea().radius / sumScl);
                a.getVelocity().mulAdd(tmpVec, 400.0f * -halfOverLen * hero.getHitArea().radius / sumScl);

                if (a.takeDamage(2)) {
                    hero.addScore(a.getHpMax() * 10);
                }
                hero.takeDamage(2);
            }
        }

        for (int i = 0; i < bulletController.getActiveList().size(); i++) {
            Bullet b = bulletController.getActiveList().get(i);
            for (int j = 0; j < enemyHeroController.getActiveList().size(); j++) {
                EnemyHero e = enemyHeroController.getActiveList().get(j);
                if(e.getHitArea().contains(b.getPosition())){
                    particleController.setup(
                            b.getPosition().x + MathUtils.random(-4, 4), b.getPosition().y + MathUtils.random(-4, 4),
                            b.getVelocity().x * -0.3f + MathUtils.random(-30, 30), b.getVelocity().y * -0.3f + MathUtils.random(-30, 30),
                            0.2f,
                            2.2f, 1.7f,
                            1.0f, 1.0f, 1.0f, 1.0f,
                            0.0f, 0.0f, 1.0f, 0.0f
                    );

                    b.deactivate();
                    e.takeDamage(1);
                    hero.addScore(150);
                    if(e.getHp()<0){
                        e.deactivate();
                    }
                }
            }
            for (int j = 0; j < asteroidController.getActiveList().size(); j++) {
                Asteroid a = asteroidController.getActiveList().get(j);

                if (a.getHitArea().contains(b.getPosition())) {

                    particleController.setup(
                            b.getPosition().x + MathUtils.random(-4, 4), b.getPosition().y + MathUtils.random(-4, 4),
                            b.getVelocity().x * -0.3f + MathUtils.random(-30, 30), b.getVelocity().y * -0.3f + MathUtils.random(-30, 30),
                            0.2f,
                            2.2f, 1.7f,
                            1.0f, 1.0f, 1.0f, 1.0f,
                            0.0f, 0.0f, 1.0f, 0.0f
                    );

                    b.deactivate();
                    if (a.takeDamage(1)) {
                        hero.addScore(a.getHpMax() * 100);
                        for (int k = 0; k < 3; k++) {
                            powerUpsController.setup(a.getPosition().x, a.getPosition().y, a.getScale() / 4.0f);
                        }
                    }
                    break;
                }
            }
        }

        for (int i = 0; i < powerUpsController.getActiveList().size(); i++) {
            PowerUp p = powerUpsController.getActiveList().get(i);
            if (hero.getHitArea().contains(p.getPosition())) {
                hero.consume(p);
                particleController.getEffectBuilder().takePowerUpEffect(p.getPosition().x, p.getPosition().y, p.getType().index);
                p.deactivate();
            }
        }
    }

    public void dispose() {
        background.dispose();
    }
}