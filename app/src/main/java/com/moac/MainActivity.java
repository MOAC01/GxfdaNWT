package com.moac;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initWidget();

    }


    public void initWidget(){
        editText=findViewById(R.id.input_text);
        search=findViewById(R.id.btn_search);

        final String regex="\\d{1,2}FW\\d{1,2}";
        search.setOnClickListener(new View.OnClickListener() {     //按钮点击事件
            @Override
            public void onClick(View v) {
                String str=editText.getText().toString();
                if(str==null || str.isEmpty()){
                    PromptMessage.alert(MainActivity.this,"还没有输入面板号",0);
                    return;
                }

                if(!str.matches(regex)){
                    PromptMessage.alert(MainActivity.this,"你输入的面板号格式不对",0);
                    return;
                }

                String url="http://10.0.5.22:8080/Switch/getip?wall="+str;
                Intent intent=new Intent(MainActivity.this,ResultActivity.class);
                intent.putExtra("requestURL",url);
                startActivity(intent);

            }
        });
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
                        "版本v2.0\n"+
                        "Powered By @Molecular\n"+
                        "QQ:1246454589\n"+
                        "tel:13347511398";

            PromptMessage.messageBox(MainActivity.this,msg,"关闭",false);
        }
        return true;
    }

}
