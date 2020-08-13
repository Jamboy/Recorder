package com.speakin.recorder;

import android.app.Application;

/**
 * Copyright 2017 SpeakIn.Inc
 * Created by west on 2017/10/25.
 */

public class RecorderApp extends Application {

    public static RecorderApp app = null;
    private String TEAM_ID; //client-salve 连接
    private String ITEM_NO;

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
    }

    public String getTeamID(){
        return TEAM_ID;
    }

    public void setTeamID(String TEAM_ID){
        this.TEAM_ID = TEAM_ID;
    }

    public String getItemNo(){
        return ITEM_NO;
    }

    public void setItemNo(String ITEM_NO){
        this.ITEM_NO = ITEM_NO;
    }

}
