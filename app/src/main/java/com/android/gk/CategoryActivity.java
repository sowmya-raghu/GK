package com.android.gk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ProgressBar;

import com.android.gk.Model.Category;
import com.android.gk.ViewHolder.CategoryViewHolder;
import com.facebook.login.LoginManager;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryActivity extends AppCompatActivity {

    MaterialButton exploreBtn;

    FirebaseDatabase database;
    DatabaseReference fromDb;

    RecyclerView recycler_category;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerOptions<Category> options;

    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;

    ArrayList<Category> itemList = new ArrayList<Category>();

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
        setContentView(R.layout.activity_category);

        getSupportActionBar().hide();

        Toolbar toolBar = (Toolbar) findViewById(R.id.topAppBar);

        //Inflating the Menu on top of the toolbar
        toolBar.inflateMenu(R.menu.topbar_menu);
        toolBar.setTitle("Explore Category");
        toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.top_signout:
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        LoginManager.getInstance().logOut();
                        mAuth.signOut();
                        Intent signoutIntent = new Intent(CategoryActivity.this, MainActivity.class);
                        startActivity(signoutIntent);
                        return true;
                }
                return true;
            }
        });

//pressing backbutton takes to home page
        toolBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent back = new Intent(CategoryActivity.this, HomeActivity.class);
                startActivity(back);
            }
        });


        //Setting the progress bar

        final ProgressBar progressBar;

        progressBar = (ProgressBar) findViewById(R.id.progressBar_cyclic);
        progressBar.setProgress(20);

        //Displaying Recycler view, data from database.

        fromDb = FirebaseDatabase.getInstance().getReference().child("Category");

        recycler_category = (RecyclerView) findViewById(R.id.recycler_category);
        recycler_category.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getApplicationContext());
        recycler_category.setLayoutManager(layoutManager);

        options = new FirebaseRecyclerOptions.Builder<Category>().setQuery(fromDb.orderByChild("isDisplay").equalTo("true"), Category.class).build();

        adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final CategoryViewHolder categoryViewHolder, int i, @NonNull final Category category) {
                categoryViewHolder.txtCategoryName.setText(category.getCategoryTitle());
                categoryViewHolder.txtCategoryDesc.setText(category.getCategorySummary());
                Picasso.with(getBaseContext()).load(category.getCategoryImage()).into(categoryViewHolder.txtCategoryImage);
                progressBar.setVisibility(View.INVISIBLE);

                //On Click of Explore Button
                categoryViewHolder.exploreButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (category.getCategoryTitle().equalsIgnoreCase("Quotes")) {
                            //Intent i = new Intent(CategoryActivity.this, QuoteActivity.class);
                           // startActivity(i);
                        } else {
                           // Intent i = new Intent(CategoryActivity.this, PublishedArticle.class);
                          //  i.putExtra("CategoryName", category.getCategoryTitle());
                          //  startActivity(i);
                        }

                    }
                });


                categoryViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CategoryViewHolder(LayoutInflater.from(CategoryActivity.this).inflate(R.layout.category_item, parent, false));
            }
        };

        recycler_category.setAdapter(adapter);
    }
}
