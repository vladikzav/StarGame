package com.star.app.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.star.app.screen.utils.Assets;

public class Shop extends Group {
    private Hero hero;
    private BitmapFont font24;

    public Shop(final Hero hero) {
        this.hero = hero;
        this.font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");

        Pixmap pixmap = new Pixmap(400, 400, Pixmap.Format.RGB888);
        pixmap.setColor(Color.rgb888(0.0f, 0.0f, 0.4f));
        pixmap.fill();

        Image image = new Image(new Texture(pixmap));

        Skin skin = new Skin();
        skin.addRegions(Assets.getInstance().getAtlas());

        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.up = skin.getDrawable("shortButton");
        textButtonStyle.font = font24;
        skin.add("simpleSkin", textButtonStyle);

        final Image[][] lamps = new Image[hero.getSkills().length][];
        this.addActor(image);

        for (int i = 0; i < lamps.length; i++) {
            lamps[i] = new Image[hero.getSkills()[i].getMaxLevel()];
            for (int j = 0; j < lamps[i].length; j++) {
                Image img = new Image(skin.getDrawable("star16"));
                img.setColor(1, 0, 0, 1);
                img.setPosition(140 + j * 20, 358 - i * 60);
                img.setScale(3.0f);
                lamps[i][j] = img;
                this.addActor(img);
            }
        }

        TextButton[] btnSkills = new TextButton[hero.getSkills().length];
        for (int i = 0; i < btnSkills.length; i++) {
            final int skillIndex = i;
            final TextButton skillBtn = new TextButton(hero.getSkills()[i].getTitle(), textButtonStyle);
            skillBtn.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (hero.getSkills()[skillIndex].isUpgradable()) {
                        int cost = hero.getSkills()[skillIndex].getCurrentLevelCost();
                        if (hero.isMoneyEnough(cost)) {
                            hero.decreaseMoney(cost);
                            hero.getSkills()[skillIndex].upgrade();
                            lamps[skillIndex][hero.getSkills()[skillIndex].getLevel() - 2].setColor(0, 1, 0, 1);
                        }
                    }
                }
            });
            skillBtn.setPosition(20, 300 - 60 * i);
            this.addActor(skillBtn);
        }

        final TextButton btnClose = new TextButton("X", textButtonStyle);
        final Shop thisShop = this;

        btnClose.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                thisShop.setVisible(false);
            }
        });
        btnClose.setTransform(true);
        btnClose.setScale(0.5f);
        btnClose.setPosition(340, 340);

        this.addActor(btnClose);
        this.setPosition(20, 300);
        this.setVisible(false);

        skin.dispose();
    }
}
