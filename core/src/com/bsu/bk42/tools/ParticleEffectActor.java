package com.bsu.bk42.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

/**
 * 为粒子封装一个Actor对象
 * Created by fengchong on 16/3/15.
 */
public class ParticleEffectActor extends Actor implements Disposable {
    ParticleEffect particle = null;
    boolean isDraw = true;
    /**
     * 构造函数，带入一个ParticleEffect对象，Actor负责绘制和移动粒子
     * @param pe
     */
    public ParticleEffectActor(ParticleEffect pe){
        particle = pe;
    }

    /**
     * 设置粒子翻转
     * @param flipX 该值为true X方向翻转
     * @param flipY 该值为true Y方向翻转
     */
    public void setFlip(boolean flipX,boolean flipY){
        particle.setFlip(flipX,flipY);
    }

    /**
     * 设置粒子的缩放效果
     * @param scaleFactor   缩放参数
     */
    public void scaleEffect(float scaleFactor){
        particle.scaleEffect(scaleFactor);
    }

    public void startEffect() {
        isDraw = true;
        particle.start();
    }

    /**
     * 立即让粒子停止绘制消失
     */
    public void stopEffect(){
        isDraw = false;
    }

    /**
     * 让粒子播放完成不再播放
     */
    public void completEffect(){
        particle.allowCompletion();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(isDraw) {
            particle.draw(batch, Gdx.graphics.getDeltaTime());
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        particle.setPosition(this.getX(),this.getY());
        particle.update(delta);
    }

    @Override
    public void dispose() {
        particle.dispose();
    }
}
