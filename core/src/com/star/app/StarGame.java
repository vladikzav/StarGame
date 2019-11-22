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
    // 2. Релизовать столкновение корабля с астероидами
    // 2. а. Просто при столкновении астероид рассыпается, а корабль получает урон
    // * 2. б. Они должна оттолкнуться друг от друга
    // 3. Добавить игроку поле Здоровье, отобразить его на экране

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
