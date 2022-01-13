package com.cvetici.beeorganised;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.Locale;

import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setLocale("en");
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ImageButton personal_use;
        personal_use = (ImageButton) findViewById(R.id.personalUse);
        personal_use.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToPersonal();
            }
        });


    }
    private void setLocale( String lng){
        Locale locale = new Locale (lng);
        Locale.setDefault(locale);
        Configuration con = new Configuration();
        con.locale = locale;

        getBaseContext().getResources().updateConfiguration(con, getBaseContext().getResources().getDisplayMetrics());

        getApplicationContext().getResources().updateConfiguration(con, getApplicationContext().getResources().getDisplayMetrics());
    }
    private void goToPersonal(){
        Intent intent = new Intent(MainActivity.this, WorkerActivity.class);
        startActivity(intent);
        save(1);
    }
    public void save(int user){
        String FILE_NAME="UserData";
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString(FILE_NAME,json);
        editor.apply();
    }




}