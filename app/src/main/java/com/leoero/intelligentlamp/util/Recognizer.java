package com.leoero.intelligentlamp.util;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/** @brief 此类为语音识别者类，负责识别语音数据 **/
public class Recognizer {
    private Context context;                        //当前Activity对象
    private RecognizerDialog recognizerDialog;      //识别的Dialog界面

    /**
     * @param context 当前活动的Activity对象
     * @brief 构造识别者
     */
    public Recognizer(@NonNull final Context context) {
        this.context = context;
        recognizerDialog = new RecognizerDialog(context, new InitListener() {
            @Override
            public void onInit(int code) {
                if (code != ErrorCode.SUCCESS) {
                    Toast.makeText(Recognizer.this.context, "初始化语音识别者失败", Toast.LENGTH_LONG).show();
                }
            }
        });

        //设置ID和subject为null，避免被之前设置了
        recognizerDialog.setParameter(SpeechConstant.CLOUD_GRAMMAR, null);
        recognizerDialog.setParameter(SpeechConstant.SUBJECT, null);
        recognizerDialog.setParameter(SpeechConstant.ASR_PTT, "0"); // //设置标点符号, 1为识别出来的单词有标点符号

        //设置返回格式为json
        //TODO:后面需要做一个Json解析类来解析返回的数据
        recognizerDialog.setParameter(SpeechConstant.RESULT_TYPE, RecognizerParams.result_type);
        recognizerDialog.setParameter(SpeechConstant.ENGINE_TYPE, RecognizerParams.cloud_engine);
        recognizerDialog.setParameter(SpeechConstant.LANGUAGE, RecognizerParams.language);
        recognizerDialog.setParameter(SpeechConstant.ACCENT, RecognizerParams.accent);
        recognizerDialog.setParameter(SpeechConstant.VAD_BOS, RecognizerParams.begin_overtime);
        recognizerDialog.setParameter(SpeechConstant.VAD_EOS, RecognizerParams.end_overtime);

    }

    /**
     * @param onRecognizerDialogListener 语音识别者dialog的监听器
     * @brief 显示语音对话框，然后解析语音数据
     */
    public void onRecognize(RecognizerDialogListener onRecognizerDialogListener) {
        recognizerDialog.setListener(onRecognizerDialogListener);
        recognizerDialog.show();
    }


    /**
     * @brief 识别者对象的参数信息
     */
    private static final class RecognizerParams {
        public static final String result_type = "json";
        public static final String cloud_engine = SpeechConstant.TYPE_CLOUD;
        public static final String local_engine = SpeechConstant.TYPE_LOCAL;
        public static final String language = "zh_cn";  //简体中文
        public static final String accent = "mandarin"; //普通话
        public static final String begin_overtime = "4000"; //开始不说话的最长时间(ms), range(1000, 10000)
        public static final String end_overtime = "1000"; //结束不说话的最长时间(ms), range(0, 10000)
    }



}
