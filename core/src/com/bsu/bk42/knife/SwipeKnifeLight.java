package com.bsu.bk42.knife;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
//import com.bsu.bk42.UGameMain;
import com.bsu.bk42.ScreenParams;
import com.bsu.bk42.knife.swipe.SwipeHandler;
import com.bsu.bk42.knife.swipe.SwipeTriStrip;
import com.ugame.gdx.tools.GameAssets;
import com.ugame.gdx.tools.ParticlePoolHelper;

public class SwipeKnifeLight extends Actor implements Disposable {
	public Texture tr_knifeNormal;
	private SwipeTriStrip tris;								//�������Ⱦ��
	private SwipeHandler swipe;								//���������.
	private Texture tex;									//����ʹ�õ�����
	private ShapeRenderer shapes;							//��״��Ⱦ
	private SpriteBatch sbatch;								//���ʶ���
	private Camera cam;										//��������
	private Array<ParticlePoolHelper> pphs = new Array<ParticlePoolHelper>();
	public SwipeKnifeLight(Camera c){

		tr_knifeNormal = new Texture(Gdx.files.internal("knife/knife_normal.png"));
		tris = new SwipeTriStrip();
		cam = c;
		swipe = new SwipeHandler(8,cam){
			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) {
				//�϶�ʱ�����õ���Ŀ��
				OrthographicCamera ocam = (OrthographicCamera)cam;
				tris.thickness = 24f;
				for(ParticlePoolHelper pph:pphs){
					//�ƶ�����
					pph.moveEffect((screenX - (ocam.position.x* ScreenParams.scaleWidth - ScreenParams.sysWidth/2 * ocam.zoom))/ocam.zoom, (screenY - ((ScreenParams.screenHeight- ocam.position.y)*ScreenParams.scaleHeight - ScreenParams.sysHeight/2 * ocam.zoom))/ocam.zoom);
				}
				return super.touchDragged(screenX, screenY, pointer);
			}

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer,
					int button) {
				OrthographicCamera ocam = (OrthographicCamera)cam;
				for(ParticlePoolHelper pph:pphs)
					pph.playAllEffect((screenX - (ocam.position.x*ScreenParams.scaleWidth - ScreenParams.sysWidth/2 * ocam.zoom))/ocam.zoom, (screenY - ((ScreenParams.screenHeight- ocam.position.y)*ScreenParams.scaleHeight - ScreenParams.sysHeight/2 * ocam.zoom))/ocam.zoom);
				return super.touchDown(screenX, screenY, pointer, button);
			}

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer,
					int button) {
				for(ParticlePoolHelper pph:pphs)
					pph.stopEffect();
				return super.touchUp(screenX, screenY, pointer, button);
			}
		};
		swipe.minDistance = 10;
		swipe.initialDistance = 1;
		setTexture(tr_knifeNormal);
		shapes = new ShapeRenderer();
		sbatch = new SpriteBatch();

		
	}
	/**
	 * ��������·��
	 * @param t		����·��
	 */
	public void setTexture(Texture t){
		tex = t;
		tex.setFilter(TextureFilter.Linear, TextureFilter.Linear);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		//��������Ч��
		sbatch.begin();
		for(ParticlePoolHelper pph:pphs)
			pph.draw(sbatch, parentAlpha);
		sbatch.end();
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		tex.bind();
		
		//��swipe��Ϊ����״̬,�������̧��ʱ,���ٵ�����,�����𽥱�խ����ʧ
		if(tris.thickness>0)
			tris.thickness-=1.5f;
		
		tris.update(swipe.path());							//��������·���ĵ���
		tris.color = Color.WHITE;							//������ɫ
		tris.draw(cam);
	}
	
	@Override
	public void act(float delta) {
		super.act(delta);
		for(ParticlePoolHelper pph:pphs)
			pph.act(delta);
	}
	
	public SwipeHandler getSwipe() {
		return swipe;
	}
	public void setCamera(Camera cam) {
		this.cam = cam;
	}
	
	/**
	 * ��������,��������Ϊ�գ�����Ϊ����û������
	 * @param pph		������
	 */
	public void addParticlePoolHelper(ParticlePoolHelper pph) {
		pphs.add(pph);
	}
	
	/**
	 * �������
	 */
	public void stopParticle(){
//		System.out.println("clear particle");
		for(ParticlePoolHelper pph:pphs){
			pph.stopEffect();
		}
		pphs.clear();
	}
	@Override
	public void dispose() {
		for(ParticlePoolHelper pph:pphs){
			pph.stopEffect();
			pph.dispose();
		}
		pphs.clear();
	}
}
