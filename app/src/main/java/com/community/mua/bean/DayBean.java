package com.community.mua.bean;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class DayBean  implements Parcelable {
    private String date;
    private String day;
    private boolean isEmpty;
    private List<DiaryBean> mDiaryBeans;

    public DayBean( String day) {
        this.day = day;
    }

    public DayBean(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    public DayBean(String date, String day) {
        this.date = date;
        this.day = day;
    }

    public DayBean(String date, String day, List<DiaryBean> diaryBeans) {
        this.date = date;
        this.day = day;
        mDiaryBeans = diaryBeans;
    }

    public DayBean(String date, String day, boolean isEmpty, List<DiaryBean> diaryBeans) {
        this.date = date;
        this.day = day;
        this.isEmpty = isEmpty;
        mDiaryBeans = diaryBeans;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<DiaryBean> getDiaryBeans() {
        return mDiaryBeans;
    }

    public void setDiaryBeans(List<DiaryBean> diaryBeans) {
        mDiaryBeans = diaryBeans;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    protected DayBean(Parcel in) {
        date = in.readString();
        day = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(day);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<DayBean> CREATOR = new Creator<DayBean>() {
        @Override
        public DayBean createFromParcel(Parcel in) {
            return new DayBean(in);
        }

        @Override
        public DayBean[] newArray(int size) {
            return new DayBean[size];
        }
    };
}
