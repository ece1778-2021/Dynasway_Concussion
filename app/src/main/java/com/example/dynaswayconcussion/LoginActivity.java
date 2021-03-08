package com.example.dynaswayconcussion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    //Logging information tag
    private static final String INFO_TAG = "[Login info]";

    //Firebase related variable
    private FirebaseAuth mAuth;

    //Layout components for fast access
    private EditText logInEmailField;
    private EditText logInPasswordField;
    private Button logInButton;
    private ImageView logoImageView;

    //Loading animation related variables
    AlphaAnimation inAnimation;
    AlphaAnimation outAnimation;

    FrameLayout progressBarHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolderLogin);

        //When log in button is clicked, check if the fields for email and pass aren't empty
        logInButton = (Button)findViewById(R.id.logInButton);
        logInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = logInEmailField.getText().toString();
                String pass = logInPasswordField.getText().toString();
                if (email == null || pass == null || email.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Email and password can't be empty.",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    new LoginTask().execute();
                }
            }
        });
        final Button signupButton = (Button)findViewById(R.id.registerButton);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUp();
            }
        });

        logInEmailField = (EditText)findViewById(R.id.login_email_textbox);
        logInPasswordField = (EditText)findViewById(R.id.login_password_textbox);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    //If sign up button is pressed, load register activity
    private void signUp() {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    //Loading animation async class to wrap the Firebase loading algorithm
    private class LoginTask extends AsyncTask<Void, Void, Void> {

        private boolean emptyFields = false;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            logInButton.setEnabled(false);
            inAnimation = new AlphaAnimation(0f, 1f);
            inAnimation.setDuration(200);
            progressBarHolder.setAnimation(inAnimation);
            progressBarHolder.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected Void doInBackground(Void... params) {
            doLogin();
            return null;
        }
    }

    private void loadMainIntent() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void doLogin() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
            logInButton.setEnabled(true);
            loadMainIntent();
        }
        else {
            //Try to log in with the given email and password. Try to check, if the log in fails,
            //why this has happened, and separate the exception handling based on the different
            //possible failure results
            String email = logInEmailField.getText().toString();
            String pass = logInPasswordField.getText().toString();
            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(INFO_TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                outAnimation = new AlphaAnimation(1f, 0f);
                                outAnimation.setDuration(200);
                                progressBarHolder.setAnimation(outAnimation);
                                progressBarHolder.setVisibility(View.GONE);
                                logInButton.setEnabled(true);
                                loadMainIntent();
                            } else {
                                outAnimation = new AlphaAnimation(1f, 0f);
                                outAnimation.setDuration(200);
                                progressBarHolder.setAnimation(outAnimation);
                                progressBarHolder.setVisibility(View.GONE);
                                logInButton.setEnabled(true);
                                try {
                                    throw task.getException();
                                } catch(FirebaseAuthWeakPasswordException e) {
                                    Toast.makeText(LoginActivity.this, "Password is incorrect.",
                                            Toast.LENGTH_SHORT).show();
                                } catch(FirebaseAuthInvalidCredentialsException e) {
                                    Toast.makeText(LoginActivity.this, "E-mail doesn't exist.",
                                            Toast.LENGTH_SHORT).show();
                                } catch(FirebaseAuthUserCollisionException e) {
                                    Toast.makeText(LoginActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();
                                } catch(Exception e) {
                                    Log.e(INFO_TAG, e.getMessage());
                                }
                                // If sign in fails, display a message to the user.
                                Log.w(INFO_TAG, "signInWithEmail:failure", task.getException());
                            }
                        }
                    });
        }
    }
}