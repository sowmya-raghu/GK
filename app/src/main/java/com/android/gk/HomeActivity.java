package com.android.gk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.gk.Common.common;
import com.android.gk.Model.Post;
import com.android.gk.Model.Quote;
import com.android.gk.ViewHolder.LatestViewHolder;
import com.android.gk.ViewHolder.QuoteViewHolder;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    DrawerLayout drawer;
    TextView textFullName;
    private AppBarConfiguration mAppBarConfiguration;
    DatabaseReference databaseEntry;

    FirebaseDatabase database;
    DatabaseReference fromDb;

    RecyclerView recycler_latest;
    RecyclerView.LayoutManager layoutManager;

    RecyclerView recycler_grid;
    RecyclerView.LayoutManager layoutManagerGrid;

    FirebaseRecyclerOptions<Post> options;

    FirebaseRecyclerAdapter<Post, LatestViewHolder> adapter;

    FirebaseRecyclerOptions<Quote> optionsQuote;

    FirebaseRecyclerAdapter<Quote, QuoteViewHolder> adapterQuote;

    LinearLayout exploreCat;

    //Start and Stop of adapters
    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        adapterQuote.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        adapterQuote.stopListening();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().hide();

        Toolbar toolBar = (Toolbar) findViewById(R.id.topAppBar);

        //Inflating the Menu on top of the toolbar
        toolBar.inflateMenu(R.menu.topbar_menu);

        databaseEntry = FirebaseDatabase.getInstance().getReference();

        toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.top_signout:
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        LoginManager.getInstance().logOut();
                        Intent signoutIntent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(signoutIntent);
                        return true;
                }
                return true;
            }
        });


        //Opening and closing the drawer, side navigation
        toolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout navDrawer = findViewById(R.id.drawer_layout);
                // If the navigation drawer is not open then open it, if its already open then close it.
                if (!navDrawer.isDrawerOpen(GravityCompat.START))
                    navDrawer.openDrawer(GravityCompat.START);
                else navDrawer.closeDrawer(GravityCompat.END);
            }
        });



        drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation);


        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        if(common.currentUser!=null){
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_mustread,R.id.nav_bookmarked,R.id.nav_explorecat,
                    R.id.nav_setting,R.id.nav_signout).setDrawerLayout(drawer).build();

        }else{
            mAppBarConfiguration = new AppBarConfiguration.Builder(
                    R.id.nav_home, R.id.nav_mustread,R.id.nav_explorecat,
                    R.id.nav_setting,R.id.nav_signout).setDrawerLayout(drawer).build();
        }

        Menu menuSide = navigationView.getMenu();
        MenuItem itemBookmark = menuSide.findItem(R.id.nav_bookmarked);
        MenuItem itemSignOut = menuSide.findItem(R.id.nav_signout);
        MenuItem itemlikeQuotes = menuSide.findItem(R.id.nav_likequote);
        MenuItem itemSettings = menuSide.findItem(R.id.nav_setting);
        MenuItem itemLogin = menuSide.findItem(R.id.nav_signin);

        if(common.currentUser==null){
            itemBookmark.setVisible(false);
            itemSignOut.setVisible(false);
            itemlikeQuotes.setVisible(false);
            itemSettings.setVisible(false);
        }else{
            itemLogin.setVisible(false);
        }




        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        Intent home = new Intent(HomeActivity.this, HomeActivity.class);
                        startActivity(home);
                        return true;
                    case R.id.nav_mustread:
                        Intent mustread = new Intent(HomeActivity.this, TopReads.class);
                        startActivity(mustread);
                        return true;
                    case R.id.nav_bookmarked:
                        Intent bookmark = new Intent(HomeActivity.this, BookmarkActivity.class);
                        startActivity(bookmark);
                        return true;
                    case R.id.nav_explorecat:
                        Intent explorecat = new Intent(HomeActivity.this, CategoryActivity.class);
                        startActivity(explorecat);
                        return true;
                    case R.id.nav_likequote:
                        Intent fav = new Intent(HomeActivity.this, FavouriteActivity.class);
                        startActivity(fav);
                        return true;
                    case R.id.nav_signin:
                        Intent signin = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(signin);
                        return true;
                    case R.id.nav_signout:
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        LoginManager.getInstance().logOut();
                        mAuth.signOut();
                        Intent signoutIntent = new Intent(HomeActivity.this, LoginActivity.class);
                        startActivity(signoutIntent);
                        return true;
                }
                return true;
            }
        });

        //Setting user email id on the side navigation header
        View headerView = navigationView.getHeaderView(0);
        textFullName = (TextView) headerView.findViewById(R.id.text_full_name);
        // Log.d("username", common.currentUser);

        if(common.currentUser!=null){
            textFullName.setText(common.currentUser);
        }else{
            textFullName.setText("Welcome Guest!!");
            common.anonymousUser = "Anonymous" + databaseEntry.push().getKey();
        }


        exploreCat = findViewById(R.id.exploreLayout);
        //Displaying Recycler view, data from database.

        fromDb = FirebaseDatabase.getInstance().getReference().child("Post");



        recycler_latest = (RecyclerView)findViewById(R.id.recycler_latest);
        recycler_latest.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) layoutManager).setReverseLayout(true);
        ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
        recycler_latest.setItemViewCacheSize(20);
        recycler_latest.setDrawingCacheEnabled(true);
        recycler_latest.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recycler_latest.setLayoutManager(layoutManager);

        options = new FirebaseRecyclerOptions.Builder<Post>().setQuery(fromDb.orderByChild("postPublishStatus").equalTo("published").limitToLast(3), Post.class).build();


        Query queryHasdata = fromDb.orderByChild("postPublishStatus").equalTo("published");
        queryHasdata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        adapter = new FirebaseRecyclerAdapter<Post, LatestViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final LatestViewHolder publishedPostViewHolder, int i, @NonNull final Post post) {

                // noPost.setVisibility(View.INVISIBLE);
                Picasso.with(getBaseContext()).load(post.getPostImage()).fit().transform(new RoundedTransformation(20,0)).into(publishedPostViewHolder.txtPostImage);

                publishedPostViewHolder.txtPostTitle.setText(post.getPostTitle());

                publishedPostViewHolder.txtPostCategory.setText(post.getPostCategory());





                publishedPostViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent article = new Intent(HomeActivity.this,SingleArticleActivity.class);
                        article.putExtra("postId",post.getPostId());
                        startActivity(article);
                    }
                });

            }

            @NonNull
            @Override
            public LatestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new LatestViewHolder(LayoutInflater.from(HomeActivity.this).inflate(R.layout.latest_item, parent, false));
            }

        };


        recycler_latest.setAdapter(adapter);

        recycler_grid = (RecyclerView)findViewById(R.id.recycler_grid);
        recycler_grid.setHasFixedSize(true);
        layoutManagerGrid = new LinearLayoutManager(getApplicationContext());
        ((LinearLayoutManager) layoutManagerGrid).setReverseLayout(true);
        ((LinearLayoutManager) layoutManagerGrid).setStackFromEnd(true);
        recycler_grid.setItemViewCacheSize(20);
        recycler_grid.setDrawingCacheEnabled(true);
        recycler_grid.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recycler_grid.setLayoutManager(new GridLayoutManager(this,2, LinearLayoutManager.VERTICAL, true));


        fromDb = FirebaseDatabase.getInstance().getReference().child("Quote");
        optionsQuote = new FirebaseRecyclerOptions.Builder<Quote>().setQuery(fromDb.orderByChild("quoteId").limitToLast(4), Quote.class).build();


        adapterQuote = new FirebaseRecyclerAdapter<Quote, QuoteViewHolder>(optionsQuote) {
            @Override
            protected void onBindViewHolder(@NonNull final QuoteViewHolder quoteViewHolder, int i, @NonNull final Quote quote) {

                // noPost.setVisibility(View.INVISIBLE);
                Picasso.with(getBaseContext()).load(quote.getQuoteImage()).fit().transform(new RoundedTransformation(20,0)).into(quoteViewHolder.quoteImage);


                quoteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent quote = new Intent(HomeActivity.this,QuoteActivity.class);
                        startActivity(quote);
                    }
                });

            }

            @NonNull
            @Override
            public QuoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new QuoteViewHolder(LayoutInflater.from(HomeActivity.this).inflate(R.layout.quote_grid, parent, false));
            }

        };


       // adapterQuote.startListening();
        recycler_grid.setAdapter(adapterQuote);




        exploreCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

    }
}
