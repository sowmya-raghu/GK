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
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.gk.Common.common;
import com.android.gk.Model.Favourite;
import com.android.gk.Model.Quote;
import com.android.gk.ViewHolder.QuoteViewHolder;
import com.facebook.login.LoginManager;
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

public class FavouriteActivity extends AppCompatActivity {


    Date currentTime;
    FirebaseDatabase database;
    DatabaseReference fromDb;

    DatabaseReference databaseEntry;
    DatabaseReference quoteEntry;
    RecyclerView recycler_quote;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerOptions<Favourite> options;

    FirebaseRecyclerAdapter<Favourite, QuoteViewHolder> adapter;

    ArrayList<Quote> itemList = new ArrayList<Quote>();

    TextView noPost;


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
        setContentView(R.layout.activity_favourite);

        getSupportActionBar().hide();

        Toolbar toolBar = (Toolbar) findViewById(R.id.topAppBar);

        //Inflating the Menu on top of the toolbar
        toolBar.inflateMenu(R.menu.topbar_menu);
        toolBar.setTitle("Favourite Quotes");
        toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.top_signout:
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        LoginManager.getInstance().logOut();
                        mAuth.signOut();
                        Intent signoutIntent = new Intent(FavouriteActivity.this, MainActivity.class);
                        startActivity(signoutIntent);
                        return true;

                    case R.id.action_search:
                        return true;
                }
                return true;
            }
        });

//pressing backbutton takes to home page
        toolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(FavouriteActivity.this, CategoryActivity.class);
                startActivity(back);
            }
        });



        //Setting the progress bar

        final ProgressBar progressBar;

        progressBar = (ProgressBar)findViewById(R.id.progressBar_cyclic);
        progressBar.setProgress(10);

        noPost = findViewById(R.id.noPost);


        //Displaying Recycler view, data from database.

        fromDb = FirebaseDatabase.getInstance().getReference().child("Favourite");

        recycler_quote = (RecyclerView)findViewById(R.id.recycler_quote);
        recycler_quote.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recycler_quote.setItemViewCacheSize(20);
        recycler_quote.setDrawingCacheEnabled(true);
        recycler_quote.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recycler_quote.setLayoutManager(layoutManager);



            options = new FirebaseRecyclerOptions.Builder<Favourite>().setQuery(fromDb.child(FirebaseAuth.getInstance()
                    .getCurrentUser().getUid()).orderByChild("isLike").equalTo("true"), Favourite.class).build();

            Query queryHasdata = fromDb.child(FirebaseAuth.getInstance().getCurrentUser()
                    .getUid()).orderByChild("isLike").equalTo("true");
            queryHasdata.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        progressBar.setVisibility(View.INVISIBLE);
                        noPost.setVisibility(View.INVISIBLE);
                    } else {
                        progressBar.setVisibility(View.INVISIBLE);
                        noPost.setVisibility(View.VISIBLE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            adapter = new FirebaseRecyclerAdapter<Favourite, QuoteViewHolder>(options) {
                @Override
                protected void onBindViewHolder(@NonNull final QuoteViewHolder quoteViewHolder, int i, @NonNull final Favourite favourite) {

                    Resources res = getResources();
                    Bitmap src = BitmapFactory.decodeResource(res, R.drawable.quote1final);
                    RoundedBitmapDrawable dr =
                            RoundedBitmapDrawableFactory.create(res, src);
                    final float roundPx = (float) src.getWidth() * 0.06f;
                    dr.setCornerRadius(roundPx);
                    quoteViewHolder.quoteImage.setImageDrawable(dr);

                    Picasso.with(getBaseContext()).load(favourite.getPostImage()).fit().into(quoteViewHolder.quoteImage);


                    if (favourite.getIsLike().equalsIgnoreCase("true")) {
                        quoteViewHolder.likeBox.setButtonDrawable(R.drawable.ic_favorite_black_24dp);

                    } else {
                        quoteViewHolder.likeBox.setButtonDrawable(R.drawable.ic_favorite_border_black_24dp);
                    }


                    databaseEntry = FirebaseDatabase.getInstance().getReference("Quote");
                    Query queryFav = databaseEntry.child(favourite.getFavId());

                    queryFav.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Quote quote = dataSnapshot.getValue(Quote.class);
                                if (quote.getNumberOfLikes().equalsIgnoreCase("0")) {
                                    quoteViewHolder.likeBox.setText("Like");
                                } else {
                                    quoteViewHolder.likeBox.setText(quote.getNumberOfLikes() + " Likes");
                                }

                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    progressBar.setVisibility(View.INVISIBLE);

                    //Comments Box
                    quoteViewHolder.commentsBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent comment = new Intent(FavouriteActivity.this, com.android.gk.CommentsActivity.class);
                            comment.putExtra("QuoteId", favourite.getFavId());
                            comment.putExtra("isPost", "false");
                            startActivity(comment);
                        }
                    });

                    //Like Button activity

                    quoteViewHolder.likeBox.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            databaseEntry = FirebaseDatabase.getInstance().getReference("Quote");
                            Query queryFav = databaseEntry.child(favourite.getFavId());

                            queryFav.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Quote quote = dataSnapshot.getValue(Quote.class);

                                        fromDb.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(favourite.getFavId()).child("isLike").setValue("false");
                                        Integer likes = Integer.parseInt(quote.getNumberOfLikes());
                                        likes = likes - 1;
                                        databaseEntry.child(quote.getQuoteId()).child("numberOfLikes").setValue(String.valueOf(likes));
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });

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
                            Uri downloadUri = Uri.parse(favourite.getPostImage());
                            DownloadManager.Request request = new DownloadManager.Request(downloadUri);
                            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                                    .setAllowedOverRoaming(false)
                                    .setTitle(favourite.getPostTitle())
                                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                    .setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES,
                                            File.separator + "Gubbiya Kalarava" + File.separator + favourite.getPostTitle());

                            dm.enqueue(request);

                        }
                    });

                    quoteViewHolder.shareButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {


                            BitmapDrawable mDrawable = (BitmapDrawable) quoteViewHolder.quoteImage.getDrawable();
                            Bitmap mBitmap = mDrawable.getBitmap();

                            String path = MediaStore.Images.Media.insertImage(getContentResolver(), mBitmap, "Image Description" + (currentTime = Calendar.getInstance().getTime()), null);
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



               /* quoteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(QuoteActivity.this, ImageSlider.class);
                        i.putExtra("quoteId",quote.getQuoteId());
                        startActivity(i);
                    }
                });*/
                }

                @NonNull
                @Override
                public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    return new QuoteViewHolder(LayoutInflater.from(FavouriteActivity.this).inflate(R.layout.quote_item, parent, false));
                }

            };

            recycler_quote.setAdapter(adapter);
            adapter.notifyDataSetChanged();

    }
}
