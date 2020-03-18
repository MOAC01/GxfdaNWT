package com.moac;

import android.os.Handler;
import android.os.Message;

import org.apache.commons.net.telnet.TelnetClient;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

public class TelnetUtil {

    private TelnetClient telnetClient;
    private static final String user="gxfda";
    private static final String password="wsldh@2018";
    private String host;
    private int port=23;
    private int room;
    private String prompt;
    private InputStream in;
    private PrintStream out;
    private Handler handler;
    private Message message=new Message();
    private ByteArrayInputStream bi;
    private InputStreamReader is;
    private List<IpInfo> list=new ArrayList<>();

    public TelnetUtil(String host, String prompt,int room) {
        this.host = host;
        this.prompt = prompt;
        this.room=room;
        telnetClient=new TelnetClient();
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public boolean connectToSwitch(){
        boolean connFlag=false;
        try {
            telnetClient.connect(host,port);
            in=telnetClient.getInputStream();
            out=new PrintStream(telnetClient.getOutputStream());
            login();
            connFlag=true;
        } catch (IOException e) {
            e.printStackTrace();
            message.what=1;              //连接错误
            handler.sendMessage(message);

        }

        return connFlag;
    }

    private void login(){
        readUntil("Username:",3);
        write(user);
        readUntil("Password:",3);
        write(password);
        readUntil(prompt,3);
        exeCuteCommand("super");                       //登录后进入特权模式
        readUntil(" Password:",3);
        write(password);
        readUntil(prompt,3);

    }

    private void write(String value) {
        out.println(value);
        out.flush();
    }

    private void exeCuteCommand(String command){
        write(command);
    }

    public List<IpInfo> get(){
        getIPConfig();
        return this.list;
    }

    private void getIPConfig(){
        String cmd="display current-configuration interface GigabitEthernet1/0/";
        String resultStr;
        for(int i=1;i<=52;i++)
        {

            exeCuteCommand(cmd+i);
            resultStr=readUntil("return",3);
            if(resultStr.endsWith(" shutdown"))
                break;

            setIPConfig(resultStr,room);
        }


    }

    private void setIPConfig(String config,int room){

        String startTag=" description";
        String[]tags;
        String[] subTags = new String[0];
        String[]ips;
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");

        try {
            bi=new ByteArrayInputStream(config.getBytes("ascii"));
            is=new InputStreamReader(bi);
            BufferedReader br=new BufferedReader(is);
            String line;
            while ((line=br.readLine())!=null){
                if(line.contains(startTag)){
                    tags=line.trim().split(" ");
                    subTags=tags[1].split("-");
                    if(subTags.length>=2){
                        if(!(pattern.matcher(subTags[1]).matches()) || (room!=Integer.parseInt(subTags[1]))){
                            break;
                        }
                    }else {
                        break;
                    }

                }

                if(line.contains("user-bind")){
                    ips=line.trim().split(" ");
                    //map.put(ips[2],subTags[0]);
                    IpInfo info=new IpInfo(subTags[0],ips[2]);
                    list.add(info);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String readUntil(String pattern,int timeOut) {
        StringBuffer sb = new StringBuffer();
        try {
            int len ;

            long startTime=System.currentTimeMillis();
            long currentTime;
            String result;
            while((len = in.read()) != -1) {

                sb.append((char)len);
                result=sb.toString();
                if(result.endsWith(pattern) || result.endsWith(" shutdown")) {
                    return result;
                }

                currentTime=System.currentTimeMillis();
                if(currentTime-startTime>=timeOut*1000){
                    throw new TimeoutException();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
            message.what=3;                //读取返回结果错误
            handler.sendMessage(message);
        } catch (TimeoutException e) {
            e.printStackTrace();
            message.what=4;               //读取超时
            handler.sendMessage(message);
        }

        return null;
    }

    public void disConnect(){
        try {
            telnetClient.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
            message.what=2;      //关闭连接错误
            handler.sendMessage(message);

        }
    }

}
