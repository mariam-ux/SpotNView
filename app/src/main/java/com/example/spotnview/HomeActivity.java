package com.example.spotnview;

import static android.Manifest.permission.CAMERA;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
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
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONObject;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HomeActivity extends BaseActivity {
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions gso;
    private Button cbtn,qrbtnlink;
    private TextView resQrViewer;
    private TextView note;
    private TextView note2;
    @Override
    protected int getContentViewId() {
        return R.layout.activity_home;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.spotnview",  // replace with your package name
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        Log.d("slectedItemId", String.valueOf(R.id.navigation_home));
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        // set the selected item
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        cbtn=findViewById(R.id.cbtn);
        qrbtnlink=findViewById(R.id.qrbtnlink);
        resQrViewer = findViewById(R.id.resQrViewer);
        note = findViewById(R.id.textView);
        note2 = findViewById(R.id.textView5);
        note.setVisibility(View.VISIBLE);
        note2.setVisibility(View.GONE);
        resQrViewer.setVisibility(View.GONE);
        qrbtnlink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    scanCode();
                }else{
                    requestPermission();
                }


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
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("volume up to flash on");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        barLaucher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String scanResult = result.getContents();
            if (isUrl(scanResult)) {
                // Open the URL in a web browser or WebView
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
                startActivity(browserIntent);
            } else {
                note.setVisibility(View.GONE);
                note2.setVisibility(View.VISIBLE);
                resQrViewer.setVisibility(View.VISIBLE);
                resQrViewer.setText(scanResult);
            }
        }
    });

    public static boolean isUrl(String str) {
        String regex = "^https?://.*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }
    private  boolean checkPermission(){
        int cameraPermission= ContextCompat.checkSelfPermission(getApplicationContext(),CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;

    }
    private void requestPermission(){
        int permission_Code=200;
        ActivityCompat.requestPermissions(this,new String[]{CAMERA},permission_Code);
    }
}