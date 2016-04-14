package com.bsu.bk42.screen;

import aurelienribon.tweenengine.*;
import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.equations.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bsu.bk42.ScreenParams;
import com.bsu.bk42.knife.SwipeKnifeLight;
import com.bsu.bk42.knife.SwipeKnifeManager;
import com.bsu.bk42.tools.PlcCommHelper;
import com.ugame.gdx.tools.UGameScreen;
import com.ugame.gdx.tween.accessor.ActorAccessor;

import java.lang.reflect.InvocationTargetException;

/**
 * 锁链切割场景,手机上出现夏侯恩身背青釭剑，夺取青釭剑划断屏幕上的两根锁链
 * Created by fengchong on 16/3/3.
 */
public class ChainScreen extends UGameScreen implements IPlcCommandListener{
    private SwipeKnifeLight skl;                                            //刀光对象
    private boolean isDrawKnifeLight = false;                               //是否绘制刀光标识

    private Texture tx_xiahouen = new Texture("chain/chara.png");
    private Texture tx_sword = new Texture("chain/sword.png");

    private Sound s_appear;

    private RoleImage img_role = null;
    private SwordImage img_sword = null;

    private Group g_root = new Group();
    private ChainGroup g_chain;

    private Timeline tl_appear;

    private StateMachine stateMachine;

    private InputListener skllistener = null;
//    private Sound sound;
    public ChainScreen(){
        stage = new Stage(new StretchViewport(ScreenParams.screenWidth,ScreenParams.screenHeight));
        this.setFPS(40);

        stateMachine = new DefaultStateMachine<ChainScreen,ChainScreenState>(this,ChainScreenState.GAME_READY);

        s_appear = Gdx.audio.newSound(Gdx.files.internal("chain/appear.ogg"));

        initLayout();
        initChain();
        initRoleAndSword();
        initSwipe();

        g_root.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println(">>>>>>>>>>>>>>>>g_root");
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        g_role.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println(">>>>>>>>>>>>>>>>g_role");
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        g_chain.addListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println(">>>>>>>>>>>>>>>>g_chain");
                return super.touchDown(event, x, y, pointer, button);
            }
        });
    }

    /**
     * 游戏背景及边框
     */
    private void initLayout(){
        Image top = ScreenFrame.getInstance().getImgTop();
        Image bg = ScreenFrame.getInstance().getImgBg();
        Image bottom = ScreenFrame.getInstance().getImgBottom();

        g_root.setSize(bg.getWidth(),bg.getHeight());


        top.setPosition(0,ScreenParams.screenHeight-top.getHeight());
        bottom.setPosition(0,0);
        g_root.setPosition(0,bottom.getHeight());
        g_root.addActor(bg);

        stage.addActor(g_root);
        stage.addActor(top);
        stage.addActor(bottom);
    }

    /**
     * 初始化刀光
     */
    private void initSwipe(){
        skl = SwipeKnifeManager.getInstance().getNomalKnife(this.stage.getCamera());
        skllistener = new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event,x,y,pointer,button);
                skl.getSwipe().touchDown((int) (x * ScreenParams.scaleWidth), (int) ScreenParams.deviceY2drawY(y * ScreenParams.scaleHeight), pointer, button);
//                System.out.println("skl touch down");
                return true;
//                return super.touchDown(event,x,y,pointer,button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                skl.getSwipe().touchUp((int) (x * ScreenParams.scaleWidth), (int) ScreenParams.deviceY2drawY(y * ScreenParams.scaleHeight), pointer, button);
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                skl.getSwipe().touchDragged((int) (x * ScreenParams.scaleWidth), (int) ScreenParams.deviceY2drawY(y * ScreenParams.scaleHeight), pointer);
                g_chain.cutChains(x,y);
                super.touchDragged(event, x, y, pointer);
            }
        };

        stage.addCaptureListener(skllistener);
    }

    private Group g_role = new Group();
    /**
     * 初始化夏侯恩与剑
     */
    private void initRoleAndSword(){
        //定义人物Image
        img_role = new RoleImage(tx_xiahouen);
        img_role.setOnRoleDeadListener(new RoleImage.DeadCompletedListener() {
            @Override
            public void onRoleDead(RoleImage ri) {
//                g_root.addActor(g_chain);
                g_chain.dropAllChain();
            }
        });

        //定义宝剑Image
        img_sword = new SwordImage(tx_sword);
        //宝剑完成后，命令人物逃跑
        img_sword.setGetSwordListener(new SwordImage.GetSwordListener() {
            @Override
            public void onGetSword(SwordImage si) {
                //夏侯恩死亡
                img_role.dead();
                //让刀光开始生效
                ChainScreen.this.isDrawKnifeLight = true;
            }
        });
        g_role.clear();

        g_role.setSize(img_role.getWidth(), img_role.getHeight());
        img_sword.setPosition(g_role.getWidth()/2,-150);

        g_role.addActor(img_role);
        g_role.addActor(img_sword);

        //夏侯恩默认透明图
        Color gcolor = g_role.getColor();
        g_role.setColor(gcolor.r,gcolor.g,gcolor.b,.0f);
        g_root.addActor(g_role);

        //g_role调试代码
//        g_role.debugAll();

    }

    /**
     * 出现锁链
     */
    private void initChain(){
        g_chain = new ChainGroup(g_root.getWidth(),g_root.getHeight(),this);
//        g_chain.debugAll();
        g_root.addActor(g_chain);
    }

    /**
     * 夏侯恩出场
     */
    private void roleAppear(){
        tl_appear = Timeline.createParallel()
                        .push(
                                Tween.call(new TweenCallback() {
                                    @Override
                                    public void onEvent(int type, BaseTween<?> source) {
                                        s_appear.play();
                                    }
                                })
                        )
                        .push(
                                Timeline.createSequence()
                                        .push(
                                                Tween.to(g_role, ActorAccessor.OPACITY, 2.0f).target(1.0f)
                                                        .ease(TweenEquations.easeNone)
                                        )
                        ).start();
    }


    @Override
    public void render(float delta) {
        super.render(delta);
        if(skl!=null && isDrawKnifeLight) {
            skl.act(delta);
            skl.draw(stage.getBatch(), 1);
        }
        if(tl_appear!=null)
            tl_appear.update(delta);
        stateMachine.update();
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void receivePlcCommand(int cmdi) {
        stateMachine.changeState(ChainScreenState.GAME_RUNNING);
    }

    /**
     * 活的锁链场景的状态机
     * @return
     */
    public StateMachine getStateMachine() {
        return stateMachine;
    }

    public static enum ChainScreenState implements State<ChainScreen>{
        GAME_READY{
            @Override
            public void enter(ChainScreen entity) {
                //不绘制剑光
                entity.isDrawKnifeLight = false;
                //重设宝剑状态
                entity.img_sword.resetSword(entity.g_role);
                //夏侯恩状态,人物图片透明度设置为1.0,g_role透明度设置为0.这样可以让人物和剑都从屏幕上消失
                Color gcolor = entity.img_role.getColor();
                entity.img_role.setColor(gcolor.r,gcolor.g,gcolor.b,1.0f);

                Color rcolor = entity.g_role.getColor();
                entity.g_role.setColor(rcolor.r,rcolor.g,rcolor.b,.0f);
                //重设锁链组
                entity.g_chain.resetChainGroup();

            }
            @Override
            public void update(ChainScreen entity) {}
            @Override
            public void exit(ChainScreen entity) {}
            @Override
            public boolean onMessage(ChainScreen entity, Telegram telegram) {return false;}
        },
        GAME_RUNNING{
            @Override
            public void enter(ChainScreen entity) {
                //游戏开始后人物出现
                entity.roleAppear();
            }
            @Override
            public void update(ChainScreen entity) {}
            @Override
            public void exit(ChainScreen entity) {}
            @Override
            public boolean onMessage(ChainScreen entity, Telegram telegram) {return false;}
        },
        GAME_OVER{
            @Override
            public void enter(ChainScreen entity) {
                //TODO:游戏结束向plc发送命令让锁链断开的操作写在此处
                PlcCommHelper.getInstance().simpleGet("/plc_send_serial?plccmd=chain");
//                entity.stage.removeCaptureListener(entity.skllistener);
            }
            @Override
            public void update(ChainScreen entity) {}
            @Override
            public void exit(ChainScreen entity) {}
            @Override
            public boolean onMessage(ChainScreen entity, Telegram telegram) {return false;}
        }};


}

/**
 * 宝剑图片
 */
class SwordImage extends Image implements Disposable {

    private Timeline tl_shake;
    private Timeline tl_getsword;
    private int life =5;                                                                                        //宝剑可被点击5次
    private boolean isShake = false;                                                                            //记录现在是否处于抖动状态
    private InputListener il = null;

    private GetSwordListener listener = null;
    public SwordImage(Texture t){
        super(t);
        this.setSize(t.getWidth(),t.getHeight());
        this.setOrigin(Align.center);
        this.rotateBy(-30);
//        this.setPosition(ScreenParams.screenWidth/2,this.getY());
//        il = new InputListener(){
//            @Override
//            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
//                System.out.println("====================shake");
//                shake();
//                return super.touchDown(event, x, y, pointer, button);
//            }
//        };

        this.addCaptureListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("sword touch down");
                shake();
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        //注册动画Accessor
        Tween.registerAccessor(Image.class, new ActorAccessor());
    }

    /**
     * 重设宝剑状态
     */
    public void resetSword(Group g_role){
//        this.setPosition(0,0);
        //设置位置
        this.setPosition(g_role.getWidth()/2,-150);
        //设置旋转原点
        this.setOrigin(Align.center);
        //设置旋转到-30度
        this.setRotation(-30);
        //设置缩放为1.0f
        this.setScale(1.0f);
        //生命恢复到5
        life=5;
        //设置抖动为false
        isShake = false;
//        this.addListener(il);
    }

    private float shakeDuration = .02f;                                                                                 //抖动持续时间
    /**
     * 当玩家点选宝剑时抖动
     */
    public void shake(){
//        System.out.println("isShake:"+isShake);
        if(!isShake) {
            tl_shake = Timeline.createSequence().push(
                    Timeline.createSequence()
                            .push(
                                    Tween.to(this, ActorAccessor.ROTATION_CPOS_XY, shakeDuration).target(this.getRotation()+5.0f)
                                            .ease(TweenEquations.easeNone)
                            )
                            .push(
                                    Tween.to(this,ActorAccessor.ROTATION_CPOS_XY,shakeDuration).target(this.getRotation())
                                            .ease(TweenEquations.easeNone)
                            )
                            .push(
                                    Tween.to(this,ActorAccessor.ROTATION_CPOS_XY,shakeDuration).target(this.getRotation()-5.0f)
                                            .ease(TweenEquations.easeNone)
                            )
                            .push(
                                    Tween.to(this,ActorAccessor.ROTATION_CPOS_XY,shakeDuration).target(this.getRotation())
                                            .ease(TweenEquations.easeNone)
                            )
                            .repeat(3, 0)
            ).push(Tween.call(new TweenCallback(){
                @Override
                public void onEvent(int i, BaseTween<?> baseTween) {
                    //当抖动完成后设置抖动状态为false
                    isShake = false;

                    //宝剑的生命值减1
                    life--;
                    SwordImage.this.setOrigin(Align.bottomLeft);
                    SwordImage.this.setPosition(SwordImage.this.getX(),SwordImage.this.getY()-10);
                    SwordImage.this.setRotation(SwordImage.this.getRotation()-5);
                    SwordImage.this.setOrigin(Align.center);
                    if(life==0){
                        getSword();
                        //获得宝剑后，让宝剑的抖动状态为true，防止以后再次抖动该宝剑
                        isShake = true;
                    }
                }
            })).start();
            isShake = true;
        }
    }
    //获得宝剑动画通知
    public void getSword(){
        tl_getsword = Timeline.createSequence()
                .push(
                        Timeline.createParallel().push(
                                Tween.to(SwordImage.this,ActorAccessor.POS_XY,1.0f).target(600,-480)
                        )
                        .push(
                                Tween.to(SwordImage.this,ActorAccessor.ROTATION_CPOS_XY,1.0f).target(-1580)
                        )
                        .push(
                                Tween.to(SwordImage.this,ActorAccessor.SCALE_XY,1.0f).target(.25f,.1f)
                        )

                )
                .push(
                        Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                if(listener!=null)
                                    listener.onGetSword(SwordImage.this);
                            }
                        })
                )
                .start();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(tl_shake !=null)
            tl_shake.update(delta);
        if(tl_getsword !=null)
            tl_getsword.update(delta);
    }

    @Override
    public void dispose() {
//        if(il!=null)
//            removeListener(il);
    }

    /**
     * 监听何时获得宝剑
     */
    public static interface GetSwordListener{
        public void onGetSword(SwordImage si);
    }

    public void setGetSwordListener(GetSwordListener listener) {
        this.listener = listener;
    }
}

/**
 * 夏侯恩图片
 */
class RoleImage extends Image{
    private Timeline tl_dead;
    private Sound s_dead;
    private DeadCompletedListener listener;

    public RoleImage(Texture t) {
        super(t);
        s_dead = Gdx.audio.newSound(Gdx.files.internal("chain/dead.ogg"));
        Tween.registerAccessor(Image.class,new ActorAccessor());
    }
    /**
     * 夏侯恩死亡
     */
    public void dead(){
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
//                                        .pushPause(3.8f)
                                .push(Timeline.createSequence()
                                        .push(Tween.to(this, ActorAccessor.POS_XY, .03f).target(this.getX() - 5, this.getY()))
                                        .push(Tween.to(this, ActorAccessor.POS_XY, .03f).target(this.getX() + 5, this.getY()))
                                        .repeat(10, .0f)
                                )
                                .push(
                                        Tween.to(this, ActorAccessor.OPACITY,1.0f).target(.0f)
                                )
                                .push(
                                        Tween.call(new TweenCallback() {
                                            @Override
                                            public void onEvent(int i, BaseTween<?> baseTween) {
//                                                g_root.addActor(g_chain);
//                                                ChainScreen.this.g_chain.dropAllChain();
                                                if(listener!=null)
                                                    listener.onRoleDead(RoleImage.this);
                                            }
                                        })
                                )
                )
                .start();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(tl_dead!=null)
            tl_dead.update(delta);
    }

    public static interface DeadCompletedListener{
        public void onRoleDead(RoleImage ri);
    }
    public void setOnRoleDeadListener(DeadCompletedListener listener){this.listener = listener;}
}

/**
 * 锁链,其中包括普通锁链，反白锁链
 */
class ChainGroup extends Group{
    private ChainActor chain1,chain2,chain3,chain4,currCutChain;
    private Array<ChainActor> chains = new Array<ChainActor>();
    private Timeline tl_dropsequence;
    private int remainChainCount ;

    private ChainScreen screen;
    private StateMachine stateMachine;
    public ChainGroup(float width,float height,ChainScreen s) {
        this.setSize(width, height);
        screen = s;
        stateMachine = new DefaultStateMachine(this,CutState.NORMAL_STATE);

        //四条锁链
        chain1 = new ChainActor(ChainActor.ShapeType.NORMAL);
        chain2 = new ChainActor(ChainActor.ShapeType.ROTATION180);
        chain3 = new ChainActor(ChainActor.ShapeType.FLIPX);
        chain4 = new ChainActor(ChainActor.ShapeType.FLIPY);

        chain1.setPosition(100, height);
        chain2.setPosition(250, height);
        chain3.setPosition(400, height);
        chain4.setPosition(550, height);

        chain1.setName("chain1");
        chain2.setName("chain2");
        chain3.setName("chain3");
        chain4.setName("chain4");

        chains.addAll(chain1,chain2,chain3,chain4);
        for(ChainActor chain:chains)
            this.addActor(chain);

        remainChainCount = chains.size;
    }

    /**
     * 让所有的锁链掉落
     */
    public void dropAllChain(){

        final Array<ChainActor> chains = new Array<>();
        chains.addAll(chain1,chain2,chain3,chain4);

        tl_dropsequence = Timeline.createSequence()
                .push(
                        Tween.call(new TweenCallback(){
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                popChainDrop(chains);
                            }
                        })
                ).pushPause(MathUtils.random(.01f, .3f))
                .push(
                        Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                popChainDrop(chains);
                            }
                        })
                ).pushPause(MathUtils.random(.01f, .3f))
                .push(
                        Tween.call(new TweenCallback(){
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                popChainDrop(chains);
                            }
                        })
                ).pushPause(MathUtils.random(.01f,.3f))
                .push(
                        Tween.call(new TweenCallback(){
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                popChainDrop(chains);
                            }
                        })
                ).start();
    }

    /**
     * 从数组中弹出一个锁链对象，并执行掉落动画
     * @param chains    保存所有锁链的数组对象
     */
    private void popChainDrop(Array<ChainActor> chains){
        int index = MathUtils.random(0,chains.size-1);
        chains.get(index).dropChain();
        chains.removeIndex(index);
    }

    private Rectangle knifeRectangle = new Rectangle();
    private Rectangle chainRectangle = new Rectangle();
    /**
     * 切割锁链，当手指经过的坐标与某个锁链发生了碰撞则播放切割动画，并设置为正在切割状态，不能再切割其他锁链
     * 切割状态结束后，设置为普通状态，可继续切割其他锁链。
     * @param x     刀光经过的坐标x
     * @param y     刀光经过的坐标y
     */
    public void cutChains(float x,float y){
        knifeRectangle.set(x,y,1,1);
        for(int i=0;i<chains.size;i++){
            ChainActor chain = chains.get(i);
            if(Intersector.overlaps(knifeRectangle,chain.getCollisionRectangle(chainRectangle))){
//                System.out.println("------------overlap cut" + chain.getName() + " " + this.stateMachine.getCurrentState());
                if(stateMachine.isInState(CutState.NORMAL_STATE)) {
                    currCutChain = chain;
                    stateMachine.changeState(CutState.CUT_STATE);
                    break;
                }
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if(tl_dropsequence!=null)
            tl_dropsequence.update(delta);
        stateMachine.update();
    }

    /**
     * 毁掉一条锁链，当锁链剩余数量为0，游戏结束
     */
    public void destroyChain(){
        remainChainCount--;
        if(remainChainCount==0)
            screen.getStateMachine().changeState(ChainScreen.ChainScreenState.GAME_OVER);
    }

    /**
     * 重设锁链组
     */
    public void resetChainGroup(){
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<resetChainGroup");

        //重设4条锁链
        for(ChainActor chain:chains){
            chain.resetChain();
        }
        //设置剩余锁链数为初始
        remainChainCount = chains.size;

    }
    /**
     * 获得该类的状态机
     * @return  返回当前类的状态机对象
     */
    public StateMachine getStateMachine() {
        return stateMachine;
    }

    public enum CutState implements State<ChainGroup> {
        NORMAL_STATE(){
            @Override
            public void enter(ChainGroup entity) {

            }
            @Override
            public void update(ChainGroup entity) {}
            @Override
            public void exit(ChainGroup entity) {}
            @Override
            public boolean onMessage(ChainGroup entity, Telegram telegram) {
                return false;
            }
        },
        CUT_STATE(){
            @Override
            public void enter(ChainGroup entity) {
                if(entity.currCutChain.getStateMachine().getCurrentState()==ChainActor.ChainState.STATE_NORMAL)
                    entity.currCutChain.getStateMachine().changeState(ChainActor.ChainState.STATE_CUT);
            }
            @Override
            public void update(ChainGroup entity) {}
            @Override
            public void exit(ChainGroup entity) {}
            @Override
            public boolean onMessage(ChainGroup entity, Telegram telegram) {return false;}
        }
    };
}

/**
 * 一条锁链的actor
 */
class ChainActor extends Group {
    private int life = 3;                                                                                               //每条锁链有3点生命
    private Image img_chain,img_chainlight;
    private StateMachine stateMachine;
    public enum ShapeType {NORMAL,ROTATION180,FLIPX,FLIPY};
    private Timeline tl_drop,tl_cut,tl_destroy;                                                                         //锁链的掉落动画

    private Sound s_cut;
    public ChainActor(ChainActor.ShapeType st){
        stateMachine = new DefaultStateMachine<>(this, ChainState.STATE_NORMAL);
        //初始化纹理到TextureRegion对象中，并设置当前绘制的TextureRegion对象
        img_chain = new Image(new Texture(Gdx.files.internal("chain/chain.png")));
        img_chainlight = new Image(new Texture(Gdx.files.internal("chain/chain_light.png")));

        s_cut = Gdx.audio.newSound(Gdx.files.internal("chain/cut.wav"));
        //默认设置锁链的反白光的透明度为0
        Color lcolor = img_chainlight.getColor();
        img_chainlight.setColor(lcolor.r,lcolor.g,lcolor.b,.0f);

        this.setSize(img_chainlight.getWidth(),img_chainlight.getHeight());
        if(st==ShapeType.ROTATION180){
            this.setOrigin(Align.center);
            this.setRotation(180);
//            this.setOrigin(Align.bottomLeft);
        }else if(st==ShapeType.FLIPX){
            this.setOrigin(Align.center);
            this.setScaleX(-1);
//            this.setOrigin(Align.bottomLeft);
        }else if(st==ShapeType.FLIPY){
            this.setOrigin(Align.center);
            this.setScaleY(-1);
//            this.setOrigin(Align.bottomLeft);
        }
        this.addActor(img_chain);
        this.addActor(img_chainlight);

        Tween.registerAccessor(Image.class,new ActorAccessor());
        Tween.registerAccessor(ChainActor.class,new ActorAccessor());

    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateMachine.update();
        if(tl_drop!=null)
            tl_drop.update(delta);
        if(tl_cut!=null)
            tl_cut.update(delta);
        if(tl_destroy!=null){
            tl_destroy.update(delta);
        }
    }

    /**
     * 让当前的锁链播放掉落动画
     */
    public void dropChain(){
        tl_drop = Timeline.createSequence()
                .push(
                        Tween.to(this,ActorAccessor.POS_XY,1.0f).target(this.getX(),0.0f)
                            .ease(Quint.IN)
//                            .ease(Bounce.OUT)
                ).start();
    }

    /**
     * 当切中锁链时播放反白动画
     */
    public void cutChain(){
//        System.out.println("cut:"+this.getName());
        life--;
        Color lcolor = img_chainlight.getColor();
        img_chainlight.setColor(lcolor.r, lcolor.g, lcolor.b,1.0f);
        tl_cut = Timeline.createSequence()
                .push(
                        Timeline.createParallel()
                                .push(
                                        Tween.call(new TweenCallback() {
                                            @Override
                                            public void onEvent(int type, BaseTween<?> source) {
                                                //砍中时,播放锁链被砍击的声音
                                                s_cut.play();
                                            }
                                        })
                                )
                                .push(
                                        Tween.to(img_chainlight, ActorAccessor.OPACITY, .3f).target(.0f)
                                                .ease(Back.INOUT)
                                )
                                .push(
                                        Timeline.createSequence()
                                                .push(
                                                        Tween.to(this, ActorAccessor.POS_XY, .05f).target(this.getX() - 5, this.getY())
                                                )
                                                .push(
                                                        Tween.to(this, ActorAccessor.POS_XY, .05f).target(this.getX(), this.getY())
                                                ).repeat(3, 0)
                                )
                )
                .push(
                        Tween.call(new TweenCallback() {
                            @Override
                            public void onEvent(int i, BaseTween<?> baseTween) {
                                ChainActor.this.stateMachine.changeState(ChainState.STATE_NORMAL);
                                if (life == 0) {
                                    ChainActor.this.stateMachine.changeState(ChainState.STATE_DESTROY);
                                }
                            }
                        })
                ).start();
    }

    /**
     * 锁链没有血后，毁坏锁链
     */
    private void destroyChain(){
        tl_destroy = Timeline.createSequence()
                        .push(
                                Tween.to(this,ActorAccessor.OPACITY,1.0f).target(.0f)
                        )
                        .push(
                                Tween.call(new TweenCallback() {
                                    @Override
                                    public void onEvent(int i, BaseTween<?> baseTween) {
                                        ChainActor.this.setPosition(ChainActor.this.getX(),ChainActor.this.getHeight());
                                        ChainGroup cg = (ChainGroup) ChainActor.this.getParent();
                                        cg.destroyChain();
                                        cg.getStateMachine().changeState(ChainGroup.CutState.NORMAL_STATE);
                                    }
                                })
                        )
                        .start();
    }

    /**
     *
     * @param r
     * @return
     */
    public Rectangle getCollisionRectangle(Rectangle r){
        float offsetWidth = 10;                                                                                         //宽度偏移量，由于锁链图有外发光和空白，导致图的宽度比实际锁链宽度大，通过此值来调整实际锁链的碰撞宽度
        r.set(this.getX() + offsetWidth, this.getY(), this.getWidth() - offsetWidth, this.getHeight());
        return r;
    }

    /**
     * 重设锁链状态
     */
    public void resetChain(){
        life = 3;
        this.setPosition(this.getX(),this.getHeight());
        Color ccolor = this.getColor();
        this.setColor(ccolor.r,ccolor.g,ccolor.b,1.0f);
        stateMachine.changeState(ChainState.STATE_NORMAL);
    }

    /**
     * 获得状态机对象
     * @return
     */
    public StateMachine getStateMachine() {
        return stateMachine;
    }

    /**
     * 控制锁链状态的状态机
     */
    public enum ChainState implements State<ChainActor> {
        STATE_NORMAL(){
            @Override
            public void enter(ChainActor entity) {
                ((ChainGroup)entity.getParent()).getStateMachine().changeState(ChainGroup.CutState.NORMAL_STATE);
            }
            @Override
            public void update(ChainActor entity) {}
            @Override
            public void exit(ChainActor entity) {}
            @Override
            public boolean onMessage(ChainActor entity, Telegram telegram) {
                return false;
            }
        },
        STATE_CUT(){
            @Override
            public void enter(ChainActor entity) {
                entity.cutChain();
            }
            @Override
            public void update(ChainActor entity) {}
            @Override
            public void exit(ChainActor entity) {}
            @Override
            public boolean onMessage(ChainActor entity, Telegram telegram) {
                return false;
            }
        },
        STATE_DESTROY(){
            @Override
            public void enter(ChainActor entity) {
                entity.destroyChain();
            }
            @Override
            public void update(ChainActor entity) {

            }
            @Override
            public void exit(ChainActor entity) {

            }
            @Override
            public boolean onMessage(ChainActor entity, Telegram telegram) {
                return false;
            }
        }
    };
}