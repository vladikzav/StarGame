package com.star.app.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.star.app.game.helpers.Poolable;
import com.star.app.screen.ScreenManager;
import com.star.app.screen.utils.Assets;

public class PowerUp implements Poolable {
    private GameController gc;
    private TextureRegion texture;
    private Vector2 position;
    private Vector2 velocity;
    private float scale;
    private boolean active;
    private Circle hitArea;
    private int type;

    private final float BASE_SIZE = 64.0f;
    private final float BASE_RADIUS = BASE_SIZE / 2.0f;

    public int getType() {
        return type;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Circle getHitArea() {
        return hitArea;
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

    public PowerUp(GameController gc) {
        this.gc = gc;
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.hitArea = new Circle(0, 0, 0);
        this.active = false;
        this.scale = 0.5f;
    }


    public void activate(float x, float y, float vx, float vy, int type) {
        this.type = type;
        this.position.set(x, y);
        this.velocity.set(vx, vy);
        this.hitArea.setPosition(position);
        this.active = true;
        this.hitArea.setRadius(BASE_RADIUS * scale * 0.9f);
        if(type == 0) {
            this.texture = Assets.getInstance().getAtlas().findRegion("hp");
        }else if(type == 1){
            this.texture = Assets.getInstance().getAtlas().findRegion("ammo");
        }
    }

    public void render(SpriteBatch batch) {
        if(type == 1) {
            batch.setBlendFunction(GL20.GL_ONE_MINUS_SRC_ALPHA, GL20.GL_ONE);
            batch.setColor(1.0f, 1.0f, 0.0f, 1.0f);
            batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, scale, scale, 0);
            batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
            batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        }else
            batch.draw(texture, position.x - 32, position.y - 32, 32, 32, 64, 64, scale, scale, 0);
    }


    public void update(float dt) {
        position.mulAdd(velocity, dt);
        if (position.x < -BASE_RADIUS * scale) {
            position.x = ScreenManager.SCREEN_WIDTH + BASE_RADIUS * scale;
        }
        if (position.x > ScreenManager.SCREEN_WIDTH + BASE_RADIUS * scale) {
            position.x = -BASE_RADIUS * scale;
        }
        if (position.y < -BASE_RADIUS * scale) {
            position.y = ScreenManager.SCREEN_HEIGHT + BASE_RADIUS * scale;
        }
        if (position.y > ScreenManager.SCREEN_HEIGHT + BASE_RADIUS * scale) {
            position.y = -BASE_RADIUS * scale;
        }
        hitArea.setPosition(position);
    }


}
