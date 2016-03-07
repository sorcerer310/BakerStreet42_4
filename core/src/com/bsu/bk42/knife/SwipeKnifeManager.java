package com.bsu.bk42.knife;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.utils.Disposable;

/**
 * 刀光管理类
 * @author fengchong
 *
 */
public class SwipeKnifeManager implements Disposable {
	private static SwipeKnifeManager instance = null;
	public static SwipeKnifeManager getInstance(){
		if(instance==null)
			instance = new SwipeKnifeManager();
		return instance;
	}
	
	private SwipeKnifeManager(){}
	private SwipeKnifeLight skl;
	
	//所有类型的刀光
//	private FireParticle fp = new FireParticle();
//	private IceParticle ip = new IceParticle();
//	private ButterflyKnifeParticle bkp = new ButterflyKnifeParticle();
//	private HeartKnifeParticle hkp = new HeartKnifeParticle();
//	private SakuraKnifeParticle skp = new SakuraKnifeParticle();
//	private LeafKnifeParticle lkp = new LeafKnifeParticle();

	/**
	 * ��һЩ���ù���
	 * @param cam
	 */
	public void reset(Camera cam){
		if(skl==null)
			skl = new SwipeKnifeLight(cam);
		skl.setCamera(cam);
		skl.stopParticle();
	}
	
	/**
	 * ��ͨ����
	 * @param cam	������������
	 * @return		���ص������
	 */
	public SwipeKnifeLight getNomalKnife(Camera cam){
		reset(cam);
		skl.setTexture(skl.tr_knifeNormal);
//		skl.addCaptureListener()
		//���
//		skl.setTexture(GameAssets.getInstance().tr_knifeButterfly);
//		skl.addParticlePoolHelper(bkp);
		//��
//		skl.setTexture(GameAssets.getInstance().tr_knifeHeart);
//		skl.addParticlePoolHelper(this.hkp);
		//ӣ��
//		skl.setTexture(GameAssets.getInstance().tr_knifeSakura);
//		skl.addParticlePoolHelper(this.skp);
		//Ҷ��
//		skl.setTexture(GameAssets.getInstance().tr_knifeLeaf);
//		skl.addParticlePoolHelper(this.lkp);
		
		return skl;
	}
	/**
	 * �ʺ�֮��
	 * @param cam	������������
	 * @return		���ص������
	 */
	public SwipeKnifeLight getRainbowKnife(Camera cam){
		reset(cam);
//		skl.setTexture(GameAssets.getInstance().tr_knifeFire);
		return skl;
	}
	/**
	 * ��û���֮��
	 * @param cam	������������
	 * @return		���ص������
	 */
	public SwipeKnifeLight getFireKnife(Camera cam){
		reset(cam);
//		skl.setTexture(GameAssets.getInstance().tr_knifeFire);
//		skl.addParticlePoolHelper(fp);
		return skl;
	}
	/**
	 * ��ñ�˪֮��
	 * @param cam	������������
	 * @return		���ص������
	 */
	public SwipeKnifeLight getIceKnife(Camera cam){
		reset(cam);
//		skl.setTexture(GameAssets.getInstance().tr_knifeIce);
//		skl.addParticlePoolHelper(ip);
		return skl;
	}
	/**
	 * ���
	 * @param cam
	 * @return
	 */
	public SwipeKnifeLight getIceFireKnife(Camera cam){
		reset(cam);
//		skl.setTexture(GameAssets.getInstance().tr_knifeIceFire);
//		skl.addParticlePoolHelper(ip);
//		skl.addParticlePoolHelper(fp);
		return skl;
	}
	/**
	 * ���
	 * @param cam
	 * @return
	 */
	public SwipeKnifeLight getButterflyKnife(Camera cam){
		reset(cam);
//		skl.setTexture(GameAssets.getInstance().tr_knifeButterfly);
//		skl.addParticlePoolHelper(bkp);
		return skl;
	}
	/**
	 * ���͵�
	 * @param cam
	 * @return
	 */
	public SwipeKnifeLight getHeartKnife(Camera cam){
		reset(cam);
//		skl.setTexture(GameAssets.getInstance().tr_knifeHeart);
//		skl.addParticlePoolHelper(hkp);
		return skl;
	}
	/**
	 * ӣ����
	 * @param cam
	 * @return
	 */
	public SwipeKnifeLight getSakuraKnife(Camera cam){
		reset(cam);
//		skl.setTexture(GameAssets.getInstance().tr_knifeSakura);
//		skl.addParticlePoolHelper(skp);
		return skl;
	}
	/**
	 * ��Ҷ��
	 * @param cam
	 * @return
	 */
	public SwipeKnifeLight getLeafKnife(Camera cam){
		reset(cam);
//		skl.setTexture(GameAssets.getInstance().tr_knifeLeaf);
//		skl.addParticlePoolHelper(lkp);
		return skl;
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		if (skl != null) {
			skl.dispose();
		}
	}
}
