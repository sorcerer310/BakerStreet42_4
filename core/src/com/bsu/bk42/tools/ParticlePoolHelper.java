package com.bsu.bk42.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.g2d.ParticleEffectPool.PooledEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.bsu.bk42.ScreenParams;

public class ParticlePoolHelper extends Actor implements Disposable {
	private ParticleEffectPool pep;
	private ParticleEffect effect;
	protected boolean additive = false;
	private Array<PooledEffect> pelist = new Array<PooledEffect>();
	private boolean isDraw = true;
//	private SpriteBatch sbatch = new SpriteBatch();
	/**
	 * ��ʼ��һ�����ӳ�,Ĭ�ϳ�ʼ��������5,���Ϊ20
	 * @param parpath	�����ļ�·��,Ҫ����������ļ���
	 * @param respath	������Դ·��,��Ҫ�������Դ�ļ���
	 */
	public ParticlePoolHelper(String parpath, String respath){
		this(parpath,respath,5,20); 
	}
	/**
	 * ��ʼ��һ�����ӳ�
	 * @param parpath	�����ļ�·��,Ҫ����������ļ���
	 * @param respath	������Դ·��,��Ҫ�������Դ�ļ���
	 * @param initialCapacity	���ӳس�ʼ����
	 * @param max			���ӳ��������
	 */
	public ParticlePoolHelper(String parpath, String respath, int initialCapacity, int max){
		effect = new ParticleEffect();
		effect.load(Gdx.files.internal(parpath), Gdx.files.internal(respath));
		pep = new ParticleEffectPool(effect, initialCapacity, max);
	}
	/**
	 * ��ʼ��һ�����ӳ�
	 * @param parpath	�����ļ�·����Ҫ����������ļ���
	 * @param name	��Դ��ǰ׺
	 * @param initialCapacity	���ӳس�ʼ����
	 * @param max	���ӳ��������
	 */
	public ParticlePoolHelper(String parpath, TextureAtlas textAltas, String name, int initialCapacity, int max){
		effect = new ParticleEffect();
		effect.load(Gdx.files.internal(parpath),
				textAltas,
				name);
		pep = new ParticleEffectPool(effect, initialCapacity, max);
	}
	/**
	 * ��ʼ��һ�����ӳ�,Ĭ�ϳ�ʼ��������5,���Ϊ20
	 * @param parpath	�����ļ�·��,Ҫ����������ļ���
	 * @param altas	�������?·��
	 * @param name	����������Դ��ǰ׺
	 */
	public ParticlePoolHelper(String parpath, TextureAtlas altas, String name){
		this(parpath,altas,name,5,20); 
	}
	
	/**
	 * ��ָ��λ�ò���һ������Ч��.Ĭ��ʹ�õ�һ��������
	 * @param x		ָ��λ��x���
	 * @param y		ָ��λ��y���
	 */
	public void playEffect(float x,float y){
		playEffect(x,y,0);													//��������ʼ��������Ч��
	}
	/**
	 * ��ָ��λ�ò���һ������Ч��,ָ�����ӵķ���������
	 * @param x		ָ��λ��x���
	 * @param y		ָ��λ��y���
	 * @param emitterIdx		����������
	 */
	public void playEffect(float x,float y,int emitterIdx){
		isDraw =true;
		PooledEffect pe = pep.obtain();										//�����ӳػ��һ��������
		pelist.add(pe);														//�������Ӽ��뵽ѭ����Ⱦ������
		
		pe.setPosition(-100, -100);											//�����Ӷ���Ų����Ļ��
		pe.allowCompletion();												//�Ƚ������������
		
		ParticleEmitter pem = pe.getEmitters().get(emitterIdx);				//��ø����ӵķ�����
		pem.setAdditive(additive);											//���ò��뱳����ɫ���
//		pem.setPosition(x/UGameMain.scaleWidth, U.deviceY2drawY(y/UGameMain.scaleHeight));					//���÷�����λ��
		pem.setPosition(x, ScreenParams.deviceY2drawY(y));
		pem.reset();	
	}
	/**
	 * ���ƶ�λ�ò���һ������Ч��ͨ�����ӷ������������Ѱ�Ҷ�Ӧ�ķ�����
	 * @param x		ָ��λ��x���
	 * @param y		ָ��λ��by���
	 * @param ename	���������
	 */
	public void playEffect(float x,float y,String ename){
		isDraw =true;
		PooledEffect pe = pep.obtain();
		pelist.add(pe);
		
		pe.setPosition(-100, -100);
		pe.allowCompletion();
		
		ParticleEmitter pem = pe.findEmitter(ename);
		pem.setAdditive(additive);
//		pem.setPosition(x/UGameMain.scaleWidth, U.deviceY2drawY(y/UGameMain.scaleHeight));					//���÷�����λ��
		pem.setPosition(x, ScreenParams.deviceY2drawY(y));
		pem.reset();	

	}
	/**
	 * ��ָ��λ�ò��Ŷ������Ч��ͨ�����ӷ������Ķ�������Ѱ�Ҷ�Ӧ������
	 * @param x		����λ��x���
	 * @param y		����λ��y���
	 * @param ename	������������飬���������������
	 */
	public void playEffect(float x,float y,String[] ename){
		isDraw =true;
		PooledEffect pe = pep.obtain();
		pelist.add(pe);
		
		pe.setPosition(-100, -100);
		pe.allowCompletion();
		
		for(String s:ename){
			ParticleEmitter pem = pe.findEmitter(s);
			pem.setAdditive(additive);
//			pem.setPosition(x/UGameMain.scaleWidth, U.deviceY2drawY(y/UGameMain.scaleHeight));					//���÷�����λ��
			pem.setPosition(x, ScreenParams.deviceY2drawY(y));
			pem.reset();		
		}
	}
	/**
	 * ��ָ��λ�ò������е�����Ч��
	 * @param x	ָ��λ�õ�x���
	 * @param y	ָ��λ�õ�y���
	 */
	public void playAllEffect(float x,float y){
		isDraw = true;
		PooledEffect pe = pep.obtain();
		pelist.add(pe);
		
		pe.setPosition(-100, -100);
		pe.allowCompletion();

		for(int i=0;i<pe.getEmitters().size;i++){
			pe.getEmitters().get(i).setAdditive(additive);
//			pe.getEmitters().get(i).setPosition(x/UGameMain.scaleWidth, U.deviceY2drawY(y)/UGameMain.scaleHeight);
			pe.getEmitters().get(i).setPosition(x, ScreenParams.deviceY2drawY(y));
			pe.getEmitters().get(i).reset();
		}
		
//		for(ParticleEmitter pem:pe.getEmitters()){
//			pem.setAdditive(additive);
//			pem.setPosition(x/UGameMain.scaleWidth, U.deviceY2drawY(y)/UGameMain.scaleHeight);					//���÷�����λ��
//			pem.reset();		
//		}
	}
	
	/**
	 * �ƶ�����Ч��
	 * @param x		�ƶ���x��
	 * @param y		�ƶ���y��
	 */
	public void moveEffect(float x,float y){
		isDraw =true;
//		for(PooledEffect pe:pelist)
//			pe.setPosition(x/UGameMain.scaleWidth, U.deviceY2drawY(y)/UGameMain.scaleHeight);
		for(int i=0;i<pelist.size;i++)
//			pelist.get(i).setPosition(x/UGameMain.scaleWidth, U.deviceY2drawY(y)/UGameMain.scaleHeight);
			pelist.get(i).setPosition(x, ScreenParams.deviceY2drawY(y));
	}
	
	/**
	 * ֹͣ����
	 */
	public void stopEffect(){
		for(PooledEffect pe: pelist){
			pe.allowCompletion();
		}
	}
	
	public void stopDraw(){
		isDraw = false;
		stopEffect();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		for(PooledEffect pe:pelist){
			if(!isDraw){
				pe.update(Gdx.graphics.getDeltaTime());
				return;
			}
			if(pe.isComplete()){
				pelist.removeValue(pe, false);
				pe.free();
			}
			else{
				pe.draw(batch, Gdx.graphics.getDeltaTime());
			}
		}
	}
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		for(PooledEffect pe: pelist)
			pe.dispose();
		effect.dispose();
		pep.clear();
		pelist.clear();
//		System.out.println("ParticlePoolHelper is dispose()=============");
	}
}
