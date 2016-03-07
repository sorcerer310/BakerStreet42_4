package com.bsu.bk42;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.ugame.gdx.tools.UGameScreen;
import com.ugame.gdx.tween.accessor.ActorAccessor;

/**
 * 地图场景
 * Created by Administrator on 2015/5/27.
 */
public class MapScreen extends UGameScreen {
    private Texture tx_map = null;                                                                                   //要绘制的地图
    private Actor map = null;                                                                                        //承载地图的actor
    private ScrollPane sp = null;

    private Array<Cloud> clouds = null;                                                                              //保存所有的遮挡迷雾
    private Texture[] tx_clouds = null;

    private Array<Mark> marks = null;
    private Texture tx_mark = null;

    private Group mapgroup = new Group();                                                                            //加载地图上所有元素的group
    private int[][] cpoints;                                                                                        //保存所有云彩的点
    private int cloudsWidth = 8;
    private int cloudsHeight = 11;
    public MapScreen(){
        stage = new Stage(new StretchViewport(700.0F, 1280.0f));
        this.setFPS(40.0f);
        //加载资源
        tx_clouds = new Texture[]{new Texture(Gdx.files.internal("map/clouds/cloud1.png"))
                ,new Texture(Gdx.files.internal("map/clouds/cloud2.png"))
                ,new Texture(Gdx.files.internal("map/clouds/cloud3.png"))
        };
        tx_map = new Texture(Gdx.files.internal("map/bg_map.jpg"));

        //初始化地图元素组
        initMapGroup();
        //初始化滚动控件
        initScrollPane();

        plcCommand(0);
//        plcCommand(1);
//        plcCommand(2);
//        plcCommand(3);
//        plcCommand(4);
//        plcCommand(5);
//        plcCommand(6);
//        plcCommand(7);
//        plcCommand(8);
//        plcCommand(9);
//        plcCommand(10);
//        plcCommand(11);
//        plcCommand(12);
//        plcCommand(13);
//        plcCommand(14);
//        plcCommand(15);
//        plcCommand(16);
    }

    /**
     * 重设整个地图数据
     */
    public void resetMap(){
        //重设云彩
        for(int i=0;i<cpoints.length;i++) {
            Cloud c = clouds.get(i);
            c.setPosition(cpoints[i][0], cpoints[i][1]);                                                              //重新设置云彩坐标
            Color color = c.getColor();
            c.setColor(color.r, color.g, color.b, 1.0f);                                                                //重新设置云彩的透明度
            c.setVisible(true);                                                                                         //重新设置云彩的可见性
        }

        //重设标记
        for(Mark m:marks)
            m.setVisible(false);

        plcCommand(0);
    }

    /**
     * 初始化地图组上的所有元素
     */
    private void initMapGroup(){

        map = new Actor(){
            {
                this.setWidth(tx_map.getWidth());
                this.setHeight(tx_map.getHeight());

                this.addListener(new InputListener() {

                    @Override
                    public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                        System.out.println("x:"+x+"   y:"+y);
                        return super.touchDown(event, x, y, pointer, button);
                    }
                });
            }
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
                if(tx_map!=null)
                    batch.draw(tx_map, this.getX(), this.getY());
            }
        };

//        map = new Image(tx_map);
        mapgroup.setBounds(0, 0, tx_map.getWidth(), tx_map.getHeight());
        mapgroup.addActor(map);
        marks = makeMarks(new int[][]{
                {770, 580},                                                     //星盘
                {620, 883}, {530, 550},                                         //乌龟,插旗
                {480, 120}, {480, 880}, {140, 350}, {140, 889}, {480, 260},     //军令台,4个脚踏
                {330, 1335},{330, 974},                                         //通道两小门
                {130, 1700}, {440, 1700},                                       //两侧墙壁铁索连环
                {620, 2040}, {700, 1715}, {760, 1430},                          //船舱门,草船借箭,擂鼓
                {330,2160},{190, 2120},                                         //宝剑咒语箱子,借东风
                {780, 2130},                                                    //追击小门
                {980, 2110}, {880, 1780}                                        //大路小门,华容道小门
        });
        for(Mark m:marks)
            mapgroup.addActor(m);

        cpoints = new int[cloudsWidth*cloudsHeight][2];
        for(int i=0;i<cloudsWidth;i++){
            for(int j=0;j<cloudsHeight;j++){
                cpoints[j*cloudsWidth+i][0] = i*150;
                cpoints[j*cloudsWidth+i][1] = j*200;
//                System.out.println("x:"+i*300+"  y:"+j*200);
            }
        }
        clouds = makeClouds(cpoints);
//        dispareClouds(0);
//        dispareClouds(1);
//        dispareClouds(2);
//        dispareClouds(3);
//        dispareClouds(4);
//        dispareClouds(5);
//        dispareClouds(6);
//        dispareClouds(7);

        for(Image c:clouds)
            mapgroup.addActor(c);
    }

    /**
     * 初始化滚动控件
     */
    private void initScrollPane(){
        sp = new ScrollPane(mapgroup, new ScrollPane.ScrollPaneStyle());
        sp.setBounds(0, 0, 700, 1280);
        sp.setWidget(mapgroup);
        sp.invalidate();
        sp.validate();
        sp.setScrollPercentX(.7f);
        sp.setScrollPercentY(1.0f);
        stage.addActor(sp);
    }

    /**
     * 设置迷雾云彩
     */
    public Array<Cloud> makeClouds(int[][] points){

        Array<Cloud> clouds = new Array<Cloud>();
        for(int i=0;i<points.length;i++) {
            Texture tx_cloud = tx_clouds[MathUtils.random(0, 2)];
            Cloud cloud = new Cloud(tx_cloud);
            cloud.setOrigin(cloud.getWidth() / 2, cloud.getHeight() / 2);
            cloud.setScale(MathUtils.random(1.2f, 1.2f));
//            cloud.setColor(cloud.getColor().r, cloud.getColor().g, cloud.getColor().b,0.5f);
//            cloud.setPosition(points[i][0]-tx_cloud.getWidth()/2, points[i][1]-tx_cloud.getHeight()/2);
              cloud.setPosition(points[i][0], points[i][1]);
            clouds.add(cloud);
        }
        return clouds;
    }

    /**
     * 按房间索引消失每组云彩++
     * @param roomi 房间索引
     */
    public void dispareClouds(int roomi){
        int[] di = null;
        switch(roomi){
            //房间1消失云彩
            case 0:
                di = new int[]{4+0*cloudsWidth,4+1*cloudsWidth,4+2*cloudsWidth,4+3*cloudsWidth,4+4*cloudsWidth
                        ,5+0*cloudsWidth,5+1*cloudsWidth,5+2*cloudsWidth,5+3*cloudsWidth,5+4*cloudsWidth
//                        ,3+0*cloudsWidth,3+1*cloudsWidth,3+2*cloudsWidth,3+3*cloudsWidth,3+4*cloudsWidth
                };
                break;
            //茅庐消失云彩
            case 1:
                di = new int[]{
                        2+0*cloudsWidth,2+1*cloudsWidth,2+2*cloudsWidth,2+3*cloudsWidth,2+4*cloudsWidth
                        ,3+0*cloudsWidth,3+1*cloudsWidth,3+2*cloudsWidth,3+3*cloudsWidth,3+4*cloudsWidth
                };
                break;
            //博望坡消失云彩
            case 2:
                di = new int[]{
                        1+0*cloudsWidth,1+1*cloudsWidth,1+2*cloudsWidth,1+3*cloudsWidth,1+4*cloudsWidth,
                        0+0*cloudsWidth,0+1*cloudsWidth,0+2*cloudsWidth,0+3*cloudsWidth,0+4*cloudsWidth
                };
                break;
            //通道云彩
            case 3:
                di = new int[]{
                        2+5*cloudsWidth,1+5*cloudsWidth,0+5*cloudsWidth,
                        2+6*cloudsWidth,1+6*cloudsWidth,0+6*cloudsWidth
                };
                break;
            //铁锁连环
            case 4:
                di = new int[]{
                        2+7*cloudsWidth,1+7*cloudsWidth,0+7*cloudsWidth,
                        2+8*cloudsWidth,1+8*cloudsWidth,0+8*cloudsWidth,
                        2+9*cloudsWidth,1+9*cloudsWidth,0+9*cloudsWidth,
                        3+10*cloudsWidth,2+10*cloudsWidth,1+10*cloudsWidth,0+10*cloudsWidth
                };
                break;
            //草船借箭
            case 5:
                di = new int[]{
                        3+6*cloudsWidth,4+6*cloudsWidth,
                        3+7*cloudsWidth,4+7*cloudsWidth,
                        3+8*cloudsWidth,4+8*cloudsWidth,
                        3+9*cloudsWidth,4+9*cloudsWidth,

                };
                break;
            //华容道前小路
            case 6:
                di = new int[]{
                        4+10*cloudsWidth,5+10*cloudsWidth
                };
                break;
            //大路
            case 7:
                di = new int[]{
                        6+6*cloudsWidth,
                        6+7*cloudsWidth,
                        7+8*cloudsWidth,6+8*cloudsWidth,
                        7+9*cloudsWidth,6+9*cloudsWidth,
                        7+10*cloudsWidth,6+10*cloudsWidth,
                        5+6*cloudsWidth
                };
                //移动其他几朵云彩调整位置
                int[] mi = new int[]{
                        5+7*cloudsWidth,5+8*cloudsWidth,5+9*cloudsWidth,
                };
                for(int i=0;i<mi.length;i++)
                    clouds.get(mi[i]).moveLeft(100);
                break;
            //华容道
            case 8:
                di = new int[]{
                        5+6*cloudsWidth,
                        5+7*cloudsWidth,
                        5+8*cloudsWidth,
                        5+9*cloudsWidth,
                };
                break;
        }
        //消失动画
        for(int i=0;i<di.length;i++)
            clouds.get(di[i]).dispare();

    }

    /**
     * 生成所有地图标记
     * @return  返回一组标记,用于标识地图上的关键点
     */
    public Array<Mark> makeMarks(int[][] points){
        tx_mark = new Texture(Gdx.files.internal("map/mark.png"));
        tx_mark.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Array<Mark> marks = new Array<Mark>();
        for(int i=0;i<points.length;i++){
            Mark mark = new Mark(tx_mark);
            mark.setPosition(points[i][0]-tx_mark.getWidth()/2, points[i][1]);
            mark.setVisible(false);
            marks.add(mark);
        }
        return marks;
    }

    /**
     * 标记机关完成
     * @param mi    标记的索引
     */
    private void appearMark(int mi){
        marks.get(mi).setVisible(true);
        marks.get(mi).jump();
    }

    /**
     * 收到plc命令执行标记闪动\云彩消失操作
     * @param cmdi  plc命令:0:初始.1:星盘.2:乌龟.3:插旗.4:军令.5:守关3处完成.6:追击.7:守关4处放火.8:铁锁连环.9:船舱门关 10:草船借箭.11:擂鼓助威.
     *                       12:借东风.13:放火.14:选择大路追击.15:选择华容道追击
     *
     *  标记:
     *  {764, 598},                                                     //星盘
        {620, 883}, {525, 202},                                         //乌龟,插旗
        {284, 156}, {417, 256}, {121, 441}, {134, 889}, {413, 889},     //军令台,4个脚踏
        {282, 974}, {282, 1335},                                        //通道两小门
        {115, 1652}, {368, 1652},                                       //两侧墙壁铁索连环
        {568, 2036}, {661, 1715}, {683, 1475},                          //船舱门,草船借箭,擂鼓
        {220,2120},{145, 2120},                                         //宝剑咒语箱子,借东风
        {736, 2121},                                                    //追击小门
        {920, 2121}, {825, 1914}                                        //大路小门,华容道小门
        消失云彩:
        0:房间1消失云彩.1:茅庐消失云彩 2:博望坡消失云彩 3:通道云彩 4:铁锁连环 5:草船借箭
        6:华容道 7:大路消失云彩
     */
    public void plcCommand(int cmdi){

        switch(cmdi){
            case 0:             //初始
                dispareClouds(0);                                                                                       //房间1云彩消失
//                moveMap();
                System.out.println("==================:" + cmdi);
                break;
            case 1:             //完成星盘
                //星盘完成
                appearMark(0);
                //房间2云彩消失
                dispareClouds(1);
                //移动地图焦点
                moveMap(.55f,1.0f);
                break;
            case 2:             //完成乌龟
                //乌龟完成
                appearMark(1);
                //移动地图焦点
                moveMap(.3f,.6f);
                break;
            case 3:             //完成插旗
                //插旗完成
                appearMark(2);
                //博望坡云彩消失
                dispareClouds(2);
                //移动地图焦点
                moveMap(.2f,1.0f);
                break;
            case 4:             //拿起军令
                //军令完成
                appearMark(3);
                //移动地图焦点
                moveMap(.0f,1.0f);
                break;
            case 5:             //踩上3个脚踏
                //3个脚踏亮
                appearMark(4);appearMark(5);appearMark(6);
                appearMark(9);
                //通道云彩消失
                dispareClouds(3);
                //移动地图焦点
                moveMap(.0f,1.0f);
                break;
            case 6:             //触发通道人体感应
                //第4个脚踏亮
                appearMark(7);
                break;
            case 7:             //第4个脚踏踩亮
                //铁锁连环云彩消失
                dispareClouds(4);
                //移动地图焦点
                moveMap(.1f,.7f);
                break;
            case 8:             //铁锁连环挂上
                //铁锁连环完成亮
                appearMark(10);appearMark(11);
                //船舱云消失
                dispareClouds(5);
                //移动焦点
                moveMap(.0f,.1f);
                break;
            case 9:             //船舱门关上
                //船舱门关
                appearMark(12);
                //移动焦点
                moveMap(.3f,.0f);
                break;
            case 10:            //草船借箭完成
                //草船借箭完成
                appearMark(13);
                //移动焦点
                moveMap(.45f, .0f);
                break;
            case 11:            //擂鼓成功
                //擂鼓完成
                appearMark(14);
                moveMap(.45f,.3f);
                break;
            case 12:
                //宝剑咒语箱子开
                appearMark(15);
                moveMap(.0f,.0f);
                break;
            case 13:            //借东风完成
                //借东风完成
                appearMark(16);
                moveMap(.0f,.0f);
                break;
            case 14:            //手机点火完成,华容道前开
                //华容道前小门开
                appearMark(17);
                //华容道前云彩消失
                dispareClouds(6);
                //移动地图焦点
                moveMap(.8f,.0f);
                break;
            case 15:            //选择大路追击
                //选择大路完成
                appearMark(18);
                //大路云彩消失
                dispareClouds(7);
                //移动地图焦点
                moveMap(.8f,.0f);
                break;
            case 16:             //选择华容道追击
                //选择华容道完成
                appearMark(19);
                //华容道云彩消失
                dispareClouds(8);
                break;
            default:
                break;
        }
    }

    /**
     * 移动整个地图
     * @param x     移动的百分比
     * @param y     移动的百分比
     */
    private void moveMap(float x,float y){
        sp.setScrollPercentX(x);
        sp.setScrollPercentY(y);
    }

    @Override
    public void show() {
        super.show();
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void resume() {
        super.resume();
        System.out.println("================MapScreen resume");
    }

    @Override
    public void render(float delta) {
        super.render(delta);
//        System.out.println("======================MapScreen render");
    }
}

/**
 * 地图上的云彩
 */
class Cloud extends Image {
    private TweenManager tm = new TweenManager();
    public Cloud(Texture t){
        super(t);
        Tween.registerAccessor(this.getClass(),new ActorAccessor());
    }

    /**
     * 云彩消失动画,播放完动画最后设置云彩visible为false
     */
    public void dispare(){
        Timeline.createParallel()
                .push(Tween.to(this, ActorAccessor.POS_XY, 1.0f).target(this.getX() + MathUtils.random(-100.0f, 100.0f),this.getY()))
                .push(Tween.to(this,ActorAccessor.OPACITY,1.0f).target(.0f))
                .push(Tween.to(this,ActorAccessor.SCALE_XY,1.0f).target(1.5f,1.5f))
                .push(Tween.call(new TweenCallback() {
                    @Override
                    public void onEvent(int type, BaseTween<?> source) {
                        if(type==TweenCallback.COMPLETE){
                            Cloud.this.setVisible(false);
                        }
                    }
                }))
                .start(tm);
    }

    /**
     * 向左移动一定距离
     * @param l 移动的距离
     */
    public void moveLeft(float l){
        Tween.to(this,ActorAccessor.POS_XY,0.5f).target(this.getX()-l,this.getY())
                .start(tm);
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        tm.update(delta);
    }
}

/**
 * 地图上的标记
 */
class Mark extends Image {
    private Timeline tl;

    public Mark(Texture t){
        super(t);
        Tween.registerAccessor(this.getClass(), new ActorAccessor());
    }

    /**
     * 跳动函数,让标记跳动,提示玩家
     */
    public void jump(){
        tl = Timeline.createSequence()
                .push(Tween.to(this,ActorAccessor.POS_XY,.3f).target(this.getX(),this.getY()-20.0f))
                .push(Tween.to(this,ActorAccessor.POS_XY,.6F).target(this.getX(),this.getY())
                    .ease(TweenEquations.easeNone)
                ).repeat(-1,.0f).start();
    }
    @Override
    public void act(float delta) {
        super.act(delta);
        if(tl!=null)
            tl.update(delta);
    }
}