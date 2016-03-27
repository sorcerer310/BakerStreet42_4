package com.bsu.bk42.com.bsu.bk42.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.bsu.bk42.ScreenParams;
import com.ugame.gdx.tools.UGameScreen;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * 拼图解谜场景
 * Created by fengchong on 16/3/9.
 */
public class PuzzleScreen extends UGameScreen {
    private Texture tx_bg_puzzle = null;
    private Texture tx_frame = null;
    private Texture tx_zhuque,tx_baihu,tx_xuanwu,tx_qinglong = null;
    private Texture tx_num_1,tx_num_2,tx_num_3,tx_num_4 = null;

    private Image img_bg = null;
    private Image img_zhuque,img_baihu,img_xuanwu,img_qinglong = null;
    private Image img_num_1,img_num_2,img_num_3,img_num_4 = null;
    private Array<Group> frames = new Array<Group>();

    private Group group = new Group();

    private Random random = new Random();

    public PuzzleScreen(){
        stage = new Stage(new StretchViewport(ScreenParams.screenWidth,ScreenParams.screenHeight));
        this.setFPS(40);

        group.setSize(720,1041);
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
        Group g_bagua = new Group();
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
        HashMap<String,Group> hm = new HashMap<String,Group>();
        hm.put("num1",makeFrameWithNumAndAddGroup( new Image(tx_frame), img_num_1));
        hm.put("num2",makeFrameWithNumAndAddGroup( new Image(tx_frame), img_num_2));
        hm.put("num3",makeFrameWithNumAndAddGroup( new Image(tx_frame), img_num_3));
        hm.put("num4",makeFrameWithNumAndAddGroup( new Image(tx_frame), img_num_4));
        //乱序生成4个frame的位置
        Group g_allFrameAndNum = randomGroupPosition(hm);
//        g_allFrameAndNum.debug();
        g_allFrameAndNum.setPosition(g_bagua.getWidth() / 2, g_bagua.getHeight() / 2, Align.center);
        g_bagua.addActor(g_allFrameAndNum);
//        g_bagua.debugAll();
        group.addActor(g_bagua);

        //初始化四象
        int offsety = 50;
        img_zhuque = new Image(tx_zhuque);
        img_zhuque.setPosition((int)(group.getWidth()*0.2),offsety,Align.bottom);
        img_baihu = new Image(tx_baihu);
        img_baihu.setPosition((int)(group.getWidth()*0.4),offsety,Align.bottom);
        img_xuanwu = new Image(tx_xuanwu);
        img_xuanwu.setPosition((int)(group.getWidth()*0.6),offsety,Align.bottom);
        img_qinglong = new Image(tx_qinglong);
        img_qinglong.setPosition((int)(group.getWidth()*0.8),offsety,Align.bottom);

        group.addActor(img_zhuque);
        group.addActor(img_baihu);
        group.addActor(img_xuanwu);
        group.addActor(img_qinglong);

    }

    /**
     * 生成带序号的框架
     * @param frame 框架图
     * @param num   数字图
     * @return      返回存放外框和数字的group
     */
    private Group makeFrameWithNumAndAddGroup(Image frame,Image num){
        Group g_frameAndNum = new Group();
        frame.setPosition(0,0,Align.center);
        g_frameAndNum.addActor(frame);
        num.setPosition(0,0,Align.center);
        g_frameAndNum.addActor(num);
//        g_frameAndNum.setPosition(0, 0, Align.center);
//        pg_bagua.addActor(g_frameAndNum);
        return g_frameAndNum;
    }

    /**
     * 随机设置边框和数字的位置，并把所有的边框加入到一个group中
     * @param hm        所有的数字和边框
     * @return          返回包含所有边框和数字的group
     */
    private Group randomGroupPosition(HashMap<String,Group> hm){
        Group g_allFrameAndNum = new Group();
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
            g_allFrameAndNum.addActor(groups[i]);
        }
        return g_allFrameAndNum;
    }

    /**
     * 从map中获得一个随机的value
     * @param map   map容器
     * @param <K>   key类型
     * @param <V>   value类型
     * @return
     */
    public <K, V> V popRandomValueFromMap(Map<K, V> map) {
        int rn = Math.abs(random.nextInt())%map.size();
        int i = 0;
        V retValue = null;
        //先获得要返回的值
        for (V value : map.values()) {
            if(i==rn){
                retValue = value;
                break;
//                return value;
            }
            i++;
        }
        //再从该集合中删除该值
        for(K key:map.keySet()){
            if(i==rn){
                map.remove(key);
                break;
            }
            i++;
        }
        return retValue;
    }

    /**
     * 初始化布局
     */
    private void initLayout(){
        Table root = new Table();
        Image top = ScreenFrame.getInstance().getImgTop();
        Image bottom = ScreenFrame.getInstance().getImgBottom();

        root.add(top).height(top.getHeight()).width(top.getWidth()).row();
        root.add(group).height(group.getHeight()).width(group.getWidth()).row();
        root.add(bottom).height(bottom.getHeight()).width(bottom.getWidth()).row();

        root.setPosition(360,640);
        stage.addActor(root);
    }
}
