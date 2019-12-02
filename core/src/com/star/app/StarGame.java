package com.star.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.star.app.screen.GameScreen;
import com.star.app.screen.ScreenManager;

public class StarGame extends Game {
    private SpriteBatch batch;

    // Домашнее задание:
    // 1. Разбор кода
    // 2. Кнопка паузы и выхода в меню на игровом экране
    // 3. Сделайте Game Over Screen. Если у корабля < 0 HP, то игра
    // переходит на Game Over Screen, и отображает там статистику игры
    // (допустим пока только счет). По нажатию на экране Game Over Screen'а
    // возвращаемся в меню
    // 4. * Настройки со сменой управления

    @Override
    public void create() {
        batch = new SpriteBatch();
        ScreenManager.getInstance().init(this, batch);
        ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.MENU);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float dt = Gdx.graphics.getDeltaTime();
        getScreen().render(dt);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
