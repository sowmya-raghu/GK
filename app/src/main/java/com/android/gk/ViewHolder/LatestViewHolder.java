package com.android.gk.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.gk.Interface.ItemClickListener;
import com.android.gk.R;

public class LatestViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtPostTitle;
    public ImageView txtPostImage;

    public TextView txtPostCategory;
    private ItemClickListener itemClickListener;


    public LatestViewHolder(@NonNull View itemView) {
        super(itemView);

        txtPostTitle = (TextView)itemView.findViewById(R.id.latestTitle);
        txtPostImage = (ImageView)itemView.findViewById(R.id.latestImage);

        txtPostCategory = (TextView) itemView.findViewById(R.id.latestCat);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onClick(v, getAdapterPosition(),false);
    }
}

