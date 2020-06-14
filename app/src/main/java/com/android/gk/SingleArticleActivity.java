package com.android.gk;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gk.Common.common;
import com.android.gk.Model.Favourite;
import com.android.gk.Model.Post;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class SingleArticleActivity extends AppCompatActivity {

    ImageView imagePost;
    TextView txtpostTitle, txtpostContent;
    FloatingActionButton facebook, gmail, twitter, downloadBtn, bookmark;

    DatabaseReference fromDb;
    DatabaseReference databaseEntry;
    String catName;

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_single_article);

        getSupportActionBar().hide();

        //Getting the post Id
        Intent getId = getIntent();
        final String postId = getId.getStringExtra("postId");

        //Getting all the fields or views

        imagePost = findViewById(R.id.postImage);
        txtpostTitle = findViewById(R.id.postTitle);
        txtpostContent = findViewById(R.id.postContent);

        facebook = findViewById(R.id.facebook);
        twitter = findViewById(R.id.twitter);
        gmail = findViewById(R.id.gmail);
        downloadBtn = findViewById(R.id.downloadPost);
        bookmark = findViewById(R.id.bookmark);

        fromDb = FirebaseDatabase.getInstance().getReference("Post");

        Query queryPost = fromDb.child(postId);

        queryPost.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Post post = dataSnapshot.getValue(Post.class);

                Picasso.with(getBaseContext()).load(post.getPostImage()).fit().transform(new RoundedTransformation(20,0)).into(imagePost);

                txtpostTitle.setText(post.getPostTitle());
                catName = post.getPostCategory();

                txtpostContent.setText(post.getPostContent());

                //Bookmarked

                if(common.currentUser!=null) {
                    databaseEntry = FirebaseDatabase.getInstance().getReference("Favourite");
                    Query queryFav = databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post.getPostId());
                    queryFav.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Favourite fav = dataSnapshot.getValue(Favourite.class);
                                if (fav.getIsBookmarked().equalsIgnoreCase("true")) {
                                    bookmark.setImageResource(R.drawable.ic_bookmark_black_24dp);

                                } else {
                                    bookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                                }
                            } else {
                                bookmark.setImageResource(R.drawable.ic_bookmark_border_black_24dp);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }else{
                    bookmark.setVisibility(View.INVISIBLE);
                }


                twitter.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharingToSocialMedia("com.twitter.android",post.getPostTitle(), post.getPostContent());
                    }
                });


                gmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        File path = getApplicationContext().getExternalFilesDir("PKLwrites");
                        Log.d("Getting path", "file path"+path);
                        File file = new File(path, post.getPostTitle()+".txt");


                        FileOutputStream stream = null;
                        try {
                            stream = new FileOutputStream(file);
                            stream.write(post.getPostContent().getBytes());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }


                        Intent intentShareFile = new Intent(Intent.ACTION_SEND);
                        File fileWithinMyDir = new File(String.valueOf(file));

                        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.android.gk.provider",fileWithinMyDir);

                        Log.d("Getting details", "file details"+file);
                        if(fileWithinMyDir.exists()) {
                            intentShareFile.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            intentShareFile.setType("text/html");
                            intentShareFile.setPackage("com.google.android.gm");
                            intentShareFile.putExtra(Intent.EXTRA_STREAM, uri);

                            intentShareFile.putExtra(Intent.EXTRA_SUBJECT,
                                    post.getPostTitle());
                            intentShareFile.putExtra(Intent.EXTRA_TEXT, post.getPostTitle());

                            startActivity(Intent.createChooser(intentShareFile, post.getPostTitle()));
                        }
                    }
                });



                downloadBtn.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                    @Override
                    public void onClick(View v) {

                       // verifyStoragePermissions(SingleArticleActivity.this);

                        File direct =
                                new File(Environment
                                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" +"Pklwrites" + "/");



                        if (!direct.exists()) {
                            direct.mkdir();
                            Log.d("Log Dir msg", "dir created for first time");
                        }


                        //File path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                        Log.d("Getting path", "file path"+direct);
                        File file = new File(direct, post.getPostTitle()+".txt");

                        Log.d("file path", "complete path"+file);


                        FileOutputStream stream = null;
                        try {
                            stream = new FileOutputStream(file);
                            stream.write(post.getPostContent().getBytes());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                stream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }

                        Toast.makeText(SingleArticleActivity.this, post.getPostTitle()+" Article saved under MyFiles/Internal Storage/Documents/Pklwrites folder",Toast.LENGTH_SHORT).show();

                    }
                });

                facebook.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SharingToSocialMedia("com.facebook.katana",post.getPostTitle(), post.getPostContent());
                    }
                });


                bookmark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        databaseEntry = FirebaseDatabase.getInstance().getReference("Favourite");
                        Query queryFav = databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post.getPostId());
                        queryFav.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()) {
                                    Favourite fav = dataSnapshot.getValue(Favourite.class);
                                    if (fav.getIsBookmarked().equalsIgnoreCase("true")) {
                                        //update database
                                        databaseEntry = FirebaseDatabase.getInstance().getReference("Favourite");
                                        databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(post.getPostId()).child("isBookmarked").setValue("false");
                                        Toast.makeText(SingleArticleActivity.this, "This Article has been removed from BookMark Category!!", Toast.LENGTH_LONG).show();

                                    } else {
                                        //update database
                                        // fromDb.child(post.getPostId()).child("isLike").setValue("true");
                                        databaseEntry = FirebaseDatabase.getInstance().getReference("Favourite");
                                        databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .child(post.getPostId()).child("isBookmarked").setValue("true");
                                        Toast.makeText(SingleArticleActivity.this, "This Article has been BookMarked!!", Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Favourite fav = new Favourite(post.getPostId(), post.getPostTitle(), post.getPostImage(), "falsep", "true");
                                    databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(post.getPostId()).setValue(fav);
                                    Toast.makeText(SingleArticleActivity.this, "This Article has been BookMarked!!", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                        //Intent publishIntent = new Intent(PublishedSingleArticle.this, PublishedSingleArticle.class);
                        // startActivity(publishIntent);
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }


    public void SharingToSocialMedia(String application, String title, String shareBody) {

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_STREAM, title);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);

        boolean installed = checkAppInstall(application);
        if (installed) {
            intent.setPackage(application);
            startActivity(intent);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Install application first", Toast.LENGTH_LONG).show();
        }

    }


    private boolean checkAppInstall(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }

        return false;
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}
