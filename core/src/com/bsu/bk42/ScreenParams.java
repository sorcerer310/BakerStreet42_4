package com.bsu.bk42;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class ScreenParams {
	public static float screenWidth = 800;
	public static float screenHeight = 480;
	public static float sysWidth,sysHeight;
	public static float scaleWidth,scaleHeight;


	public static void initScreenParams(float psw,float psh){
		screenWidth = psw;
		screenHeight = psh;
		sysWidth = Gdx.graphics.getWidth();
		sysHeight = Gdx.graphics.getHeight();
		scaleWidth = ((float)sysWidth)/screenWidth;
		scaleHeight = ((float)sysHeight)/screenHeight;
	}

	/**
	 * 设备Y坐标转为绘制Y坐标
	 * @param y
	 * @return
	 */
	public static float deviceY2drawY(float y){
		return sysHeight - y;
	}
}
