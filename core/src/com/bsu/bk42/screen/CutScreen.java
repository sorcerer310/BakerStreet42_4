package com.bsu.bk42.screen;

import aurelienribon.tweenengine.BaseTween;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenCallback;
import aurelienribon.tweenengine.equations.Back;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bsu.bk42.ScreenParams;
import com.bsu.bk42.tools.PlcCommHelper;
import com.ugame.gdx.tools.UGameScreen;
import com.ugame.gdx.tween.accessor.ActorAccessor;

/**
 * 刀伤界面，展示一张淳于导的图，身上有长短不一的刀伤，根据刀伤数分析出谜题的答案
 * Created by fengchong on 16/3/9.
 */
public class CutScreen extends UGameScreen implements IPlcCommandListener{
    private Texture tx_role = null;
    private Image img_role = null;

    private Group g_role = new Group();
    private Group g_root = new Group();

    private Timeline tl_appear,tl_dead,tl_appearDelay;
    private Sound s_appear,s_dead;

    private StateMachine stateMachine;
    public CutScreen(){
        this.stage = new Stage(new StretchViewport(720,1280));
        ScreenParams.initScreenParams(720,1280);
        this.setFPS(40);

        stateMachine = new DefaultStateMachine<CutScreen,CutScreenState>(this,CutScreenState.GAME_READY);

        s_appear = Gdx.audio.newSound(Gdx.files.internal("cut/appear.ogg"));
        s_dead = Gdx.audio.newSound(Gdx.files.internal("cut/dead.ogg"));
        Tween.registerAccessor(Group.class,new ActorAccessor());
        //初始化布局
        initLayout();
        //初始化角色
        initRole();

//        roleAppear();
//        roleDead();
    }

    /**
     * 初始化角色
     */
    private void initRole(){
        //初始化刀伤
        tx_role = new Texture(Gdx.files.internal("cut/role.png"));
        img_role = new Image(tx_role);



        //增加人物，初始化刀伤
        g_role.addActor(img_role);
        g_role.setSize(img_role.getWidth(),img_role.getHeight());
        Array<Image> cuts = initCut();
        for(Image cut:cuts){
            g_role.addActor(cut);
        }

        g_role.setOrigin(Align.center);
        g_role.setPosition(g_root.getWidth()/2,g_role.getHeight()/2,Align.center);
//        g_role.setScale(1.0f);
        g_role.setScale(.0f);

        g_root.addActor(g_role);
        //定位坐标测试代码
//        g_role.addListener(new InputListener() {
//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                System.out.println("x:" + x + " y:" + y);
//                return super.touchDown(event, x, y, pointer, button);
//            }
//        });
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
//        cuts.add(CutFactory.getInstance().mediumCut(359,278,0));

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

        g_root.setSize(bg.getWidth(),bg.getHeight());
        g_root.addActor(bg);

        root.add(top).height(top.getHeight()).width(top.getWidth()).row();
        root.add(g_root).height(bg.getHeight()).width(bg.getWidth()).row();
        root.add(bottom).height(bottom.getHeight()).width(bottom.getWidth()).row();

        root.setPosition(360,640);
//        root.setPosition(0,0, Align.center);
        stage.addActor(root);
    }

    /**
     * 人物出场
     */
    public void roleAppear(){
        tl_appear = Timeline.createParallel()
                .push(
                        Tween.to(g_role, ActorAccessor.SCALE_XY, .5f).target(1.0f,1.0f)
                            .ease(Back.IN)
                )
                .push(
                        Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                s_appear.play();
                            }
                        })
                )
                .start();
    }

    /**
     * 人物死亡
     */
    public void roleDead(){
        tl_dead = Timeline.createParallel()
                .push(
                        Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                s_dead.play();
                            }
                        })
                )
                .push(
                        Timeline.createSequence()
                            .pushPause(3.8f)
                            .push(Timeline.createSequence()
                                    .push(Tween.to(g_role,ActorAccessor.POS_XY,.03f).target(g_role.getX()-5,g_role.getY()))
                                    .push(Tween.to(g_role,ActorAccessor.POS_XY,.03f).target(g_role.getX()+5,g_role.getY()))
                                    .repeat(10,.0f)
                            )
                            .push(
                                    Tween.to(g_role, ActorAccessor.OPACITY,1.0f).target(.0f)
                            )
                                //10秒后启动锁链功能
                                .pushPause(10)
                            .push(Tween.call(new TweenCallback() {
                                @Override
                                public void onEvent(int type, BaseTween<?> source) {
                                    //向服务器发送chain命令，启动锁链界面
                                    PlcCommHelper.getInstance().simpleGet("/notification.do?action=send&broadcast=A&username=&title=title&message=message&uri=chain%3A0");
                                }
                            }))
                )
                .start();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if(tl_appear!=null)
            tl_appear.update(delta);
        if(tl_dead!=null)
            tl_dead.update(delta);
        if(tl_appearDelay!=null)
            tl_appearDelay.update(delta);
        stateMachine.update();
    }

    @Override
    public void receivePlcCommand(int cmdi) {
        if(cmdi==0){
            //延时1分钟后触发游戏开始
            tl_appearDelay = Timeline.createSequence()
                    .pushPause(60)
                    .push(Tween.call(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            //游戏开始
                            stateMachine.changeState(CutScreenState.GAME_RUNNING);
                        }
                    })).start();

        }else if(cmdi==1){
            //游戏结束
            stateMachine.changeState(CutScreenState.GAME_OVER);
        }
    }

    /**
     * 获得状态机对象
     * @return
     */
    public StateMachine getStateMachine() {
        return stateMachine;
    }

    public static enum CutScreenState implements State<CutScreen> {
        GAME_READY{
            @Override
            public void enter(CutScreen entity) {
                //TODO:似乎是不用重设状态，待测试
            }
            @Override
            public void update(CutScreen entity) {}
            @Override
            public void exit(CutScreen entity) {}
            @Override
            public boolean onMessage(CutScreen entity, Telegram telegram) {return false;}
        },
        GAME_RUNNING{
            @Override
            public void enter(CutScreen entity) {
                //人物出场
                entity.roleAppear();
            }
            @Override
            public void update(CutScreen entity) {}
            @Override
            public void exit(CutScreen entity) {}
            @Override
            public boolean onMessage(CutScreen entity, Telegram telegram) {return false;}
        },
        GAME_OVER{
            @Override
            public void enter(CutScreen entity) {
                //人物死亡
                entity.roleDead();
            }
            @Override
            public void update(CutScreen entity) {}
            @Override
            public void exit(CutScreen entity) {}
            @Override
            public boolean onMessage(CutScreen entity, Telegram telegram) {return false;}
        }};
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
