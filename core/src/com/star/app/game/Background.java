package com.star.app.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.star.app.StarGame;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;

public class Background {
    private class Star {
        private Vector2 position;
        private Vector2 velocity;
        private float scale;

        public Star() {
            this.position = new Vector2(MathUtils.random(-200, ScreenManager.SCREEN_WIDTH + 200), MathUtils.random(-200, ScreenManager.SCREEN_HEIGHT + 200));
            this.velocity = new Vector2(MathUtils.random(-15, -1), 0);
            this.scale = Math.abs(velocity.x) / 15.0f * 0.7f;
        }

        public void update(float dt) {
            if(gc != null) {
                position.x += (velocity.x - gc.getHero().getVelocity().x / 4.0f) * dt;
                position.y += (velocity.y - gc.getHero().getVelocity().y / 4.0f) * dt;
            } else {
                position.mulAdd(velocity, dt);
            }
            if (position.x < -200) {
                position.x = ScreenManager.SCREEN_WIDTH + 20;
                position.y = MathUtils.random(-200, ScreenManager.SCREEN_HEIGHT + 200);
                velocity.x = MathUtils.random(-15, -1);
                scale = Math.abs(velocity.x) / 15.0f * 0.7f;
            }
            if (position.y < -200) {
                position.y = ScreenManager.SCREEN_HEIGHT + 200;
            }
            if (position.y > ScreenManager.SCREEN_HEIGHT + 200) {
                position.y = -200;
            }
            if (position.x > ScreenManager.SCREEN_WIDTH + 200) {
                position.x = -200;
            }
        }
    }

    private final int STARS_COUNT = 600;
    private GameController gc;
    private Texture textureCosmos;
    private TextureRegion textureStar;
    private Star[] stars;

    public Background(GameController gc) {
        this.gc = gc;
        this.textureCosmos = new Texture("images/back1920.jpg");
        this.textureStar = Assets.getInstance().getAtlas().findRegion("star16");
        this.stars = new Star[STARS_COUNT];
        for (int i = 0; i < stars.length; i++) {
            stars[i] = new Star();
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(textureCosmos, 0, 0);
        for (int i = 0; i < stars.length; i++) {
            batch.draw(textureStar, stars[i].position.x - 8, stars[i].position.y - 8, 8, 8, 16, 16, stars[i].scale, stars[i].scale, 0);
            if (MathUtils.random(0, 500) < 2) {
                batch.draw(textureStar, stars[i].position.x - 8, stars[i].position.y - 8, 8, 8, 16, 16, stars[i].scale * 2, stars[i].scale * 2, 0);
            }
        }
    }

    public void update(float dt) {
        for (int i = 0; i < stars.length; i++) {
            stars[i].update(dt);
        }
    }

    public void dispose() {
        textureCosmos.dispose();
    }
}
