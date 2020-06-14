package com.android.gk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.gk.Common.common;
import com.android.gk.Model.Favourite;
import com.android.gk.Model.Post;
import com.android.gk.ViewHolder.PublishedPostViewHolder;
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

public class BookmarkActivity extends AppCompatActivity {

    FirebaseDatabase database;
    DatabaseReference fromDb;
    DatabaseReference databaseEntry;

    RecyclerView recycler_post;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerOptions<Favourite> options;

    FirebaseRecyclerAdapter<Favourite, PublishedPostViewHolder> adapter;

    //ArrayList<Post> itemList = new ArrayList<Post>();

    FirebaseStorage storage;
    StorageReference storageReference;

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
        setContentView(R.layout.activity_bookmark);

        getSupportActionBar().hide();

        Toolbar toolBar = (Toolbar) findViewById(R.id.topAppBar);

        //Inflating the Menu on top of the toolbar
        toolBar.inflateMenu(R.menu.topbar_menu);
        toolBar.setTitle("Posts");
        toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.top_signout:
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        LoginManager.getInstance().logOut();
                        mAuth.signOut();
                        Intent signoutIntent = new Intent(BookmarkActivity.this, MainActivity.class);
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
                Intent back = new Intent(BookmarkActivity.this,CategoryActivity.class);
                startActivity(back);
            }
        });




        //Setting the progress bar

        final ProgressBar progressBar;

        progressBar = (ProgressBar)findViewById(R.id.progressBar_cyclic);
        progressBar.setProgress(20);

        noPost = findViewById(R.id.noPost);

       /* Menu menu = toolBar.getMenu();
        MenuItem menuItem = menu.findItem(R.id.action_search);

        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(LikeQuotes.this,SearchQuoteActivity.class);
                i.putExtra("Search",query);
                startActivity(i);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Intent i = new Intent(LikeQuotes.this,SearchQuoteActivity.class);
                i.putExtra("Search",newText);
                startActivity(i);
                return false;
            }
        }); */

        //Displaying Recycler view, data from database.

        fromDb = FirebaseDatabase.getInstance().getReference().child("Favourite").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        recycler_post = (RecyclerView)findViewById(R.id.recycler_post);
        recycler_post.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recycler_post.setItemViewCacheSize(20);
        recycler_post.setDrawingCacheEnabled(true);
        recycler_post.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recycler_post.setLayoutManager(layoutManager);

        options = new FirebaseRecyclerOptions.Builder<Favourite>().setQuery(fromDb.orderByChild("isBookmarked").equalTo("true"), Favourite.class).build();


        Query queryHasdata = fromDb.orderByChild("isBookmarked").equalTo("true");
        queryHasdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    noPost.setVisibility(View.INVISIBLE);
                }else{
                    progressBar.setVisibility(View.INVISIBLE);
                    noPost.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter = new FirebaseRecyclerAdapter<Favourite, PublishedPostViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final PublishedPostViewHolder postViewHolder, int i, @NonNull final Favourite fav) {

                noPost.setVisibility(View.INVISIBLE);
                Picasso.with(getBaseContext()).load(fav.getPostImage()).fit().transform(new RoundedTransformation(20,0)).into(postViewHolder.txtPostImage);

                postViewHolder.txtPostTitle.setText(fav.getPostTitle());


                progressBar.setVisibility(View.INVISIBLE);


                databaseEntry = FirebaseDatabase.getInstance().getReference("Favourite");
                Query queryFav = databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child(fav.getFavId());
                queryFav.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Favourite fav = dataSnapshot.getValue(Favourite.class);
                            if (fav.getIsLike().equalsIgnoreCase("truep")) {
                                postViewHolder.likeBtn.setButtonDrawable(R.drawable.ic_favorite_black_24dp);
                            } else if (fav.getIsLike().equalsIgnoreCase("falsep")) {
                                postViewHolder.likeBtn.setButtonDrawable(R.drawable.ic_favorite_border_black_24dp);
                            }
                        } else {
                            postViewHolder.likeBtn.setButtonDrawable(R.drawable.ic_favorite_border_black_24dp);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                databaseEntry = FirebaseDatabase.getInstance().getReference("Post");
                Query postQuery = databaseEntry.child(fav.getFavId());
                postQuery.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Post post = dataSnapshot.getValue(Post.class);
                        if (post.getNumberOfLikes().equalsIgnoreCase("0")) {
                            postViewHolder.likeBtn.setText("Like");
                        } else {
                            postViewHolder.likeBtn.setText(post.getNumberOfLikes() + " Likes");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



                //Like Button activity

                postViewHolder.likeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (fav.getIsLike().equalsIgnoreCase("truep")) {
                            //update database
                            databaseEntry = FirebaseDatabase.getInstance().getReference("Favourite");
                            databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(fav.getFavId()).child("favId").setValue(fav.getFavId());
                            databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(fav.getFavId()).child("isLike").setValue("falsep");
                            //fromDb.child(post.getPostId()).child("isLike").setValue("false");

                            databaseEntry = FirebaseDatabase.getInstance().getReference("Post");
                            Query postQuery = databaseEntry.child(fav.getFavId());
                            postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Post post = dataSnapshot.getValue(Post.class);
                                    Integer likes = Integer.parseInt(post.getNumberOfLikes());
                                    likes = likes - 1;
                                    databaseEntry.child(fav.getFavId()).child("numberOfLikes").setValue(String.valueOf(likes));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });


                        } else {
                            //update database
                            databaseEntry = FirebaseDatabase.getInstance().getReference("Favourite");
                            databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(fav.getFavId()).child("favId").setValue(fav.getFavId());
                            databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(fav.getFavId()).child("isLike").setValue("truep");
                            //fromDb.child(post.getPostId()).child("isLike").setValue("false");

                            databaseEntry = FirebaseDatabase.getInstance().getReference("Post");
                            Query postQuery = databaseEntry.child(fav.getFavId());
                            postQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Post post = dataSnapshot.getValue(Post.class);
                                    Integer likes = Integer.parseInt(post.getNumberOfLikes());
                                    likes = likes + 1;
                                    databaseEntry.child(fav.getFavId()).child("numberOfLikes").setValue(String.valueOf(likes));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                }
                            });
                        }

                    }
                });

                //Comments Box
                postViewHolder.commentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent comment = new Intent(BookmarkActivity.this, CommentsActivity.class);
                        comment.putExtra("QuoteId", fav.getFavId());
                        comment.putExtra("isPost", "true");
                        startActivity(comment);
                    }
                });

                postViewHolder.shareBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int applicationNameId = BookmarkActivity.this.getApplicationInfo().labelRes;
                        final String appPackageName = BookmarkActivity.this.getPackageName();
                        Intent i = new Intent(Intent.ACTION_SEND);
                        i.setType("text/plain");
                        i.putExtra(Intent.EXTRA_SUBJECT, BookmarkActivity.this.getString(applicationNameId));
                        String text = "Would recommend this application: ";
                        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
                        i.putExtra(Intent.EXTRA_TEXT, text + " " + link);
                        startActivity(Intent.createChooser(i, "Share link:"));
                    }
                });

                postViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent article = new Intent(BookmarkActivity.this,SingleArticleActivity.class);
                        article.putExtra("postId",fav.getFavId());
                        startActivity(article);
                    }
                });

            }

            @NonNull
            @Override
            public PublishedPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new PublishedPostViewHolder(LayoutInflater.from(BookmarkActivity.this).inflate(R.layout.published_post_item, parent, false));
            }

        };

        recycler_post.setAdapter(adapter);
        adapter.notifyDataSetChanged();



    }
}
