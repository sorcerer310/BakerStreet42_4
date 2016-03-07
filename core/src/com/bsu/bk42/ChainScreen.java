package com.bsu.bk42;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.bsu.bk42.knife.SwipeKnifeLight;
import com.bsu.bk42.knife.SwipeKnifeManager;
import com.ugame.gdx.tools.UGameScreen;

/**
 * 锁链切割场景
 * Created by fengchong on 16/3/3.
 */
public class ChainScreen extends UGameScreen {
    private SwipeKnifeLight skl;                                            //刀光对象
    private Sound sound;
    public ChainScreen(){
        ScreenParams.initScreenParams(this.getStage().getWidth(), this.getStage().getHeight());
        sound = Gdx.audio.newSound(new FileHandle(""));
        skl = SwipeKnifeManager.getInstance().getNomalKnife(this.stage.getCamera());

        stage.addCaptureListener(new InputListener(){
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                skl.getSwipe().touchDown((int)(x* ScreenParams.scaleWidth),(int) ScreenParams.deviceY2drawY(y * ScreenParams.scaleHeight),pointer,button);
                return true;
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                skl.getSwipe().touchUp((int)(x* ScreenParams.scaleWidth),(int) ScreenParams.deviceY2drawY(y * ScreenParams.scaleHeight),pointer,button);
                super.touchUp(event, x, y, pointer, button);
            }

            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) {
                skl.getSwipe().touchDragged((int)(x* ScreenParams.scaleWidth),  (int) ScreenParams.deviceY2drawY(y * ScreenParams.scaleHeight), pointer);
                super.touchDragged(event, x, y, pointer);
            }
        });
    }
}
