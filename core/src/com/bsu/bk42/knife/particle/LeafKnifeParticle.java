package com.bsu.bk42.knife.particle;

import com.bsu.bk42.tools.ParticlePoolHelper;

/**
 * ��Ҷ������
 * @author fengchong
 *
 */
public class LeafKnifeParticle extends ParticlePoolHelper {
	public LeafKnifeParticle(){
		super("game/particle/knifelight/leaf.p", "game/particle/knifelight");
		this.additive = true;
	}
}
