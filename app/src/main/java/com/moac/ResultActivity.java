package com.moac;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity {

    private IPAdapter adapter;
    private ListView listView;
    private TextView textView_no;
    private TextView textView_result;
    private ProgressBar progressBar;
    private List<IpInfo> mList;

    private Request request;
    private OkHttpClient client;
    private String requestUrl;

    private Handler uiHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case -1:
                    PromptMessage.messageBox(ResultActivity.this,"连接失败，请检查网络","好的",true);
                    break;
                case -2:
                    PromptMessage.messageBox(ResultActivity.this,"服务器错误","关闭",true);
                    break;
                case 2:
                    PromptMessage.messageBox(ResultActivity.this,"操作超时","好的",false);
                    break;
                case 1:
                    progressBar.setEnabled(false);
                    dealResult();
                    break;


            }
        }
    };



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        initViews();
        prepare();
    }

    public void initViews(){
        listView=findViewById(R.id.list_view);
        progressBar=findViewById(R.id.load);
        textView_no=findViewById(R.id.no_result);
        textView_result=findViewById(R.id.s_res);

    }

    public void prepare(){
        listView=findViewById(R.id.list_view);
        Intent intent=getIntent();
        requestUrl=intent.getStringExtra("requestURL");
        new RequestThread().start();

    }

    public void dealResult(){
        progressBar.setVisibility(View.INVISIBLE);
        if(mList==null || mList.size()==0){
            textView_no.setVisibility(View.VISIBLE);
            return;
        }

        adapter=new IPAdapter(ResultActivity.this,android.R.layout.simple_list_item_2,mList);
        listView.setAdapter(adapter);
        String text="找到以下结果：";
        textView_result.setText(text);
        textView_result.setVisibility(View.VISIBLE);
        PromptMessage.alert(ResultActivity.this,"查找完成",0);

    }


    private  class RequestThread extends Thread{
        @Override
        public void run() {

              Message message=new Message();
              client=new OkHttpClient();
              request=new Request.Builder().url(requestUrl).build();
              try {
                    Response response=client.newCall(request).execute();
                    int code=response.code();
                    String respStr=response.body().string();

                    if(code==200){
                        message.what=1;
                        try {
                            mList= JSON.parseArray(respStr,IpInfo.class);
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }else if(code>=500){
                        message.what=-2;
                    }else if(code>=400){
                        message.what=2;
                    }

                } catch (IOException e) {
                    message.what=-1;
                    e.printStackTrace();
                }

              uiHandler.sendMessage(message);

        }
    }
}
