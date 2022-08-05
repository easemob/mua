package com.community.mua.utils;

import com.community.mua.R;
import com.community.mua.bean.GvFeelBean;

import java.util.ArrayList;
import java.util.List;

public class BatteryUtils {

    public static int getBatteryRes(int value,boolean isCharging){
        if (isCharging){
            return R.drawable.battery_bolt;
        }
        if (value < 25){
            return R.drawable.battery_1;
        } else if(value >= 25  && value < 50){
            return R.drawable.battery_25;
        } else if(value >= 50  && value < 75){
            return R.drawable.battery_50;
        } else if(value >= 75  && value < 100){
            return R.drawable.battery_75;
        } else if (value == 0){
            return R.drawable.battery_0;
        } else {
            return R.drawable.battery_100;
        }
    }

    public static  List<GvFeelBean> list = new ArrayList<>();
    static {
        list.add(new GvFeelBean(R.drawable.calm,"平静"));
        list.add(new GvFeelBean(R.drawable.happy,"开心"));
        list.add(new GvFeelBean(R.drawable.emo,"Emo"));
        list.add(new GvFeelBean(R.drawable.tired,"疲惫"));
        list.add(new GvFeelBean(R.drawable.mad,"生气"));
        list.add(new GvFeelBean(R.drawable.sad,"伤心"));
        list.add(new GvFeelBean(R.drawable.proud,"得意"));
        list.add(new GvFeelBean(R.drawable.boring,"无聊"));
        list.add(new GvFeelBean(0,""));
        list.add(new GvFeelBean(R.drawable.fret,"烦躁"));
        list.add(new GvFeelBean(R.drawable.lone,"孤单"));
        list.add(new GvFeelBean(0,""));
    }
}
