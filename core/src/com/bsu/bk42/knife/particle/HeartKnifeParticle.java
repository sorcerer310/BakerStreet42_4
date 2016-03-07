package com.bsu.bk42.knife.particle;

import com.bsu.bk42.tools.ParticlePoolHelper;

/**
 * ���͵�����
 * @author fengchong
 *
 */
public class HeartKnifeParticle extends ParticlePoolHelper {
	public HeartKnifeParticle(){
		super("game/particle/knifelight/heart.p", "game/particle/knifelight");
		this.additive = false;
	}
}
