package com.example.spotnview;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class HomeActivity extends BaseActivity {
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions gso;
    private Button cbtn,qrbtnlink;

    @Override
    protected int getContentViewId() {
        return R.layout.activity_home;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d("slectedItemId", String.valueOf(R.id.navigation_home));
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // set the selected item
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        cbtn=findViewById(R.id.cbtn);
        qrbtnlink=findViewById(R.id.qrbtnlink);
        qrbtnlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent j=new Intent(HomeActivity.this,QRScanActivity.class);
                startActivity(j);
            }
        });
        cbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(HomeActivity.this,ScanActivity.class);
                startActivity(i);
            }
        });
    }

}