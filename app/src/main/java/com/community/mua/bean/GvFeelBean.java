package com.community.mua.bean;

import java.io.Serializable;

public class GvFeelBean implements Serializable {

    private int desId;
    private String name;
    private boolean isSelect;
    public GvFeelBean(int desId, String name) {
        this.desId = desId;
        this.name = name;
    }

    public int getDesId() {
        return desId;
    }

    public void setDesId(int desId) {
        this.desId = desId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
