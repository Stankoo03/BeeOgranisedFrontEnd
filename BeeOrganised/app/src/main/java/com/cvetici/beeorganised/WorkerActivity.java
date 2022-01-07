package com.cvetici.beeorganised;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.IntArrayEvaluator;
import android.app.ActivityOptions;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;

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
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;



import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class WorkerActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private Calendar calendar;
    private TextView d1,d2,d3;
    private TextView w1,w2,w3;

    private boolean clicked=false;
    private FloatingActionButton main,routine,task;

    private Animation rotateOpen,rotateClose,fromButton,toButton ;
    private Animation openListView,closeListView;

    private LinearLayout LinearViewHolder,ManualTimeLayout,AiLayout,AfterCalculateBtn;
    private boolean AlreadyClicked = false;

    private RadioGroup RG;
    private View bottomSheetView;
    private BottomSheetDialog bottomSheetDialog;

    private Button FromTime,ToTime,CaluculateBtn,SetBtn,TaskButton;
    private String Vreme="";
    private int Danas=0,Sutra,PSutra,Month,Month1,Month2,Year,Year1,Year2;

    private Switch daynightSwitch;
    private ImageView sat;

    private EditText enterTask;
    private SmartToDo std;
    private int h1=-1,m1=-1,h2=-1,m2=-1;
    private RelativeLayout TaskLayout;
    private RecyclerView ListaTaskova;
    private ArrayList<Task> taskovi;
    private ListaTaskovaAdapter adapter = new ListaTaskovaAdapter();
    private Spinner prioritySp,timeSp,durationSp;
    private ImageButton datumPrvi,datumDrugi,datumTreci;
    private SimpleDateFormat dateFormat;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_worker);
        calendar = Calendar.getInstance();
        bottomSheetDialog = new BottomSheetDialog(
                WorkerActivity.this, R.style.BottomSheetDialogTheme
        );
        bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.layout_bottom_sheet,(LinearLayout)findViewById(R.id.bottomSheetContainer)
        );
        bottomSheetDialog.setContentView(bottomSheetView);

        FindViews();
        RadioGroupClicked();
        FromToTimeSetter();
        SwitchListener();
        PostaviDatume();
        AiTaskCalculation();
        CalendarButtonClick();

    }
    private void SwitchListener(){

        daynightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    sat.setImageResource(R.drawable.ic_amclock);
                }else{
                    sat.setImageResource(R.drawable.ic_pmclock);
                }
            }
        });



    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void CalendarButtonClick(){

        datumPrvi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        datumDrugi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        datumTreci.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

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

        LinearViewHolder =  (LinearLayout) findViewById(R.id.taskLinearHolderId);

        ListaTaskova = (RecyclerView) findViewById(R.id.ListaRV);
        rotateOpen = AnimationUtils.loadAnimation(this,R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this,R.anim.rotate_close_anim);
        fromButton = AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim);
        toButton = AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim);

         openListView = AnimationUtils.loadAnimation(this, R.anim.open_list_animation);
        closeListView = AnimationUtils.loadAnimation(this, R.anim.close_list_animation);

        main = (FloatingActionButton) findViewById(R.id.MainButton);
        routine = (FloatingActionButton) findViewById(R.id.RoutineButton);
        task = (FloatingActionButton) findViewById(R.id.SimpleButton);

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


        TaskLayout = findViewById(R.id.ListRelative);

        taskovi = new ArrayList<>();


        adapter.setTaskovi(taskovi);
        ListaTaskova.setAdapter(adapter);
        ListaTaskova.setLayoutManager(new LinearLayoutManager(WorkerActivity.this));

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
        SimpleDateFormat mesec = new SimpleDateFormat("mm");
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
        Year = Integer.parseInt(Godina);

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
            Sutra = Integer.parseInt(danas);
        }
        Month1=Integer.parseInt(Mesec1);
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
        Month2=Integer.parseInt(Mesec2);
        Year2 = Integer.parseInt(Godina2);

        String PSutraNedelja = DateFormat.getDateInstance(DateFormat.FULL).format(currentTime);

        d3.setText(psutra);
        w3.setText(PSutraNedelja.substring(0,3).toUpperCase(Locale.ROOT));

    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    public void otvoriKalendar(View view) {
        Intent intent = new Intent(WorkerActivity.this , Kalendar.class);
        ImageButton button = (ImageButton) findViewById(R.id.datumCetvrti);
        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(this,button,"transition_calendar");
        startActivity(intent,options.toBundle());

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
        if(!clicked){
            task.setVisibility(View.VISIBLE) ;
            routine.setVisibility(View.VISIBLE);
        }else{
            task.setVisibility(View.INVISIBLE) ;
            routine.setVisibility(View.INVISIBLE);
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
            RG.clearCheck();
            SetBtn.setVisibility(View.GONE);
            ManualTimeLayout.setVisibility(View.GONE);
            AiLayout.setVisibility(View.GONE);
            AfterCalculateBtn.setVisibility(View.GONE);
            CaluculateBtn.setVisibility(View.GONE);
            durationSp.setVisibility(View.GONE);
            enterTask.setText("");
            bottomSheetDialog.show();

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int Hour, int Minute) {
        Fragment dateTo =WorkerActivity.this.getSupportFragmentManager().findFragmentByTag("tp1");
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
                    Task temp = new Task(enterTask.getText().toString(),new Interval(new DateTime(Year,Month,Danas,h1,m1),new DateTime(Year,Month,Danas,h2,m2)));
                    std.AddTask(temp);
                    taskovi.add(temp);
                    adapter.notifyDataSetChanged();
                    Toast.makeText(WorkerActivity.this, "Task uspesno postavljen u odredjenom vremenskom intervalu", Toast.LENGTH_LONG).show();
                }
            });

        }
    }
    public void expandTaskList(View view) {
        if(AlreadyClicked){
            TaskLayout.startAnimation(closeListView);
            TaskLayout.setVisibility(View.INVISIBLE);
            TaskButton.setBackground(getResources().getDrawable(R.drawable.background_for_exbtn));
            AlreadyClicked=false;
        }else {
            TaskLayout.startAnimation(openListView);
            TaskLayout.setVisibility(View.VISIBLE);
            AlreadyClicked=true;
            TaskButton.setBackground(getResources().getDrawable(R.drawable.background_for_exbtnuser));
        }
    }

    public void AiTaskCalculation(){

        CaluculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int priority=prioritySp.getSelectedItemPosition();
                int time = timeSp.getSelectedItemPosition();
                int duration = durationSp.getSelectedItemPosition();

                Toast.makeText(WorkerActivity.this, priority+" "+time+" "+duration, Toast.LENGTH_LONG).show();

                AfterCalculateBtn.setVisibility(View.VISIBLE);



            }
        });


    }







}