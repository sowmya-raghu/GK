package com.android.gk.ViewHolder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.gk.Interface.ItemClickListener;
import com.android.gk.R;
import com.google.android.material.button.MaterialButton;

public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtCategoryName,txtCategoryDesc,noDisplay, yesDisplay;
    public ImageView txtCategoryImage;

    public MaterialButton exploreButton, editBtn;
    private ItemClickListener itemClickListener;

   public SwitchCompat switchDisplay;


    public CategoryViewHolder(@NonNull View itemView) {
        super(itemView);

        txtCategoryName = (TextView)itemView.findViewById(R.id.category_title);
        txtCategoryImage = (ImageView)itemView.findViewById(R.id.category_image);
        txtCategoryDesc = (TextView)itemView.findViewById(R.id.category_desc);


        exploreButton = (MaterialButton)itemView.findViewById(R.id.explore_category);
       // yesDisplay = (TextView) itemView.findViewById(R.id.yesDisplay);
//        txtCategoryImage.setClipToOutline(true);
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

