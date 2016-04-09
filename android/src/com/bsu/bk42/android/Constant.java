package com.bsu.bk42.android;

import android.app.Activity;
import android.content.Intent;
import com.bsu.bk42.android.activity.MsgActivity;

public class Constant {

	public static String mTextviewArray[] = {"AndroidLauncher", "AndroidLauncher", "AndroidLauncher", "AndroidLauncher","AndroidLauncher","MsgActivity"};
	
	public static Class mTabClassArray[]= {AndroidLauncher.class,
		AndroidLauncher.class,
		AndroidLauncher.class,
		AndroidLauncher.class,
			AndroidLauncher.class,
			MsgActivity.class
	};

	/**
	 * 切换Activity时附加的意图数据
	 * @param activity	源activity
	 * @param index		标识
	 * @return				返回携带数据的意图对象
	 */
	public static Intent getTabItemIntent(Activity activity,int index){
		Intent intent = new Intent(activity,mTabClassArray[index]);
		return intent;
	}
}
