package com.bsu.bk42.android;

import android.os.Bundle;

import android.view.KeyEvent;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.bsu.bk42.ScreenParams;

public class AndroidLauncher extends AndroidApplication {

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		MainTabActivity.game = new ScreenParams();
		initialize(MainTabActivity.game, config);

//		this.graphics.getView();
		//初始化重置对话框
//		initResetGameDialog();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//截获back键
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return true;
		}

		if (KeyEvent.KEYCODE_HOME == keyCode)
			return true;
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void onResume() {
		super.onResume();
	}
}
