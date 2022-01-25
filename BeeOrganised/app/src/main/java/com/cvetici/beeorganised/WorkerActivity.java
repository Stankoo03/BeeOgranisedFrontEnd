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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
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


public class WorkerActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener,ListaTaskovaAdapter.OnTaskListener{

    private ImageView background;
    private Calendar calendar,notifyCalendar;
    private TextView d1,d2,d3;
    private TextView w1,w2,w3;
    private TextView AiStartTime,AiEndTime;

    private CheckBox Mon,Tue,Wed,Thu,Fri,Sat,Sun;

    int MainDay,MainMonth,MainYear;

    private boolean clicked;
    private FloatingActionButton main;
    private ExtendedFloatingActionButton task,routine;

    private Animation rotateOpen,rotateClose,fromButton,toButton,slowlyCLose,slowlyOpen;

    private LinearLayout ManualTimeLayout,AiLayout,AfterCalculateBtn,weekChecboxHolder;

    private RadioGroup RG;
    private View bottomSheetView,ListView,RoutinesView;
    private BottomSheetDialog bottomSheetDialog;
    private BottomSheetDialog ListaItema,Routines;

    private Button FromTime,ToTime,CaluculateBtn,SetBtn,ConfirmBtn,ApplyBtn;
    private int Danas=0,Sutra,PSutra,Month,Month1,Month2,Year,Year1,Year2,globalTaskPosition;

    private NotificationHelper mHelper;

    private Switch daynightSwitch;
    private ImageView sat;

    private EditText enterTask,enterTaskDuration;
    private SmartToDo std;
    private int h1=-1,m1=-1,h2=-1,m2=-1;
    private RecyclerView ListaTaskova,ListaRutina;
    private ListaTaskovaAdapter adapter = new ListaTaskovaAdapter(this::onTaskClick);
    private Spinner prioritySp,timeSp,durationSp,routineSp;
    private ImageButton datumPrvi,datumDrugi,datumTreci, podeshavanje, lang, srb, eng, ger, spa, fran, help;
    private ImageButton changeUserBtn,textApply;
    public  List<Task> currentList,mainList;
    public boolean dan,backgroundClicked;
    Dialog dialog;
    private RelativeLayout routineHolder,changeTaskHolder;

    private CrtajObaveze crtaj;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadLocale();
        setContentView(R.layout.activity_worker);
        calendar = Calendar.getInstance();
        bottomSheetDialog = new BottomSheetDialog(
                WorkerActivity.this, R.style.BottomSheetDialogTheme
        );
        bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.layout_bottom_sheet,(LinearLayout)findViewById(R.id.bottomSheetContainer)
        );
        bottomSheetDialog.setContentView(bottomSheetView);

        ListaItema = new BottomSheetDialog(WorkerActivity.this,R.style.BottomSheetDialogTheme);
        ListView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.list_layout,(RelativeLayout)findViewById(R.id.ListRelative)
        );
        ListaItema.setContentView(ListView);

        Routines = new BottomSheetDialog(WorkerActivity.this,R.style.BottomSheetDialogTheme);
        RoutinesView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.layout_rutine,(RelativeLayout)findViewById(R.id.ListRelative)
        );
        Routines.setContentView(RoutinesView);

        dialog = new Dialog(WorkerActivity.this);



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
        clicked=false;
        backgroundClicked =false;

        daynightSwitch = (Switch) findViewById(R.id.daynightSwitch);
        d1 = (TextView) findViewById(R.id.firstDate);
        d2 = (TextView) findViewById(R.id.secondDate);
        d3 = (TextView) findViewById(R.id.thirdDate);
        w1 = (TextView) findViewById(R.id.firstWeek);
        w2 = (TextView) findViewById(R.id.secondWeek);
        w3 = (TextView) findViewById(R.id.thirdWeek);
        changeTaskHolder = findViewById(R.id.changeTask);
        background = findViewById(R.id.backgroundd);

        datumPrvi = (ImageButton)findViewById(R.id.datumPrvi);
        datumDrugi = (ImageButton)findViewById(R.id.datumDrugi);
        datumTreci = (ImageButton)findViewById(R.id.datumTreci);


        ListaTaskova = ListView.findViewById(R.id.ListaRV);
        ListaRutina =  RoutinesView.findViewById(R.id.ListaRV);
        routineSp = RoutinesView.findViewById(R.id.routineSp);
        weekChecboxHolder = RoutinesView.findViewById(R.id.weekDayHolder);
        routineHolder = RoutinesView.findViewById(R.id.repeatholder);
        ApplyBtn = Routines.findViewById(R.id.applyRoutine);
        Mon = Routines.findViewById(R.id.mon);
        Tue = Routines.findViewById(R.id.tue);
        Wed = Routines.findViewById(R.id.wed);
        Thu = Routines.findViewById(R.id.thu);
        Fri = Routines.findViewById(R.id.fri);
        Sat = Routines.findViewById(R.id.sat);
        Sun = Routines.findViewById(R.id.sun);

        rotateOpen = AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim);
        fromButton = AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim);
        toButton = AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim);
        slowlyCLose = AnimationUtils.loadAnimation(this,R.anim.polako_zatvori);
        slowlyOpen = AnimationUtils.loadAnimation(this,R.anim.polako_pojavi);

        changeTaskHolder =findViewById(R.id.changeTask);

        main = (FloatingActionButton) findViewById(R.id.MainButton);
        routine = (ExtendedFloatingActionButton) findViewById(R.id.RoutineButton);
        task = (ExtendedFloatingActionButton) findViewById(R.id.SimpleButton);


        sat = (ImageView) findViewById(R.id.sat);

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
        ConfirmBtn = bottomSheetView.findViewById(R.id.ConfirmBtn);
        textApply = bottomSheetView.findViewById(R.id.textApply);



        globalTaskPosition=-1;

        mHelper = new NotificationHelper(this);

        crtaj = new CrtajObaveze(getApplicationContext());
        crtaj = findViewById(R.id.crtajObaveze);

        textApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(enterTask.getWindowToken(),0);
            }
        });



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
                MainDay = Danas;
                MainMonth = Month;
                MainYear = Year;
                datumPrvi.setBackground(getResources().getDrawable(R.drawable.ic_datum_selected));
                datumDrugi.setBackground(getResources().getDrawable(R.drawable.ic_datum_fixed_fixed));
                datumTreci.setBackground(getResources().getDrawable(R.drawable.ic_datum_fixed_fixed));
                currentList = std.GetTasksInInterval(new Interval(new DateTime(MainYear,MainMonth,MainDay,0,0),new DateTime(MainYear,MainMonth,MainDay,23,59)));
                adapter.setTaskovi(currentList);
                crtaj.drawLists(currentList);
                crtaj.Refreshuj();
            }
        });

        datumDrugi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainDay = Sutra;
                MainMonth = Month1;
                MainYear = Year1;
                datumDrugi.setBackground(getResources().getDrawable(R.drawable.ic_datum_selected));
                datumPrvi.setBackground(getResources().getDrawable(R.drawable.ic_datum_fixed_fixed));
                datumTreci.setBackground(getResources().getDrawable(R.drawable.ic_datum_fixed_fixed));
                currentList = std.GetTasksInInterval(new Interval(new DateTime(MainYear,MainMonth,MainDay,0,0),new DateTime(MainYear,MainMonth,MainDay,23,59)));
                adapter.setTaskovi(currentList);
                crtaj.drawLists(currentList);
                crtaj.Refreshuj();
            }
        });
        datumTreci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainDay = PSutra;
                MainMonth = Month2;
                MainYear = Year2;
                datumTreci.setBackground(getResources().getDrawable(R.drawable.ic_datum_selected));
                datumDrugi.setBackground(getResources().getDrawable(R.drawable.ic_datum_fixed_fixed));
                datumPrvi.setBackground(getResources().getDrawable(R.drawable.ic_datum_fixed_fixed));
                currentList = std.GetTasksInInterval(new Interval(new DateTime(MainYear,MainMonth,MainDay,0,0),new DateTime(MainYear,MainMonth,MainDay,23,59)));
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
        load();
        std.setTasks((ArrayList<Task>) mainList);
        currentList = std.GetTasksInInterval(new Interval(new DateTime(Year,Month,Danas,0,0),new DateTime(Year,Month,Danas,23,59)));
        adapter.setTaskovi(currentList);
        ListaTaskova.setAdapter(adapter);
        ListaRutina.setAdapter(adapter);
        ListaRutina.setLayoutManager(new LinearLayoutManager(WorkerActivity.this));
        ListaTaskova.setLayoutManager(new LinearLayoutManager(WorkerActivity.this));
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
        Intent intent = new Intent(WorkerActivity.this , Kalendar.class);
        ImageButton button = (ImageButton) findViewById(R.id.datumCetvrti);
        startActivity(intent);

    }

    public void expandButtons(View view) {
        setClickable(clicked);
        setAnimation(clicked);
        setVisibility(clicked);
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
            task.setVisibility(View.GONE) ;
            routine.setVisibility(View.GONE);
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
    private void showBottomCard(){
        RG.clearCheck();
        SetBtn.setVisibility(View.GONE);
        ManualTimeLayout.setVisibility(View.GONE);
        AiLayout.setVisibility(View.GONE);
        AfterCalculateBtn.setVisibility(View.GONE);
        CaluculateBtn.setVisibility(View.GONE);
        durationSp.setVisibility(View.GONE);
        enterTask.setText("");
        enterTaskDuration.setVisibility(View.GONE);
        ConfirmBtn.setVisibility(View.GONE);
        bottomSheetDialog.show();

    }
    public void simpleTaskCard(View view) {
        if(task.isExtended()){
            showBottomCard();
            task.shrink();
        }else{
            task.extend();

        }

    }
    @Override
    public void onTimeSet(TimePicker timePicker, int Hour, int Minute) {
        Fragment dateTo =WorkerActivity.this.getSupportFragmentManager().findFragmentByTag("tp1");
        String hourString,minuteString;
        if(Hour<10){
            hourString="0"+Hour;
        }else{
            hourString=Hour+"";
        }
        if(Minute<10){
            minuteString="0"+Minute;
        }else{
            minuteString=""+Minute;
        }
        if(dateTo!=null) {

            FromTime.setText("Starting time: " +hourString+ ":" + minuteString);
            h1=Hour;
            m1=Minute;
            dateTo=null;

        }else{
            h2=Hour;
            m2=Minute;
            ToTime.setText("Starting time: " +hourString+ ":" + minuteString);
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
                        Toast.makeText(WorkerActivity.this, R.string.settask, Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(WorkerActivity.this, R.string.setproblem, Toast.LENGTH_LONG).show();
                    }
                    currentList = std.GetTasksInInterval(new Interval(new DateTime(MainYear,MainMonth,MainDay,0,0),new DateTime(MainYear,MainMonth,MainDay,23,59)));
                    crtaj.drawLists(currentList);
                    crtaj.Refreshuj();
                    adapter.setTaskovi(currentList);
                    save();


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
    int tempPosition;
    public void applyRoutines(){
        routineSp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                tempPosition=position;
                tempPosition++;
                if(globalTaskPosition!=-1){
                    if(tempPosition==4){
                        weekChecboxHolder.setVisibility(View.VISIBLE);
                    }else{
                        weekChecboxHolder.setVisibility(View.GONE);
                    }
                    ApplyBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Task current = currentList.get(globalTaskPosition);
                            current.SetRoutine(new Routine(new boolean[]{Mon.isChecked(),Tue.isChecked(),Wed.isChecked(),Thu.isChecked(),Fri.isChecked(),Sat.isChecked(),Sun.isChecked()}));
                            save();
                        }
                    });
                }


                ApplyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Task current = currentList.get(globalTaskPosition);
                        current.SetRoutine(new Routine(tempPosition));
                        save();

                    }
                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ApplyBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Task current = currentList.get(globalTaskPosition);
                        current.SetRoutine(new Routine(1));
                        save();
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
                //TODO Teodora
                taskName=enterTask.getText().toString();
                priority=prioritySp.getSelectedItemPosition();
                time = timeSp.getSelectedItemPosition();
                durationN = durationSp.getSelectedItemPosition();
                if(priority==0){
                    Toast.makeText(WorkerActivity.this, R.string.priorityprob, Toast.LENGTH_SHORT).show();
                }else if(time==0){
                    Toast.makeText(WorkerActivity.this, R.string.timeprob, Toast.LENGTH_SHORT).show();
                }else if(durationN==0){
                    Toast.makeText(WorkerActivity.this, R.string.durationprob, Toast.LENGTH_SHORT).show();
                }else {
                    if (durationN == 7) {
                        duration = Integer.parseInt(enterTaskDuration.getText().toString());
                    }

                    Interval tempI = std.CalcAiTask(new AiTask(taskName,durationN,priority,std.GetInterval(time,new DateTime(MainYear,MainMonth,MainDay,0,0))));
                    if(!std.isPossible()){
                        Toast.makeText(WorkerActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
                    }else{
                        AfterCalculateBtn.setVisibility(View.VISIBLE);
                        ConfirmBtn.setVisibility(View.VISIBLE);
                        AiStartTime.setText(tempI.GetStartTime().ToStringTime());
                        AiEndTime.setText(tempI.GetEndTime().ToStringTime());
                        ConfirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                std.AcceptGuess();
                                Toast.makeText(WorkerActivity.this, R.string.settask, Toast.LENGTH_SHORT).show();
                                currentList = std.GetTasksInInterval(new Interval(new DateTime(MainYear,MainMonth,MainDay,0,0),new DateTime(MainYear,MainMonth,MainDay,23,59)));
                                crtaj.drawLists(currentList);
                                crtaj.Refreshuj();
                                adapter.setTaskovi(currentList);
                                save();
                            }
                        });



                    }



                }

            }
        });


    }

    public void save(){
        String FILE_NAME="taskLists";
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(std.getTasks());
        editor.putString(FILE_NAME,json);
        editor.apply();
    }
    public void load(){
        String FILE_NAME="taskLists";
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(FILE_NAME,null);
        Type type = new TypeToken<List<Task>>() {}.getType();
        mainList = gson.fromJson(json,type);
        if(mainList==null){
            mainList = new ArrayList<>();
        }
        adapter.notifyDataSetChanged();

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
                Intent intent = new Intent(WorkerActivity.this, MainActivity.class);
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