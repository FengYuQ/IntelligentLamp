package intelligentlamp.leoero.com.intelligentlamp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.leoero.intelligentlamp.constant.Commands;
import com.leoero.intelligentlamp.util.JsonParser;
import com.leoero.intelligentlamp.util.Recognizer;
import com.leoero.intelligentlamp.util.Speaker;
import com.leoero.intelligentlamp.util.Util;


/** 主界面 **/
public class MainActivity extends AppCompatActivity {

    private static String manualSwitch;                   //手动界面的开关按钮
    private final Context context = MainActivity.this;    //当前对象
    private Button manualButton;                          //手动界面按钮
    private Button speechButton;                          //语音界面按钮
    private ImageButton manualControlButton;              //手动控制开关按钮
    private ImageButton speechControlButton;              //语音控制按钮
    private ImageButton lightingButton;                   //调亮按钮
    private ImageButton dimmingButton;                    //调暗按钮
    private Recognizer recognizer;                        //语音识别者
    private Speaker speaker;                              //语音播报者
    private ImageView launchView;                         //启动动画
    private AlphaAnimation alphaAnimation;                //渐变动画播放器
    private String manual_switch_open = "open";           //手动开关界面——开
    private String manual_switch_close = "close";         //手动开关界面——关

    @Override
    protected void onCreate(Bundle savedInstanceState) { //一开始创建Activity
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //初始化
        alphaAnimation = new AlphaAnimation(1.0f, 0f);
        recognizer = new Recognizer(context);
        speaker = new Speaker(context);

        initView();
        animate();

        //蓝牙连接不成功
        if (!Application.getInstance().isConnected()) {
            new AlertDialog.Builder(this).setTitle("警告")
                    .setMessage("当前无法与远端蓝牙设备建立连接，请退出程序建立连接，继续使用可能会出现异常")
                    .setIcon(R.mipmap.ic_launcher_round)
                    .create().show();
        }
    }

    /**
     * @brief 播放启动动画
     */
    private void animate() {
        alphaAnimation.setDuration(2400);
        alphaAnimation.setFillAfter(true);
        launchView = findViewById(R.id.launchView);
        launchView.startAnimation(alphaAnimation);
    }

    /**
     * @brief 初始化控件布局
     */
    private void initView() {

        //安卓Activity寻找layout(xml)里的空间
        manualButton = findViewById(R.id.ManualButton);
        speechButton = findViewById(R.id.SpeechButton);
        speechControlButton = findViewById(R.id.speech_image_button);
        manualControlButton = findViewById(R.id.manual_image_button);
        lightingButton = findViewById(R.id.lighting_button);
        dimmingButton = findViewById(R.id.dimming_button);


        //初始化一开始显示的界面
        manualSwitch = manual_switch_close;
        speechButton.setBackground(getResources().getDrawable(R.drawable.circle_button_clicked_style));
        speechButton.setTextColor(getResources().getColor(R.color.colorGainsboro));
        lightingButton.setVisibility(View.GONE);
        dimmingButton.setVisibility(View.GONE);

        //进入语音控制界面，刷新按钮的界面
        speechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechButton.setBackground(getResources().getDrawable(R.drawable.circle_button_clicked_style));
                speechButton.setTextColor(getResources().getColor(R.color.colorGainsboro));
                manualButton.setBackground(getResources().getDrawable(R.drawable.circle_button_unclicked_style));
                manualButton.setTextColor(getResources().getColor(R.color.colorPureWhite));
                lightingButton.setVisibility(View.GONE);
                dimmingButton.setVisibility(View.GONE);
                speechControlButton.setVisibility(View.VISIBLE);
                manualControlButton.setVisibility(View.GONE);
            }
        });

        //进入手动控制界面，刷新按钮的界面
        manualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speechButton.setBackground(getResources().getDrawable(R.drawable.circle_button_unclicked_style));
                speechButton.setTextColor(getResources().getColor(R.color.colorPureWhite));
                manualButton.setBackground(getResources().getDrawable(R.drawable.circle_button_clicked_style));
                manualButton.setTextColor(getResources().getColor(R.color.colorGainsboro));
                lightingButton.setVisibility(View.VISIBLE);
                dimmingButton.setVisibility(View.VISIBLE);
                speechControlButton.setVisibility(View.GONE);
                manualControlButton.setVisibility(View.VISIBLE);
            }
        });


        //语音控制按钮的监听器，负责识别语音数据
        speechControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recognizer.onRecognize(new RecognizerDialogListener() {
                    @Override
                    public void onResult(RecognizerResult recognizerResult, boolean isNoRecognized) {
                        if (isNoRecognized) {
                            return;
                        }

                        //TODO:获取语音识别的解析数据, 对比预设的命令，错误则用speaker提醒，正确则通过蓝牙发送对应的命令
                        String result = JsonParser.parse(recognizerResult.getResultString());
                        Log.i("speaker", result);

                        //这是显示语音关键词的Toast
                        //Util.INSTANCE.showToast(MainActivity.this, result);

                        //判断result是否在预设的汉字集合里面
                        if(Commands.speech_open_commands.contains(result)) {
                            if (send(Commands.open_command)) {
                                speakAndShow("成功开启台灯");
                                manualSwitch = manual_switch_open;
                            } else {
                                speakAndShow("无法开启台灯请检查蓝牙连接");
                            }
                            manualSwitch = manual_switch_open;
                        } else if(Commands.speech_close_commands.contains(result)) {
                            if (send(Commands.close_command)) {
                                speakAndShow("成功关闭台灯");
                                manualSwitch = manual_switch_close;
                            } else {
                                speakAndShow("无法关闭台灯请检查蓝牙连接");
                            }
                            manualSwitch = manual_switch_close;
                        } else if(Commands.speech_lighting_commands.contains(result)) {
                            if (send(Commands.lighting_command)) {
                                //连续发送三次调亮命令，让台灯亮得更快
                                send(Commands.lighting_command);
                                send(Commands.lighting_command);
                                speakAndShow("调亮成功");
                            } else {
                                speakAndShow("调亮失败请检查蓝牙连接");
                            }
                        } else if(Commands.speech_dimming_commands.contains(result)) {
                            if (send(Commands.dimming_command)) {
                                //连续发送三次调暗命令，让台灯暗得更快
                                send(Commands.dimming_command);
                                send(Commands.dimming_command);
                                speakAndShow("调暗成功");
                            } else {
                                speakAndShow("调暗失败请检查蓝牙连接");
                            }
                        } else {
                            speakAndShow("关键词不符合预设命令");
                            Log.i("recognize", result);
                        }
                    }

                    @Override
                    public void onError(SpeechError speechError) {
                        Log.e("recognizerError", speechError.getErrorCode() + ":" + speechError.getErrorDescription());
                    }
                });
            }
        });

        //手动控制开关的监听器，手动控制灯的开关
        manualControlButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manualSwitch.equals(manual_switch_close)) {
                    manualSwitch = manual_switch_open;
                    manualControlButton.setImageDrawable(getResources().getDrawable(R.mipmap.manual_open));
                    if (MainActivity.this.send(Commands.open_command)) {
                        speakAndShow("成功开启台灯");
                    } else {
                        speakAndShow("无法开启台灯请检查蓝牙连接");
                    }
                } else {
                    manualSwitch = manual_switch_close;
                    manualControlButton.setImageDrawable(getResources().getDrawable(R.mipmap.manual_close));
                    if (MainActivity.this.send(Commands.close_command)) {
                        speakAndShow("成功关闭台灯");
                    } else {
                        speakAndShow("无法关闭台灯请检查蓝牙连接");
                    }
                }
            }
        });

        //调亮按钮
        lightingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manualSwitch.equals(manual_switch_close)) {
                    speakAndShow("请先开启台灯再进行调亮操作");
                    return;
                }
                if (MainActivity.this.send(Commands.lighting_command)) {
                    speakAndShow("调亮成功");
                } else {
                    speakAndShow("调亮失败请检查蓝牙连接设置");
                }
            }
        });

        //调暗按钮
        dimmingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manualSwitch.equals(manual_switch_close)) {
                    speakAndShow("请先开启台灯再进行调暗操作");
                    return;
                }
                if (MainActivity.this.send(Commands.dimming_command)) {
                    speakAndShow("调暗成功");
                } else {
                    speakAndShow("调暗失败请检查蓝牙连接");
                }
            }
        });
    }

    /**
     * @param words
     * @brief 打印Toast并且让语音助手说话
     */
    private void speakAndShow(@NonNull String words) {
        speaker.speak(words);
        Util.INSTANCE.showToast(MainActivity.this, words);
    }

    /**
     * @param command 控制命令
     * @brief 通过蓝牙控制台灯
     */
    private boolean send(@NonNull String command) {
        return Application.getInstance().getBluetooth().send(command);
    }

}