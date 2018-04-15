package com.example.gaurang.object_detection;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by gaurang on 15-04-2018.
 */

public class DataModel {
    // init the item view's
    String name,desc,sm;
    int image;

    public DataModel(String name, String desc, String sm, int image) {
        this.name = name;
        this.desc = desc;
        this.sm = sm;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getSm() {
        return sm;
    }

    public void setSm(String sm) {
        this.sm = sm;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}