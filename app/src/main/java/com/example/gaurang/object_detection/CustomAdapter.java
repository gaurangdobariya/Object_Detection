package com.example.gaurang.object_detection;

/**
 * Created by gaurang on 16-04-2018.
 */import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.MyViewHolder> {

    private ArrayList<DataModel> dataSet;

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView name, desc, sm;
        ImageView image;

        public MyViewHolder(View itemView) {
            super(itemView);

// get the reference of item view's
            name = (TextView) itemView.findViewById(R.id.card_view_location_name);
            image = (ImageView) itemView.findViewById(R.id.card_view_image);
            desc = itemView.findViewById(R.id.card_view_location_description);
            sm = itemView.findViewById(R.id.card_view_show_more);
        }
    }
    public CustomAdapter(ArrayList<DataModel> data) {
        this.dataSet = data;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,
                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.header_logo, parent, false);

        view.setOnClickListener(MainActivity.myOnClickListener);

        MyViewHolder myViewHolder = new MyViewHolder(view);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int listPosition) {

        TextView textViewname = holder.name;

        TextView textViewsm = holder.sm;
        TextView textViewdesc = holder.desc;
        ImageView imageView = holder.image;

        textViewname.setText(dataSet.get(listPosition).getName());
        textViewsm.setText(dataSet.get(listPosition).getSm());
        textViewdesc.setText(dataSet.get(listPosition).getDesc());

        imageView.setImageResource(dataSet.get(listPosition).getImage());
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
