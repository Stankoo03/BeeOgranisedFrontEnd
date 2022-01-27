package com.cvetici.beeorganised;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChildWorkerActivity extends AppCompatActivity {

    ImageView qrcode;
    Button generate;
    EditText nameInput,passwordInput;
    boolean prekidac =false;
    Bitmap bitmap ;
    Intent intent;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ListenerRegistration listener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child_worker);
        intent=new Intent(this,ChildActivity.class);

        nameInput= findViewById(R.id.usernameInput);
        passwordInput = findViewById(R.id.passwordInput);
        generate = findViewById(R.id.btn_generate);
        qrcode = findViewById(R.id.qrcode);

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = nameInput.getText().toString().trim();
                DocumentReference docId = db.collection("users").document(s);
               listener = docId.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if(value.exists()){
                            ListClass LC = value.toObject(ListClass.class);
                            if(LC.getPassword()==passwordInput.getText().toString()&&LC.getIdChild()==s){
                                prekidac=true;
                            }else{
                                Toast.makeText(ChildWorkerActivity.this, "Try using different username/password combination", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                            MultiFormatWriter writer = new MultiFormatWriter();
                            try {
                                BitMatrix matrix = writer.encode(s, BarcodeFormat.QR_CODE,350,350);
                                BarcodeEncoder encoder = new BarcodeEncoder();
                                bitmap = encoder.createBitmap(matrix);
                                qrcode.setImageBitmap(bitmap);
                                InputMethodManager manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                manager.hideSoftInputFromWindow(nameInput.getApplicationWindowToken(),0);
                                ListClass LC = new ListClass(s,passwordInput.getText().toString(),new ArrayList<Task>());
                                intent.putExtra("id",s);
                                docId.set(LC);
                                prekidac = true;

                            } catch (WriterException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });



            }
        });



    }

    @Override
    protected void onStop() {
        super.onStop();
        listener.remove();
    }

    public void nastavi(View view) {
        if(prekidac==true){
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            startActivity(intent);
            save(2);
            finish();
        }

    }

    public void save(int user){
        String FILE_NAME="UserData";
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("brUsera",json);
        editor.apply();
    }

}