package com.bsu.bk42.android;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TabHost;
import com.bsu.bk42.ScreenParams;
import org.androidpn.client.Constants;
import org.androidpn.client.ServiceManager;

/**
 * Created by fengchong on 16/1/23.
 */
public class MainTabActivity extends TabActivity {
    public static ScreenParams game;

    private TabHost m_tabHost;
    private RadioGroup m_radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_tab);
        init();
        initservice();

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
            if(ss[0].equals("map")){

            }else if(ss[0].equals("other")){

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        DeviceUtils.onActivityResult(this,requestCode,resultCode,data);
    }

    private void init(){
        m_tabHost = getTabHost();
        int count = Constant.mTabClassArray.length;
        for(int i=0;i<count;i++){
            TabHost.TabSpec tabSpec = m_tabHost.newTabSpec(Constant.mTextviewArray[i])
                    .setIndicator(Constant.mTextviewArray[i])
                    .setContent(Constant.getTabItemIntent(this,i));
            m_tabHost.addTab(tabSpec);
        }
        m_radioGroup = (RadioGroup) findViewById(R.id.main_radiogroup);
        m_radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch(i){
                    case R.id.main_tab_map:
                        break;
                    case R.id.main_tab_star:
                        break;
                    case R.id.main_tab_fire:
                        break;
                    case R.id.main_tab_followup:
                        break;
                }
            }
        });
        ((RadioButton) m_radioGroup.getChildAt(0)).toggle();
    }

    /**
     * 初始化服务
     */
    private void initservice(){
        ServiceManager serviceManager = new ServiceManager(this);
        serviceManager.setNotificationIcon(R.drawable.notification);
        serviceManager.startService();;
    }
}
