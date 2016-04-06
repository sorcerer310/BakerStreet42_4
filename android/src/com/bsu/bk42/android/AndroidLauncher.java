package com.bsu.bk42.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.*;
import android.widget.EditText;
import android.widget.Toast;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.bsu.bk42.BakerStreet42;
import com.bsu.bk42.screen.ChainScreen;
import com.bsu.bk42.screen.CutScreen;
import com.bsu.bk42.screen.PuzzleScreen;

public class AndroidLauncher extends AndroidApplication {
	private final String PREFERENCES_CLEAR_PASSWORD = "12345";
	private EditText et;
	private AlertDialog dlg_rstgame;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		MainTabActivity.game = new BakerStreet42();
		initialize(MainTabActivity.game, config);
		//初始化重置对话框
		initResetGameDialog();
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
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main,menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		int id = item.getItemId();
		if(id==R.id.action_settings){
			Toast.makeText(this,"action_setting",Toast.LENGTH_LONG).show();
			dlg_rstgame.show();
			et.setText("");
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	/**
	 * 初始化输入密码对话框
	 */
	private void initResetGameDialog(){
		//获得密码编辑框的view
		LayoutInflater li = LayoutInflater.from(this);
		View gameResetDialogView = li.inflate(R.layout.game_reset_dialog, null);

		//获得密码编辑框
		et = (EditText) gameResetDialogView.findViewById(R.id.et_password);
		et.setText("");

		//定义输入密码的对话框
		dlg_rstgame = new AlertDialog.Builder(this)
				.setTitle("游戏重置密码")
				.setView(gameResetDialogView)
				.setPositiveButton("确定", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int arg1) {
						if(et.getText().toString().equals(PREFERENCES_CLEAR_PASSWORD)){
//							Toast.makeText(AndroidLauncher.this, "重置游戏成功", Toast.LENGTH_SHORT).show();
							//密码正确先重置服务器数据
							try {
								//向服务器发送命令重置换服务器各项数据

								byte[] bytes = Utils.sendPostRequestByForm("http://192.168.1.113:8080/pgc2/plc_init_serial", "");
								String retstr = new String(bytes);
								Toast.makeText(AndroidLauncher.this, "重置服务器状态成功:" + retstr, Toast.LENGTH_SHORT).show();
							} catch (Exception e) {
								Toast.makeText(AndroidLauncher.this, "重置服务器状态失败:"+e.toString(), Toast.LENGTH_SHORT).show();
							}
							//密码正确则清除数据
							//重置客户端数据
							resetGame();
//							Toast.makeText(AndroidLauncher.this,retstr,Toast.LENGTH_LONG).show();
						}
						else
							Toast.makeText(AndroidLauncher.this, "密码错误", Toast.LENGTH_SHORT).show();
						dialog.dismiss();
					}})
				.setNeutralButton("取消", null)
				.create();
	}

	/**
	 * 重设游戏
	 */
	private void resetGame(){
		MainTabActivity.game.getMapScreen().resetMap();
		MainTabActivity.game.getLifeScreen().resetLifeScreen();
		MainTabActivity.game.getCutScreen().getStateMachine().changeState(CutScreen.CutScreenState.GAME_READY);
		MainTabActivity.game.getChainScreen().getStateMachine().changeState(ChainScreen.ChainScreenState.GAME_READY);
		MainTabActivity.game.getPuzzleScreen().getStateMachine().changeState(PuzzleScreen.PuzzleState.GAME_READY);
		MainTabActivity.game.resetServer();
	}
}
