package com.bsu.bk42;

import com.badlogic.gdx.Game;
import com.bsu.bk42.screen.*;
import com.bsu.bk42.tools.PlcCommHelper;
//import com.bsu.bk42.screen.FireScreen;
//import com.bsu.bk42.screen.FollowUpScreen;
//import com.bsu.bk42.screen.StarScreen;

public class BakerStreet42 extends Game {
//	SpriteBatch batch;
//	Texture img;
	public static final int MAPSCREEN = 0;																				//地图场景的默认索引
	public static final int LIFESCREEN = 1;																				//续命场景的默认索引
	public static final int CUTSCREEN = 2;																				//刀伤场景的默认索引
	public static final int CHAINSCREEN = 3;																			//锁链场景的默认索引
	public static final int PUZZLESCREEN = 4;																			//四象场景的默认索引

	public static MapScreen ms = null;																					//地图场景
	public static LifeScreen ls = null;																					//续命场景
	public static CutScreen cuts = null;																				//刀伤场景
	public static ChainScreen cs = null;																				//锁链场景
	public static PuzzleScreen ps = null;																				//四象场景

	@Override
	public void create () {
//		ScreenParams.initScreenParams(this.getStage().getWidth(), this.getStage().getHeight());
		ScreenParams.initScreenParams(720,1280);
		if(ms==null)
			ms = new MapScreen();
		if(ls==null)
			ls = new LifeScreen();
		if(cuts == null)
			cuts = new CutScreen();
		if(cs==null)
			cs= new ChainScreen();
		if(ps==null)
			ps = new PuzzleScreen();
		this.setScreen(ms);
	}

	/**
	 * 设置当前场景
	 * @param map	对应场景的索引
	 */
	public void setScreen(int map){
		switch(map) {
			case MAPSCREEN:
				this.setScreen(ms);
				break;
			case LIFESCREEN:
				this.setScreen(ls);
				ls.queryStars();
				break;
			case CUTSCREEN:
				this.setScreen(cuts);
				break;
			case CHAINSCREEN:
				this.setScreen(cs);
				break;
			case PUZZLESCREEN:
				this.setScreen(ps);
				break;
		}
	}

	/**
	 * 设置地图当前的显示索引
	 * @param id	地图当前显示到的机关索引
	 */
	public void setMapCurrIndex(String id){
		//0:初始.1:大厅灯开启，朱雀门开，朱雀房间1迷雾消失.2:朱雀通道门开，朱雀通道迷雾散.3:朱雀计时开始，朱雀房间2迷雾开.
		//4:象棋，朱雀武器，朱雀通关门.5:白虎门，白虎房间1迷雾散开.6:刀伤，车零件1，.7:锁链，车零件.8:虎战车，白虎宫房间2迷雾.
		//9:白虎宫计时器 10:白虎宫车轮、阿斗、白虎宫武器.11:玄武门、玄武房间1迷雾消失.
		//12:分贝仪1触发.13:分贝仪2触发.14:分贝仪3触发，吊桥起，玄武宫房间2迷雾散开.15:酒坛盒.16:武器架，青龙门，青龙房间1迷雾散
		//17:5令牌开锁.18:地图，青龙门，青龙房间2迷雾散开.19:对联青龙武器.20:青龙出口小门.21:祭坛
		int i = Integer.parseInt(id);
		ms.receivePlcCommand(i);
	}

	/**
	 * 设置剩余生命
	 * @param id 	当前灭的灯的索引
	 */
	public void setLeftLife(String id){
		System.out.println("==========setLeftLife");
		int i=Integer.parseInt(id);
		ls.receivePlcCommand(i);
		ls.queryStars();
	}

	/**
	 * 设置淳于导出场
	 * @param id	接收plc命令的数据
	 */
	public void setCutStart(String id){
		cuts.receivePlcCommand(Integer.parseInt(id));
	}

	/**
	 * 设置锁链界面开始
	 * @param id
	 */
	public void setChainStart(String id){
		cs.receivePlcCommand(Integer.parseInt(id));
	}

	/**
	 * 解谜界面开始
	 * @param id
	 */
	public void setPuzzleStart(String id){
		ps.receivePlcCommand(Integer.parseInt(id));
	}

	public MapScreen getMapScreen() {return ms;}
	public LifeScreen getLifeScreen() {return ls;}
	public CutScreen getCutScreen() {return cuts;}
	public ChainScreen getChainScreen() {return cs;}
	public PuzzleScreen getPuzzleScreen() {return ps;}
	//重置服务器状态
	public void resetServer(){
		PlcCommHelper pch = PlcCommHelper.getInstance();
		pch.simpleGet("/plc_init_serial");
//		pch.setListener(new PlcCommHelper.ResponseListener() {
//			@Override
//			public void getResponse(String string) {
//				System.out.println(string);
//			}
//		});
	}
}
