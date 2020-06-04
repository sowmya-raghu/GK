package com.android.gk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.gk.Common.common;
import com.facebook.login.LoginManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    DrawerLayout drawer;
    TextView textFullName;
    private AppBarConfiguration mAppBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        getSupportActionBar().hide();

        Toolbar toolBar = (Toolbar) findViewById(R.id.topAppBar);

        //Inflating the Menu on top of the toolbar
        toolBar.inflateMenu(R.menu.topbar_menu);

        toolBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.top_signout:
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        LoginManager.getInstance().logOut();
                        mAuth.signOut();
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
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_mustread,R.id.nav_bookmarked,R.id.nav_explorecat,
                R.id.nav_setting,R.id.nav_signout).setDrawerLayout(drawer).build();

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
                    case R.id.nav_explorecat:
                        Intent explorecat = new Intent(HomeActivity.this, CategoryActivity.class);
                        startActivity(explorecat);
                        return true;
                    case R.id.nav_signout:
                        FirebaseAuth mAuth = FirebaseAuth.getInstance();
                        LoginManager.getInstance().logOut();
                        mAuth.signOut();
                        Intent signoutIntent = new Intent(HomeActivity.this, MainActivity.class);
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
        textFullName.setText(common.currentUser);


    }
}
