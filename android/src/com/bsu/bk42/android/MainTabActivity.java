package com.bsu.bk42.android;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import com.bsu.bk42.BakerStreet42;
import com.bsu.bk42.android.activity.MsgActivity;
import org.androidpn.client.Constants;
import org.androidpn.client.ServiceManager;

/**
 * Created by fengchong on 16/1/23.
 */
public class MainTabActivity extends TabActivity {
    public static BakerStreet42 game;

    private TabHost m_tabHost;
    private RadioGroup m_radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);
        init();
        initservice();

//        DisplayMetrics metrics = new DisplayMetrics();
//        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        System.out.println("------------------>"+TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, metrics));

//        DeviceUtils.activeDeviceManager(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String urivalue = intent.getStringExtra(Constants.NOTIFICATION_URI);
        if(urivalue==null || urivalue.equals(""))
            return;

        DeviceUtils.vibrate(this,500);

        if(DeviceUtils.isScreenLocked(this)) {
            DeviceUtils.wakeScreen(this);
//            DeviceUtils.unlockScreen(this);
        }

        if(urivalue.contains(":")){
            String[] ss = urivalue.split(":");
            if(ss.length<2)
                return;
            //如果发来的消息为地图
            if(ss[0].equals("map")){
                m_radioGroup.check(R.id.main_tab_map);
                game.setMapCurrIndex(ss[1]);
            }else if(ss[0].equals("life")){
                m_radioGroup.check(R.id.main_tab_life);
                //ss[1]为当前灭的灯的索引数，根据索引数计算剩余生命值
                game.setLeftLife(ss[1]);
            }else if(ss[0].equals("cut")){
                m_radioGroup.check(R.id.main_tab_cut);
                //当ss[1]为0时，淳于导出场；当ss[1]为1时，淳于导死亡
                game.setCutStart(ss[1]);
            }else if(ss[0].equals("chain")){
                m_radioGroup.check(R.id.main_tab_chain);
                //当ss[1]为0时，夏侯恩出场。
                game.setChainStart(ss[1]);
            }else if(ss[0].equals("puzzle")){
                m_radioGroup.check(R.id.main_tab_paint);
                //当ss[1]为0时，解谜开始
                game.setPuzzleStart(ss[1]);
            }else if(ss[0].equals("msg")){
                m_radioGroup.check(R.id.main_tab_msg);
                MsgActivity ma=(MsgActivity)m_tabHost.getCurrentView().getContext();
                ma.addMsg("testtitle",ss[1]);
//                Intent nintent = new Intent(this, MsgActivity.class);
//                nintent.putExtra("msg","message");
//                this.startActivityForResult(nintent,9999);
//                View view = m_tabHost.getCurrentTabView();
//                view.toString();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DeviceUtils.onActivityResult(this,requestCode,resultCode,data);
    }

    private void init() {
        m_tabHost = getTabHost();
        int count = Constant.mTabClassArray.length;
        for (int i = 0; i < count; i++) {
            TabHost.TabSpec tabSpec = m_tabHost.newTabSpec(Constant.mTextviewArray[i])
                    .setIndicator(Constant.mTextviewArray[i])
                    .setContent(Constant.getTabItemIntent(this, i));
            m_tabHost.addTab(tabSpec);
        }
        m_radioGroup = (RadioGroup) findViewById(R.id.main_radiogroup);
        m_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.main_tab_map:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[0]);
                        game.setScreen(BakerStreet42.MAPSCREEN);
                        break;
                    case R.id.main_tab_life:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[1]);
                        game.setScreen(BakerStreet42.LIFESCREEN);
                        break;
                    case R.id.main_tab_cut:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[2]);
                        game.setScreen(BakerStreet42.CUTSCREEN);
                        break;
                    case R.id.main_tab_chain:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[3]);
                        game.setScreen(BakerStreet42.CHAINSCREEN);
                        break;
                    case R.id.main_tab_paint:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[4]);
                        game.setScreen(BakerStreet42.PUZZLESCREEN);
                        break;

                    case R.id.main_tab_msg:
                        m_tabHost.setCurrentTabByTag(Constant.mTextviewArray[5]);

                        break;
                }
            }
            //当切换界面时执行一次抬起操作
//            MainTabActivity.game.getStarScreen().getStage().touchUp(0,0,0,0);
        });
        ((RadioButton) m_radioGroup.getChildAt(0)).toggle();
    }

    /**
     * 初始化服务
     */
    private void initservice() {
        ServiceManager serviceManager = new ServiceManager(this);
        serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();
        ;
    }
}