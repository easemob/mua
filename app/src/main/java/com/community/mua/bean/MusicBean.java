package com.community.mua.bean;

public class MusicBean {
    private int icon;
    private String name;
    private String musicName;
    public int getIcon() {
        return icon;
    }

    public MusicBean(int icon, String name) {
        this.icon = icon;
        this.name = name;
    }

    public MusicBean(int icon, String name, String musicName) {
        this.icon = icon;
        this.name = name;
        this.musicName = musicName;
    }

    public String getMusicName() {
        return musicName;
    }

    public void setMusicName(String musicName) {
        this.musicName = musicName;
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
