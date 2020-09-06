package com.example.facedetection;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Button cameraButton;
    public final static int REQUEST_IMAGE_CAPTURE = 123;
    private FirebaseVisionImage image;
    private FirebaseVisionFaceDetector detector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        cameraButton = findViewById(R.id.camera_button);

        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if(takePicture.resolveActivity(getPackageManager()) !=null){
                    startActivityForResult(takePicture,REQUEST_IMAGE_CAPTURE);
                    //Toast.makeText(MainActivity.this,"after detect funtion",Toast.LENGTH_SHORT).show();

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Toast.makeText(MainActivity.this,"Before detect funtion",Toast.LENGTH_SHORT).show();
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK){
            Bundle extras = data.getExtras();
            Bitmap bitmap = (Bitmap)extras.get("data");

            detectFace(bitmap);
           // Toast.makeText(MainActivity.this,"after detect funtion",Toast.LENGTH_SHORT).show();
        }
    }

    private void detectFace(Bitmap bitmap) {
        FirebaseVisionFaceDetectorOptions highAccuracyOpts =
                new FirebaseVisionFaceDetectorOptions.Builder()
                        .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE)
                        .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
                        .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                        .build();
        try {
            image = FirebaseVisionImage.fromBitmap(bitmap);
            detector = FirebaseVision.getInstance().getVisionFaceDetector(highAccuracyOpts);
        } catch (Exception e) {
            e.printStackTrace();
        }

        detector.detectInImage(image).addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
            @Override
            public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                int i=0;
                String resultText = "";
                for(FirebaseVisionFace face:firebaseVisionFaces){
                    i++;
                    resultText = resultText.concat("\n face: "+i+"").concat("\n Smile "+face.getSmilingProbability()*100+"%")
                            .concat("\n Right Eye open "+face.getRightEyeOpenProbability()*100+"%")
                            .concat("\n Left Eye open "+face.getLeftEyeOpenProbability()*100+"%");

                }
                if(firebaseVisionFaces.size() == 0){
                    Toast.makeText(MainActivity.this,"No Face Detected",Toast.LENGTH_SHORT).show();
                }
                else{
//                    Bundle bundle = new Bundle();
//                    bundle.putString(LCOfaceDetection.RESULT_TEXT,resultText);
//                    DialogFragment dialogFragment = new DialogFragment();
//                    dialogFragment.setArguments(bundle);
//                    dialogFragment.setCancelable(false);
//                    dialogFragment.show(getSupportFragmentManager(),LCOfaceDetection.RESULT_DIALOG);
                    //Toast.makeText(MainActivity.this,resultText,Toast.LENGTH_LONG).show();
                    showMessage("NO. of faces is "+i,resultText);
                }
            }
        });
    }
    private void showMessage(String title,String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.create();
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }
}
