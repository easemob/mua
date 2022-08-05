package com.community.mua.bean;

public class SettingGridBean {
    private int icon;
    private String name;
    public int getIcon() {
        return icon;
    }

    public SettingGridBean(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
