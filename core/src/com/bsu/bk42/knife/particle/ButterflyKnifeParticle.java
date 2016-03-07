package com.bsu.bk42.knife.particle;

import com.bsu.bk42.tools.ParticlePoolHelper;

/**
 * �������
 * @author fengchong
 *
 */
public class ButterflyKnifeParticle extends ParticlePoolHelper {
	public ButterflyKnifeParticle(){
		super("knifelight/butterfly.p", "knifelight");
		this.additive = false;
	}
}
