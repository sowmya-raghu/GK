package com.android.gk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.gk.Model.User;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ProfileSettingActivity extends AppCompatActivity {

    TextInputEditText nameEdit,emailId;
    MaterialButton saveProfile, changePassword, backBtn;
    DatabaseReference fromDb;
    DatabaseReference databaseEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // making activity full screen
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        setContentView(R.layout.activity_profile_setting);

        getSupportActionBar().hide();

        nameEdit = (TextInputEditText)findViewById(R.id.nameEdit);
        emailId = (TextInputEditText)findViewById(R.id.emailAddress);

        backBtn = findViewById(R.id.profilebackbtn);
        saveProfile = (MaterialButton)findViewById(R.id.saveProfile);

        changePassword = (MaterialButton)findViewById(R.id.changePwd);

        fromDb = FirebaseDatabase.getInstance().getReference("User");
        Query queryUsers = fromDb.child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        queryUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User users = dataSnapshot.getValue(User.class);

                Log.d("User Name", "Getting Username"+ users.getName());

                nameEdit.setText(users.getName());
                emailId.setText(users.getEmail());
                emailId.setEnabled(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        saveProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                databaseEntry = FirebaseDatabase.getInstance().getReference("User");
                User users = new User(emailId.getText().toString(),nameEdit.getText().toString());
                databaseEntry.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(users);

                Toast.makeText(ProfileSettingActivity.this,"Profile has been Updated",Toast.LENGTH_LONG).show();
                Intent intent = new Intent(ProfileSettingActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent change = new Intent(ProfileSettingActivity.this, ChangePasswordActivity.class);
                startActivity(change);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileSettingActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

    }
}
