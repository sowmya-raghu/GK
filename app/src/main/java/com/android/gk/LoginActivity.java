package com.android.gk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.android.gk.Model.User;
import com.facebook.AccessToken;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;


import com.android.gk.Common.common;
import com.facebook.CallbackManager;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    public TextInputLayout loginEmailId, logInpasswd;
    Button btnLogIn;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private CallbackManager mCallbackManager;

    MaterialButton regField;

    FirebaseDatabase database;
    DatabaseReference fromDb;
    AccessToken accessToken;
    GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);

        getSupportActionBar().hide();

        FacebookSdk.sdkInitialize(getApplicationContext());
       // AppEventsLogger.activateApp(this);

        firebaseAuth = FirebaseAuth.getInstance();
        loginEmailId = findViewById(R.id.emailTextField);
        logInpasswd = findViewById(R.id.passwordField);
        btnLogIn = findViewById(R.id.login_button);
        regField = findViewById(R.id.regField);

        final ProgressDialog mDialog = new ProgressDialog(LoginActivity.this);


        FirebaseUser user = firebaseAuth.getCurrentUser();


        if (user != null) {
            String usrEmail = user.getEmail();

            common.currentUser = usrEmail;


            Toast.makeText(LoginActivity.this, "User Logged In!!", Toast.LENGTH_SHORT).show();
            Intent I = new Intent(LoginActivity.this, HomeActivity.class);
            startActivity(I);
        } else {
            Toast.makeText(LoginActivity.this, "Login to continue", Toast.LENGTH_SHORT).show();
        }



        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDialog.setMessage("Please Wait!!!");
                mDialog.show();
                final String userEmail = loginEmailId.getEditText().getText().toString();
                final String userPaswd = logInpasswd.getEditText().getText().toString();
                if (userEmail.isEmpty() && userPaswd.isEmpty()) {
                    mDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Fields are Empty!", Toast.LENGTH_SHORT).show();
                } else if (userEmail.isEmpty()) {
                    mDialog.dismiss();
                    loginEmailId.setError("Provide your Email!");
                    loginEmailId.requestFocus();
                } else if (userPaswd.isEmpty()) {
                    mDialog.dismiss();
                    logInpasswd.setError("Enter Password!");
                    logInpasswd.requestFocus();
                } else if (!(userEmail.isEmpty() && userPaswd.isEmpty())) {
                    loginEmailId.setError(null);
                    logInpasswd.setError(null);
                    firebaseAuth.signInWithEmailAndPassword(userEmail, userPaswd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener() {
                        @Override
                        public void onComplete(@NonNull Task task) {
                            if (!task.isSuccessful()) {
                                mDialog.dismiss();

                            } else {
                                fromDb = FirebaseDatabase.getInstance().getReference().child("User");
                                Query query = fromDb.orderByChild("email").equalTo(loginEmailId.getEditText().getText().toString());
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (dataSnapshot.exists()) {
                                            mDialog.dismiss();
                                            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                                            startActivity(homeIntent);
                                            finish();
                                        } else {
                                            mDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, "You haven't Registered Yet! Please Register to Continue", Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if (e instanceof FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(LoginActivity.this, "Incorrect Password!!", Toast.LENGTH_SHORT).show();
                            } else if (e instanceof FirebaseAuthInvalidUserException) {
                                Toast.makeText(LoginActivity.this, "Invalid Email!!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication Failed!!", Toast.LENGTH_SHORT).show();
                            }
                            mDialog.dismiss();
                        }
                    });
                } else {
                    mDialog.dismiss();

                    Toast.makeText(LoginActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });

        regField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(signUp);
            }
        });


        // Initialize Facebook Login button
        LoginManager.getInstance().logOut();
        mCallbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = findViewById(R.id.login_fb);
        loginButton.setReadPermissions("email", "public_profile", "user_friends");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Fb", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("Fb", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Fb", "facebook:onError", error);
                // ...
            }
        });
// ...

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);

        SignInButton signIn = findViewById(R.id.sign_in_button);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, 101);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("Google Sign in error", "Google sign in failed", e);
                // ...
            }
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("Handle", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Success", "signInWithCredential:success");
                            FirebaseUser users = firebaseAuth.getCurrentUser();

                            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(homeIntent);
                            User user = new com.android.gk.Model.User(users.getEmail(),users.getDisplayName());
                            FirebaseDatabase.getInstance().getReference("User").child(
                                   FirebaseAuth.getInstance().getCurrentUser().getUid()
                           ).setValue(user);
                            common.currentUser = users.getDisplayName();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("Inside Google Auth", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("SignInWithCredential", "signInWithCredential:success");
                            FirebaseUser users = firebaseAuth.getCurrentUser();
                            common.currentUser = users.getDisplayName();
                            User user = new User(firebaseAuth.getCurrentUser().getEmail(),"");
                            FirebaseDatabase.getInstance().getReference("Users").child(
                                    FirebaseAuth.getInstance().getCurrentUser().getUid()
                            ).setValue(user);
                            Intent homeIntent = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(homeIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Google Sign In Fail", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }

                        // ...
                    }
                });
    }


}
