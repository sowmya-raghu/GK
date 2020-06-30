package com.android.gk;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.android.gk.Common.common;
import com.android.gk.Model.Favourite;
import com.android.gk.Model.Quote;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ImageSlider extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference fromDb;

    DatabaseReference databaseEntry;

    ArrayList<String> imageUrls = new ArrayList<String>();
    ArrayList<String> quoteIds = new ArrayList<String>();
    String[] urlPaths, quoteFinalIds;
    ImageView imageView;

   CheckBox likeBtn;
     MaterialButton saveBtn;
     MaterialButton shareBtn;

     ViewPager viewPager;
     Bitmap mBitmap;
Date currentTime;
     int mposition;
    int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // making activity full screen

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_image_slider);


        getSupportActionBar().hide();
        Intent i = getIntent();
        final String quoteId = i.getStringExtra("quoteId");
        fromDb = FirebaseDatabase.getInstance().getReference();

        likeBtn = (CheckBox) findViewById(R.id.like_image);
        saveBtn = (MaterialButton) findViewById(R.id.save_image);
        shareBtn = (MaterialButton) findViewById(R.id.share_image);
        viewPager = (ViewPager) findViewById(R.id.view_image_page);

        imageView = new ImageView(ImageSlider.this);




        Log.d("Intent Id", "Quote From Intent"+quoteId);
        Query queryImage = fromDb.child("Quote");


        queryImage.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                     final Quote quote = postSnapshot.getValue(Quote.class);


                    Picasso.with(ImageSlider.this).load(quote.getQuoteImage()).fit().centerInside().into(imageView);

                     if(quoteId.equalsIgnoreCase(quote.getQuoteId())){
                         imageUrls.add(0,quote.getQuoteImage());
                         quoteIds.add(0,quote.getQuoteId());
                     }else{
                         imageUrls.add(quote.getQuoteImage());
                         quoteIds.add(quote.getQuoteId());
                     }


                    if(quoteId.equalsIgnoreCase(quote.getQuoteId())) {
                        fromDb = FirebaseDatabase.getInstance().getReference("Quote");

                        Query query = fromDb.child(quoteId);

                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                final Quote quote = dataSnapshot.getValue(Quote.class);

                                //Downloading Quote to gallery
                                saveBtn.setOnClickListener(new View.OnClickListener() {
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
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


                        shareBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                //  container.addView(imageView);

                                shareFunction();
                            }
                        });


                        //Like Button activity

                        if (common.currentUser != null) {
                            databaseEntry = FirebaseDatabase.getInstance().getReference("Favourite");
                            Query queryFav = databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(quote.getQuoteId());

                            queryFav.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.exists()) {
                                        Favourite fav = dataSnapshot.getValue(Favourite.class);
                                        if (fav.getIsLike().equalsIgnoreCase("true")) {
                                            likeBtn.setButtonDrawable(R.drawable.ic_favorite_black_24dp);

                                        } else {
                                            likeBtn.setButtonDrawable(R.drawable.ic_favorite_border_black_24dp);
                                        }
                                    } else {
                                        likeBtn.setButtonDrawable(R.drawable.ic_favorite_border_black_24dp);
                                    }

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }else{
                            likeBtn.setButtonDrawable(R.drawable.ic_favorite_border_black_24dp);
                        }


                        if (common.currentUser != null) {
                            likeBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
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
                                }
                            });

                        }else{
                            likeBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    likeBtn.setButtonDrawable(R.drawable.ic_favorite_black_24dp);
                                }
                            });
                        }
                    }



                    // count++;

                    callAdapter(imageUrls, quoteIds, quote);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Failure Deletion", "onCancelled", databaseError.toException());
            }
        });

       // Log.d("Image Url", "Size"+imageUrls.size());


       /*  */




    }

    private void shareFunction() {

        BitmapDrawable mDrawable = (BitmapDrawable)imageView.getDrawable();
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

    private void callAdapter(ArrayList<String> actualPath, ArrayList<String> quoteIds, final Quote quote) {
        final ViewPager viewPager = findViewById(R.id.view_image_page);



         urlPaths = actualPath.toArray(new String[actualPath.size()]);

         quoteFinalIds = quoteIds.toArray(new String[quoteIds.size()]);


        ViewPagerAdapter adapter = new ViewPagerAdapter(ImageSlider.this, urlPaths, likeBtn,saveBtn,shareBtn,quoteFinalIds,viewPager);
        viewPager.setAdapter(adapter);


    }
}
