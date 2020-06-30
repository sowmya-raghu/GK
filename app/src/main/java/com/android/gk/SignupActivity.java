package com.android.gk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.gk.Common.common;
import com.android.gk.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    public TextInputLayout personName, loginEmailId, logInpasswd, phNumber;
    Button phoneBtn;
    MaterialButton btnLogIn;
    MaterialButton signInButton;
    MaterialButton signup;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_signup);
        getSupportActionBar().hide();

        firebaseAuth = FirebaseAuth.getInstance();
        personName = findViewById(R.id.nameTextField);
        loginEmailId = findViewById(R.id.emailTextField);
        logInpasswd = findViewById(R.id.passwordField);
        signup = findViewById(R.id.signUpBtn);

        final ProgressDialog mDialog = new ProgressDialog(SignupActivity.this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailID = loginEmailId.getEditText().getText().toString().trim();
                final String paswd = logInpasswd.getEditText().getText().toString().trim();
                final String perName = personName.getEditText().getText().toString().trim();
                if (emailID.isEmpty()) {
                    loginEmailId.setError("Provide your Email first!");
                    loginEmailId.requestFocus();
                } else if (paswd.isEmpty()) {
                    logInpasswd.setError("Set your password");
                    logInpasswd.requestFocus();
                } else if(perName.isEmpty()){
                    personName.setError("Provide your Full Name");
                    personName.requestFocus();
                }else if (emailID.isEmpty() && paswd.isEmpty()) {
                    Toast.makeText(SignupActivity.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(emailID.isEmpty() && paswd.isEmpty())) {
                    // mDialog.setMessage("Please Wait!!!");
                    //mDialog.show();
                    firebaseAuth.createUserWithEmailAndPassword(emailID, paswd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            //mDialog.dismiss();
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this.getApplicationContext(),
                                        "SignUp unsuccessful: " + task.getException().getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                //User user = new User(emailID,perName);

                                User user = new com.android.gk.Model.User(emailID,perName);
                                FirebaseDatabase.getInstance().getReference("User").child(
                                        FirebaseAuth.getInstance().getCurrentUser().getUid()
                                ).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        Toast.makeText(SignupActivity.this, "You are Registered!", Toast.LENGTH_SHORT).show();
                                        FirebaseUser users = firebaseAuth.getCurrentUser();
                                        common.currentUser = users.getEmail();
                                    }
                                });
                                startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                                FirebaseUser users = firebaseAuth.getCurrentUser();
                                common.currentUser = users.getEmail();
                            }
                        }
                    });
                } else {
                    Toast.makeText(SignupActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
