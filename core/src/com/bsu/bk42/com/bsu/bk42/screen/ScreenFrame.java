package com.bsu.bk42.com.bsu.bk42.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/**
 * 应用框架类，用来展现每个应用的上下边框装饰
 * Created by fengchong on 16/3/24.
 */
public class ScreenFrame {
    private static ScreenFrame instance = null;
    public static ScreenFrame getInstance(){
        if(instance==null)
            instance = new ScreenFrame();
        return instance;
    }

    private Texture tx_bg_top,tx_bg_bottom = null;
    private Image img_bg_top,img_bg_bottom = null;
    private ScreenFrame(){
        tx_bg_top = new Texture(Gdx.files.internal("bg-1.jpg"));
        tx_bg_bottom = new Texture(Gdx.files.internal("bg-2.jpg"));
        img_bg_top = new Image(tx_bg_top);
        img_bg_bottom = new Image(tx_bg_bottom);
    }

    /**
     * 获得上边框的Image对象
     * @return
     */
    public Image getImgTop() {
        return img_bg_top;
    }

    /**
     * 获得下边框的Image对象
     * @return
     */
    public Image getImgBottom() {
        return img_bg_bottom;
    }
}
