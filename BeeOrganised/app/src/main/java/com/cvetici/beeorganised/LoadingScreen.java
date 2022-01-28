package com.cvetici.beeorganised;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LoadingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Animation top, bottom;
        ImageView topi, bottomi;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);
        top = AnimationUtils.loadAnimation(this, R.anim.loading_top);
        bottom = AnimationUtils.loadAnimation(this, R.anim.loading_bottom);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        topi = findViewById(R.id.gore);
        bottomi = findViewById(R.id.dole);
        topi.setAnimation(top);
        bottomi.setAnimation(bottom);

        new Handler().postDelayed(new Runnable() {

            public void run() {

                if(load()==0) {
                    Intent intent = new Intent(LoadingScreen.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }else if(load()==1){
                   Intent intent = new Intent(LoadingScreen.this, WorkerActivity.class);
                    startActivity(intent);
                    finish();
                }else if(load()==2){
                    Intent intent = new Intent(LoadingScreen.this, ChildActivity.class);
                    startActivity(intent);
                    finish();
                }else if(load()==3){
                    Intent intent = new Intent(LoadingScreen.this, ParentWorkerActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        }, 1500);


    }
    public int load(){
        String FILE_NAME="UserData";
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("brUsera",null);
        if(json==null){
            return 0;
        }else{
            return Integer.parseInt(json);
        }

    }




}