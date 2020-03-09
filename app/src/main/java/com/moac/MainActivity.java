package com.moac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private TelnetUtil telnetUtil;
    private EditText editText;
    private Button search;
    private ProgressBar progressBar;
    private Map<String,String> ipaddress=new HashMap<>();

    private Handler uiHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    messageBox("连接失败，请检查网络","好的",true);
                    break;
                case 2:
                    alert("断开连接失败，可能已经断开或未连接",1);
                    break;
                case 3:
                    messageBox("读取过程中错误","好的",false);
                    break;
                case 4:
                    messageBox("操作超时","好的",false);
                    break;
                case 5:
                    progressBar.setVisibility(View.INVISIBLE);
                    editText.setEnabled(true);
                    search.setEnabled(true);
                    alert("查找完成",0);
                    break;


            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();

    }

    public void alert(String msg,int time){
        Toast.makeText(this,msg,time).show();
    }


    public void initWidget(){
        editText=findViewById(R.id.input_text);
        search=findViewById(R.id.btn_search);
        progressBar=findViewById(R.id.progress);
        progressBar.setVisibility(View.INVISIBLE);

        search.setOnClickListener(new View.OnClickListener() {   //按钮点击事件
            @Override
            public void onClick(View v) {
                String str=editText.getText().toString();
                if(str==null || str.isEmpty()){
                    alert("还没有输入办公室",0);
                    return;
                }
                int room=Integer.parseInt(str);
                if(initTelnetParam(room)){
                    new TelnetThread().start();
                    progressBar.setVisibility(View.VISIBLE);
                    editText.setEnabled(false);
                    search.setEnabled(false);
                }


            }
        });
    }

    public boolean initTelnetParam(int room){
        int floor=room/100;
        String ipPrefix="10.0.0.";
        String ipSurfix=String.valueOf(floor*10);
        if(floor==0 || floor>20){
            alert("办公室输入错误，请检查",0);
            return false;
        }
        String host=ipPrefix+ipSurfix;
        String prompt="<"+floor+"FW01";
        telnetUtil=new TelnetUtil(host,prompt,room);
        telnetUtil.setHandler(uiHandler);
        Log.d("MainActivity-Prefix",ipPrefix);
        Log.d("MainActivity-Surfix",ipSurfix);
        Log.d("MainActivity-Host",host);
        Log.d("MainActivity-Prompt",prompt);
        return true;

    }

    public void messageBox(String msg,String positi,boolean cancle){

        AlertDialog.Builder dialog=new AlertDialog.Builder(MainActivity.this);
        dialog.setMessage(msg);
        dialog.setPositiveButton(positi, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.setCancelable(cancle);
        dialog.show();

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

            messageBox(msg,"关闭",false);
        }
        return true;
    }

    private  class TelnetThread extends Thread{
        @Override
        public void run() {
            Log.v("TelnetObject", String.valueOf(telnetUtil==null));
            if(telnetUtil.connectToSwitch()){
                ipaddress=telnetUtil.get();
            }else {
                return;
            }

            telnetUtil.disConnect();

            Set<String> keys=ipaddress.keySet();
            Iterator iterator=keys.iterator();
            while (iterator.hasNext()){
                String disc= (String) iterator.next();
                Log.d("TelnetThread",disc+":"+ipaddress.get(disc));
            }

            Message message=new Message();
            message.what=5;
            uiHandler.sendMessage(message);

        }
    }




}
