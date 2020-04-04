package com.example.artifact;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.core.Tag;

public class loginactivity extends AppCompatActivity {

    private Button LoginButton;
    private TextInputEditText UserEmail, UserPassword;
    private TextInputLayout emaillayout, passwordlayout;
    private TextView NeedAccount, ForgetPassword;
    private ProgressDialog loadingBar;
    private ImageView googlesigninbutton;
    private static final int RC_SIGN_IN =1;
    private GoogleApiClient mGoogleSignInClient;
    private static final String TAG = "LoginActivity";


    private FirebaseAuth mAuth;

//    On Create Method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginactivity);

        emaillayout = (TextInputLayout) findViewById(R.id.login_email_layout);
        passwordlayout = (TextInputLayout) findViewById(R.id.login_password_layout);
        NeedAccount = (TextView) findViewById(R.id.need_account);
        UserEmail = (TextInputEditText) findViewById(R.id.login_email);
        UserPassword = (TextInputEditText) findViewById(R.id.login_password);
        LoginButton = (Button) findViewById(R.id.login_button);
        ForgetPassword = (TextView) findViewById(R.id.forgot_password_link);
        googlesigninbutton  = (ImageView) findViewById(R.id.google_signin_button);
        loadingBar = new ProgressDialog(this);


        mAuth = FirebaseAuth.getInstance();


        NeedAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                SendUserToRegisterActivity();
            }
        });

        ForgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(loginactivity.this, ResetPasswordActivity.class));
            }
        });

        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllowingUserToLogin();
            }
        });

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(loginactivity.this, "Connection to Google Sign in failed...", Toast.LENGTH_SHORT).show();

                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();



        googlesigninbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                signIn();
            }
        });

    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleSignInClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN)
        {
            loadingBar.setTitle("google Sign In");
            loadingBar.setMessage("Please wait, while we are allowing you to login using your Google Account...");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);

            if (result.isSuccess())
            {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Toast.makeText(this, "Please wait, while we are getting your auth result...", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(this, "Can't get Auth result.", Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            Log.d(TAG, "signInWithCredential:success");
                            SendUserToMainActivity();
                            loadingBar.dismiss();
                        }
                        else
                        {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            String message = task.getException().toString();
                            SendUserToLoginActivity();
                            Toast.makeText(loginactivity.this, "Not Authenticated : " + message, Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                        }
                    }
                });
    }



    //    On Start Method
    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            SendUserToMainActivity();
        }


    }

//    Login method while having certain Validations
    public void AllowingUserToLogin()
    {
        String email = UserEmail.getText().toString();
        String password = UserPassword.getText().toString();

        if(TextUtils.isEmpty(email))
        {
            emaillayout.setError("Please Enter Your Email");
            UserEmail.setError("Required");
        }
        else if(TextUtils.isEmpty(password))
        {
            passwordlayout.setError("Please Enter Your Password");
            UserPassword.setError("Required");
        }
        else
        {
            loadingBar.setTitle("Logged in Successfully");
            loadingBar.setMessage("Good Times are here");
            loadingBar.show();
            loadingBar.setCanceledOnTouchOutside(true);
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
                                SendUserToMainActivity();

                                Toast.makeText(loginactivity.this,"You are Logged in Successfully", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();
                            }
                            else
                            {
                                String message =task.getException().getMessage();
                                Toast.makeText(loginactivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                                Log.d("Login Error", message);
                                loadingBar.dismiss();


                            }
                        }
                    });

        }

    }

//    Method to Send User to Register Activity
    private void SendUserToRegisterActivity() {

        Intent registerIntent = new Intent(loginactivity.this, RegisterActivity.class);
        startActivity(registerIntent);
    }

//    Method to Send User To Main Activity
    private void SendUserToMainActivity() {

        Intent mainIntent = new Intent(loginactivity.this, MainActivity.class);
        mainIntent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        startActivity(mainIntent);
        finish();

    }

    private void SendUserToLoginActivity() {

        Intent mainIntent = new Intent(loginactivity.this, loginactivity.class);
        mainIntent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        startActivity(mainIntent);
        finish();
    }

}

