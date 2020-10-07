package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;
import com.star.app.screen.utils.OptionsUtils;

public class Bot extends Ship {
    private Vector2 dst;
    private float visionRadius;

    public Bot(GameController gc) {
        super(gc, 50);
        this.texture = Assets.getInstance().getAtlas().findRegion("ship");
        this.changePosition(2640, 1360);
        this.enginePower = 800.0f;
        this.ownerType = OwnerType.BOT;
        this.currentWeapon = new Weapon(
                gc, this, "GreenLaser", 0.3f, 1, 3, 320.0f, 500.0f, -1,
                new Vector3[]{new Vector3(24, 0, 0)});
        this.dst = new Vector2(MathUtils.random(0, GameController.SPACE_WIDTH), MathUtils.random(0, GameController.SPACE_HEIGHT));
        this.visionRadius = 1000.0f;
    }

    public void update(float dt) {
        super.update(dt);
        tmpVector.set(dst).sub(position).nor();
        if (position.dst(gc.getHero().getPosition()) < visionRadius) {
            tmpVector.set(gc.getHero().getPosition()).sub(position).nor();
        }
        if (position.dst(dst) < 100.0f) {
            dst.set(MathUtils.random(0, GameController.SPACE_WIDTH), MathUtils.random(0, GameController.SPACE_HEIGHT));
        }

        angle = tmpVector.angle();
        if (gc.getHero().getPosition().dst(position) > currentWeapon.getRadius() * 0.8f) {
            accelerate(dt);
        }
        if (gc.getHero().getPosition().dst(position) < currentWeapon.getRadius()) {
            currentWeapon.tryToFire();
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
                        1.0f, 0.0f, 0.0f, 1.4f,
                        1.0f, 0.0f, 0.0f, 0.0f
                );
            }
        }
    }
}
