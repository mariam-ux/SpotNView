package com.example.spotnview;

import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;

import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.integration.android.IntentIntegrator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



//


import android.widget.TextView;

public class QRScanActivity extends AppCompatActivity {
    Button btn_scan;
    TextView resQrViewer;

     private String qrResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrscan);
        btn_scan=findViewById(R.id.btn_scanner);
        resQrViewer=findViewById(R.id.resQrViewer);
        btn_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
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

        /*@Override
        public void onActivityResult ( int requestCode, int resultCode, Intent intent){
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
            if (qrResult != null) {
                String scanResult = result.getContents();
                String dataType = detectDataType(scanResult);
                if (dataType.equals("URL")) {
                    // Open the URL in a web browser or WebView
                } else if (dataType.equals("Barcode")) {
                    // Process the barcode data
                } else {
                    // Handle the case where the scanned data is not recognized
                }
            } else {
                super.onActivityResult(requestCode, resultCode, intent);
            }
        }*/

  /*  private String detectDataType(String data) {
        String regexUrl = "^(http|https)://.*$";
        String regexBarcode = "^\\d+$"; // Assumes barcode contains only numbers
        if (data.matches(regexUrl)) {
            return "URL";
        } else if (data.matches(regexBarcode)) {
            return "Barcode";
        } else {
            return "Unknown";
        }*/

     /*   Uri searchUri = Uri.parse("https://www.google.com/search?q=" + qrResult);
        Intent searchIntent = new Intent(Intent.ACTION_VIEW, searchUri);
        searchIntent.setPackage("com.google.android.googlequicksearchbox");
        startActivity(searchIntent);

        // Check if the scanned data is a valid URL
        /*if (Patterns.WEB_URL.matcher(qrResult).matches()) {
            // If it is a valid URL, create an intent to open a web browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/search?q=" + qrResult));
            startActivity(browserIntent);
        }
        else {
            resQrViewer.setText(qrResult);


        }*/








   /* ActivityResultLauncher<ScanOptions> barLaucher=registerForActivityResult(new ScanContract(),result ->
    {
        if(result.getContents()!=null)
        {
            AlertDialog.Builder builder =new AlertDialog.Builder(QRScanActivity.this);

            builder.setTitle("result");
            builder.setMessage(result.getContents());
            qrResult=(String)result.getContents();
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });//.show();*/
   ActivityResultLauncher<ScanOptions> barLaucher = registerForActivityResult(new ScanContract(), result -> {
       if (result.getContents() != null) {
           String scanResult = result.getContents();
           if (isUrl(scanResult)) {
               // Open the URL in a web browser or WebView
               Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(scanResult));
               startActivity(browserIntent);
           } else {
               // Handle the case where the scanned data is not a URL
             /*  AlertDialog.Builder builder = new AlertDialog.Builder(QRScanActivity.this);
               builder.setTitle("Result");
               builder.setMessage(scanResult);
               builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss());
               builder.show();*/
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





}
