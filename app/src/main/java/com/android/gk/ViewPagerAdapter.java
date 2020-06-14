package com.android.gk;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

public class ViewPagerAdapter extends PagerAdapter {

    Date currentTime;
    private Context context;
    private String[] imageUrls, quoteIds;
   private ViewPager viewPager;

    private MaterialButton saveBtn, shareBtn;
    private CheckBox likeBtn;

    FirebaseDatabase database;
    DatabaseReference fromDb;

    DatabaseReference databaseEntry;

    ValueEventListener myValueEvent;


    public ViewPagerAdapter(Context context, String[] imageUrls,
                            CheckBox likebtn, MaterialButton savebtn, MaterialButton sharebtn, String[] quoteIds, ViewPager viewPager) {
        this.context = context;
        this.imageUrls = imageUrls;
        likeBtn = likebtn;
        saveBtn = savebtn;
        shareBtn = sharebtn;
        this.quoteIds = quoteIds;
        this.viewPager = viewPager;
    }

    @Override
    public int getCount() {
        return imageUrls.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull final ViewGroup container, final int position) {
        final ImageView imageView = new ImageView(context);

        Log.d("Position Value", "Position 1"+position);
        Picasso.with(context).load(imageUrls[position]).fit().centerInside().into(imageView);
     //  imageView.setScaleType(ImageView.ScaleType.CENTER);
        container.addView(imageView);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ImageView imageView1 = new ImageView(context);
                Picasso.with(context).load(imageUrls[position]).fit().centerInside().into(imageView);
                callSave(position,imageView);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        Log.d("Position Value", "Position 2"+position);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(common.imageClicked.equalsIgnoreCase("false")){
                    likeBtn.setVisibility(View.INVISIBLE);
                    saveBtn.setVisibility(View.INVISIBLE);
                    shareBtn.setVisibility(View.INVISIBLE);
                    common.imageClicked="true";
                }else{
                    likeBtn.setVisibility(View.VISIBLE);
                    saveBtn.setVisibility(View.VISIBLE);
                    shareBtn.setVisibility(View.VISIBLE);
                    common.imageClicked="false";
                }
            }
        });
        return imageView;
    }

    private void callSave(final int positionFinal, final ImageView imageView) {

               // final int positionFinal = position - 1;
                fromDb = FirebaseDatabase.getInstance().getReference("Quote");

                final Query queryImage = fromDb.orderByChild("quoteId").equalTo(quoteIds[positionFinal]);

                Log.d("Position Value", "Position 3"+positionFinal);
                queryImage.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Log.d("Position Value", "Position 4" + positionFinal);
                        final Quote quote = dataSnapshot.child(quoteIds[positionFinal]).getValue(Quote.class);
                        //String quoteId = (String) dataSnapshot.child("quoteId").getValue();
                        // final String quoteCatName = (String) dataSnapshot.child("quoteCatName").getValue();

                        Log.d("Final Image", "Final Id" + quote.getQuoteId());

                        saveBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View v) {
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

                        //Like Button activity
                        if(common.currentUser!=null) {
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

                        if(common.currentUser!=null) {
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

                        shareBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                               BitmapDrawable mDrawable = (BitmapDrawable)imageView.getDrawable();
                                Bitmap mBitmap = mDrawable.getBitmap();

                                String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), mBitmap, "Image Description"+(currentTime = Calendar.getInstance().getTime()), null);

                                Uri uri = Uri.parse(path);

                                Intent share = new Intent(Intent.ACTION_SEND);

                                // If you want to share a png image only, you can do:
                                // setType("image/png"); OR for jpeg: setType("image/jpeg");
                                share.setType("image/jpeg");



                                // Uri uri = Uri.parse(quote.getQuoteImage());

                                share.putExtra(Intent.EXTRA_STREAM, uri);

                                context.startActivity(Intent.createChooser(share, "Share Image!"));
                            }
                        });

                    }

                        @Override
                        public void onCancelled (@NonNull DatabaseError databaseError){

                        }


            });

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }


}
