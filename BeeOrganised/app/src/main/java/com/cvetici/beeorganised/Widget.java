package com.cvetici.beeorganised;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.DateFormat;
import android.icu.text.LocaleDisplayNames;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Widget extends AppWidgetProvider {
    private Button dugme;
    private Calendar calendar;
    private int MainDay,MainMonth,MainYear;
    private List<Task> currentList;
    private int k=0;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for(int appWidgetId:appWidgetIds){
            postaviDatume();
            load(MainDay,MainMonth,MainYear,context);
            RemoteViews views = new RemoteViews(context.getPackageName(),R.layout.layout_widget);

            //views.setOnClickPendingIntent(R.id.widget_holder,pendingIntent);
            if(currentList!=null)
            while (currentList.get(k).GetTime().GetEndTime().GetHour() < calendar.get(Calendar.HOUR_OF_DAY)) {

                k++;
            }
            Toast.makeText(context,  calendar.get(Calendar.HOUR_OF_DAY)+" ", Toast.LENGTH_SHORT).show();
            if(currentList.get(k)!=null){
                views.setCharSequence(R.id.vremeTaska1,"setText",currentList.get(k).ToStringTime());
                views.setCharSequence(R.id.imeTaska1,"setText",currentList.get(k).title.toString());
            }
            if(currentList.get(k++)!=null){
                views.setCharSequence(R.id.vremeTaska2,"setText",currentList.get(k).ToStringTime());
                views.setCharSequence(R.id.imeTaska2,"setText",currentList.get(k).title.toString());
            }else{
                views.setViewVisibility(R.id.task2layout,View.INVISIBLE);
            }

/*
            Intent intentSync = new Intent(context, Widget.class);
            intentSync.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            PendingIntent pendingSync = PendingIntent.getBroadcast(context,0, intentSync, PendingIntent.FLAG_UPDATE_CURRENT); //You need to specify a proper flag for the intent. Or else the intent will become deleted.
            views.setOnClickPendingIntent(R.id.refresh,pendingSync);
*/


            appWidgetManager.updateAppWidget(appWidgetId,views);


        }


    }



    private void postaviDatume() {
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd" );
        SimpleDateFormat mesec = new SimpleDateFormat("MM");
        SimpleDateFormat godina = new SimpleDateFormat("yyyy");

        String danas = dateFormat.format(calendar.getTime());
        String Mesec = mesec.format(calendar.getTime());
        String Godina = godina.format(calendar.getTime());


        if(danas.startsWith("0")){
            MainDay = Integer.parseInt(danas.substring(1,2));
        }else{
            MainDay = Integer.parseInt(danas);
        }
        if(Mesec.startsWith("0")){
            MainMonth = Integer.parseInt(Mesec.substring(1,2));
        }else{
            MainMonth = Integer.parseInt(Mesec);
        }
        MainYear = Integer.parseInt(Godina);


    }
    public void load(int dan,int mesec,int godina,Context context){
        String FILE_NAME=dan+"_"+mesec+"_"+godina;
        SharedPreferences sharedPreferences = context.getSharedPreferences(FILE_NAME,Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(FILE_NAME,null);
        Type type = new TypeToken<List<Task>>() {}.getType();
        currentList = gson.fromJson(json,type);
        if(currentList==null){
            currentList = new ArrayList<>();
        }


    }

}
