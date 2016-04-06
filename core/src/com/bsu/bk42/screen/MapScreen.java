package com.bsu.bk42.screen;

import aurelienribon.tweenengine.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bsu.bk42.ScreenParams;
import com.ugame.gdx.tools.UGameScreen;
import com.ugame.gdx.tween.accessor.ActorAccessor;

/**
 * 地图场景
 * Created by Administrator on 2015/5/27.
 */
public class MapScreen extends UGameScreen implements IPlcCommandListener {
    private Texture tx_map = null;                                                                                   //要绘制的地图
    private Texture tx_map_bg = null;

    private Actor map = null;                                                                                        //承载地图的actor
    private Actor map_bg = null;
    private ScrollPane sp = null;

    private Array<Cloud> clouds = null;                                                                              //保存所有的遮挡迷雾
    private Texture[] tx_clouds = null;

    private Array<Mark> marks = null;
    private Texture tx_mark = null;

    private Group mapgroup = new Group();                                                                            //加载地图上所有元素的group
    private int[][] cpoints;                                                                                        //保存所有云彩的点
    private int cloudsWidth = 10;
    private int cloudsHeight = 12;


    public MapScreen(){
        stage = new Stage(new StretchViewport(720,1280));
        ScreenParams.initScreenParams(720,1280);
        this.setFPS(40.0f);
        //加载资源
        tx_clouds = new Texture[]{new Texture(Gdx.files.internal("map/clouds/cloud1.png"))
                ,new Texture(Gdx.files.internal("map/clouds/cloud2.png"))
                ,new Texture(Gdx.files.internal("map/clouds/cloud3.png"))
        };
        tx_map = new Texture(Gdx.files.internal("map/map.png"));
        tx_map_bg = new Texture(Gdx.files.internal("map/map_bg.jpg"));

        //初始化地图元素组
        initMapGroup();
        //初始化滚动控件
        initScrollPane();

        initLayout();

        //PLC命令调试代码
        receivePlcCommand(0);
        receivePlcCommand(1);
        receivePlcCommand(2);
        receivePlcCommand(3);
        receivePlcCommand(4);
        receivePlcCommand(5);
        receivePlcCommand(6);
        receivePlcCommand(7);
        receivePlcCommand(8);
        receivePlcCommand(9);
        receivePlcCommand(10);
        receivePlcCommand(11);
        receivePlcCommand(12);
        receivePlcCommand(13);
        receivePlcCommand(14);
        receivePlcCommand(15);
        receivePlcCommand(16);
        receivePlcCommand(17);
        receivePlcCommand(18);
        receivePlcCommand(19);
        receivePlcCommand(20);
        receivePlcCommand(21);

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

        receivePlcCommand(0);
    }

    /**
     * 初始化地图组上的所有元素
     */
    private void initMapGroup(){

        map = new Actor(){
            {
                this.setWidth(tx_map.getWidth());
                this.setHeight(tx_map.getHeight());
//                this.debug();
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
//                super.draw(batch, parentAlpha);
                if(tx_map!=null)
                    batch.draw(tx_map, this.getX(), this.getY());
            }
        };

        map_bg = new Actor(){
            {
                this.setSize(tx_map_bg.getWidth(),tx_map_bg.getHeight());
            }

            @Override
            public void draw(Batch batch, float parentAlpha) {
//                super.draw(batch, parentAlpha);
                if(tx_map_bg!=null)
                    batch.draw(tx_map_bg,this.getX(),this.getY());
            }
        };

//        stage.addActor(map_bg);
        mapgroup.addActor(map_bg);
//        map = new Image(tx_map);
        mapgroup.setBounds(0, 0, tx_map.getWidth(), tx_map.getHeight());
        mapgroup.addActor(map);
        marks = makeMarks(new int[][]{
                {740, 260}, {634,412},{600,100},                                 //大厅七星灯,朱雀门,朱雀通道门
                {370, 125}, {365, 250},{135,260},{410,378},                     //朱雀计时器,象棋,朱雀武器,朱雀通关门
                {637, 800}, {45, 743}, {40, 520}, {305, 495}, {610, 670},       //白虎门，刀伤，车零件1，锁链，车零件2
                {347, 960}, {350, 996},{100, 1216},{210, 1218},{119, 975},{229, 974},{43, 993},{250, 913},        //虎战车，白虎计时器，4车轮，阿斗，白虎武器
                {850, 630}, {1080, 477},{1160, 477},{1240, 477},{1128, 816},        //玄武门,分贝仪1,分贝仪2,分贝仪3,吊桥
                {1353,853},{1425, 500}, {1290, 490},{1360,453},                     //玄武计时器,酒坛,武器架,青龙门1
                {1042,366},{1140, 439},{1020,75},                                  //5令牌开锁,地图开门,青龙门2
                {915, 22},{921,427},{973,422},                                     //对联，青龙计时器,青龙武器
                {732,785}                                                          //祭坛
        });
        for(Mark m:marks)
            mapgroup.addActor(m);

        cpoints = new int[cloudsWidth*cloudsHeight][2];
        for(int i=0;i<cloudsWidth;i++){
            for(int j=0;j<cloudsHeight;j++){
                cpoints[j*cloudsWidth+i][0] = i*155;
                cpoints[j*cloudsWidth+i][1] = j*100;
//                System.out.println("x:"+i*300+"  y:"+j*200);
            }
        }

        //屏幕触摸调试代码
        mapgroup.addListener(new InputListener() {

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                System.out.println("x:" + x + " y:" + y);
                return super.touchDown(event, x, y, pointer, button);
            }
        });

        clouds = makeClouds(cpoints);
//        dispareClouds(0);
//        dispareClouds(1);
//        dispareClouds(2);
//        dispareClouds(3);
//        dispareClouds(4);
//        dispareClouds(5);
//        dispareClouds(6);
//        dispareClouds(7);
//        dispareClouds(8);
//        dispareClouds(9);

        for(Image c:clouds)
            mapgroup.addActor(c);

//        mapgroup.debugAll();
    }

    /**
     * 初始化滚动控件
     */
    private void initScrollPane(){
        sp = new ScrollPane(mapgroup, new ScrollPane.ScrollPaneStyle());
        sp.setVisible(true);
        sp.setBounds(0, 0, ScreenParams.screenWidth
                , ScreenParams.screenHeight-ScreenFrame.getInstance().getImgTop().getHeight()-ScreenFrame.getInstance().getImgBottom().getHeight());
//        sp.setBounds(0, 0, ScreenParams.screenWidth, ScreenParams.screenHeight);
        sp.setWidget(mapgroup);
        sp.invalidate();
        sp.validate();
//        sp.setScrollPercentX(.7f);
//        sp.setScrollPercentY(1.0f);
//        stage.addActor(sp);
    }


    private void initLayout(){
        Table root = new Table();
        Image top = ScreenFrame.getInstance().getImgTop();
        Image bottom = ScreenFrame.getInstance().getImgBottom();
        root.add(top).height(top.getHeight()).expandX().row();
        root.add(sp).width(ScreenParams.screenWidth).height(sp.getHeight()).row();
        root.add(bottom).height(bottom.getHeight()).expandX().row();
//        root.setOrigin(0,0);
        root.setPosition(360,640);
        stage.addActor(root);
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
            //大厅迷雾消失
            case 0:
                di = new int[]{4+0*cloudsWidth,4+1*cloudsWidth,4+2*cloudsWidth,4+3*cloudsWidth,4+4*cloudsWidth
                                ,4+5*cloudsWidth,4+6*cloudsWidth,4+7*cloudsWidth,4+8*cloudsWidth,4+9*cloudsWidth};
                break;
            //朱雀宫房间1迷雾消失
            case 1:
                di = new int[]{2+1*cloudsWidth,2+2*cloudsWidth,2+3*cloudsWidth
                                ,3+1*cloudsWidth,3+2*cloudsWidth,3+3*cloudsWidth,3+4*cloudsWidth};
                break;
            //朱雀宫通道迷雾消失
            case 2:
                di=new int[]{0+0*cloudsWidth,0+1*cloudsWidth,0+2*cloudsWidth,0+3*cloudsWidth
                                ,1+0*cloudsWidth,2+0*cloudsWidth,3+0*cloudsWidth,4+0*cloudsWidth};
                break;
            //朱雀宫房间2迷雾消失
            case 3:
                di=new int[]{1+1*cloudsWidth,1+2*cloudsWidth,1+3*cloudsWidth};
                break;
            //白虎宫房间1迷雾消失
            case 4:
                di = new int[]{0+6*cloudsWidth,0+7*cloudsWidth,0+8*cloudsWidth,0+4*cloudsWidth,0+5*cloudsWidth
                            ,1+6*cloudsWidth,1+7*cloudsWidth,1+8*cloudsWidth,1+4*cloudsWidth,1+5*cloudsWidth
                            ,2+6*cloudsWidth,2+7*cloudsWidth,2+8*cloudsWidth,2+4*cloudsWidth,2+5*cloudsWidth
                            ,3+6*cloudsWidth,3+7*cloudsWidth,3+8*cloudsWidth,3+4*cloudsWidth,3+5*cloudsWidth
                            ,4+6*cloudsWidth,4+7*cloudsWidth,4+8*cloudsWidth,4+4*cloudsWidth,4+5*cloudsWidth
                            ,3+9*cloudsWidth
                };
                break;
            //白虎宫房间2迷雾消失
            case 5:
                di = new int[]{0+9*cloudsWidth,0+10*cloudsWidth,0+11*cloudsWidth
                            ,1+9*cloudsWidth,1+10*cloudsWidth,1+11*cloudsWidth
                            ,2+9*cloudsWidth,2+10*cloudsWidth,2+11*cloudsWidth};
                break;
            //玄武宫房间1迷雾消失
            case 6:
                di = new int[]{
                        5+4*cloudsWidth,5+5*cloudsWidth,5+6*cloudsWidth,5+7*cloudsWidth,5+8*cloudsWidth
                        ,6+4*cloudsWidth,6+5*cloudsWidth,6+6*cloudsWidth,6+7*cloudsWidth,6+8*cloudsWidth
                        ,7+4*cloudsWidth,7+5*cloudsWidth,7+6*cloudsWidth,7+7*cloudsWidth,7+8*cloudsWidth
                };
                break;
            //玄武宫房间2迷雾消失
            case 7:
                di = new int[]{
                        8+4*cloudsWidth,8+5*cloudsWidth,8+6*cloudsWidth,8+7*cloudsWidth,8+8*cloudsWidth
                        ,9+4*cloudsWidth,9+5*cloudsWidth,9+6*cloudsWidth,9+7*cloudsWidth,9+8*cloudsWidth
                };
                break;
            //青龙宫房间1迷雾消失
            case 8:
                di = new int[]{
                        6+0*cloudsWidth,6+1*cloudsWidth,6+2*cloudsWidth,6+3*cloudsWidth,
                        7+0*cloudsWidth,7+1*cloudsWidth,7+2*cloudsWidth,7+3*cloudsWidth,
                        8+0*cloudsWidth,8+1*cloudsWidth,8+2*cloudsWidth,8+3*cloudsWidth,
                        9+0*cloudsWidth,9+1*cloudsWidth,9+2*cloudsWidth,9+3*cloudsWidth,
                };
                break;
            //青龙宫房间2迷雾消失
            case 9:
                di = new int[]{
                        5+0*cloudsWidth,5+1*cloudsWidth,5+2*cloudsWidth,5+3*cloudsWidth
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
     * @param cmdi  plc命令:0:初始.1:大厅开启、朱雀门开.2:朱雀通道开、通道迷雾散.3:计时器开始，朱雀房间2迷雾散开.
     *              4:象棋，朱雀武器，朱雀通关门.5:白虎门，白虎房间1迷雾散开，.6:刀伤、车零件1，当刀伤机关解开后触发.
     *              7:锁链、车零件2，当锁链机关解开后触发.8:虎战车、白虎宫房间2迷雾.9:白虎宫计时器 10:白虎宫车轮、阿斗、白虎宫武器.
     *              11:玄武门、玄武房间1迷雾消失.12:分贝仪1触发.13:分贝仪2触发.14:分贝仪3触发，吊桥起，玄武宫房间2迷雾散开.
     *              15:酒坛盒.16:武器架、青龙门，青龙房间1迷雾散开.17:5令牌开锁.18:地图,青龙门,青龙房间2迷雾散开.
     *              19:对联，青龙武器.20:青龙出口小门.21:祭坛.
     *
     *
     *  标记:
     *      {740, 260}, {634,412},{600,100},                                 //大厅七星灯W5.01,朱雀门102.00,朱雀通道门102.05
            {370, 125}, {365, 250},{135,260},{410,378},                      //朱雀计时器107.01,象棋0.01,朱雀武器102.07,朱雀通关门100.06
            7{637, 800}, {45, 743}, {40, 520}, {305, 495}, {610, 670},       //白虎门102.01，刀伤，车零件1 103.00，锁链，车零件2 103.02
            12{347, 960}, {350, 996},{100, 1216},{210, 1218},{119, 975},{229, 974},{43, 993},{250, 913},
            //虎战车100.02，白虎计时器107.02，4车轮0.11，阿斗0.10，白虎武器103.05
            20{850, 630}, {1080, 477},{1160, 477},{1240, 477},{1128, 816},    //玄武门102.02，分贝仪1 107.06，分贝仪2 107.05，分贝仪3 107.07，吊桥101.07
            25{1353,853},{1425, 500}, {1290, 490},{1360,453}                  //玄武计时器107.03 ,酒坛1.07,武器架104.00,青龙门1 103.06
            29{1042,366},{1140, 439},{1020,75},                               //5令牌开锁1.09,地图104.06，青龙门2 104.02
            32{915, 22},{921,427},{973,422}                                   //对联,青龙计时器107.04,青龙武器106.03
            35{732,785}                                                       //祭坛W5.05
        消失云彩:
        0:大厅迷雾消失.1:朱雀宫房间1迷雾消失 2:朱雀宫通道迷雾消失 3:朱雀宫房间2迷雾消失 4:白虎宫房间1迷雾消失
        5:白虎宫房间2迷雾消失 6:玄武宫房间1迷雾消失 7:玄武宫房间2迷雾消失 8:青龙宫房间1迷雾消失 9:青龙宫房间2迷雾消失
     */
    @Override
    public void receivePlcCommand(int cmdi) {
        System.out.println("==================:" + cmdi);
        switch(cmdi){
            case 0:                 //初始
                dispareClouds(0);                                                                                       //大厅迷雾消失
                moveMap(.55f,1.0f);
                break;
            case 1:                 //大厅灯开启W5.01，朱雀门开102.00，朱雀房间1迷雾消失
                appearMark(0);      //七星灯标记
                appearMark(1);      //朱雀门标记
                //朱雀房间1迷雾消失
                dispareClouds(1);
                //移动地图焦点
                moveMap(.2f,1.0f);
                break;
            case 2:                 //朱雀通道门开102.05，朱雀通道迷雾散开
                appearMark(2);      //朱雀通道门
                dispareClouds(2);   //朱雀通道迷雾
                //移动地图焦点
                moveMap(.0f,.0f);
                break;
            case 3:                 //朱雀计时器开始107.01，朱雀房间2迷雾散开
                //计时器标记
                appearMark(3);
                //朱雀房间2迷雾散开
                dispareClouds(3);
                //移动地图焦点
                moveMap(.0f,.0f);
                break;
            case 4:                 //象棋0.01，朱雀武器102.07，朱雀通关门100.06
                //象棋
                appearMark(4);
                //朱雀武器
                appearMark(5);
                //朱雀通关门
                appearMark(6);
                break;
            case 5:                 //白虎门102.01，白虎房间1迷雾散开，
                //白虎门
                appearMark(7);
                //白虎房间1迷雾散开
                dispareClouds(4);
                //移动地图焦点
//                moveMap(.0f,1.0f);
                break;
            case 6:             //刀伤、车零件1 103.00，当刀伤机关解开后触发
                //刀伤
                appearMark(8);
                //车零件1
                appearMark(9);
                break;
            case 7:             //锁链、车零件2 103.02，当锁链机关解开后触发
                //锁链
                appearMark(10);
                //车零件2
                appearMark(11);
                break;
            case 8:             //虎战车100.02、白虎宫房间2迷雾
                //虎战车
                appearMark(12);
                //船舱云消失
                dispareClouds(5);
                //移动焦点
//                moveMap(.0f,.1f);
                break;
            case 9:             //白虎宫计时器107.02
                //计时器
                appearMark(13);
                //移动焦点
//                moveMap(.3f,.0f);
                break;
            case 10:            //白虎宫车轮0.11、阿斗0.10、白虎宫武器103.05
                //4个车轮
                appearMark(17);appearMark(14);appearMark(15);appearMark(16);
                //阿斗
                appearMark(18);
                //白虎宫武器
                appearMark(19);
                //移动焦点
//                moveMap(.45f, .0f);
                break;
            case 11:            //玄武门102.02、玄武房间1迷雾消失
                //玄武门
                appearMark(20);
                //玄武房间1迷雾消失
                dispareClouds(6);
                moveMap(1.0f,.0f);
                break;
            case 12:            //分贝仪1触发107.06
                appearMark(21);
//                moveMap(.0f,.0f);
                break;
            case 13:            //分贝仪2触发107.05
                appearMark(22);
                break;
            case 14:            //分贝仪3触发107.07，吊桥起101.07，玄武宫房间2迷雾散开
                //分贝仪3
                appearMark(23);
                //吊桥起
                appearMark(24);
                //玄武宫房间2迷雾散
                dispareClouds(7);
                break;
            case 15:            //玄武计时器107.03,酒坛盒1.07
                //玄武计时器
                appearMark(25);
                //酒坛盒子
                appearMark(26);
                break;
            case 16:                //武器架104.00、青龙门103.06，青龙房间1迷雾散开
                appearMark(27);appearMark(28);
                dispareClouds(8);
                break;
            case 17:                //5令牌开锁1.09
                appearMark(29);
                break;
            case 18:                //地图104.06,青龙门104.02,青龙房间2迷雾散开
                appearMark(30);
                appearMark(31);
                dispareClouds(9);
                break;
            case 19:                //对联，青龙计时器107.04
                appearMark(32);appearMark(33);
                break;
            case 20:                //青龙武器106.03
                appearMark(34);
            case 21:                //祭坛W5.05
                appearMark(35);
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
//        tm.getObjects().size();
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