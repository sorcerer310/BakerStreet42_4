package com.bsu.bk42.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by fengchong on 16/4/3.
 */
public class ShaderUtils {
    public static ShaderProgram s_dimmed = new ShaderProgram(Gdx.files.internal("shader/dimmed.vertex.glsl")
            ,Gdx.files.internal("shader/dimmed.fragment.glsl"));
    /**
     * 纹理变灰效果
     * @return  返回纹理变灰效果的shader
     */
//    public static ShaderProgram dimmed(){
//        ShaderProgram shader = new ShaderProgram(Gdx.files.internal("shader/dimmed.vertex.glsl")
//                ,Gdx.files.internal("shader/dimmed.fragment.glsl"));
//        return shader;
//    }

    /**
     * shader效果开始
     * @param batch     带入shader要影响的batch
     * @param shader    要实现的shader效果
     * @return          返回batch原有的shader效果,以备恢复batch使用
     */
    public static ShaderProgram begin(Batch batch,ShaderProgram shader){
        ShaderProgram oldShader = batch.getShader();
        batch.setShader(shader);
        return oldShader;
    }

    /**
     * shader效果结束，恢复原旧有的shader
     * @param batch     要恢复的batch
     * @param oldShader 要恢复的旧有的shader
     */
    public static void end(Batch batch,ShaderProgram oldShader){
        batch.setShader(oldShader);
    }

}
