package com.maimai.server.beans;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;

public class userText {
    private String name;
    private String text;
    private String time;

//    public userText(String message) {
//        try {
//            JSONObject jsonObject= JSON.parseObject(message);
//            this.name=jsonObject.getString("name");
//            this.text=jsonObject.getString("text");
//            this.date=jsonObject.getString("date");
//        }
//        catch (Exception e){e.printStackTrace();}
//    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }





}
