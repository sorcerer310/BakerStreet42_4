package com.bsu.bk42.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.DragListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bsu.bk42.ScreenParams;
import com.bsu.bk42.tools.ParticleEffectActor;
import com.bsu.bk42.tools.PlcCommHelper;
import com.bsu.bk42.tools.ShaderUtils;
import com.ugame.gdx.tools.UGameScreen;

import java.util.HashMap;
import java.util.Random;

/**
 * 拼图解谜场景
 * Created by fengchong on 16/3/9.
 */
public class PuzzleScreen extends UGameScreen implements IPlcCommandListener {
    private Texture tx_bg_puzzle = null;
    private Texture tx_frame = null;
    private Texture tx_zhuque,tx_baihu,tx_xuanwu,tx_qinglong = null;
    private Texture tx_num_1,tx_num_2,tx_num_3,tx_num_4 = null;

    private Image img_bg = null;
    private Image img_zhuque,img_baihu,img_xuanwu,img_qinglong = null;
    private Image img_num_1,img_num_2,img_num_3,img_num_4 = null;
    private Array<Group> frames = new Array<Group>();

    private Group g_root;

    private Random random = new Random();
    private HashMap<String,FrameGroup> hm_FrameAndNum = new HashMap<String,FrameGroup>();                                               //所有的边框对象
    private HashMap<String,Image> hm_correctImage = new HashMap<String,Image>();                                                   //边框中应放的正确Image

    private ParticleEffectPool pep;                                                                                     //边框的粒子效果
    private StateMachine stateMachine;
    public PuzzleScreen(){
        stage = new Stage(new StretchViewport(720,1280));
        ScreenParams.initScreenParams(720,1280);
        this.setFPS(40);
        stateMachine =new DefaultStateMachine<PuzzleScreen,PuzzleState>(this,PuzzleState.GAME_READY);
        g_root = new Group();
        g_root.setSize(720, 1041);

        //初始化边框粒子
        ParticleEffect pe = new ParticleEffect();
        pe.load(Gdx.files.internal("puzzle/frame.p"),Gdx.files.internal("puzzle"));
        pep = new ParticleEffectPool(pe,5,10);

        //初始化所有图片元素
        initImage();
        initLayout();
    }

    /**
     * 初始化所有图片
     */
    private void initImage(){
        tx_bg_puzzle = new Texture(Gdx.files.internal("puzzle/bg_puzzle.png"));
        tx_frame = new Texture(Gdx.files.internal("puzzle/frame.png"));
        tx_zhuque = new Texture(Gdx.files.internal("puzzle/ic_zhuque.png"));
        tx_baihu = new Texture(Gdx.files.internal("puzzle/ic_baihu.png"));
        tx_xuanwu = new Texture(Gdx.files.internal("puzzle/ic_xuanwu.png"));
        tx_qinglong  = new Texture(Gdx.files.internal("puzzle/ic_qinglong.png"));
        tx_num_1 = new Texture(Gdx.files.internal("puzzle/num_1.png"));
        tx_num_2 = new Texture(Gdx.files.internal("puzzle/num_2.png"));
        tx_num_3  = new Texture(Gdx.files.internal("puzzle/num_3.png"));
        tx_num_4 = new Texture(Gdx.files.internal("puzzle/num_4.png"));

        //初始化背景
        Group g_bagua = new Group(){
            @Override
            public void draw(Batch batch, float parentAlpha) {

                if (stateMachine.getCurrentState() == PuzzleState.GAME_READY){
                    //开始shader效果
                    ShaderProgram oldShader = ShaderUtils.begin(batch, ShaderUtils.s_dimmed);
                    //正常的绘制代码
                    super.draw(batch, parentAlpha);
                    //结束shader效果
                    ShaderUtils.end(batch, oldShader);
                }else
                    super.draw(batch,parentAlpha);
            }
        };
        g_bagua.setName("bagua");
        img_bg = new Image(tx_bg_puzzle);
//        img_bg.setPosition();
        g_bagua.setSize(img_bg.getWidth(),img_bg.getHeight());
        g_bagua.setPosition(360,600,Align.center);
        g_bagua.addActor(img_bg);

        //初始化4个带数字的框
        img_num_1 = new Image(tx_num_1);
        img_num_2 = new Image(tx_num_2);
        img_num_3 = new Image(tx_num_3);
        img_num_4 = new Image(tx_num_4);
        hm_FrameAndNum.put("num1", makeFrameWithNumAndAddGroup(new Image(tx_frame), img_num_1));
        hm_FrameAndNum.put("num2", makeFrameWithNumAndAddGroup(new Image(tx_frame), img_num_2));
        hm_FrameAndNum.put("num3", makeFrameWithNumAndAddGroup(new Image(tx_frame), img_num_3));
        hm_FrameAndNum.put("num4", makeFrameWithNumAndAddGroup(new Image(tx_frame), img_num_4));
        //乱序生成4个frame的位置
        Group g_allFrameAndNum = randomGroupPosition(hm_FrameAndNum);
//        g_allFrameAndNum.debug();
//        g_allFrameAndNum.setPosition(g_bagua.getWidth() / 2, g_bagua.getHeight() / 2,Align.center);
        g_allFrameAndNum.setPosition(0,0);
        g_bagua.addActor(g_allFrameAndNum);

//        g_bagua.debugAll();
        g_root.addActor(g_bagua);

        initSiXiangImage();


    }

    /**
     * 生成带序号的框架
     * @param frame 框架图
     * @param num   数字图
     * @return      返回存放外框和数字的group
     */
    private FrameGroup makeFrameWithNumAndAddGroup(Image frame,Image num){
        num.setName("num");
        FrameGroup g_frameAndNum = new FrameGroup(pep,frame.getWidth(),frame.getHeight());         //设置group的宽高和frame的宽高一样
        frame.setPosition(0,0);                                                                                         //设置框的坐标
        g_frameAndNum.addActor(frame);
        num.setPosition(g_frameAndNum.getWidth()/2,g_frameAndNum.getHeight()/2,Align.center);
        g_frameAndNum.addActor(num);
        return g_frameAndNum;
    }

    /**
     * 随机设置边框和数字的位置，并把所有的边框加入到一个group中
     * @param hm        所有的数字和边框
     * @return          返回包含所有边框和数字的group
     */
    private Group randomGroupPosition(HashMap<String,FrameGroup> hm){
        Group g_allFrameAndNum = new Group();
        g_allFrameAndNum.setName("allframe");
        g_allFrameAndNum.setSize(tx_bg_puzzle.getWidth(),tx_bg_puzzle.getHeight());
        Array<Vector2> pos = new Array<Vector2>();
        int offset = 220;
        pos.add(new Vector2(0+g_allFrameAndNum.getWidth()/2,offset+g_allFrameAndNum.getHeight()/2));
        pos.add(new Vector2(offset+g_allFrameAndNum.getWidth()/2,0+g_allFrameAndNum.getHeight()/2));
        pos.add(new Vector2(0+g_allFrameAndNum.getWidth()/2,-offset+g_allFrameAndNum.getHeight()/2));
        pos.add(new Vector2(-offset+g_allFrameAndNum.getWidth()/2,0+g_allFrameAndNum.getHeight()/2));

        Group[] groups = hm.values().toArray(new Group[]{});
        for(int i=0;i<4;i++) {
            Vector2 v = pos.random();
            pos.removeValue(v, true);
            groups[i].setPosition(v.x,v.y,Align.center);
//            groups[i].setPosition(v.x+g_allFrameAndNum.getWidth()/2,v.y+g_allFrameAndNum.getHeight());
            g_allFrameAndNum.addActor(groups[i]);
        }
        return g_allFrameAndNum;
    }

    /**
     * 初始化布局
     */
    private void initLayout(){
        Table root = new Table();
        Image top = ScreenFrame.getInstance().getImgTop();
        Image bottom = ScreenFrame.getInstance().getImgBottom();

        root.add(top).height(top.getHeight()).width(top.getWidth()).row();
        root.add(g_root).height(g_root.getHeight()).width(g_root.getWidth()).row();
        root.add(bottom).height(bottom.getHeight()).width(bottom.getWidth()).row();

        root.setPosition(360,640);
        stage.addActor(root);
    }
    private int sixiangOffsetY = 50;
    /**
     * 初始化四象图片
     */
    private void initSiXiangImage(){

        //初始化四象

        img_zhuque = new Image(tx_zhuque);
        img_zhuque.setPosition((int)(g_root.getWidth()*0.2), sixiangOffsetY,Align.bottom);
        img_zhuque.addListener(new DragListener(){
            private float startX,startY;
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                super.dragStart(event, x, y, pointer);
                startX = x;
                startY = y;
                dragSixiangImage(event.getTarget());
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                super.drag(event, x, y, pointer);
                dragActor(startX,startY,x,y,event.getTarget());
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                super.dragStop(event, x, y, pointer);
//                for(Group frame:hm_FrameAndNum.values())
                for(String key:hm_FrameAndNum.keySet())
                    dropActorToFrame(key, event.getTarget(), hm_FrameAndNum.get(key), hm_FrameAndNum.get(key).getChildren().get(1));
            }
        });

        img_zhuque.localToStageCoordinates(new Vector2(img_zhuque.getX(),img_zhuque.getY()));

        img_baihu = new Image(tx_baihu);
        img_baihu.setPosition((int)(g_root.getWidth()*0.4), sixiangOffsetY,Align.bottom);
        img_baihu.addListener(new DragListener(){
            private float startX,startY;
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                super.dragStart(event, x, y, pointer);
                startX = x;
                startY = y;
                dragSixiangImage(event.getTarget());
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                super.drag(event, x, y, pointer);
                dragActor(startX,startY,x,y,event.getTarget());
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                super.dragStop(event, x, y, pointer);
                for(String key:hm_FrameAndNum.keySet())
                    dropActorToFrame(key, event.getTarget(), hm_FrameAndNum.get(key), hm_FrameAndNum.get(key).getChildren().get(1));
            }

        });

        img_xuanwu = new Image(tx_xuanwu);
        img_xuanwu.setPosition((int)(g_root.getWidth()*0.6), sixiangOffsetY,Align.bottom);
        img_xuanwu.addListener(new DragListener(){
            private float startX,startY;
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                super.dragStart(event, x, y, pointer);
                startX = x;
                startY = y;
                dragSixiangImage(event.getTarget());
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                super.drag(event, x, y, pointer);
                dragActor(startX,startY,x,y,event.getTarget());
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                super.dragStop(event, x, y, pointer);
                for(String key:hm_FrameAndNum.keySet())
                    dropActorToFrame(key, event.getTarget(), hm_FrameAndNum.get(key), hm_FrameAndNum.get(key).getChildren().get(1));
            }

        });

        img_qinglong = new Image(tx_qinglong);
        img_qinglong.setPosition((int)(g_root.getWidth()*0.8), sixiangOffsetY,Align.bottom);
        img_qinglong.addListener(new DragListener(){
            private float startX,startY;
            @Override
            public void dragStart(InputEvent event, float x, float y, int pointer) {
                super.dragStart(event, x, y, pointer);
                startX = x;
                startY = y;
                dragSixiangImage(event.getTarget());
            }

            @Override
            public void drag(InputEvent event, float x, float y, int pointer) {
                super.drag(event, x, y, pointer);
                dragActor(startX,startY,x,y,event.getTarget());
            }

            @Override
            public void dragStop(InputEvent event, float x, float y, int pointer) {
                super.dragStop(event, x, y, pointer);
                for(String key:hm_FrameAndNum.keySet())
                    dropActorToFrame(key, event.getTarget(), hm_FrameAndNum.get(key), hm_FrameAndNum.get(key).getChildren().get(1));
            }

        });

        g_root.addActor(img_zhuque);
        g_root.addActor(img_baihu);
        g_root.addActor(img_xuanwu);
        g_root.addActor(img_qinglong);

        //四象正确答案
        hm_correctImage.put("num1",img_zhuque);
        hm_correctImage.put("num2",img_baihu);
        hm_correctImage.put("num3",img_xuanwu);
        hm_correctImage.put("num4",img_qinglong);

    }

    /**
     * 拖动Actor操作
     * @param sx    拖动操作的开始坐标x，计算使用
     * @param sy    拖动操作的开始坐标y，计算使用
     * @param x     拖动操作的坐标x
     * @param y     拖动操作的坐标y
     * @param actor 要操作的actor
     */
    private void dragActor(float sx,float sy,float x,float y,Actor actor){
        float x0 = actor.getX();
        float y0 = actor.getY();
        if (x != sx)
            x0 += x - sx;
        if (y != sy)
            y0 += y - sy;
        actor.setPosition(x0, y0);
    }

    private Rectangle rec1,rec2,rec3;
    /**
     * 拖动dragActor到Frame附近，如果再Frame上方则落到Frame中
     * @param dragActor     拖动的actor
     * @param frame         要放置的frame
     */
    private boolean dropActorToFrame(String str_num,Actor dragActor,FrameGroup frame,Actor num){
        //获得拖拽actor在根group中的坐标
        Vector2 v1 = new Vector2(dragActor.getX(),dragActor.getY());
        //数字的在根group中的坐标
        Vector2 v2 = num.localToAscendantCoordinates(g_root,new Vector2());
        //底框在根group中的坐标，用与定位拖拽后的对齐操作
        Vector2 v3 = frame.localToAscendantCoordinates(g_root, new Vector2());
        //获得两个actor的矩形
        Rectangle r_dragActor = new Rectangle(v1.x,v1.y*ScreenParams.scaleHeight,dragActor.getWidth(),dragActor.getHeight());
        Rectangle r_numActor = new Rectangle(v2.x,v2.y*ScreenParams.scaleHeight,num.getWidth(), num.getHeight());
        Rectangle r_frameActor = new Rectangle(v3.x,v3.y*ScreenParams.scaleHeight,frame.getWidth(),frame.getHeight());
        rec1 = r_dragActor;
        rec2 = r_numActor;
        rec3 = r_frameActor;
        //如果两个矩形发生碰撞，把拖拽actor放置到底框的位置
        if(Intersector.overlaps(r_dragActor,r_numActor) && !frame.isFilled()) {
            dragActor.setPosition(v3.x + r_frameActor.getWidth() / 2, v3.y + r_frameActor.getHeight() / 2, Align.center);
            //当四象图放置在框架中时，设置四象图与框架关联，并设置框架现在为被占有状态
            dragActor.setUserObject(frame);
            frame.setIsFilled(true);
            //判断放置的的四象图是否正确
            if(hm_correctImage.get(str_num)==dragActor)
                frame.setIsCorrect(true);
            else
                frame.setIsCorrect(false);
            //执行判断是否答案正确的操作
            dropCorrect();
            return true;
        }
        return false;
    }

    /**
     * 判断现在放置的四象图像是否正确
     */
    private void dropCorrect(){
        for(FrameGroup fg:hm_FrameAndNum.values()){
            //如果有一个不正确则返回函数
            if(!fg.isCorrect()){
                return;
            }
        }
        //全部正确执行正确操作
        stateMachine.changeState(PuzzleState.GAME_OVER);
    }


    /**
     * 开始拖拽四象图时，设置关联的框架为未占有状态，并设置四象图关联框架为空
     * @param target    拖拽的四象图
     */
    private void dragSixiangImage(Actor target){
        if(target.getUserObject()!=null){
            FrameGroup fg = (FrameGroup) target.getUserObject();
            fg.setIsFilled(false);
            target.setUserObject(null);
        }
    }

    private ShapeRenderer srenderer = new ShapeRenderer();

    @Override
    public void render(float delta) {
        super.render(delta);

        stateMachine.update();
        //调试代码
        srenderer.begin(ShapeRenderer.ShapeType.Filled);
        if(rec1!=null && rec2!=null){
//            srenderer.rect(rec1.x,rec1.y,rec1.getWidth(),rec1.getHeight()*ScreenParams.scaleHeight);
//            srenderer.rect(rec2.x,rec2.y,rec2.getWidth(),rec2.getHeight()*ScreenParams.scaleHeight);
//            srenderer.rect(rec3.x+rec3.getWidth()/2,rec3.y+rec3.getHeight()/2+ScreenFrame.getInstance().getImgBottom().getHeight(),rec3.getWidth(),rec3.getHeight()*ScreenParams.scaleHeight);
        }
        srenderer.end();
    }

    @Override
    public void show() {
//        receivePlcCommand(0);
//        Gdx.input.setInputProcessor(null);
//        stateMachine.changeState(PuzzleState.GAME_READY);
    }

    @Override
    public void receivePlcCommand(int cmdi) {
        stateMachine.changeState(PuzzleState.GAME_RUNNING);
    }

    public StateMachine getStateMachine(){return stateMachine;}

    public static enum PuzzleState implements State<PuzzleScreen>{
        GAME_READY{
            @Override
            public void enter(PuzzleScreen entity) {
                //设置四象位置
                entity.img_zhuque.setPosition((int)(entity.g_root.getWidth()*0.2), entity.sixiangOffsetY,Align.bottom);
                entity.img_baihu.setPosition((int) (entity.g_root.getWidth() * 0.4), entity.sixiangOffsetY, Align.bottom);
                entity.img_xuanwu.setPosition((int)(entity.g_root.getWidth()*0.6), entity.sixiangOffsetY,Align.bottom);
                entity.img_qinglong.setPosition((int)(entity.g_root.getWidth()*0.8), entity.sixiangOffsetY,Align.bottom);

                Gdx.input.setInputProcessor(null);
            }

            @Override
            public void update(PuzzleScreen entity) {

            }
            @Override
            public void exit(PuzzleScreen entity) {

            }
            @Override
            public boolean onMessage(PuzzleScreen entity, Telegram telegram) {return false;}
        },
        GAME_RUNNING{
            @Override
            public void enter(PuzzleScreen entity) {Gdx.input.setInputProcessor(entity.stage);}
            @Override
            public void update(PuzzleScreen entity) {}
            @Override
            public void exit(PuzzleScreen entity) {}
            @Override
            public boolean onMessage(PuzzleScreen entity, Telegram telegram) {return false;}
        },GAME_OVER{
            @Override
            public void enter(PuzzleScreen entity) {
                Gdx.input.setInputProcessor(null);
                //TODO:游戏结束后向plc发送命升起四神兽印
                PlcCommHelper.getInstance().simpleGet("/plc_send_serial?plccmd=sixiangup");
            }
            @Override
            public void update(PuzzleScreen entity) {

            }
            @Override
            public void exit(PuzzleScreen entity) {

            }
            @Override
            public boolean onMessage(PuzzleScreen entity, Telegram telegram) {
                return false;
            }
        }};
}

/**
 * 放置四象的框架Group
 */
class FrameGroup extends Group{
    private boolean isFilled = false;
    private boolean isCorrect = false;
    private ParticleEffectActor pea;

    public FrameGroup(ParticleEffectPool pep,float width,float height){
        super();
        this.setSize(width,height);
        pea = new ParticleEffectActor(pep.obtain());
        pea.setPosition(this.getWidth()/2,this.getHeight()/2,Align.center);
        pea.stopEffect();
        this.addActor(pea);

//        pea.setZIndex(0);
    }
    public boolean isFilled() {
        return isFilled;
    }
    public void setIsFilled(boolean isFilled) {
        if(isFilled) {
            pea.setZIndex(999);
            pea.startEffect();
        }
        this.isFilled = isFilled;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setIsCorrect(boolean isCorrect) {
        this.isCorrect = isCorrect;
    }
}
