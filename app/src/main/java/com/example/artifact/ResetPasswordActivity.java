package com.example.artifact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private Button ResetPasswordButton;
    private TextInputEditText ResetEmailInput;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mToolbar = (Toolbar) findViewById(R.id.forget_password_toolbar);
        mAuth = FirebaseAuth.getInstance();
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Reset Password");

        ResetPasswordButton =(Button) findViewById(R.id.reset_button);
        ResetEmailInput = (TextInputEditText) findViewById(R.id.reset_email);

        ResetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = ResetEmailInput.getText().toString();

                if (TextUtils.isEmpty(userEmail))
                {
                    ResetEmailInput.setError("Required");
                }
                else
                {
                    mAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful())
                            {
                                startActivity(new Intent(ResetPasswordActivity.this, loginactivity.class));
                            }
                            else
                            {
                                String message = task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this, "Error:" + message, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });
    }
}
