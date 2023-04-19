package com.example.spotnview;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONObject;

public class ProfileActivity extends BaseActivity implements BottomNavigationView.OnNavigationItemSelectedListener{
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions gso;

    private Button logoutBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

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


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);


        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        if(acct != null){
            String personEmial = acct.getEmail();

        }



    }

    public void singOut(){
        LoginManager.getInstance().logOut();
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                FirebaseAuth.getInstance().signOut(); // sign out the user
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation item selection
        int itemId = item.getItemId();
        if (itemId == R.id.navigation_home) {
            startActivity(new Intent(ProfileActivity.this, ProfileActivity.class));
            return true;
        } else if (itemId == R.id.navigation_reviews) {
            startActivity(new Intent(ProfileActivity.this, ReviewsActivity.class));
            return true;
        } else if (itemId == R.id.navigation_history) {
            startActivity(new Intent(ProfileActivity.this, HistoryActivity.class));
            return true;
        }
        return false;
    }
    @Override
    protected int getContentViewId() {
        return R.layout.activity_profile;
    }
}