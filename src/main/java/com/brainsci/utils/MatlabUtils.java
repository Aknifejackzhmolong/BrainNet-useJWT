package com.brainsci.utils;

import com.brainsci.springsecurity.util.GsonPlus;
import com.brainsci.websocket.form.WebSocketMessageForm;
import com.brainsci.websocket.server.WebSocketServer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MatlabUtils {
    public static File matlabApplication;
    private static MatlabUtils matlabUtils = new MatlabUtils();
    public static HashMap<String, String> state = new HashMap<>();
    public static MatlabUtils getMatlabUtils(){
        return matlabUtils;
    }
    public void run(String mcmd, String token, File path){
        String[] cmd = {
                matlabApplication.getAbsolutePath(),
                "-noFigureWindows",
                "-nodesktop",
                "-nosplash",
                "-logfile "+path.getAbsolutePath()+"/matlab.log",
                "-r", "try " + mcmd + "exit;catch ErrorInfo; disp('An Exception Occurred');disp(ErrorInfo);rmdir('"+path.getAbsolutePath()+"','s');exit; end"};
        try{
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = br.readLine();
            while(p.isAlive())
            if(line != null){
                WebSocketServer.sendMessage(GsonPlus.GSON.toJson(new WebSocketMessageForm("gretna", line)),token);
                line = br.readLine();
            }
            br.close();
            p.destroy();
            if (path.exists()){
                String timestamp = String.valueOf(new Date().getTime()/1000);  // 精确到秒
                File zipTag = new File(path.getPath()+"_"+timestamp+".zip");
                System.out.println("compress...");
                FileOutputStream fos = new FileOutputStream(zipTag);
                ZipUtils.toZip(path.getAbsolutePath(), fos, true);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
