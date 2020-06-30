package com.android.gk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.android.gk.Common.common;
import com.android.gk.Model.Favourite;
import com.android.gk.Model.Quote;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SearchQuoteActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference fromDb;
    DatabaseReference databaseEntry;
    Date currentTime;

    RecyclerView recycler_quote;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerOptions<Quote> options;

    FirebaseRecyclerAdapter<Quote, QuoteViewHolder> adapter;

    ArrayList<Quote> itemList = new ArrayList<Quote>();

    FirebaseStorage storage;
    StorageReference storageReference;


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
        setContentView(R.layout.activity_search_quote);

        getSupportActionBar().hide();

        Toolbar toolBar = (Toolbar) findViewById(R.id.topAppBar);

        //Inflating the Menu on top of the toolbar
        //  toolBar.inflateMenu(R.menu.topbar_menu);
        toolBar.setTitle("Quotes");

//pressing backbutton takes to home page
        toolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(SearchQuoteActivity.this,QuoteActivity.class);
                startActivity(back);
            }
        });




        //Setting the progress bar

        final ProgressBar progressBar;

        progressBar = (ProgressBar)findViewById(R.id.progressBar_cyclic);
        progressBar.setProgress(20);



        Intent i = getIntent();
        String searchText = i.getStringExtra("Search");
        //Displaying Recycler view, data from database.

        fromDb = FirebaseDatabase.getInstance().getReference().child("Quote");

        recycler_quote = (RecyclerView)findViewById(R.id.recycler_quote);
        recycler_quote.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recycler_quote.setLayoutManager(layoutManager);

        options = new FirebaseRecyclerOptions.Builder<Quote>().setQuery(fromDb.orderByChild("quoteContent").startAt(searchText).endAt(searchText + "\uf8ff"), Quote.class).build();

        adapter = new FirebaseRecyclerAdapter<Quote, QuoteViewHolder>(options) {



            @Override
            protected void onBindViewHolder(@NonNull final QuoteViewHolder quoteViewHolder, int i, @NonNull final Quote quote) {
                //ImageView quoteImage = findViewById(R.id.quote_image);

                Resources res = getResources();
                Bitmap src = BitmapFactory.decodeResource(res,R.drawable.quote1final );
                RoundedBitmapDrawable dr =
                        RoundedBitmapDrawableFactory.create(res, src);
                final float roundPx = (float) src.getWidth() * 0.06f;
                dr.setCornerRadius(roundPx);
                quoteViewHolder.quoteImage.setImageDrawable(dr);

                Picasso.with(getBaseContext()).load(quote.getQuoteImage()).fit().into(quoteViewHolder.quoteImage);



                if(common.currentUser!=null) {
                    databaseEntry = FirebaseDatabase.getInstance().getReference("Favourite");
                    Query queryFav = databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(quote.getQuoteId());

                    queryFav.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Favourite fav = dataSnapshot.getValue(Favourite.class);
                                Log.d("quoteId","Inside like"+fav.getFavId());
                                if (fav.getIsLike().equalsIgnoreCase("true")) {
                                    quoteViewHolder.likeBox.setButtonDrawable(R.drawable.ic_favorite_black_24dp);

                                } else {
                                    quoteViewHolder.likeBox.setButtonDrawable(R.drawable.ic_favorite_border_black_24dp);
                                }
                            } else {
                                quoteViewHolder.likeBox.setButtonDrawable(R.drawable.ic_favorite_border_black_24dp);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }

                if(quote.getNumberOfLikes().equalsIgnoreCase("0")){
                    quoteViewHolder.likeBox.setText("Like");
                }else{
                    quoteViewHolder.likeBox.setText(quote.getNumberOfLikes()+" Likes");
                }

                progressBar.setVisibility(View.INVISIBLE);

                //Comments Box
                quoteViewHolder.commentsBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent comment = new Intent(SearchQuoteActivity.this, com.android.gk.CommentsActivity.class);
                        comment.putExtra("QuoteId", quote.getQuoteId());
                        comment.putExtra("isPost", "false");
                        startActivity(comment);
                    }
                });

                //Like Button activity

                quoteViewHolder.likeBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(common.currentUser!=null) {
                            Query queryFav = databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(quote.getQuoteId());

                            queryFav.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Favourite fav = dataSnapshot.getValue(Favourite.class);


                                        if (fav.getIsLike().equalsIgnoreCase("true")) {
                                            databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(quote.getQuoteId()).child("isLike").setValue("false");
                                            Integer likes = Integer.parseInt(quote.getNumberOfLikes());
                                            likes = likes - 1;
                                            fromDb.child(quote.getQuoteId()).child("numberOfLikes").setValue(String.valueOf(likes));

                                        } else {
                                            databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(quote.getQuoteId()).child("isLike").setValue("true");
                                            Integer likes = Integer.parseInt(quote.getNumberOfLikes());
                                            likes = likes + 1;
                                            fromDb.child(quote.getQuoteId()).child("numberOfLikes").setValue(String.valueOf(likes));
                                        }
                                    } else {
                                        Favourite fav = new Favourite(quote.getQuoteId(), quote.getQuoteCatName(), quote.getQuoteImage(), "true", "false");
                                        databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(quote.getQuoteId()).setValue(fav);
                                        Integer likes = Integer.parseInt(quote.getNumberOfLikes());
                                        likes = likes + 1;
                                        fromDb.child(quote.getQuoteId()).child("numberOfLikes").setValue(String.valueOf(likes));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else{
                            Integer likes = Integer.parseInt(quote.getNumberOfLikes());
                            likes = likes + 1;
                            fromDb.child(quote.getQuoteId()).child("numberOfLikes").setValue(String.valueOf(likes));
                            quoteViewHolder.likeBox.setButtonDrawable(R.drawable.ic_favorite_black_24dp);
                        }

                        //Intent intent = new Intent(QuoteActivity.this,QuoteActivity.class);
                        //startActivity(intent);
                    }
                });



                //Downloading Quote to gallery
                quoteViewHolder.saveBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        File direct =
                                new File(Environment
                                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                                        .getAbsolutePath() + "/" + "Gubbiya Kalarava" + "/");


                        if (!direct.exists()) {
                            direct.mkdir();
                            Log.d("Log Dir msg", "dir created for first time");
                        }

                        DownloadManager dm = (DownloadManager) v.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri downloadUri = Uri.parse(quote.getQuoteImage());
                        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                                .setAllowedOverRoaming(false)
                                .setTitle(quote.getQuoteCatName())
                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                                        File.separator + "Gubbiya Kalarava" + File.separator + quote.getQuoteCatName());

                        dm.enqueue(request);

                    }
                });

                quoteViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        BitmapDrawable mDrawable = (BitmapDrawable)quoteViewHolder.quoteImage.getDrawable();
                        Bitmap mBitmap = mDrawable.getBitmap();

                        String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Image Description"+(currentTime = Calendar.getInstance().getTime()), null);
                        Uri uri = Uri.parse(path);

                        Intent share = new Intent(Intent.ACTION_SEND);

                        // If you want to share a png image only, you can do:
                        // setType("image/png"); OR for jpeg: setType("image/jpeg");
                        share.setType("image/jpeg");



                        // Uri uri = Uri.parse(quote.getQuoteImage());

                        share.putExtra(Intent.EXTRA_STREAM, uri);

                        startActivity(Intent.createChooser(share, "Share Image!"));
                    }
                });



                quoteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(SearchQuoteActivity.this, ImageSlider.class);
                        i.putExtra("quoteId",quote.getQuoteId());
                        startActivity(i);
                    }
                });
            }

            @NonNull
            @Override
            public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new QuoteViewHolder(LayoutInflater.from(SearchQuoteActivity.this).inflate(R.layout.quote_item, parent, false));
            }

        };

        recycler_quote.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }
}
