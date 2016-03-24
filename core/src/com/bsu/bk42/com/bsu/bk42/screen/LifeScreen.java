package com.bsu.bk42.com.bsu.bk42.screen;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bsu.bk42.ScreenParams;
import com.ugame.gdx.tools.UGameScreen;
import com.ugame.gdx.tween.accessor.ActorAccessor;

/**
 * 七星续命灯场景，展示星空7盏灯，每过一段时间灯会熄灭一盏
 * Created by fengchong on 16/3/7.
 */
public class LifeScreen extends UGameScreen{
    private Texture tx_bg = null;
    private Texture tx_star = null;

    private Actor bg = null;
    private Array<Star> stars = new Array<Star>();

    private Group group = new Group();

    public LifeScreen(){
        this.stage = new Stage(new StretchViewport(ScreenParams.screenWidth,ScreenParams.screenHeight));
        this.setFPS(40);

        tx_bg = new Texture(Gdx.files.internal("life/bg_life.jpg"));
        tx_star = new Texture(Gdx.files.internal("life/life.png"));

        //屏幕触摸调试代码
        group.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("x:" + x + " y:" + y);
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        Gdx.input.setInputProcessor(stage);

        initActor();
        initLayout();

    }

    /**
     * 初始化所有的Actor
     */
    private void initActor(){
        //初始化背景actor
        bg = new Actor(){
            {
                this.setSize(tx_bg.getWidth(),tx_bg.getHeight());
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
                if(tx_bg!=null)
                    batch.draw(tx_bg,this.getX(),this.getY());
            }
        };

        //初始化星星Actor
        for(int i=0;i<7;i++){
            stars.add(new Star(tx_star));
        }

        Star star = stars.get(6);

        stars.get(0).setPosition(697,373,Align.center);
        stars.get(1).setPosition(502,431,Align.center);
        stars.get(2).setPosition(424,549,Align.center);
        stars.get(3).setPosition(319,685,Align.center);
        stars.get(4).setPosition(344,841,Align.center);
        stars.get(5).setPosition(135,956,Align.center);
        stars.get(6).setPosition(37,812,Align.center);

        group.addActor(bg);
        for(Star actor:stars) {
            group.addActor(actor);
        }
    }

    /**
     * 初始化布局部分把游戏上下边框加入
     */
    private void initLayout(){
        Table root = new Table();
        Image top = ScreenFrame.getInstance().getImgTop();
        Image bottom = ScreenFrame.getInstance().getImgBottom();
        root.add(top).height(top.getHeight()).width(top.getWidth()).row();
        root.add(group).width(ScreenParams.screenWidth).height(bg.getHeight()).row();
        root.add(bottom).height(bottom.getHeight()).expandX().row();

        root.setPosition(360,640);
        stage.addActor(root);
    }

    /**
     * 设置显示剩余的生命
     * @param lc    剩余生命的数量
     */
    public void setLeftLife(int lc){
        for(int i=0;i<stars.size-lc;i++)
            stars.get(i).disappear();
    }

}

/**
 * 星星类
 */
class Star extends Image implements Disposable {
    private Texture tx = null;
    private Timeline tl_idle = null;                                    //待机动画
    private Tween tl_dis,tl_app = null;                                 //消失和出现动画
    public Star(Texture ptx){
        super(ptx);
        tx = ptx;
        this.setSize(tx.getWidth(), tx.getHeight());
        this.setOrigin(Align.center);
        Tween.registerAccessor(Image.class,new ActorAccessor());
        initTween();
    }

    /**
     * 初始化动画
     */
    private void initTween(){
        this.setScale(1.5f);
        Color color = this.getColor();
        this.setColor(color.r,color.g,color.b,.0f);

        float delay = MathUtils.random(.5f,.9f);
        float minval = MathUtils.random(.4f,.6f);
//        minval = 1.0f;
        this.setColor(color.r,color.g,color.b,1.0f);

        tl_idle = Timeline.createSequence()
                .push(
                        Timeline.createParallel()
                                .push(Tween.to(this, ActorAccessor.OPACITY, delay).target(minval)
                                                .ease(TweenEquations.easeNone)
                                )
                                .push(Tween.to(this, ActorAccessor.SCALE_XY, delay).target(minval, minval)
                                                .ease(TweenEquations.easeNone)
                                )
                )
                .push(
                        Timeline.createParallel()
                                .push(Tween.to(this, ActorAccessor.OPACITY, delay).target(1.0f)
                                                .ease(TweenEquations.easeNone)
                                )
                                .push(Tween.to(this, ActorAccessor.SCALE_XY, delay).target(1.5f, 1.5f)
                                                .ease(TweenEquations.easeNone)
                                )
                ).repeat(-1,0).start();
        tl_dis = Tween.to(this,ActorAccessor.OPACITY,.6f).target(.0f)
                .ease(TweenEquations.easeNone).setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        System.out.println("tl_dis complete");
                    }
                });
        tl_app = Tween.to(this,ActorAccessor.OPACITY,.6f).target(1.5f)
                .ease(TweenEquations.easeNone).setCallback(new TweenCallback() {
                    @Override
                    public void onEvent(int i, BaseTween<?> baseTween) {
                        System.out.println("tl_app complete");
                    }
                });

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(tl_idle!=null)
            tl_idle.update(delta);
        if(tl_dis!=null)
            tl_dis.update(delta);
        if(tl_app!=null)
            tl_app.update(delta);
    }

    /**
     * 星星消失函数
     */
    public void disappear(){
        tl_dis.start();
    }

    /**
     * 星星出现函数
     */
    public void appear(){
        tl_app.start();
    }

    @Override
    public void dispose() {
        if(tx!=null)
            tx.dispose();
    }
}
