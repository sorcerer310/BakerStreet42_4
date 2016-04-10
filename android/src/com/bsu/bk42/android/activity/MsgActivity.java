package com.bsu.bk42.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import com.bsu.bk42.android.R;
import com.bsu.bk42.android.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MsgActivity extends Activity {
    private ListView lv_msg;
    private static List<Map<String,Object>> listdata ;
    private static ListViewSimpleAdapter sa ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //全屏显示
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_msg);


        initMessage();
    }

    /**
     * 增加消息数据
     * @param title
     * @param msg
     */
    public void addMsg(String title,String msg){
        listdata.add(Utils.makeListItemData(title,msg));
        sa.notifyDataSetChanged();                              //通知数据改变
        lv_msg.setSelection(sa.getCount()-1);                   //滚动到最后一行
    }

    /**
     * 清除所有的消息
     */
    public static void clearMsg(){
        if(listdata!=null)
            listdata.clear();
        if(sa!=null)
            sa.notifyDataSetChanged();;
    }


    /**
     * 初始化收件箱消息部分
     */
    private void initMessage(){
        if(listdata==null)
            listdata= new ArrayList<Map<String,Object>>();
        lv_msg = (ListView) findViewById(R.id.lv_message);

        sa = new ListViewSimpleAdapter(this,listdata,R.layout.listitem
                ,new String[]{"title"}
                ,new int[]{R.id.item_title});

        lv_msg.setAdapter(sa);
        lv_msg.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> l, View v, int position, long id) {
                Map<String,Object> mapitem = listdata.get(position);
//                startVideoActivity(mapitem.get("title").toString(),
//                        vpath+((Integer)mapitem.get("oggpath")));
            }});
    }
}
