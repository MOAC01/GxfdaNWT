package com.moac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private TelnetUtil telnetUtil;
    private EditText editText;
    private Button search;
    private List<String> ipaddress=new ArrayList<>();


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
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str=editText.getText().toString();
                if(str==null || str.isEmpty()){
                    alert("还没有输入办公室",0);
                    return;
                }
                int room=Integer.parseInt(str);
                initTelnetParam(room);
                //alert(str,0);
                new TelnetThread().start();

            }
        });
    }

    public void initTelnetParam(int room){
        int floor=room/100;
        String ipPrefix="10.0.0.";
        String ipSurfix=String.valueOf(floor*10);
        if(floor==0 || floor>20){
            alert("办公室输入错误，请检查",0);
            return;
        }
        String host=ipPrefix+ipSurfix;
        String prompt="<"+floor+"FW01";
        telnetUtil=new TelnetUtil(host,prompt,room);
        Log.d("MainActivity-Prefix",ipPrefix);
        Log.d("MainActivity-Surfix",ipSurfix);
        Log.d("MainActivity-Host",host);
        Log.d("MainActivity-Prompt",prompt);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private  class TelnetThread extends Thread{
        @Override
        public void run() {
            telnetUtil.connectToSwitch();
            ipaddress=telnetUtil.get();
            telnetUtil.disConnect();

        }
    }




}
