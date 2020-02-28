package com.leoero.intelligentlamp.util;

import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.app.AlertDialog;
import android.util.Log;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;
import intelligentlamp.leoero.com.intelligentlamp.Application;


/** @brief 此类为蓝牙通信类 **/
public class Bluetooth {

    private BluetoothAdapter bluetoothAdapter;      //蓝牙适配器
    private BluetoothDevice bluetoothDevice;        //蓝牙设备
    private BluetoothSocket bluetoothSocket;        //蓝牙socket
    private OutputStream outputStream;              //蓝牙通信输出流

    /**
     * @brief 蓝牙构造函数
     */
    public Bluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled()) { //当前未开启蓝牙，请求开启蓝牙
            bluetoothAdapter.enable();
        }
    }

    /**
     * @brief 建立蓝牙连接
     * @param context 上下文对象
     * @return 是否成功连接
     */
    public boolean link(final Context context) {
        if(bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) { //蓝牙功能出现问题，GG...
            Util.INSTANCE.showToast(context, "未打开蓝牙功能或蓝牙功能出现异常，无法与下位机通信");
            return false;
        }

        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices.size() <= 0) { //无已配对的设备信息，要提醒使用者先配对蓝牙信息
            Util.INSTANCE.showToast(context, "蓝牙未先配对台灯，请先配对台灯");
            return false;
        } else if(bondedDevices.size() >= 2) { //配对的设备信息过多，会对后续程序造成影响
            Util.INSTANCE.showToast(context, "当前配对的信息过多，请到蓝牙界面删除不必要配对");
            return false;
        }

        for(BluetoothDevice device : bondedDevices) { //这里是已配对的设备信息，要保证只有已连接的下位机
            this.bluetoothDevice = device;
        }

        //枚举所有可能的UUID，尝试连接正确的那个
        ParcelUuid ids[] = bluetoothDevice.getUuids();
        for(int i = 0; i < ids.length; i++) {
            ParcelUuid id = ids[i];
            UUID uuid = id.getUuid();
            try {
                bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(uuid);
                if(bluetoothSocket == null) {
                    break;
                } else {
                    bluetoothSocket.connect();
                }
            } catch(IOException e) {
                e.printStackTrace();
            }

            if(bluetoothSocket.isConnected()) { //成功连接上远程蓝牙端了
                Log.i("****", "Connected remote bluetooth device");
                break;
            }
        }

        //若无法成功连接socket，会返回false生成AlertDialog提醒用户
        if(bluetoothSocket == null || !bluetoothSocket.isConnected()) {
            return false;
        }

        //获取输出流
        try {
            outputStream = bluetoothSocket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    /**
     * @brief 发送控制命令给下位机
     * @param command 控制命令
     */
    public boolean send(@NonNull final String command) {
        if(!isEnable()) {
            Log.w("socket", "The BluetoothSocket had not connected!");
            return false;
        } else if(outputStream == null) {
            Log.wtf("OutputStream", "OutputStream is null!");
            return false;
        }

        //将命令转换成字节，然后发送给下位机
        byte[] bytes = command.getBytes();
        try {
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    /**
     * @brief 蓝牙功能和Socket服务是否能够正常使用
     * @return
     */
    public boolean isEnable() {
        if(bluetoothAdapter == null || bluetoothSocket == null) {
            return false;
        }
        return bluetoothAdapter.isEnabled() && bluetoothSocket.isConnected();
    }

    /**
     * @brief 关闭蓝牙通信资源
     */
    public void shutdown() {
        try {
            outputStream.close();
            bluetoothSocket.close();
        } catch (IOException e) {
            Log.wtf("resource", "Release bluetooth resource failed!");
            e.printStackTrace();
        }
    }
}
