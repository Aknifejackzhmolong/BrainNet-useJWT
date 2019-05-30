package com.brainsci.websocket.server;

import com.brainsci.springsecurity.util.GsonPlus;
import com.brainsci.websocket.form.WebSocketMessageForm;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ServerEndpoint(value = "/webSocket/{token}")
@Component
public class WebSocketServer {
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);
    private static WebSocketServer webSocketServer;
    private Session session;
    private static Map<String,Session> sessionPool = new HashMap<String,Session>();
    private static Map<String,String> sessionIds = new HashMap<String,String>();

    // 解决无法注入的问题
    @PostConstruct
    public void init() {
        webSocketServer = this;
    }
    /**
     * 用户连接时触发
     * @param session
     * @param token
     */
    @OnOpen
    public void open(Session session,@PathParam(value="token")String token){
        this.session = session;
        sessionPool.put(token, session);
        sessionIds.put(session.getId(), token);
    }

    /**
     * 收到信息时触发
     * @param message
     */
    @OnMessage
    public void onMessage(String message){
        sendMessage(GsonPlus.GSON.toJson(new WebSocketMessageForm("heartbeat", "1")),sessionIds.get(session.getId()));
        System.out.println("发送人:"+sessionIds.get(session.getId())+"内容:"+message);
    }

    /**
     * 连接关闭触发
     */
    @OnClose
    public void onClose(){
        sessionPool.remove(sessionIds.get(session.getId()));
        sessionIds.remove(session.getId());
    }

    /**
     * 发生错误时触发
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
    }

    /**
     *信息发送的方法
     * @param message
     * @param token
     */
    public static void sendMessage(String message,String token){
        Session s = sessionPool.get(token);
        if(s!=null){
            try {
                s.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取当前连接数
     * @return
     */
    public static int getOnlineNum(){
        if(sessionIds.values().contains("niezhiliang9595")) {

            return sessionPool.size()-1;
        }
        return sessionPool.size();
    }

    /**
     * 获取在线用户名以逗号隔开
     * @return
     */
    public static String getOnlineUsers(){
        StringBuffer users = new StringBuffer();
        for (String key : sessionIds.keySet()) {//niezhiliang9595是服务端自己的连接，不能算在线人数
            if (!"niezhiliang9595".equals(sessionIds.get(key)))
            {
                users.append(sessionIds.get(key)+",");
            }
        }
        return users.toString();
    }

    /**
     * 信息群发
     * @param msg
     */
    public static void sendAll(String msg) {
        for (String key : sessionIds.keySet()) {
            if (!"niezhiliang9595".equals(sessionIds.get(key)))
            {
                sendMessage(msg, sessionIds.get(key));
            }
        }
    }

    /**
     * 多个人发送给指定的几个用户
     * @param msg
     * @param persons  用户s
     */

    public static void SendMany(String msg,String [] persons) {
        for (String token : persons) {
            sendMessage(msg, token);
        }

    }
}