package com.star.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.screen.GameScreen;

public class StarGame extends Game {
    private SpriteBatch batch;
    private GameScreen gameScreen;

    // Домашнее задание:
    // 1. Разобраться с кодом
    // 2. Заворачивайте ваши астероиды в контроллер
    // 3. * Сделайте уничтожение астероидов с одного попадания

	@Override
    public void create() {
        batch = new SpriteBatch();
        gameScreen = new GameScreen(batch);
        setScreen(gameScreen);
    }

    @Override
    public void render() {
        float dt = Gdx.graphics.getDeltaTime();
        getScreen().render(dt);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
