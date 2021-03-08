package com.example.dynaswayconcussion;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dynaswayconcussion.Utils.ImageLoadingHelper;
import com.example.dynaswayconcussion.ui.CircularImageView;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    //Debug tag for Log.i
    private static final String INFO_TAG = "[Register info]";

    //Firebase variables
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    //Layout components for fast access
    private EditText signUpEmailField;
    private EditText signUpPasswordField;
    private EditText signUpConfirmPasswordField;
    private EditText signUpNameField;
    private EditText signUpSurnameField;
    private EditText signUpAgeField;
    private EditText signUpSportField;
    private CheckBox signUpIsCoachField;
    private Button signUpButton;

    //Loading animation related variables
    private AlphaAnimation inAnimation;
    private AlphaAnimation outAnimation;

    private FrameLayout progressBarHolder;

    //Temporary storage for profile image bitmap and for profile color
    private Bitmap toUploadProfileImage = null;
    private byte[] imageToUploadData;

    private ImageLoadingHelper imageLoadingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        CircularImageView profileImage = (CircularImageView)findViewById(R.id.inputProfilePicture);
        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage(RegisterActivity.this);
            }
        });
        TextView profilePictureTooltip = (TextView)findViewById(R.id.profile_picture_tooltip);
        profilePictureTooltip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage(RegisterActivity.this);
            }
        });
        progressBarHolder = (FrameLayout) findViewById(R.id.progressBarHolder);
        signUpButton = (Button)findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signUpClick(RegisterActivity.this);
            }
        });

        signUpEmailField = (EditText)findViewById(R.id.signup_email_textbox);
        signUpPasswordField = (EditText)findViewById(R.id.signup_password_textbox);
        signUpConfirmPasswordField = (EditText)findViewById(R.id.signup_confirmpassword_textbox);
        signUpNameField = (EditText)findViewById(R.id.name_textbox);
        signUpSurnameField = (EditText)findViewById(R.id.surname_textbox);
        signUpAgeField = (EditText)findViewById(R.id.age_textbox);
        signUpSportField = (EditText)findViewById(R.id.sport_textbox);
        signUpIsCoachField = (CheckBox)findViewById(R.id.is_coach_checkbox);
    }

    //On sign up, start the loading task
    private void signUpClick(Context context) {
        new CreateUserTask().execute();
    }

    //Load the profile activity
    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    //Create a dialog with three options: take photo with camera, load photo from gallery or cancel
    private void pickImage(Context context) {
        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Choose your profile picture");
        if (!imageLoadingHelper.checkIfAlreadyhavePermission(context)) {
            Log.i(INFO_TAG, "Requested storage permission");
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        else {
            builder.setItems(options, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int item) {

                    if (options[item].equals("Take Photo")) {
                        Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);

                    } else if (options[item].equals("Choose from Gallery")) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , 1);

                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        }
    }

    //Callback for permission granted confirmation
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==1){
            if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
                pickImage(RegisterActivity.this);
            }
            else{
                Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    //When a button is pressed on the pop up photo selection view, several things can be done.
    //Once the picture has either been taken or selected from gallery, this callback is called.
    //It is supposed that this code won't be executed unless the user allowed storage permissions
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CircularImageView imageView = (CircularImageView)findViewById(R.id.inputProfilePicture);

        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0:
                    if (resultCode == RESULT_OK && data != null) {
                        //Load a bitmap from the picture taken from the camera
                        Bundle extras = data.getExtras();
                        Bitmap selectedImage = (Bitmap) extras.get("data");
                        toUploadProfileImage = selectedImage;
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        toUploadProfileImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        imageToUploadData = baos.toByteArray();
                        imageView.setImageBitmap(toUploadProfileImage);
                        Log.i(INFO_TAG, "Loaded image from gallery");
                    }

                    break;
                case 1:
                    if (resultCode == RESULT_OK && data != null) {
                        //Load a picture from a path from gallery
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        if (selectedImage != null) {
                            Cursor cursor = getContentResolver().query(selectedImage,
                                    filePathColumn, null, null, null);
                            if (cursor != null) {
                                cursor.moveToFirst();

                                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                String picturePath = cursor.getString(columnIndex);
                                toUploadProfileImage = BitmapFactory.decodeFile(picturePath);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                try {
                                    toUploadProfileImage = imageLoadingHelper.getCorrectlyOrientedImage(this, selectedImage);
                                    toUploadProfileImage.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                                    imageToUploadData = baos.toByteArray();
                                    imageView.setImageBitmap(toUploadProfileImage);
                                } catch (IOException e) {
                                    Toast.makeText(getApplicationContext(), "Error loading image.", Toast.LENGTH_LONG).show();
                                }
                                cursor.close();
                                Log.i(INFO_TAG, "Loaded image from camera");
                            }
                        }

                    }
                    break;
                default:
                    break;
            }
        }
    }

    private class CreateUserTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            signUpButton.setEnabled(false);
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
            createAccountUploadPicture();
            return null;
        }
    }

    //Load all information from text fields, image bitmap and profile color, first create the
    //account with email and password while checking if the password and the confirmation are equal.
    //After that, upload the picture to Firebase Storage and upload all the user data on to Firestore
    private void createAccountUploadPicture() {
        String email = signUpEmailField.getText().toString();
        String pass = signUpPasswordField.getText().toString();
        String confirmPass = signUpConfirmPasswordField.getText().toString();
        if (!pass.equals(confirmPass)) {
            outAnimation = new AlphaAnimation(1f, 0f);
            outAnimation.setDuration(200);
            progressBarHolder.setAnimation(outAnimation);
            progressBarHolder.setVisibility(View.GONE);
            signUpButton.setEnabled(true);
            Toast.makeText(RegisterActivity.this, "Passwords don't match.",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //First, upload the image so that the url can be referenced in the
                            //Firestore data
                            if (imageToUploadData != null) {
                                StorageReference storageRef = storage.getReference();
                                StorageReference profilePicRef = storageRef.child("profile_pictures/" + mAuth.getUid() + ".jpg");
                                UploadTask uploadTask = profilePicRef.putBytes(imageToUploadData);
                                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw task.getException();
                                        }

                                        // Continue with the task to get the download URL
                                        return profilePicRef.getDownloadUrl();
                                    }
                                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        if (task.isSuccessful()) {
                                            Uri downloadUri = task.getResult();
                                            // Sign in success, update UI with the signed-in user's information
                                            Log.d(INFO_TAG, "createUserWithEmail:success");
                                            createUserData(email, downloadUri.toString());
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Failed to upload picture.",
                                                    Toast.LENGTH_SHORT).show();
                                            try {
                                                throw task.getException();
                                            } catch (Exception e) {
                                                Log.e(INFO_TAG, e.getMessage());
                                            }
                                            Log.w(INFO_TAG, "uploadPicture:failure", task.getException());
                                            outAnimation = new AlphaAnimation(1f, 0f);
                                            outAnimation.setDuration(200);
                                            progressBarHolder.setAnimation(outAnimation);
                                            progressBarHolder.setVisibility(View.GONE);
                                            signUpButton.setEnabled(true);
                                        }
                                    }
                                });
                            }
                            else {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(INFO_TAG, "createUserWithEmail:success");
                                createUserData(email, "");
                            }

                        } else {
                            try
                            {
                                throw task.getException();
                            }
                            // if user enters wrong email.
                            catch (FirebaseAuthWeakPasswordException weakPassword)
                            {
                                Log.d(INFO_TAG, "onComplete: weak_password");
                                Toast.makeText(RegisterActivity.this, "Password is too weak.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // if user enters wrong password.
                            catch (FirebaseAuthInvalidCredentialsException malformedEmail)
                            {
                                Log.d(INFO_TAG, "onComplete: malformed_email");
                                Toast.makeText(RegisterActivity.this, "E-mail format is not correct.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            catch (FirebaseAuthUserCollisionException existEmail)
                            {
                                Log.d(INFO_TAG, "onComplete: exist_email");
                                Toast.makeText(RegisterActivity.this, "E-mail already exists.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            catch (Exception e)
                            {
                                Log.d(INFO_TAG, "onComplete: " + e.getMessage());
                                Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            // If sign in fails, display a message to the user.
                            Log.w(INFO_TAG, "createUserWithEmail:failure", task.getException());
                            outAnimation = new AlphaAnimation(1f, 0f);
                            outAnimation.setDuration(200);
                            progressBarHolder.setAnimation(outAnimation);
                            progressBarHolder.setVisibility(View.GONE);
                            signUpButton.setEnabled(true);
                        }
                    }
                });
    }

    private void createUserData(String email, String profile_image_uri) {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        String name = signUpNameField.getText().toString();
        String surname = signUpSurnameField.getText().toString();
        String age = signUpAgeField.getText().toString();
        String sport = signUpSportField.getText().toString();
        boolean is_coach = signUpIsCoachField.isChecked();

        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("first_name", name);
        user.put("last_name", surname);
        user.put("age", age);
        user.put("sport", sport);
        user.put("is_coach", is_coach);
        user.put("profile_image_ref", profile_image_uri);
        db.collection("users").document(mAuth.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(INFO_TAG, "Username info successfully added.");
                        outAnimation = new AlphaAnimation(1f, 0f);
                        outAnimation.setDuration(200);
                        progressBarHolder.setAnimation(outAnimation);
                        progressBarHolder.setVisibility(View.GONE);
                        signUpButton.setEnabled(true);
                        startMainActivity();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(INFO_TAG, "Error adding document", e);
                        outAnimation = new AlphaAnimation(1f, 0f);
                        outAnimation.setDuration(200);
                        progressBarHolder.setAnimation(outAnimation);
                        progressBarHolder.setVisibility(View.GONE);
                        signUpButton.setEnabled(true);
                    }
                });
    }

}