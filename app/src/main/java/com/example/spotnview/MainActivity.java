package com.example.spotnview;


import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import android.util.Log;
import android.view.MenuItem;


import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoginStatusCallback;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;



import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;


import java.util.Arrays;

public class MainActivity extends BaseActivity {

    private Button Gbtn;
    private static final String TAG = "GoogleActivity";
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions gso;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private CallbackManager callbackManager;
    private Button facebookBtn;
    private TextView signedIn;


    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        //instance of the BottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_signin);
        Log.d("slectedItemId", String.valueOf(R.id.navigation_signin));

        //if user is signed in a texted view is displayed
        signedIn = findViewById(R.id.signedIn);

        //facebook sign-in bottom handlings:
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());//

        callbackManager = CallbackManager.Factory.create();
        //ckeck if the user is already signed in using facebook
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
        if(isLoggedIn) {
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }


        //setting the fb sign-in bottom
        facebookBtn = findViewById(R.id.fbBtn);
        facebookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile"));
                LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        updateUI(currentUser);
                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(MainActivity.this, "cancled", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(FacebookException error) {
                        Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });

                LoginManager.getInstance().retrieveLoginStatus(MainActivity.this, new LoginStatusCallback() {
                    @Override
                    public void onCompleted(AccessToken accessToken) {
                        Toast.makeText(MainActivity.this, "Logged in as successfully" , Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    }
                    @Override
                    public void onFailure() {
                        // No access token could be retrieved for the user
                    }
                    @Override
                    public void onError(Exception exception) {
                        // An error occurred
                    }
                });



            }
        });



        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_ID))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();


        Gbtn = findViewById(R.id.Google_btn);




        Gbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();

            }
        });

        //handle log out popup menu
        Button menuButton = findViewById(R.id.menu_button);
        PopupMenu popupMenu = new PopupMenu(this, menuButton);

        popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_logout:
                        singOut();
                        return true;
                    default:
                        return false;
                }
            }
        });

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Handle the response
                    }
                });





        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct != null){
            String personEmial = acct.getEmail();

        }



    }

    public void singOut(){
        //facebook logout
        LoginManager.getInstance().logOut();
        //google logout
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                FirebaseUser currentUser = mAuth.getCurrentUser();
                if(currentUser != null) {
                    FirebaseAuth.getInstance().signOut();
                    updateUI(null);
                } else {
                    Toast.makeText(MainActivity.this, "you are signed out", Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageView profileImageView = findViewById(R.id.profileImageView);
        profileImageView.setImageDrawable(null); // Remove the image

// Set the default profile picture as a placeholder
        int placeholderResourceId = R.drawable.profileuser2; // Replace with the resource ID of your default profile picture
        profileImageView.setImageResource(placeholderResourceId);



    }


     public void signIn(){
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);

     }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            Log.e(TAG, "Intent data is null.");
            return;
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());

             } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }

        }
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Get the Facebook access token to use it to get their info
            AccessToken token = AccessToken.getCurrentAccessToken();
            if (token == null) {
                Log.e(TAG, "Facebook access token is null");
                return;
            }

            // Authenticate with Firebase using the Facebook access token
            AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
            FirebaseAuth.getInstance().signInWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                                handleFacebookAccessToken(token);
                                Log.d(TAG, "Firebase authentication success: " + user.getEmail());
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "Firebase authentication failed: ", task.getException());
                                Toast.makeText(MainActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });


        }


    }



    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            FirebaseUser user = mAuth.getCurrentUser();
                            String displayName = user.getDisplayName();
                            String email = user.getEmail();
                            //google profile
                            String profilePictureUrl = "";
                            for (UserInfo profile : user.getProviderData()) {
                                // Check if the provider is Google
                                if (GoogleAuthProvider.PROVIDER_ID.equals(profile.getProviderId())) {
                                    profilePictureUrl = profile.getPhotoUrl().toString();
                                    break;
                                }
                            }
                            //save the url to use it when the user sign-in
                            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("profilePictureUrl", profilePictureUrl);
                            editor.apply();
                            // Load and display the profile picture
                            Log.d(TAG, "Profile Picture URL: " + profilePictureUrl);
                            ImageView profileImageView = findViewById(R.id.profileImageView);
                            Picasso.get()
                                    .load(profilePictureUrl)
                                    .into(profileImageView);
                            // Create a new node for the user in the Realtime Database and set the user data
                            DatabaseReference userRef = database.getReference("users/" + user.getUid());
                            User newUser = new User(displayName, email);
                            userRef.setValue(newUser);
                            updateUI(user);
                            Toast.makeText(MainActivity.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });

    }

    private void handleFacebookAccessToken(@Nullable AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            FirebaseUser user = mAuth.getCurrentUser();
                            //facebook profile
                            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(Uri.parse("https://graph.facebook.com/" + user.getProviderData().get(0).getUid() + "/picture?height=500"))
                                    .build();
                            user.updateProfile(profile)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                String profilePictureUrl = user.getPhotoUrl().toString();
                                                //save the url
                                                SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("profilePictureUrl", profilePictureUrl);
                                                editor.apply();
                                                Log.d(TAG, "Profile Picture URL: " + profilePictureUrl);
                                                //load the image
                                                ImageView profileImageView = findViewById(R.id.profileImageView);
                                                Picasso.get()
                                                        .load(profilePictureUrl)
                                                        .into(profileImageView);
                                            }
                                        }
                                    });
                            String displayName = user.getDisplayName();
                            String email = user.getEmail();
                            DatabaseReference userRef = database.getReference("users/" + user.getUid());
                            User newUser = new User(displayName, email);
                            userRef.setValue(newUser);
                            updateUI(user);
                            Toast.makeText(MainActivity.this, "Signed in successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }


    private void updateUI(FirebaseUser user) {
        if(user != null) {
            Gbtn.setVisibility(View.GONE);
            facebookBtn.setVisibility(View.GONE);
            signedIn.setVisibility(View.VISIBLE);
            TextView signIn_text = findViewById(R.id.signIn_text);
            signIn_text.setText(R.string.welcome_title);
            SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
            String profilePictureUrl = sharedPreferences.getString("profilePictureUrl", "");

            // Display the profile picture if a URL is available
            if (!profilePictureUrl.isEmpty()) {
                ImageView profileImageView = findViewById(R.id.profileImageView);
                Picasso.get().load(profilePictureUrl).into(profileImageView);
            }
        } else {
            Gbtn.setVisibility(View.VISIBLE);
            facebookBtn.setVisibility(View.VISIBLE);
            signedIn.setVisibility(View.GONE);

            ImageView profileImageView = findViewById(R.id.profileImageView);
            profileImageView.setImageDrawable(null); // Set the placeholder image resource
            int placeholderResourceId = R.drawable.profileuser2; // Replace with the resource ID of your default profile picture
            profileImageView.setImageResource(placeholderResourceId);
        }

    }







}