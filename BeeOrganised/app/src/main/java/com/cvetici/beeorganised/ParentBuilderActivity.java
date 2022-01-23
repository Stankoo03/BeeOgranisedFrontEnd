package com.cvetici.beeorganised;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class ParentBuilderActivity extends AppCompatActivity {

    private int CAMERA_PERMISSION_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_builder);
    }

    public void Scan(View view) {
        if(ContextCompat.checkSelfPermission(ParentBuilderActivity.this,
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(this,ParentCallerActivity.class);
            startActivity(intent);

        }else{
            requestCameraPermission();
        }
    }
    private void requestCameraPermission() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.CAMERA)){
            new AlertDialog.Builder(this)
                    .setTitle("Dozvola potrebna")
                    .setMessage("Kamera je potrebna kako bi ste skenirali kod")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(ParentBuilderActivity.this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
                        }
                    })
                    .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create().show();

        }else{
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},CAMERA_PERMISSION_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(ParentBuilderActivity.this, "Permission GRANTED", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this,ParentCallerActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(ParentBuilderActivity.this, "Permission DENIED", Toast.LENGTH_SHORT).show();
            }

        }
    }


}