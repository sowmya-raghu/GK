package com.android.gk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private FirebaseUser user;

    TextInputEditText txtEmail, txtOldPwd, txtNewPwd;
    MaterialButton changePassword, passwordBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        getSupportActionBar().hide();

        txtEmail = findViewById(R.id.emailIdChangeEdit);
        txtOldPwd = findViewById(R.id.oldpwdedit);
        txtNewPwd = findViewById(R.id.newpwdedit);
        changePassword = findViewById(R.id.changePwd);
        passwordBackBtn = findViewById(R.id.passwordbackbtn);

        passwordBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentBack = new Intent(ChangePasswordActivity.this, ProfileSettingActivity.class);
                startActivity(intentBack);
            }
        });

        changePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                user = FirebaseAuth.getInstance().getCurrentUser();
                final String email = user.getEmail();
                String emailId = txtEmail.getEditableText().toString();
                String oldpass = txtOldPwd.getEditableText().toString();
                final String newPass = txtNewPwd.getEditableText().toString();
                AuthCredential credential = EmailAuthProvider.getCredential(email,oldpass);


                if (emailId.isEmpty()) {
                    txtEmail.setError("Provide your Email!!");
                    txtEmail.requestFocus();
                }else if (oldpass.isEmpty()) {
                    txtOldPwd.setError("Enter Old Password!");
                    txtOldPwd.requestFocus();
                }else if (newPass.isEmpty()) {
                    txtNewPwd.setError("Enter Password!");
                    txtNewPwd.requestFocus();
                }else{

                    user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if(!task.isSuccessful()){
                                            Toast.makeText(ChangePasswordActivity.this, "Something went wrong. Please try again later", Toast.LENGTH_SHORT).show();
                                        }else {
                                            Toast.makeText(ChangePasswordActivity.this, "Password Successfully Modified", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ChangePasswordActivity.this, ProfileSettingActivity.class);
                                            startActivity(intent);
                                        }
                                    }
                                });
                            }else {
                                Toast.makeText(ChangePasswordActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }



            }
        });
    }
}
