package com.bsu.bk42.com.bsu.bk42.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bsu.bk42.ScreenParams;
import com.ugame.gdx.tools.UGameScreen;

/**
 * 刀伤界面，展示一张淳于导的图，身上有长短不一的刀伤，根据刀伤数分析出谜题的答案
 * Created by fengchong on 16/3/9.
 */
public class CutScreen extends UGameScreen{
    private Texture tx_role = null;
    private Image img_role = null;

    private Group group = new Group();

    public CutScreen(){
        this.stage = new Stage(new StretchViewport(ScreenParams.screenWidth,ScreenParams.screenHeight));
        this.setFPS(40);
        //初始化角色
        tx_role = new Texture(Gdx.files.internal("cut/role.png"));
        img_role = new Image(tx_role);

        //增加人物，初始化刀伤
        group.addActor(img_role);
        Array<Image> cuts = initCut();
        for(Image cut:cuts){
            group.addActor(cut);
        }
        group.setPosition(0,125);

        initLayout();

        stage.addActor(group);


        //定位坐标测试代码
        group.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("x:"+ x+" y:"+y);
                return super.touchDown(event, x, y, pointer, button);
            }
        });
        Gdx.input.setInputProcessor(stage);

    }

    /**
     * 初始化刀伤
     */
    private Array<Image> initCut(){
        Array<Image> cuts = new Array<Image>();
        cuts.add(CutFactory.getInstance().bigCut(326,454,-20));
        cuts.add(CutFactory.getInstance().bigCut(507,430,30));

        cuts.add(CutFactory.getInstance().mediumCut(397,570,13));
        cuts.add(CutFactory.getInstance().mediumCut(340,336,-55));
        cuts.add(CutFactory.getInstance().mediumCut(460,481,0));
        cuts.add(CutFactory.getInstance().mediumCut(558,517,20));
        cuts.add(CutFactory.getInstance().mediumCut(359,278,0));

        cuts.add(CutFactory.getInstance().smallCut(454, 836,0));
        cuts.add(CutFactory.getInstance().smallCut(261, 610,-30));
        cuts.add(CutFactory.getInstance().smallCut(383, 722,20));
        cuts.add(CutFactory.getInstance().smallCut(426, 202,-80));
        cuts.add(CutFactory.getInstance().smallCut(530, 481,30));
        cuts.add(CutFactory.getInstance().smallCut(425, 205,0));
        cuts.add(CutFactory.getInstance().smallCut(438, 282,45));

        return cuts;
    }

    /**
     * 初始化布局
     */
    private void initLayout(){
        Table root = new Table();
        Image top = ScreenFrame.getInstance().getImgTop();
        Image bg = ScreenFrame.getInstance().getImgBg();
        Image bottom = ScreenFrame.getInstance().getImgBottom();

        root.add(top).height(top.getHeight()).width(top.getWidth()).row();
        root.add(bg).height(bg.getHeight()).width(bg.getWidth()).row();
        root.add(bottom).height(bottom.getHeight()).width(top.getWidth()).row();

        root.setPosition(360,640);
//        root.setPosition(0,0, Align.center);
        stage.addActor(root);
    }
}

class CutFactory{
    private static CutFactory instance = null;
    public static CutFactory getInstance(){
        if(instance==null)
            instance = new CutFactory();
        return instance;
    }

    private CutFactory(){
        tx = new Texture(Gdx.files.internal("cut/cut.png"));
    }

    private static Texture tx = null;

    /**
     * 返回小伤口
     * @return
     */
    public Image smallCut(float x,float y,int degrees){
        Image cut = new Image(tx);
        cut.setPosition(x,y,Align.center);
//        cut.setScale(.4f);
        cut.setScaleY(.5f);
        cut.setScaleX(.3f);
        cut.setOrigin(Align.center);
        cut.setRotation(degrees);
        return cut;
    }

    /**
     * 返回中等伤口
     * @return
     */
    public Image mediumCut(float x,float y,int degrees){
        Image cut = new Image(tx);
        cut.setPosition(x,y,Align.center);
        cut.setScale(.75f);
        cut.setOrigin(Align.center);
        cut.setRotation(degrees);
        return cut;
    }

    /**
     * 返回大伤口
     * @return
     */
    public Image bigCut(float x,float y,int degrees){
        Image cut = new Image(tx);
        cut.setPosition(x,y,Align.center);
        cut.setScale(1.25f);
        cut.setOrigin(Align.center);
        cut.setRotation(degrees);
        return cut;
    }

}
