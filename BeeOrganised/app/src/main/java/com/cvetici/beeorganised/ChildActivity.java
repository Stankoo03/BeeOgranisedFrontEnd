package com.cvetici.beeorganised;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.IntArrayEvaluator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import android.app.Dialog;
import com.google.gson.reflect.TypeToken;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class ChildActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,ListaTaskovaAdapter.OnTaskListener{

    private Calendar calendar,notifyCalendar;
    private TextView d1,d2,d3;
    private TextView w1,w2,w3;
    private TextView AiStartTime,AiEndTime;

    int MainDay,MainMonth,MainYear;

    private boolean clicked=false;
    private FloatingActionButton main;
    private ExtendedFloatingActionButton task,routine;

    private Animation rotateOpen,rotateClose,fromButton,toButton ;

    private LinearLayout ManualTimeLayout,AiLayout,AfterCalculateBtn,weekChecboxHolder;

    private RadioGroup RG;
    private View bottomSheetView,ListView,RoutinesView;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetDialog ListaItema,Routines;

    private Button FromTime,ToTime,CaluculateBtn,SetBtn,TaskButton,ApplyBtn;
    private int Danas=0,Sutra,PSutra,Month,Month1,Month2,Year,Year1,Year2,globalTaskPosition;

    private NotificationHelper mHelper;

    private Switch daynightSwitch;
    private ImageView sat;
    private Bitmap qrCode;
    private Intent qrCodeIntent;
    private String username;

    private EditText enterTask,enterTaskDuration;
    private SmartToDo std;
    private int h1=-1,m1=-1,h2=-1,m2=-1;
    private RecyclerView ListaTaskova,ListaRutina;
    private ListaTaskovaAdapter adapter = new ListaTaskovaAdapter(this::onTaskClick);
    private Spinner prioritySp,timeSp,durationSp,routineSp;
    private ImageButton datumPrvi,datumDrugi,datumTreci, podeshavanje, lang, srb, eng, ger, spa, fran, help;
    private ImageButton changeUserBtn;
    public  List<Task> currentList;
    public boolean dan;
    Dialog dialog;
    private RelativeLayout routineHolder;

    private CrtajObaveze crtaj;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_child);
        calendar = Calendar.getInstance();
        bottomSheetDialog = new BottomSheetDialog(
                ChildActivity.this, R.style.BottomSheetDialogTheme
        );
        bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.layout_bottom_sheet,(LinearLayout)findViewById(R.id.bottomSheetContainer)
        );
        bottomSheetDialog.setContentView(bottomSheetView);

        ListaItema = new BottomSheetDialog(ChildActivity.this,R.style.BottomSheetDialogTheme);
        ListView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.list_layout,(RelativeLayout)findViewById(R.id.ListRelative)
        );
        ListaItema.setContentView(ListView);

        Routines = new BottomSheetDialog(ChildActivity.this,R.style.BottomSheetDialogTheme);
        RoutinesView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.layout_rutine,(RelativeLayout)findViewById(R.id.ListRelative)
        );
        Routines.setContentView(RoutinesView);

        dialog = new Dialog(ChildActivity.this);


        FindViews();
        RadioGroupClicked();
        FromToTimeSetter();
        SwitchListener();
        PostaviDatume();
        AiTaskCalculation();
        CalendarButtonClick();
        openSettings();
        applyRoutines();



    }

    private void FindViews(){

        std = new SmartToDo(5);

        daynightSwitch = (Switch) findViewById(R.id.daynightSwitch);
        d1 = (TextView) findViewById(R.id.firstDate);
        d2 = (TextView) findViewById(R.id.secondDate);
        d3 = (TextView) findViewById(R.id.thirdDate);
        w1 = (TextView) findViewById(R.id.firstWeek);
        w2 = (TextView) findViewById(R.id.secondWeek);
        w3 = (TextView) findViewById(R.id.thirdWeek);

        datumPrvi = (ImageButton)findViewById(R.id.datumPrvi);
        datumDrugi = (ImageButton)findViewById(R.id.datumDrugi);
        datumTreci = (ImageButton)findViewById(R.id.datumTreci);


        ListaTaskova = ListView.findViewById(R.id.ListaRV);

        ListaRutina =  RoutinesView.findViewById(R.id.ListaRV);
        routineSp = RoutinesView.findViewById(R.id.routineSp);
        weekChecboxHolder = RoutinesView.findViewById(R.id.weekDayHolder);
        routineHolder = RoutinesView.findViewById(R.id.repeatholder);
        ApplyBtn = Routines.findViewById(R.id.applyRoutine);


        rotateOpen = AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim);
        fromButton = AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim);
        toButton = AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim);


        main = (FloatingActionButton) findViewById(R.id.MainButton);
        routine = (ExtendedFloatingActionButton) findViewById(R.id.RoutineButton);
        task = (ExtendedFloatingActionButton) findViewById(R.id.SimpleButton);

        sat = (ImageView) findViewById(R.id.sat);
        TaskButton = (Button) findViewById(R.id.TaskButton);


        RG = bottomSheetView.findViewById(R.id.RadioGroup);
        ManualTimeLayout = bottomSheetView.findViewById(R.id.ManualTimeLayout);
        AiLayout = bottomSheetView.findViewById(R.id.AiTimeLayout);
        FromTime = bottomSheetView.findViewById(R.id.fromTime);
        ToTime = bottomSheetView.findViewById(R.id.toTime);
        AfterCalculateBtn = bottomSheetView.findViewById(R.id.AfterBtnClicked);
        CaluculateBtn = bottomSheetView.findViewById(R.id.CalculateBtn);
        SetBtn = (Button) bottomSheetView.findViewById(R.id.SetBtn);
        enterTask = (EditText) bottomSheetView.findViewById(R.id.enterTask);
        prioritySp = bottomSheetView.findViewById(R.id.prioritySpinner);
        timeSp = bottomSheetView.findViewById(R.id.whenSpinner);
        durationSp = bottomSheetView.findViewById(R.id.durationSpinner);
        enterTaskDuration = bottomSheetView.findViewById(R.id.durationEditText);
        AiStartTime = bottomSheetView.findViewById(R.id.AiStarTime);
        AiEndTime = bottomSheetView.findViewById(R.id.AiEndTime);

        globalTaskPosition=-1;

        mHelper = new NotificationHelper(this);

        crtaj = new CrtajObaveze(getApplicationContext());
        crtaj = findViewById(R.id.crtajObaveze);

        if(loadUserName()=="") {
            qrCodeIntent = getIntent();
            qrCode = BitmapFactory.decodeByteArray(qrCodeIntent.getByteArrayExtra("qrcode"), 0, qrCodeIntent.getByteArrayExtra("qrcode").length);
            username = qrCodeIntent.getStringExtra("name");
            saveUserName(username);
        }else{
            username = loadUserName();
        }
        Toast.makeText(ChildActivity.this, username, Toast.LENGTH_SHORT).show();

        routine.shrink();
        task.shrink();

    }

    private void SwitchListener(){
        if(calendar.get(Calendar.HOUR_OF_DAY)>=12){
            daynightSwitch.setChecked(false);
            sat.setImageResource(R.drawable.ic_amclock_ontop);
            crtaj.CrtajDan(true);
            crtaj.Refreshuj();
        }else{
            daynightSwitch.setChecked(true);
            sat.setImageResource(R.drawable.ic_pmclock_ontop);
            crtaj.CrtajDan(false);
            crtaj.Refreshuj();
        }

        daynightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sat.setImageResource(R.drawable.ic_amclock_ontop);
                    crtaj.CrtajDan(false);
                }else{

                    sat.setImageResource(R.drawable.ic_pmclock_ontop);
                    crtaj.CrtajDan(true);
                }
            }
        });



    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void CalendarButtonClick(){

        MainDay = Danas;
        MainMonth = Month;
        MainYear = Year;

        datumPrvi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(MainDay,MainMonth,MainYear);
                MainDay = Danas;
                MainMonth = Month;
                MainYear = Year;
                datumPrvi.setBackground(getResources().getDrawable(R.drawable.ic_datum_selected));
                datumDrugi.setBackground(getResources().getDrawable(R.drawable.ic_datum_fixed_fixed));
                datumTreci.setBackground(getResources().getDrawable(R.drawable.ic_datum_fixed_fixed));
                load(Danas,Month,Year);
                std.setTasks((ArrayList<Task>) currentList);
                adapter.setTaskovi(currentList);
                crtaj.drawLists(currentList);
                crtaj.Refreshuj();
            }
        });

        datumDrugi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(MainDay,MainMonth,MainYear);
                MainDay = Sutra;
                MainMonth = Month1;
                MainYear = Year1;
                datumDrugi.setBackground(getResources().getDrawable(R.drawable.ic_datum_selected));
                datumPrvi.setBackground(getResources().getDrawable(R.drawable.ic_datum_fixed_fixed));
                datumTreci.setBackground(getResources().getDrawable(R.drawable.ic_datum_fixed_fixed));
                load(Sutra,Month1,Year1);
                adapter.setTaskovi(currentList);
                crtaj.drawLists(currentList);
                crtaj.Refreshuj();
                std.setTasks((ArrayList<Task>) currentList);
            }
        });
        datumTreci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(MainDay,MainMonth,MainYear);
                MainDay = PSutra;
                MainMonth = Month2;
                MainYear = Year2;
                datumTreci.setBackground(getResources().getDrawable(R.drawable.ic_datum_selected));
                datumDrugi.setBackground(getResources().getDrawable(R.drawable.ic_datum_fixed_fixed));
                datumPrvi.setBackground(getResources().getDrawable(R.drawable.ic_datum_fixed_fixed));
                load(PSutra,Month2,Year2);
                std.setTasks((ArrayList<Task>) currentList);
                adapter.setTaskovi(currentList);
                crtaj.drawLists(currentList);
                crtaj.Refreshuj();
            }
        });


    }
    private void RadioGroupClicked(){

        RG.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.ManualTime:
                        ManualTimeLayout.setVisibility(View.VISIBLE);
                        AiLayout.setVisibility(View.GONE);
                        AfterCalculateBtn.setVisibility(View.GONE);
                        CaluculateBtn.setVisibility(View.GONE);
                        durationSp.setVisibility(View.GONE);
                        break;
                    case R.id.AiTime:
                        if(ManualTimeLayout.getVisibility()==View.VISIBLE){
                            ManualTimeLayout.setVisibility(View.GONE);
                        }
                        AiLayout.setVisibility(View.VISIBLE);
                        durationSp.setVisibility(View.VISIBLE);
                        CaluculateBtn.setVisibility(View.VISIBLE);
                }
            }
        });

    }
    private void FromToTimeSetter(){
        FromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker  = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"tp1");
            }
        });
        ToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker  = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(),"tp2");
            }
        });

    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void PostaviDatume(){

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd" );
        SimpleDateFormat mesec = new SimpleDateFormat("MM");
        SimpleDateFormat godina = new SimpleDateFormat("yyyy");

        String danas = dateFormat.format(calendar.getTime());
        String Mesec = mesec.format(calendar.getTime());
        String Godina = godina.format(calendar.getTime());
        Date currentTime  = calendar.getTime();

        String DanasNedelja = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime);

        d1.setText(danas);
        if(danas.startsWith("0")){
            Danas = Integer.parseInt(danas.substring(1,2));
        }else{
            Danas = Integer.parseInt(danas);
        }
        Month=Integer.parseInt(Mesec);
        if(Mesec.startsWith("0")){
            Month = Integer.parseInt(Mesec.substring(1,2));
        }else{
            Month = Integer.parseInt(Mesec);
        }
        Year = Integer.parseInt(Godina);
        load(Danas,Month,Year);
        std.setTasks((ArrayList<Task>) currentList);
        adapter.setTaskovi(currentList);
        ListaTaskova.setAdapter(adapter);
        ListaRutina.setAdapter(adapter);
        ListaRutina.setLayoutManager(new LinearLayoutManager(ChildActivity.this));
        ListaTaskova.setLayoutManager(new LinearLayoutManager(ChildActivity.this));
        crtaj.drawLists(currentList);
        crtaj.Refreshuj();



        w1.setText(DanasNedelja.substring(0,3).toUpperCase(Locale.ROOT));

        //////////////Sutrasnji Dan
        calendar.add(Calendar.DATE,1);
        currentTime = calendar.getTime();
        String sutra = dateFormat.format(calendar.getTime());
        String Mesec1 = mesec.format(calendar.getTime());
        String Godina1 = godina.format(calendar.getTime());
        if(sutra.startsWith("0")){
            Sutra = Integer.parseInt(sutra.substring(1,2));
        }else{
            Sutra = Integer.parseInt(sutra);
        }
        if(Mesec1.startsWith("0")){
            Month1 = Integer.parseInt(Mesec1.substring(1,2));
        }else{
            Month1 = Integer.parseInt(Mesec1);
        }

        Year1 = Integer.parseInt(Godina1);


        String SutraNedelja = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime);

        d2.setText(sutra);
        w2.setText(SutraNedelja.substring(0,3).toUpperCase(Locale.ROOT));

        //////////////Prekosutrasnji Dan
        calendar.add(Calendar.DATE,1);
        currentTime = calendar.getTime();
        String Mesec2 = mesec.format(calendar.getTime());
        String Godina2 = godina.format(calendar.getTime());

        String psutra = dateFormat.format(calendar.getTime());
        if(psutra.startsWith("0")){
            PSutra = Integer.parseInt(psutra.substring(1,2));
        }else{
            PSutra = Integer.parseInt(psutra);
        }
        if(Mesec1.startsWith("0")){
            Month2 = Integer.parseInt(Mesec2.substring(1,2));
        }else{
            Month2 = Integer.parseInt(Mesec2);
        }
        Year2 = Integer.parseInt(Godina2);

        String PSutraNedelja = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime);

        d3.setText(psutra);
        w3.setText(PSutraNedelja.substring(0,3).toUpperCase(Locale.ROOT));

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void otvoriKalendar(View view) {
        Intent intent = new Intent(ChildActivity.this , Kalendar.class);
        ImageButton button = (ImageButton) findViewById(R.id.datumCetvrti);
        startActivity(intent);

    }
    public void expandButtons(View view) {
        setVisibility(clicked);
        setAnimation(clicked);
        if(!clicked){
            clicked = true;
        }else{
            clicked = false;
        }

    }
    private void setVisibility(boolean clicked) {
        globalTaskPosition=-1;
        if(!clicked){
            task.setVisibility(View.VISIBLE) ;
            routine.setVisibility(View.VISIBLE);
        }else{
            task.setVisibility(View.INVISIBLE) ;
            routine.setVisibility(View.INVISIBLE);
            task.shrink();
            routine.shrink();
        }
    }
    private void setAnimation(boolean clicked){
        if(!clicked){
            task.startAnimation(fromButton);
            routine.startAnimation(fromButton);
            main.startAnimation(rotateOpen);
        }else{
            task.startAnimation(toButton);
            routine.startAnimation(toButton);
            main.startAnimation(rotateClose);

        }

    }
    private void setClickable(boolean clicked){
        if(!clicked){
            task.setClickable(true);
            routine.setClickable(true);
        }else{
            task.setClickable(false);
            routine.setClickable(false);

        }
    }
    public void simpleTaskCard(View view) {
        if(task.isExtended()){
            RG.clearCheck();
            SetBtn.setVisibility(View.GONE);
            ManualTimeLayout.setVisibility(View.GONE);
            AiLayout.setVisibility(View.GONE);
            AfterCalculateBtn.setVisibility(View.GONE);
            CaluculateBtn.setVisibility(View.GONE);
            durationSp.setVisibility(View.GONE);
            enterTask.setText("");
            enterTaskDuration.setVisibility(View.GONE);
            bottomSheetDialog.show();
            task.shrink();
        }else{
            task.extend();

        }

    }
    @Override
    public void onTimeSet(TimePicker timePicker, int Hour, int Minute) {
        Fragment dateTo =ChildActivity.this.getSupportFragmentManager().findFragmentByTag("tp1");
        if(dateTo!=null) {
            FromTime.setText("Starting time: " + Hour+ ":" + Minute);
            h1=Hour;
            m1=Minute;
            dateTo=null;

        }else{
            h2=Hour;
            m2=Minute;
            ToTime.setText("Ending Time: "+ Hour+ ":" + Minute);
            ShowSetSimpleTaskBtn();
        }

    }
    public void ShowSetSimpleTaskBtn(){
        if(h1+h2+m1+m2>0){
            SetBtn.setVisibility(View.VISIBLE);
            SetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Task temp = new Task(enterTask.getText().toString(),new Interval(new DateTime(MainYear,MainMonth,MainDay,h1,m1),new DateTime(MainYear,MainMonth,MainDay,h2,m2)));
                    if(std.AddTask(temp)){
                        Toast.makeText(ChildActivity.this, "Task uspesno postavljen", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(ChildActivity.this, "Postoji problem sa dodavanjem vaseg taska", Toast.LENGTH_LONG).show();
                    }
                    currentList = std.GetTasksInInterval(new Interval(new DateTime(MainYear,MainMonth,MainDay,0,0),new DateTime(MainYear,MainMonth,MainDay,23,59)));
                    crtaj.drawLists(currentList);
                    crtaj.Refreshuj();
                    adapter.setTaskovi(currentList);
                    save(MainDay,MainMonth,MainYear);


                }
            });

        }
    }
    public void openRoutines(View view) {
        if(routine.isExtended()){
            if(globalTaskPosition==-1){
                routineHolder.setVisibility(View.GONE);
                weekChecboxHolder.setVisibility(View.GONE);
            }
            Routines.show();
            routine.shrink();
        }{
            routine.extend();
        }
    }

    public void applyRoutines(){
        routineSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(globalTaskPosition!=-1){
                    if(position==3){
                        weekChecboxHolder.setVisibility(View.VISIBLE);
                    }else{
                        weekChecboxHolder.setVisibility(View.GONE);
                    }
                }


                ApplyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Task current = currentList.get(globalTaskPosition);


                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ApplyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
        });




    }

    public void expandTaskList(View view) {
        ListaItema.show();
    }

    int priority,time,durationN,duration;
    String taskName;
    public void AiTaskCalculation(){


        durationSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==7){
                    enterTaskDuration.setVisibility(View.VISIBLE);
                }else{
                    enterTaskDuration.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        CaluculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                taskName=enterTask.getText().toString();
                priority=prioritySp.getSelectedItemPosition();
                time = timeSp.getSelectedItemPosition();
                durationN = durationSp.getSelectedItemPosition();
                if(priority==0){
                    Toast.makeText(ChildActivity.this, "Niste izabrali prioritet", Toast.LENGTH_SHORT).show();
                }else if(time==0){
                    Toast.makeText(ChildActivity.this, "Niste izabrali trajanje", Toast.LENGTH_SHORT).show();
                }else if(durationN==0){
                    Toast.makeText(ChildActivity.this, "Niste izabrali prioritet", Toast.LENGTH_SHORT).show();
                }else {
                    if (durationN == 7) {
                        duration = Integer.parseInt(enterTaskDuration.getText().toString());
                    }

                    Interval tempI = std.CalcAiTask(new AiTask(taskName,durationN,priority,std.GetInterval(time,new DateTime(MainYear,MainMonth,MainDay,0,0))));
                    if(!std.isPossible()){
                        Toast.makeText(ChildActivity.this, "Greska", Toast.LENGTH_SHORT).show();
                    }else{
                        AfterCalculateBtn.setVisibility(View.VISIBLE);
                        AiStartTime.setText(tempI.GetStartTime().ToStringTime());
                        AiEndTime.setText(tempI.GetEndTime().ToStringTime());
                        std.AcceptGuess();//TODO napravi toast sa buttonom

                        currentList = std.GetTasksInInterval(new Interval(new DateTime(MainYear,MainMonth,MainDay,0,0),new DateTime(MainYear,MainMonth,MainDay,23,59)));
                        crtaj.drawLists(currentList);
                        crtaj.Refreshuj();
                        adapter.setTaskovi(currentList);
                    }



                }

            }
        });


    }

    public void save(int dan, int mesec, int godina){
        String FILE_NAME=dan+"_"+mesec+"_"+godina+"_"+username;
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(currentList);
        editor.putString(FILE_NAME,json);
        editor.apply();
    }
    public void load(int dan,int mesec,int godina){
        String FILE_NAME=dan+"_"+mesec+"_"+godina+"_"+username;
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(FILE_NAME,null);
        Type type = new TypeToken<List<Task>>() {}.getType();
        currentList = gson.fromJson(json,type);
        if(currentList==null){
            currentList = new ArrayList<>();
        }
        adapter.notifyDataSetChanged();

    }
    public void saveUserName(String user){
        String FILE_NAME="UserName";
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(user);
        editor.putString("username",json);
        editor.apply();
    }
    public String loadUserName(){
        String FILE_NAME="UserName";
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("username",null);
        if(json==null){
            return "";
        }else{
            return json ;
        }

    }


    View prosli;
    @Override
    public void onTaskClick(int position,View itemView) {
        itemView.setBackground(getResources().getDrawable(R.drawable.background_froclicked));
        routineHolder.setVisibility(View.VISIBLE);
        if(prosli!=null){
            prosli.setBackground(getResources().getDrawable(R.drawable.background_forunclicked));
        }
        prosli=itemView;
        globalTaskPosition = position;


    }

    private void openSettings(){

        podeshavanje = findViewById(R.id.podeshavanja);
        podeshavanje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.setContentView(R.layout.settings);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                lang = (ImageButton) dialog.findViewById(R.id.language);
                changeUserBtn =(ImageButton) dialog.findViewById(R.id.changeUser);
                help =(ImageButton) dialog.findViewById(R.id.help);
                changeUser();
                openLanguages();
                openHelp();
                dialog.show();

            }
        });

    }
    private void openLanguages(){
        lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.setContentView(R.layout.languages);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                srb = (ImageButton) dialog.findViewById(R.id.srb);
                eng = (ImageButton) dialog.findViewById(R.id.eng);
                ger = (ImageButton) dialog.findViewById(R.id.ger);
                spa = (ImageButton) dialog.findViewById(R.id.spa);
                fran = (ImageButton) dialog.findViewById(R.id.fran);
                srpski();
                engleski();
                nemacki();
                francuski();
                spanski();
                dialog.show();
            }
        });
    }
    private void openHelp(){
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dialog.setContentView(R.layout.help);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });
    }
    private void changeUser(){
        changeUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(ChildActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });


    }

    private void srpski(){
        srb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("sr");
                recreate();
                dialog.dismiss();
            }
        });
    }
    private void engleski(){
        eng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("en");
                recreate();
                dialog.dismiss();
            }
        });
    }
    private void nemacki(){
        ger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("de");
                recreate();
                dialog.dismiss();
            }
        });
    }
    private void spanski(){
        spa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("es");
                recreate();
                dialog.dismiss();
            }
        });
    }
    private void francuski(){
        fran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLocale("fr");
                recreate();
                dialog.dismiss();
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

        SharedPreferences.Editor editor = getSharedPreferences("Settings", MODE_PRIVATE).edit();
        editor.putString("my lan", lng);
        editor.apply();
    }
    public void loadLocale(){
        SharedPreferences prefs = getSharedPreferences("Settings", Application.MODE_PRIVATE);
        String lang = prefs.getString("my lan","");
        setLocale( lang);
    }


}