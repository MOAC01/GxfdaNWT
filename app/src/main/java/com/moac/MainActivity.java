package com.moac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button search;
    private TextView wlan_state;
    private WlanReceiver wlanReceiver;
    boolean isRegReceiver=false;

    private Handler uiHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 0:
                    wlan_state.setText("WiFi已关闭");
                    search.setEnabled(false);
                    break;
                case 1:
                    break;
                case -1:
                    wlan_state.setText("WiFi已断开");
                    break;

                case 2:
                    wlan_state.setText("WiFi已连接");
                    search.setEnabled(true);
                    break;

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        wlanReceiver=new WlanReceiver();
        wlanReceiver.setHandler(uiHandler);
        registerReceiver(wlanReceiver,filter);
        isRegReceiver=true;

    }



    public void initWidget(){
        editText=findViewById(R.id.input_text);
        search=findViewById(R.id.btn_search);
        wlan_state=findViewById(R.id.wifi_state);
        search.setOnClickListener(new View.OnClickListener() {   //按钮点击事件
            @Override
            public void onClick(View v) {
                String str=editText.getText().toString();
                if(str==null || str.isEmpty()){
                    PromptMessage.alert(MainActivity.this,"还没有输入办公室",0);;
                    return;
                }
                int room=Integer.parseInt(str);
                initTelnetParam(room);

            }
        });
    }

    public void initTelnetParam(int room){
        int floor=room/100;
        String ipPrefix="10.0.0.";
        String ipSurfix=String.valueOf(floor*10);
        if(floor==0 || floor>20){
            PromptMessage.alert(MainActivity.this,"办公室输入错误，请检查",0);;
            return;
        }
        String host=ipPrefix+ipSurfix;
        String prompt="<"+floor+"FW01";
        if(!netWorkTest(host)){
            PromptMessage.alert(MainActivity.this,"你的网络不能连接到交换机，请换一个重试",1);
            return;
        }
        Intent intent=new Intent(MainActivity.this,ResultActivity.class);
        intent.putExtra("mHost",host);
        intent.putExtra("mPrompt",prompt);
        intent.putExtra("mRoom",room);
        startActivity(intent);

    }

    public boolean netWorkTest(String ip){

        boolean flag=false;
        try {
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 3 " + ip);    //ping3次
            int status = p.waitFor();
            if(status==0){
                flag=true;
            }
        } catch (IOException e) {
            e.printStackTrace();

        } catch (InterruptedException e) {
            e.printStackTrace();

        }

        return flag;
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.about){
            String msg="GxfdaNWT交换机IP查询工具\n"+
                        "版本v1.0\n"+
                        "Powered By @Molecular\n"+
                        "QQ:1246454589\n"+
                        "tel:13347511398";

            PromptMessage.messageBox(MainActivity.this,msg,"关闭",false);
        }
        return true;
    }

    @Override
    protected void onPause() {
        if(isRegReceiver){
            unregisterReceiver(wlanReceiver);
            isRegReceiver=false;
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if(isRegReceiver){
            unregisterReceiver(wlanReceiver);
            isRegReceiver=false;
        }
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if(!isRegReceiver){
            IntentFilter filter = new IntentFilter();
            filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
            filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
            registerReceiver(wlanReceiver,filter);
            isRegReceiver=false;
        }

        super.onResume();
    }
}
