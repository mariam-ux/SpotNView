package com.example.spotnview;

import androidx.appcompat.app.AppCompatActivity;
import static android.Manifest.permission.CAMERA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;

import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

public class ScanActivity extends AppCompatActivity {
    private ImageView img;
    private TextView resulttv;
    private Button Snapbtn;
    private Button Detectbtn;
    private Bitmap imageBitMap;
    static final int request_Image_Capture=1;
    private ImageButton searchBtn;
    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        img=findViewById(R.id.Imagecontainer);
        resulttv=findViewById(R.id.textdetedcted);
        Snapbtn=findViewById(R.id.Snapbtn);
        Detectbtn=findViewById(R.id.dbtn);
        //search button
        searchBtn = findViewById(R.id.searchBTN);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Editable editable = (Editable) resulttv.getText();
                String searchText = editable != null ? editable.toString() : "";
                SharedPreferences sharedPrefs = getSharedPreferences("MyPrefs", ScanActivity.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putBoolean("shouldStartWebDriver", true);
                editor.apply();
                Intent intent = new Intent(ScanActivity.this, ReviewsActivity.class);
                intent.putExtra("detectedText", searchText);
                startActivity(intent);
            }
        });
        Detectbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                detectText();
            }
        });

        Snapbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermission()){
                    captureImage();
                }else{
                    requestPermission();
                }

            }
        });




    }

    private  boolean checkPermission(){
        int cameraPermission= ContextCompat.checkSelfPermission(getApplicationContext(),CAMERA);
        return cameraPermission == PackageManager.PERMISSION_GRANTED;

    }
    private void requestPermission(){
        int permission_Code=200;
        ActivityCompat.requestPermissions(this,new String[]{CAMERA},permission_Code);
    }

    private void captureImage(){
        Intent takePicture=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePicture.resolveActivity(getPackageManager())!=null){
            startActivityForResult(takePicture,request_Image_Capture);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0){
            boolean cameraPermission=grantResults[0]==PackageManager.PERMISSION_GRANTED;
            if(cameraPermission){
                Toast.makeText(this,"permission Granted..",Toast.LENGTH_SHORT).show();
                captureImage();
            }
            else{
                Toast.makeText(this,"Permission Denied..",Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==request_Image_Capture && resultCode==RESULT_OK);
        Bundle extras=data.getExtras();
        imageBitMap=(Bitmap)extras.get("data");
        img.setImageBitmap(imageBitMap);

    }

    private void detectText(){
        InputImage image1=InputImage.fromBitmap(imageBitMap,0);
        TextRecognizer recognizer= TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        Task<Text> result=recognizer.process(image1).addOnSuccessListener(new OnSuccessListener<Text>() {
            @Override
            public void onSuccess(@NonNull Text text) {
                StringBuilder result = new StringBuilder();
                for(Text.TextBlock block: text.getTextBlocks()){
                    String blockText=block.getText();
                    Point[] blockCornerPoint=block.getCornerPoints();
                    Rect blockFrame=block.getBoundingBox();
                    for(Text.Line line: block.getLines()){
                        String lineText=line.getText();
                        Point[] lineCornerPoint=line.getCornerPoints();
                        Rect lineRect=line.getBoundingBox();
                        for(Text.Element element:line.getElements() ){
                            String elementText=element.getText();
                            result.append(elementText);

                        }

                        resulttv.setText(blockText);

                    }

                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ScanActivity.this, "Fail To Detect Text From Image"+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }



}
