package com.community.mua.bean;

public class RecordBean {
    private String work;
    private String day;
    private String timeStamp;
    private boolean isSelect;
    public RecordBean() {
    }

    public RecordBean(String work, String day) {
        this.work = work;
        this.day = day;
    }

    public RecordBean(String work, String day, String timeStamp) {
        this.work = work;
        this.day = day;
        this.timeStamp = timeStamp;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
