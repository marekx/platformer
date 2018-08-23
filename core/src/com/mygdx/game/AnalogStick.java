package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;


public class AnalogStick extends Touchpad {

    private static Touchpad.TouchpadStyle touchpadStyle;
    private static Skin touchpadSkin;
    private static Drawable touchBackground;
    private static Drawable touchKnob;

    public AnalogStick(float x, float y) {
        super(10, getTouchpadStyle());
        setBounds(15, 15, 250, 250);
        setPosition(x,y);

    }

    private static Touchpad.TouchpadStyle getTouchpadStyle() {

        touchpadSkin = new Skin();
        touchpadSkin.add("touchBackground", new Texture("data/touchBackground.png"));

        touchpadSkin.add("touchKnob", new Texture("data/touchKnob.png"));

        touchpadStyle = new Touchpad.TouchpadStyle();

        touchBackground = touchpadSkin.getDrawable("touchBackground");
        touchKnob = touchpadSkin.getDrawable("touchKnob");

        touchpadStyle.background = touchBackground;
        touchpadStyle.knob = touchKnob;

        return touchpadStyle;
    }
}
