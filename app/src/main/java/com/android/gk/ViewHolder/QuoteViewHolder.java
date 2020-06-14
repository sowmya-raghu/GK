package com.android.gk.ViewHolder;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.gk.Interface.ItemClickListener;
import com.android.gk.R;
import com.google.android.material.button.MaterialButton;

public class QuoteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public ImageView quoteImage;

    public MaterialButton shareButton, deleteButton, saveBox, commentsBox;
    public CheckBox likeBox;
    private ItemClickListener itemClickListener;

   public SwitchCompat switchDisplay;


    public QuoteViewHolder(@NonNull View itemView) {
        super(itemView);

        quoteImage = (ImageView)itemView.findViewById(R.id.quote_image);

        shareButton = (MaterialButton)itemView.findViewById(R.id.iconShare);
        commentsBox = (MaterialButton)itemView.findViewById(R.id.iconComments);

        likeBox = (CheckBox) itemView.findViewById(R.id.iconLike);
        saveBox = (MaterialButton) itemView.findViewById(R.id.iconSave);

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

