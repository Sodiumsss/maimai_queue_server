package com.maimai.server.websocket;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.maimai.server.beans.userText;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/socket/{acc}")
@Component
public class wb {
    private static final Map<String, Session> onlineSessions = new ConcurrentHashMap<>();
    private static final ArrayList<String> nameQueue =new ArrayList<>();
    private static final HashSet<String> nameSet =new HashSet<>();
    private static final ArrayList<userText> textArray =new ArrayList<>();
    private static final boolean debug=false;

    private static final String Information="你说得对，但是《》是";

    //排卡队列功能开始
    public ArrayList<String> getQueue()

    {
        return nameQueue;
    }
    public boolean pushQueue(String acc)
    {
        if (onlineSessions.get(acc)==null){return false;}

        if (nameSet.add(acc))
        {
            if (nameQueue.add(acc))
            {
                printQueue();
                return true;
            }
        }
        return false;
    }
    public boolean popQueue(String acc)
    {
        try {
            nameSet.remove(acc);
            nameQueue.remove(acc);
            printQueue();
            return true;
        }catch (Exception e)
        {
            return false;
        }

    }
    public boolean clearQueue()
    {
        nameQueue.clear();
        nameSet.clear();
        return true;
    }
    public boolean balanceQueue()
    {
        if (nameQueue.size()>2)
        {
            String v1= nameQueue.get(0);
            String v2= nameQueue.get(1);
            nameQueue.remove(0);
            nameQueue.remove(0);
            nameQueue.add(v1);
            nameQueue.add(v2);
            return true;
        }
        return false;
    }
    public void printQueue()
    {
        StringBuilder tmp= new StringBuilder("[List]{");
        for (String i : nameQueue)
        {
            tmp.append(i).append(" ");
        }
        tmp.append("}");
        System.out.println(tmp);
    }
    //排卡队列功能结束

    //消息数组功能开始
    public void pushText(userText userText)
    {
        while (textArray.size()>30)
        {
            textArray.remove(0);
        }
        textArray.add(userText);

        if (debug)
        {
            System.out.println("[NowTextSize]"+textArray.size());
        }
    }
    //public void clearText()
    //    {
    //        textArray.clear();
    //    }

    //消息数组功能结束




    @OnOpen
    public void onOpen(Session session, @PathParam("acc") String acc) {
        if(acc==null)
        {
            return;
        }

        if (onlineSessions.get(acc)==null)
        {
            onlineSessions.put(acc,session);

            if (debug)
            {
                System.out.println("[Connected]"+acc+"[Online]"+onlineSessions.size());
            }

        }
        else
        {
            if (debug)
            {
                System.out.println("[Reconnected]"+acc+"[Online]"+onlineSessions.size());
            }
        }


    }

    @OnClose
    public void onClose(Session session,@PathParam("acc")String acc) throws IOException {
        if (debug)
        {
            System.out.println("[Closed]"+acc);
        }
        session.close();
        onlineSessions.remove(acc);
    }


    @OnError
    public void onError(Session session,Throwable throwable)
    {
        if (debug)
        {
            System.out.println("[Error]"+session.getId());
        }
        throwable.printStackTrace();
    }


    @OnMessage
    public void onMessage(String message,Session session) {
        if (debug)
        {
            System.out.println("[Message]"+message);

        }
        try {
            JSONObject jsonObject=JSON.parseObject(message);
            int type=jsonObject.getInteger("type");
            JSONObject object = new JSONObject();
            switch (type)
            {
                case 1 -> {
                    if (debug)
                    {
                        System.out.println("[GetInformation]");

                    }
                    object.put("type", 1);
                    object.put("data", Information);
                    session.getAsyncRemote().sendText(object.toString());
                }
                case 2 -> {
                    int index=jsonObject.getInteger("index");
                    boolean status=false;
                    switch (index)
                    {
                        case 1->
                        {
                            String name = jsonObject.getString("name");
                            if (debug)
                            {
                                System.out.println("[Try_PushQueue]"+name);

                            }
                            status=pushQueue(name);
                        }
                        case 2->
                        {
                            String name = jsonObject.getString("name");
                            if (debug)
                            {
                                System.out.println("[Try_PopQueue]"+name);

                            }
                            status=popQueue(name);
                        }
                        case 3 -> {
                            status=balanceQueue();
                            if (debug)
                            {
                                System.out.println("[Try_BalanceQueue]");

                            }
                        }
                        case 4 -> {
                            status=clearQueue();
                            if (debug)
                            {
                                System.out.println("[Try_ClearQueue]");

                            }
                        }
                    }
                    object.put("type",2);
                    object.put("status",status);
                    session.getAsyncRemote().sendText(object.toString());
                }
                case 3-> {
                    int index=jsonObject.getInteger("index");
                    object.put("type",3);
                    object.put("data",getQueue());
                    switch (index)
                    {
                        case 1 -> {
                            session.getAsyncRemote().sendText(object.toString());
                            if (debug)
                            {
                                System.out.println("[SendMessage]");

                            }
                        }
                        case 2 -> {
                            onlineSessions.forEach((acc, ses) -> ses.getAsyncRemote().sendText(object.toString()));
                            if (debug)
                            {
                                System.out.println("[SendMessage_All]");

                            }
                        }
                    }
                }
                case 4->{

                    int index=jsonObject.getInteger("index");
                    object.put("type",4);
                    switch (index)
                    {
                        case 1->{//获取当前消息
                            object.put("data",textArray);
                            session.getAsyncRemote().sendText(object.toString());

                        }
                        case 2->{//添加新消息
                            userText userText=jsonObject.getObject("userText", com.maimai.server.beans.userText.class);
                            if (debug)
                            {
                                System.out.println("[AddText]"+userText.getTime());
                            }
                            pushText(userText);
                            object.put("data",textArray);
                            onlineSessions.forEach((acc, ses) -> ses.getAsyncRemote().sendText(object.toString()));
                        }
                    }



                }
            }
        }catch (Exception e){e.printStackTrace();}
    }
}
