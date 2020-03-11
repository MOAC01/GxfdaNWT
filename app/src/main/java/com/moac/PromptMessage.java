package com.moac;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

public class PromptMessage {

    public static void messageBox(Context context,String msg,String positive,boolean cancleble){
        AlertDialog.Builder dialog=new AlertDialog.Builder(context);
        dialog.setMessage(msg);
        dialog.setPositiveButton(positive, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog.setCancelable(cancleble);
        dialog.show();

    }

    public static void alert(Context context,String msg,int pause){
        Toast.makeText(context,msg,pause).show();
    }
}
