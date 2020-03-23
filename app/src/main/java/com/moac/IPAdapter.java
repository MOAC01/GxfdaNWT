package com.moac;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class IPAdapter extends ArrayAdapter<IpInfo> {

    private int resourceID;

    public IPAdapter(@NonNull Context context, int resource, @NonNull List<IpInfo> objects) {
        super(context, resource, objects);
        resourceID=resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //return super.getView(position, convertView, parent);
        IpInfo info=getItem(position);
        View view;
        ViewHolder viewHolder;
        if(convertView==null){
            view= LayoutInflater.from(getContext()).inflate(resourceID,null);
            viewHolder=new ViewHolder();
            viewHolder.textView1=view.findViewById(android.R.id.text1);
            viewHolder.textView2=view.findViewById(android.R.id.text2);
            view.setTag(viewHolder);
        }else {
            view=convertView;
            viewHolder= (ViewHolder) view.getTag();
        }
        viewHolder.textView1.setText(info.getIpAddr());
        viewHolder.textView2.setText(info.getWall());
        return view;

    }

    class ViewHolder{
        TextView textView1,textView2;
    }
}

