package com.leoero.intelligentlamp.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;


//TODO:该类为语音朗诵者，用来播报语音
@SuppressWarnings("deprecation")
public class Speaker {

    private SpeechSynthesizer synthesizer;               //讯飞语音合成者
    private Context context;                             //当前所要播放语音的Activity对象
    private SpeakerListener speakerListener;             //语音播报的监听者


    /**
     * @param context 当前所要播放语音的Activity对象
     * @brief 构造并初始化synthesizer
     */
    public Speaker(@NonNull final Context context) {
        this.context = context;
        speakerListener = new SpeakerListener();
        synthesizer = SpeechSynthesizer.createSynthesizer(this.context, null);

        //合成者初始化失败
        if (synthesizer == null) {
            Toast.makeText(context, "语音合成者初始化失败", Toast.LENGTH_LONG).show();
        }

        synthesizer.setParameter(SpeechConstant.VOICE_NAME, SpeakerParams.voicer);
        synthesizer.setParameter(SpeechConstant.SPEED, SpeakerParams.speed);
        synthesizer.setParameter(SpeechConstant.VOLUME, SpeakerParams.volume);
        synthesizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeakerParams.cloud_engine);
    }

    /**
     * @param text 语音播报的内容
     * @brief 播放传入的语音数据
     */
    public void speak(String text) {
        synthesizer.startSpeaking(text, speakerListener);
    }

    /**
     * @brief 语音合成者参数
     */
    private static final class SpeakerParams {
        public static final String voicer = "xiaoyan";
        public static final String volume = "100";  //range[0, 100]
        public static final String speed = "50";   //range[0, 100] ?
        public static final String cloud_engine = SpeechConstant.TYPE_CLOUD;
        public static final String local_engine = SpeechConstant.TYPE_LOCAL;
    }

    /**
     * @brief Speaker播报监听器
     * @TODO 这一部分的接口可以根据后面所需再来改写
     */
    private class SpeakerListener implements SynthesizerListener {

        @Override
        public void onSpeakBegin() {

        }

        @Override
        public void onBufferProgress(int i, int i1, int i2, String s) {

        }

        @Override
        public void onSpeakPaused() {

        }

        @Override
        public void onSpeakResumed() {

        }

        @Override
        public void onSpeakProgress(int i, int i1, int i2) {

        }

        @Override
        public void onCompleted(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    }

}
