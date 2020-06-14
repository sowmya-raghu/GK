package com.android.gk.ViewHolder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.gk.Interface.ItemClickListener;
import com.android.gk.R;
import com.google.android.material.button.MaterialButton;

public class PublishedPostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtPostTitle;
    public ImageView txtPostImage;

    public MaterialButton continueReading, commentBtn, shareBtn;
    public CheckBox likeBtn;
    private ItemClickListener itemClickListener;


    public PublishedPostViewHolder(@NonNull View itemView) {
        super(itemView);

        txtPostTitle = (TextView)itemView.findViewById(R.id.postTitle);
        txtPostImage = (ImageView)itemView.findViewById(R.id.post_image);

        continueReading = (MaterialButton)itemView.findViewById(R.id.continueReading);

        likeBtn = (CheckBox)itemView.findViewById(R.id.iconLike);
        commentBtn = (MaterialButton)itemView.findViewById(R.id.iconComments);
        shareBtn = (MaterialButton)itemView.findViewById(R.id.iconShare);

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

