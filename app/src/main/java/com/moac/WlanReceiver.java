package com.moac;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;


public class WlanReceiver extends BroadcastReceiver {

    private static final String TAG = "wifiReceiver";
    private Handler handler;
    public void setHandler(Handler handler) {
        this.handler = handler;
    }
    @Override
    public void onReceive(Context context, Intent intent) {

        Message message=new Message();

        if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
               // Log.i(TAG, "wifi断开");
                message.what=-1;
                handler.sendMessage(message);

            } else if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                //获取当前wifi名称
                //Log.i(TAG, "连接到网络 " + wifiInfo.getSSID());
                message.what=2;
                handler.sendMessage(message);

            }
        }


        if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int wifi_state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
            if (wifi_state == WifiManager.WIFI_STATE_DISABLED) {
                //Log.i(TAG, "系统关闭wifi");
                message.what=0;
                handler.sendMessage(message);
            } else if (wifi_state == WifiManager.WIFI_STATE_ENABLED) {
                Log.i(TAG, String.valueOf(handler==null));
                Log.i(TAG, String.valueOf(message==null));
                message.what=1;
                handler.sendMessage(message);
            }
        }

    }

}
