package com.android.gk.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.gk.Interface.ItemClickListener;
import com.android.gk.R;

public class CommentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtUserName, txtTime, txtCommentContent;

    public TextView txtUserInitial;
    private ItemClickListener itemClickListener;

   public SwitchCompat switchDisplay;


    public CommentsViewHolder(@NonNull View itemView) {
        super(itemView);

        txtUserInitial = (TextView) itemView.findViewById(R.id.userInitial);

        txtUserName = (TextView) itemView.findViewById(R.id.userName);

        txtTime = (TextView) itemView.findViewById(R.id.dateTime);

        txtCommentContent = (TextView) itemView.findViewById(R.id.commentContent);

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

