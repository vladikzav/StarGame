package com.star.app.screen.utils;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.ui.Label;

public class TextInputListener implements Input.TextInputListener {
    private Label label;

    public TextInputListener(Label targetLabel) {
        label = targetLabel;
    }

    @Override
    public void input(String text) {
        char stoper;
        if(text.equals(" ")||text.equals("SPACE")||text.equals("Space")){
            text = Input.Keys.toString(62);
        }else if(text.length()>1 || Input.Keys.valueOf(text)<0) {
            stoper = text.charAt(0);
            text = String.valueOf(stoper);
            text = text.toUpperCase();
        }
        label.setText(text);
        System.out.println(text);
        System.out.println(Input.Keys.valueOf(text));
    }

    @Override
    public void canceled() {
        label.setText("!!!");
    }

}