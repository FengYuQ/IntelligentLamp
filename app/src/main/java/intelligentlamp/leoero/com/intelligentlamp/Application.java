package intelligentlamp.leoero.com.intelligentlamp;

import android.bluetooth.BluetoothAdapter;

import android.content.Context;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.leoero.intelligentlamp.util.Bluetooth;
import com.leoero.intelligentlamp.util.Util;

import java.net.ConnectException;
import java.net.SocketException;

/**
 * @note Application是单例对象
 */
public class Application extends android.app.Application {

    private AudioManager audioManager;                  //音量控制
    private Bluetooth bluetooth;                        //蓝牙通信类
    private Handler handler;                            //UI线程对象
    private ConnectivityManager netManager;             //网络管理者
    private static Application application;             //Application单例对象

    /**
     * @return Application的单例对象
     */
    public static Application getInstance() {
        return application;
    }


    /**
     * @brief 获取蓝牙对象
     * @return 蓝牙对象
     */
    public Bluetooth getBluetooth() { return bluetooth; }

    /**
     * @brief 查询蓝牙是否连接成功
     * @return 连接成功标志
     */
    public boolean isConnected() { return this.bluetooth.isEnable(); }

    @Override
    public void onCreate() {
        super.onCreate();

        //默认将媒体音量调到最大，防止语音提示的音量过小
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                                     audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                                     AudioManager.FLAG_PLAY_SOUND);

        //建立子线程用于蓝牙连接
        handler = new Handler();
        bluetooth = new Bluetooth();

        handler.post(new Runnable() {
            @Override
            public void run() {
                bluetooth.link(Application.this);
            }
        });


        //判断是否有网络连接，无则提醒用户
        netManager = (ConnectivityManager)Application.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = netManager.getActiveNetworkInfo();
        if(networkInfo == null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Util.INSTANCE.showToast(Application.this, "当前无网络连接请注意!");
                }
            });
        }

        //应用程序入口处调用，避免手机内存过小，杀死后台进程后通过历史intent进入Activity造成SpeechUtility对象为null
        //注意：此接口在非主进程调用会返回null对象，如需在非主进程使用语音功能，请增加参数：SpeechConstant.FORCE_LOGIN+"=true"
        //参数间使用半角“,”分隔, 并且id 必须和下载的SDK保持一致，否则会出现10407错误
        //初始化讯飞接口并设置打印流输出
        SpeechUtility.createUtility(Application.this, SpeechConstant.APPID + "=" + getString(R.string.app_id));
        Setting.setShowLog(false);

        application = this;
    }

    @Override
    public void onTerminate() {
        //关闭蓝牙资源
        bluetooth.shutdown();
        super.onTerminate();
    }

}

