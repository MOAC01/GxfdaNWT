package com.moac;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class ResultActivity extends AppCompatActivity {

    private IPAdapter adapter;
    private ListView listView;
    private TextView textView_no;
    private TextView textView_result;
    private ProgressBar progressBar;
    private TelnetUtil telnetUtil;
    private List<IpInfo> mList;
    private int mRoom;

    private Handler uiHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:
                    PromptMessage.messageBox(ResultActivity.this,"连接失败，请检查网络","好的",true);
                    break;
                case 2:
                    PromptMessage.alert(ResultActivity.this,"断开连接失败，可能已经断开或未连接",1);
                    break;
                case 3:
                    PromptMessage.messageBox(ResultActivity.this,"读取过程中错误","好的",false);
                    break;
                case 4:
                    PromptMessage.messageBox(ResultActivity.this,"操作超时","好的",false);
                    break;
                case 5:
                    progressBar.setVisibility(View.INVISIBLE);
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
        String host=intent.getStringExtra("mHost");
        String prompt=intent.getStringExtra("mPrompt");
        int room=intent.getIntExtra("mRoom",0);
        mRoom=room;
        telnetUtil=new TelnetUtil(host,prompt,room);
        telnetUtil.setHandler(uiHandler);
        new TelnetThread().start();

    }

    public void dealResult(){
        progressBar.setVisibility(View.INVISIBLE);
        if(mList.size()==0){
            textView_no.setVisibility(View.VISIBLE);
            return;
        }

        adapter=new IPAdapter(ResultActivity.this,android.R.layout.simple_list_item_2,mList);
        listView.setAdapter(adapter);
        String text=mRoom+"室的墙口IP如下：";
        textView_result.setText(text);
        textView_result.setVisibility(View.VISIBLE);
        PromptMessage.alert(ResultActivity.this,"查找完成",0);

    }


    private  class TelnetThread extends Thread{
        @Override
        public void run() {
            Message message=new Message();
            if(telnetUtil.connectToSwitch()){
                mList=telnetUtil.get();
            }else {
                return;
            }

            telnetUtil.disConnect();
            message.what=5;
            uiHandler.sendMessage(message);

        }
    }
}
