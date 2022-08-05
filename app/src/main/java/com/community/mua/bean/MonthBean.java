package com.community.mua.bean;

import java.util.ArrayList;
import java.util.List;

public class MonthBean {
    private Integer month;
    private boolean isSelect;

    public MonthBean(Integer month) {
        this.month = month;
    }

    public MonthBean(Integer month, boolean isSelect) {
        this.month = month;
        this.isSelect = isSelect;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }
}
