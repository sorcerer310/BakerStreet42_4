package com.bsu.bk42.knife.particle;

import com.bsu.bk42.tools.ParticlePoolHelper;

public class FireParticle extends ParticlePoolHelper {

	public FireParticle() {
		super("game/particle/knifelight/fireknife.p","game/particle/knifelight");

		this.additive = true;
	}

}
