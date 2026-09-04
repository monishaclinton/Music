package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {

    // =========================================================
    // VIEWS
    // =========================================================

    ImageView profileImage;
    ImageView editPasswordButton;

    private LinearLayout profileLayout;

    private TextView profileName;
    TextView profileText;
    private TextView profileEmail;
    private TextView password;

    private Button logoutButton;


    // =========================================================
    // FIREBASE / SHARED PREFERENCES
    // =========================================================

    private SharedPreferences preferences;
    private FirebaseFirestore db;


    // =========================================================
    // PROFILE IMAGE
    // =========================================================

    private static final int PICK_IMAGE = 100;


    // =========================================================
    // PASSWORD
    // =========================================================

    private String actualPassword = "";

    /*
     * 0 = password hidden
     * 1 = password visible
     *
     * First click  -> show password
     * Second click -> open update dialog
     */
    private int passwordClickCount = 0;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);


        // =====================================================
        // FIND VIEWS
        // =====================================================

        profileLayout =
                findViewById(R.id.profileSettings);

        profileImage =
                findViewById(R.id.profileImage);

        profileName =
                findViewById(R.id.profileName);

        profileEmail =
                findViewById(R.id.profileEmail);

        password =
                findViewById(R.id.editPassword);

        profileText =
                findViewById(R.id.prof_text);

        editPasswordButton =
                findViewById(R.id.editPasswordButton);

        logoutButton =
                findViewById(R.id.logoutButton);


        // =====================================================
        // PAGE ANIMATIONS
        // =====================================================

        // -----------------------------------------------------
        // Profile title
        // -----------------------------------------------------

        if (profileText != null) {

            profileText.startAnimation(
                    AnimationUtils.loadAnimation(
                            this,
                            R.anim.profile_title
                    )
            );
        }


        // -----------------------------------------------------
        // Profile image
        // -----------------------------------------------------

        if (profileImage != null) {

            profileImage.postDelayed(() -> {

                profileImage.startAnimation(
                        AnimationUtils.loadAnimation(
                                this,
                                R.anim.profile_image
                        )
                );

            }, 200);
        }


        // -----------------------------------------------------
        // Name
        // -----------------------------------------------------

        if (profileName != null) {

            profileName.postDelayed(() -> {

                profileName.startAnimation(
                        AnimationUtils.loadAnimation(
                                this,
                                R.anim.profile_text
                        )
                );

            }, 400);
        }


        // -----------------------------------------------------
        // Email
        // -----------------------------------------------------

        if (profileEmail != null) {

            profileEmail.postDelayed(() -> {

                profileEmail.startAnimation(
                        AnimationUtils.loadAnimation(
                                this,
                                R.anim.profile_text
                        )
                );

            }, 550);
        }


        // -----------------------------------------------------
        // Edit Password
        // -----------------------------------------------------

        if (profileLayout != null) {

            profileLayout.postDelayed(() -> {

                profileLayout.startAnimation(
                        AnimationUtils.loadAnimation(
                                this,
                                R.anim.profile_left
                        )
                );

            }, 600);
        }


        // -----------------------------------------------------
        // Logout
        // -----------------------------------------------------

        if (logoutButton != null) {

            logoutButton.postDelayed(() -> {

                logoutButton.startAnimation(
                        AnimationUtils.loadAnimation(
                                this,
                                R.anim.profile_bottom
                        )
                );

            }, 800);
        }


        // =====================================================
        // SHARED PREFERENCES
        // =====================================================

        preferences =
                getSharedPreferences(
                        "UserPrefs",
                        MODE_PRIVATE
                );


        // =====================================================
        // FIRESTORE
        // =====================================================

        db =
                FirebaseFirestore.getInstance();


        // =====================================================
        // GET EMAIL
        // =====================================================

        String email =
                preferences.getString(
                        "email",
                        ""
                );


        if (email.isEmpty()) {

            Toast.makeText(
                    this,
                    "User email not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // =====================================================
        // LOAD USER
        // =====================================================

        loadUserFromFirestore(email);


        // =====================================================
        // PROFILE IMAGE CLICK
        // =====================================================

        if (profileImage != null) {

            profileImage.setOnClickListener(v -> {

                Intent intent =
                        new Intent(
                                Intent.ACTION_OPEN_DOCUMENT
                        );

                intent.setType("image/*");

                intent.addCategory(
                        Intent.CATEGORY_OPENABLE
                );

                intent.addFlags(
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                                |
                                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
                );

                startActivityForResult(
                        intent,
                        PICK_IMAGE
                );
            });
        }


        // =====================================================
         // PASSWORD EDIT ICON
        // =====================================================

        if (editPasswordButton != null) {

            editPasswordButton.setOnClickListener(v -> {

                // =================================================
                // FIRST CLICK
                // SHOW PASSWORD
                // =================================================

                if (passwordClickCount == 0) {

                    if (actualPassword != null
                            && !actualPassword.isEmpty()) {

                        if (password != null) {

                            password.setText(
                                    actualPassword
                            );
                        }

                        passwordClickCount = 1;

                    } else {

                        Toast.makeText(
                                Profile.this,
                                "Password not available",
                                Toast.LENGTH_SHORT
                        ).show();
                    }


                }

                // =================================================
                // SECOND CLICK
                // OPEN UPDATE PASSWORD DIALOG
                // =================================================

                else {

                    showChangePasswordDialog();
                }
            });
        }


        // =====================================================
        // LOGOUT
        // =====================================================

        if (logoutButton != null) {

            logoutButton.setOnClickListener(
                    v -> logoutUser()
            );
        }
    }


    // =========================================================
    // LOAD USER FROM FIRESTORE
    // =========================================================

    private void loadUserFromFirestore(
            String email) {

        db.collection("Users")

                .whereEqualTo(
                        "email",
                        email
                )

                .get()

                .addOnSuccessListener(
                        querySnapshot -> {

                            // =================================
                            // USER NOT FOUND
                            // =================================

                            if (querySnapshot.isEmpty()) {

                                Toast.makeText(
                                        Profile.this,
                                        "User not found",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            // =================================
                            // GET USER DOCUMENT
                            // =================================

                            DocumentSnapshot document =
                                    querySnapshot
                                            .getDocuments()
                                            .get(0);


                            // =================================
                            // NAME
                            // =================================

                            String name =
                                    document.getString(
                                            "name"
                                    );


                            // =================================
                            // EMAIL
                            // =================================

                            String firestoreEmail =
                                    document.getString(
                                            "email"
                                    );


                            // =================================
                            // PASSWORD
                            // =================================

                            String firebasePassword =
                                    document.getString(
                                            "password"
                                    );


                            // =================================
                            // GOOGLE PROFILE IMAGE
                            // =================================

                            String googleProfilePicture =
                                    document.getString(
                                            "profilePicture"
                                    );


                            // =================================
                            // SHOW NAME
                            // =================================

                            if (profileName != null) {

                                if (name != null
                                        && !name.isEmpty()) {

                                    profileName.setText(
                                            name
                                    );

                                } else {

                                    profileName.setText(
                                            "User"
                                    );
                                }
                            }


                            // =================================
                            // SHOW EMAIL
                            // =================================

                            if (profileEmail != null) {

                                if (firestoreEmail != null
                                        && !firestoreEmail.isEmpty()) {

                                    profileEmail.setText(
                                            firestoreEmail
                                    );
                                }
                            }


                            // =================================
                            // PASSWORD
                            // =================================

                            if (password != null) {

                                if (firebasePassword != null
                                        && !firebasePassword.isEmpty()) {

                                    // Store actual password
                                    actualPassword =
                                            firebasePassword;


                                    // Hide by default
                                    passwordClickCount = 0;

                                    password.setText(
                                            "******"
                                    );

                                } else {

                                    // Google account
                                    actualPassword = "";

                                    passwordClickCount = 0;

                                    password.setText(
                                            "Google Account"
                                    );
                                }
                            }


                            // =================================
                            // LOCAL PROFILE IMAGE
                            // =================================

                            String localProfilePicture =
                                    preferences.getString(
                                            "profileImage",
                                            ""
                                    );


                            // =================================
                            // LOAD PROFILE IMAGE
                            // =================================

                            loadProfileImage(
                                    localProfilePicture,
                                    googleProfilePicture
                            );


                            // =================================
                            // SAVE USER DATA
                            // =================================

                            preferences.edit()

                                    .putString(
                                            "name",
                                            name
                                    )

                                    .putString(
                                            "email",
                                            firestoreEmail
                                    )

                                    .apply();
                        }
                )

                .addOnFailureListener(
                        e -> {

                            Log.e(
                                    "PROFILE",
                                    "Error loading user",
                                    e
                            );

                            Toast.makeText(
                                    Profile.this,
                                    "Failed to load profile: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }


    // =========================================================
    // LOAD PROFILE IMAGE
    // =========================================================

    private void loadProfileImage(
            String localProfilePicture,
            String googleProfilePicture) {

        if (profileImage == null) {
            return;
        }


        // =====================================================
        // LOCAL IMAGE
        // =====================================================

        if (localProfilePicture != null
                && !localProfilePicture.isEmpty()) {

            try {

                Uri imageUri =
                        Uri.parse(
                                localProfilePicture
                        );

                profileImage.setImageURI(
                        imageUri
                );

                return;

            } catch (Exception e) {

                Log.e(
                        "PROFILE_IMAGE",
                        "Error loading local image",
                        e
                );
            }
        }


        // =====================================================
        // GOOGLE IMAGE
        // =====================================================

        if (googleProfilePicture != null
                && !googleProfilePicture.isEmpty()) {

            Glide.with(this)

                    .load(
                            googleProfilePicture
                    )

                    .placeholder(
                            R.drawable.profile
                    )

                    .error(
                            R.drawable.profile
                    )

                    .into(
                            profileImage
                    );

            return;
        }


        // =====================================================
        // DEFAULT IMAGE
        // =====================================================

        profileImage.setImageResource(
                R.drawable.profile
        );
    }


    // =========================================================
    // PROFILE IMAGE RESULT
    // =========================================================

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(
                requestCode,
                resultCode,
                data
        );


        if (requestCode == PICK_IMAGE
                && resultCode == RESULT_OK
                && data != null) {

            Uri imageUri =
                    data.getData();


            if (imageUri != null) {

                // =============================================
                // KEEP IMAGE PERMISSION
                // =============================================

                try {

                    getContentResolver()
                            .takePersistableUriPermission(
                                    imageUri,
                                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                            );

                } catch (Exception e) {

                    Log.e(
                            "PROFILE_IMAGE",
                            "Could not persist image permission",
                            e
                    );
                }


                // =============================================
                // SHOW IMAGE
                // =============================================

                profileImage.setImageURI(
                        imageUri
                );


                // =============================================
                // SAVE IMAGE URI
                // =============================================

                preferences.edit()

                        .putString(
                                "profileImage",
                                imageUri.toString()
                        )

                        .apply();


                Toast.makeText(
                        this,
                        "Profile picture updated",
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }


    // =========================================================
    // CUSTOM CHANGE PASSWORD DIALOG
    // =========================================================

    private void showChangePasswordDialog() {

        // =====================================================
        // INFLATE CUSTOM XML
        // =====================================================

        View view =
                getLayoutInflater().inflate(
                        R.layout.dialog_change_password,
                        null
                );


        // =====================================================
        // FIND DIALOG VIEWS
        // =====================================================

        EditText newPassword =
                view.findViewById(
                        R.id.newPassword
                );

        Button updateButton =
                view.findViewById(
                        R.id.updateButton
                );

        Button cancelButton =
                view.findViewById(
                        R.id.cancelButton
                );


        // =====================================================
        // CREATE DIALOG
        // =====================================================

        AlertDialog dialog =
                new AlertDialog.Builder(this)
                        .setView(view)
                        .create();


        // =====================================================
        // TRANSPARENT DEFAULT BACKGROUND
        // =====================================================

        if (dialog.getWindow() != null) {

            dialog.getWindow()
                    .setBackgroundDrawableResource(
                            android.R.color.transparent
                    );
        }


        // =====================================================
        // CANCEL BUTTON
        // =====================================================

        cancelButton.setOnClickListener(v -> {

            dialog.dismiss();

        });


        // =====================================================
        // UPDATE BUTTON
        // =====================================================

        updateButton.setOnClickListener(v -> {

            String newPasswordValue =
                    newPassword
                            .getText()
                            .toString()
                            .trim();


            // ================================================
            // EMPTY PASSWORD
            // ================================================

            if (newPasswordValue.isEmpty()) {

                newPassword.setError(
                        "Password cannot be empty"
                );

                newPassword.requestFocus();

                return;
            }


            // ================================================
            // PASSWORD LENGTH
            // ================================================

            if (newPasswordValue.length() < 6) {

                newPassword.setError(
                        "Password must be at least 6 characters"
                );

                newPassword.requestFocus();

                return;
            }


            // ================================================
            // UPDATE FIRESTORE
            // ================================================

            updatePasswordInFirestore(
                    newPasswordValue,
                    dialog
            );

        });


        // =====================================================
        // SHOW DIALOG
        // =====================================================

        dialog.show();


        // =====================================================
        // DIALOG WIDTH
        // =====================================================

        if (dialog.getWindow() != null) {

            int width =
                    (int) (
                            getResources()
                                    .getDisplayMetrics()
                                    .widthPixels * 0.90
                    );

            dialog.getWindow().setLayout(
                    width,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
        }
    }


    // =========================================================
    // UPDATE PASSWORD IN FIRESTORE
    // =========================================================

    private void updatePasswordInFirestore(
            String newPassword,
            AlertDialog dialog) {

        // =====================================================
        // GET EMAIL
        // =====================================================

        String email =
                preferences.getString(
                        "email",
                        ""
                );


        if (email.isEmpty()) {

            Toast.makeText(
                    Profile.this,
                    "Email not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        Log.d(
                "PASSWORD_UPDATE",
                "Updating password for: " + email
        );


        // =====================================================
        // FIND USER
        // =====================================================

        db.collection("Users")

                .whereEqualTo(
                        "email",
                        email
                )

                .get()

                .addOnSuccessListener(
                        querySnapshot -> {

                            // =================================
                            // USER NOT FOUND
                            // =================================

                            if (querySnapshot.isEmpty()) {

                                Toast.makeText(
                                        Profile.this,
                                        "User not found",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            // =================================
                            // GET DOCUMENT
                            // =================================

                            DocumentSnapshot document =
                                    querySnapshot
                                            .getDocuments()
                                            .get(0);


                            String documentId =
                                    document.getId();


                            // =================================
                            // UPDATE FIRESTORE
                            // =================================

                            db.collection("Users")

                                    .document(documentId)

                                    .update(
                                            "password",
                                            newPassword
                                    )

                                    .addOnSuccessListener(
                                            unused -> {

                                                Log.d(
                                                        "PASSWORD_UPDATE",
                                                        "Password updated in Firestore"
                                                );


                                                // =================
                                                // SAVE NEW PASSWORD
                                                // =================

                                                actualPassword =
                                                        newPassword;


                                                // =================
                                                // HIDE PASSWORD
                                                // =================

                                                passwordClickCount =
                                                        0;


                                                if (password != null) {

                                                    password.setText(
                                                            "******"
                                                    );
                                                }


                                                // =================
                                                // SUCCESS MESSAGE
                                                // =================

                                                Toast.makeText(
                                                        Profile.this,
                                                        "Password updated successfully",
                                                        Toast.LENGTH_SHORT
                                                ).show();


                                                // =================
                                                // CLOSE DIALOG
                                                // =================

                                                dialog.dismiss();

                                            }
                                    )

                                    .addOnFailureListener(
                                            e -> {

                                                Log.e(
                                                        "PASSWORD_UPDATE",
                                                        "Firestore update failed",
                                                        e
                                                );

                                                Toast.makeText(
                                                        Profile.this,
                                                        "Failed to update password: "
                                                                + e.getMessage(),
                                                        Toast.LENGTH_LONG
                                                ).show();
                                            }
                                    );
                        }
                )

                .addOnFailureListener(
                        e -> {

                            Log.e(
                                    "PASSWORD_UPDATE",
                                    "Error finding user",
                                    e
                            );

                            Toast.makeText(
                                    Profile.this,
                                    "Error: "
                                            + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                );
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    private void logoutUser() {

        // =====================================================
        // CLEAR USER DATA
        // =====================================================

        preferences.edit()
                .clear()
                .apply();


        // =====================================================
        // MESSAGE
        // =====================================================

        Toast.makeText(
                Profile.this,
                "Logged out",
                Toast.LENGTH_SHORT
        ).show();


        // =====================================================
        // OPEN MAIN ACTIVITY
        // =====================================================

        Intent intent =
                new Intent(
                        Profile.this,
                        MainActivity.class
                );


        intent.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK
                        |
                        Intent.FLAG_ACTIVITY_CLEAR_TASK
        );


        startActivity(intent);

        finish();
    }
}