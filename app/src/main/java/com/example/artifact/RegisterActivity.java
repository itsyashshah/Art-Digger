package com.example.artifact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText UserEmail, UserPassword, UserConfirmPassword;
    private TextInputLayout useremaillayout,userpasswordlayout, userconfirmpasswordlayout;
    private Button CreateAccountButton;
    private ProgressDialog loadingbar;
    private FirebaseAuth mAuth;


//    OnCreate Method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();

        UserEmail = (TextInputEditText) findViewById(R.id.register_email_edittext);
        useremaillayout = (TextInputLayout) findViewById(R.id.register_email_layout);
        UserPassword = (TextInputEditText) findViewById(R.id.register_password_edittext);
        userpasswordlayout = (TextInputLayout) findViewById(R.id.register_password_layout);
        UserConfirmPassword = (TextInputEditText) findViewById(R.id.register_confirm_password_edittext);
        userconfirmpasswordlayout = (TextInputLayout) findViewById(R.id.register_confirm_password_layout);
        CreateAccountButton = (Button) findViewById(R.id.register_button);
        loadingbar = new ProgressDialog(this);

        CreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CreateNewAccount();
            }
        });
    }

//    On start Method (working fine)
    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            SendUserToMainActivity();
        }
    }




//   Method to Create a new account
    private void CreateNewAccount() {

        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();
        String confirmPassword = UserConfirmPassword.getText().toString();


        if(TextUtils.isEmpty(email))
        {
            useremaillayout.setError("Email Required");
        }
        else if(TextUtils.isEmpty(password))
        {
            userpasswordlayout.setError("Password Required");
        }
        else  if(TextUtils.isEmpty(confirmPassword))
        {
            userconfirmpasswordlayout.setError("Confirm Password Required ");
        }

        else if (!password.equals(confirmPassword))
        {
            userconfirmpasswordlayout.setError("Password Doesn't Match");
        }
        else
        {
            loadingbar.setTitle("Creating New Account");
            loadingbar.setMessage("Good things take some time. Please be patient. ");
            loadingbar.show();
            loadingbar.setCanceledOnTouchOutside(true);
            mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task)
                        {
                            if(task.isSuccessful())
                            {
                                SendUserToSetupActivity();
                                Toast.makeText(RegisterActivity.this,"You are Authenticated Successfully", Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(RegisterActivity.this,"Error: " + message, Toast.LENGTH_SHORT).show();
                                loadingbar.dismiss();
                            }

                        }
                    });
        }

    }

    //  Method to Send User to Main Activity (working fine)
    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
        mainIntent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        startActivity(mainIntent);
        finish();

    }


// Method to Send User to Setup Activity (working fine)
    private void SendUserToSetupActivity() {
        Intent setupIntent = new Intent(RegisterActivity.this, SetupActivity.class);
        setupIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(setupIntent);
        finish();
    }
}
