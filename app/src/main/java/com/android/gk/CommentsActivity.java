package com.android.gk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.gk.R;
import com.android.gk.Common.common;
import com.android.gk.Model.Comments;
import com.android.gk.Model.Quote;
import com.android.gk.ViewHolder.CommentsViewHolder;
import com.android.gk.ViewHolder.QuoteViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CommentsActivity extends AppCompatActivity {

    DatabaseReference fromDb;
    DatabaseReference databaseEntry;
    RecyclerView recycler_comments;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerOptions<Comments> options;

    FirebaseRecyclerAdapter<Comments, CommentsViewHolder> adapter;

    EditText commentText;
    ImageView sendIcon;
    String userName;
    TextView commentLikes;

    public static final SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    //Start and Stop of adapters
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_comments);

        getSupportActionBar().hide();


        //Getting Recycler and Text Views and Image Views
        Intent i = getIntent();
        final String quoteId = i.getStringExtra("QuoteId");
        final String isPost = i.getStringExtra("isPost");

        sendIcon = findViewById(R.id.send);

        commentText = findViewById(R.id.commenttext);

        final String currentDateandTime = inputFormat.format(new Date());

        commentLikes = findViewById(R.id.like_box);

        //Adding to Comments Table

        sendIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String commentContent = commentText.getEditableText().toString();
                databaseEntry = FirebaseDatabase.getInstance().getReference("Comments");

                String id = databaseEntry.push().getKey();

                if(common.currentUser!=null){
                    Comments comments = new Comments(id,quoteId, common.currentUser,commentContent, currentDateandTime);

                    databaseEntry.child(quoteId).child(id).setValue(comments);
                }else{
                    Comments comments = new Comments(id,quoteId, common.anonymousUser,commentContent, currentDateandTime);

                    databaseEntry.child(quoteId).child(id).setValue(comments);
                }


                commentText.setText("");
            }
        });


        if(isPost.equalsIgnoreCase("true")){
            DatabaseReference fromPost = FirebaseDatabase.getInstance().getReference();
            Query postQuery = fromPost.child("Post");
            postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String numberOfLikes = (String) dataSnapshot.child(quoteId).child("numberOfLikes").getValue();

                    if (numberOfLikes.equalsIgnoreCase("0")){
                        commentLikes.setText("No Likes Yet!!");
                    }else{
                        commentLikes.setText(numberOfLikes + " Likes");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }else{

            DatabaseReference fromQuote = FirebaseDatabase.getInstance().getReference();
            Query queryQuote = fromQuote.child("Quote");
            queryQuote.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String numberOfLikes = (String) dataSnapshot.child(quoteId).child("numberOfLikes").getValue();

                    if (numberOfLikes.equalsIgnoreCase("0")){
                        commentLikes.setText("No Likes Yet!!");
                    }else{
                        commentLikes.setText(numberOfLikes + " Likes");
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }



        fromDb = FirebaseDatabase.getInstance().getReference("Comments");

        recycler_comments = (RecyclerView)findViewById(R.id.comments_recycler_view);
        recycler_comments.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recycler_comments.setLayoutManager(layoutManager);

        options = new FirebaseRecyclerOptions.Builder<Comments>().setQuery(fromDb.child(quoteId), Comments.class).build();

        adapter = new FirebaseRecyclerAdapter<Comments, CommentsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CommentsViewHolder commentsViewHolder, int i, @NonNull Comments comments) {

                DatabaseReference fromUser = FirebaseDatabase.getInstance().getReference();
               /* Query queryUser = fromUser.child("Comments");
                queryUser.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         String userName = (String) dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("name").getValue();
                         Log.d("User Name", "Inside Admin"+userName);

                    }



                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });*/

                commentsViewHolder.txtUserName.setText(comments.getUserName());
                commentsViewHolder.txtUserInitial.setText(comments.getUserName().substring(0,1));
                commentsViewHolder.txtCommentContent.setText(comments.getCommentContent());

                Date date = null;
                try {
                    date = inputFormat.parse(comments.getLastUpdatedDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                String niceDateStr = (String) DateUtils.getRelativeTimeSpanString(date.getTime() , Calendar.getInstance().getTimeInMillis(), DateUtils.MINUTE_IN_MILLIS);

                commentsViewHolder.txtTime.setText(niceDateStr);



            }

            @NonNull
            @Override
            public CommentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CommentsViewHolder(LayoutInflater.from(CommentsActivity.this).inflate(R.layout.comment_item, parent, false));
            }
        };
        recycler_comments.setAdapter(adapter);

    }
}
