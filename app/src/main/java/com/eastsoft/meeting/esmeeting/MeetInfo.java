package com.eastsoft.meeting.esmeeting;

import cn.bmob.v3.BmobObject;

/**
 * Created by Admin on 2015/11/12.
 */
public class MeetInfo extends BmobObject {
    private String startTime;//开始时间
    private String endTime;//会议结束时间
    private String name;//会议名称
    private String address;//会议地址
    private String participant;//参会人员
    private String day;//天

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getParticipant() {
        return participant;
    }

    public void setParticipant(String participant) {
        this.participant = participant;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
