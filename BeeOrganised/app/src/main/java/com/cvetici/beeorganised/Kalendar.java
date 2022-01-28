package com.cvetici.beeorganised;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Kalendar extends AppCompatActivity implements CalendarAdapter.OnItemListener, TimePickerDialog.OnTimeSetListener ,ListaTaskovaAdapter.OnTaskListener {

    private TextView monthYearText,AiStartTime,AiEndTime;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectDate;
    private ExtendedFloatingActionButton task;
    private Animation fromButton,toButton;
    private int currentDay,currentMonth,currentYear;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private RadioGroup RG;
    private ImageView hexagonIV,prevHexagon,textApply;

    private EditText enterTask,enterTaskDuration;
    private Button CalculateBtn,SetBtn;
    private LinearLayout ManualTimeLayout,AiLayout,AfterCalculateBtn;
    private Spinner prioritySp,timeSp,durationSp;

    private Button FromTime,ToTime,ConfirmBtn,CaluculateBtn;
    private int h1=-1,h2=-1,m1=-1,m2=-1,priority,durationN,time,duration;
    private SmartToDo std;
    private BottomSheetDialog ListaItema;
    private View ListView;
    private RecyclerView ListaTaskova;
    private ListaTaskovaAdapter adapter = new ListaTaskovaAdapter(this::onTaskClick);
    private String taskName;

    private ArrayList<Task> currentList,mainList;
    private ArrayList<AiTask>AiTaskList;
    private Button TaskButton;

    @RequiresApi(api = Build.VERSION_CODES.O)
    //istrazi sta je ovo Requires API
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalendar);

        bottomSheetDialog = new BottomSheetDialog(
                Kalendar.this, R.style.BottomSheetDialogTheme
        );
        bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.layout_bottom_sheet,(LinearLayout)findViewById(R.id.bottomSheetContainer)
        );
        bottomSheetDialog.setContentView(bottomSheetView);

        ListaItema = new BottomSheetDialog(Kalendar.this,R.style.BottomSheetDialogTheme);
        ListView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.list_layout,(RelativeLayout)findViewById(R.id.ListRelative)
        );
        ListaItema.setContentView(ListView);

        initWigets();
        selectDate = LocalDate.now();
        FindViews();
        setMonthView();
        RadioGroupClicked();
        FromToTimeSetter();
        AiTaskCalculation();

    }
    private void FindViews(){
        task = (ExtendedFloatingActionButton)findViewById(R.id.SimpleButtonKalendar);
        fromButton = AnimationUtils.loadAnimation(this,R.anim.from_bottom_anim);
        toButton = AnimationUtils.loadAnimation(this,R.anim.to_bottom_anim);

        ListaTaskova = ListView.findViewById(R.id.ListaRV);
        ListaTaskova.setLayoutManager(new LinearLayoutManager(Kalendar.this));

        TaskButton = findViewById(R.id.TaskButton);


        RG = bottomSheetView.findViewById(R.id.RadioGroup);
        ManualTimeLayout = bottomSheetView.findViewById(R.id.ManualTimeLayout);
        AiLayout = bottomSheetView.findViewById(R.id.AiTimeLayout);
        FromTime = bottomSheetView.findViewById(R.id.fromTime);
        ToTime = bottomSheetView.findViewById(R.id.toTime);
        AfterCalculateBtn = bottomSheetView.findViewById(R.id.AfterBtnClicked);
        CalculateBtn = bottomSheetView.findViewById(R.id.CalculateBtn);
        SetBtn = (Button) bottomSheetView.findViewById(R.id.SetBtn);
        enterTask = (EditText) bottomSheetView.findViewById(R.id.enterTask);
        prioritySp = bottomSheetView.findViewById(R.id.prioritySpinner);
        timeSp = bottomSheetView.findViewById(R.id.whenSpinner);
        durationSp = bottomSheetView.findViewById(R.id.durationSpinner);
        ConfirmBtn = bottomSheetView.findViewById(R.id.ConfirmBtn);
        CaluculateBtn = bottomSheetView.findViewById(R.id.CalculateBtn);
        enterTaskDuration = bottomSheetView.findViewById(R.id.durationEditText);
        AiStartTime = bottomSheetView.findViewById(R.id.AiStarTime);
        AiEndTime = bottomSheetView.findViewById(R.id.AiEndTime);
        textApply = bottomSheetView.findViewById(R.id.textApply);

        std = new SmartToDo(5);
        task.shrink();
        load();
        std.setTasks((ArrayList<Task>) mainList);
        adapter.setTaskovi(new ArrayList<Task>());
        AiLayout.setVisibility(View.GONE);
        ListaTaskova.setAdapter(adapter);

        textApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(enterTask.getWindowToken(),0);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setMonthView(){
        monthYearText.setText(monthYearFromDate(selectDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectDate);
        CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(),7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = selectDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();

        for(int i=1; i<=42; i++){
                if(i<=dayOfWeek|| i>daysInMonth+dayOfWeek){
                    daysInMonthArray.add(" ");
                }else{
                    daysInMonthArray.add(String.valueOf(i-dayOfWeek));
                }
        }
        return daysInMonthArray;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String monthYearFromDate(LocalDate date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);

    }

    private void initWigets() {
        hexagonIV= findViewById(R.id.hexagon);
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void nextClick(View view) {
        selectDate = selectDate.plusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void previousClick(View view) {
        selectDate = selectDate.minusMonths(1);
        setMonthView();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onItemClick(int position, String dayText,ImageView hexagon) {

        if(!dayText.equals(" ")){
            if(prevHexagon!=null){
                prevHexagon.setImageDrawable(getResources().getDrawable(R.drawable.ic_datehexa));
            }
            hexagon.setImageDrawable(getResources().getDrawable(R.drawable.ic_datehexa_selected));
            prevHexagon=hexagon;
            if(task.getVisibility()!=View.VISIBLE) {
                task.setVisibility(View.VISIBLE);
                TaskButton.setVisibility(View.VISIBLE);
                TaskVisibilityAnimation();
                TaskVisibilityAnimationBottom();
            }
            if(dayText.startsWith("0")){
                currentDay= Integer.parseInt(dayText.substring(1,2));
            }else {
                currentDay = Integer.parseInt(dayText);
            }
            String GodinaMesec = YearMonth.from(selectDate).toString();
            currentYear = Integer.parseInt(GodinaMesec.substring(0,4));
            String Mesec = GodinaMesec.substring(5,7);
            if(Mesec.startsWith("0")){
                currentMonth= Integer.parseInt(Mesec.substring(1,2));
            }else {
                currentMonth = Integer.parseInt(Mesec);
            }
            currentList = std.GetTasksInInterval(new Interval(new DateTime(currentYear,currentMonth,currentDay,0,0),new DateTime(currentYear,currentMonth,currentDay,23,59)));
            adapter.setTaskovi(currentList);

        }else{
            if(task.getVisibility()==View.VISIBLE) {

                TaskButton.setVisibility(View.INVISIBLE);
                task.setVisibility(View.INVISIBLE);
                TaskVisibilityAnimation();
                TaskVisibilityAnimationBottom();
            }

        }

    }
    private void TaskVisibilityAnimation(){
        if(task.getVisibility()==View.INVISIBLE){
            task.startAnimation(toButton);
        }else{
            task.startAnimation(fromButton);
        }


    }
    private void TaskVisibilityAnimationBottom(){
        if(TaskButton.getVisibility()==View.INVISIBLE){
            TaskButton.startAnimation(toButton);
        }else{
            TaskButton.startAnimation(fromButton);
        }


    }

    public void dodajTaskNaKalendar(View view) {
        if(task.isExtended()){
            bottomSheetDialog.show();
            task.shrink();
        }else{
            task.extend();
        }


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
                        ConfirmBtn.setVisibility(View.GONE);
                        SetBtn.setVisibility(View.GONE);
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


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Fragment dateTo =Kalendar.this.getSupportFragmentManager().findFragmentByTag("tp1");
        String hourString,minuteString;
        if(hourOfDay<10){
            hourString="0"+hourOfDay;
        }else{
            hourString=hourOfDay+"";
        }
        if(minute<10){
            minuteString="0"+minute;
        }else{
            minuteString=""+minute;
        }
        if(dateTo!=null) {

            FromTime.setText("Starting time: " +hourString+ ":" + minuteString);
            h1=hourOfDay;
            m1=minute;
            dateTo=null;

        }else{
            h2=hourOfDay;
            m2=minute;
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
                    String taskName = enterTask.getText().toString();
                    if(taskName.isEmpty()){
                        taskName = getResources().getString(R.string.NewTask);
                    }
                    Task temp = new Task(taskName,new Interval(new DateTime(currentYear,currentMonth,currentDay,h1,m1),new DateTime(currentYear,currentMonth,currentDay,h2,m2)));
                    if(std.AddTask(temp)){
                        Toast.makeText(Kalendar.this, R.string.settask, Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(Kalendar.this, R.string.setproblem, Toast.LENGTH_LONG).show();
                    }
                    currentList = std.GetTasksInInterval(new Interval(new DateTime(currentYear,currentMonth,currentDay,h1,m1),new DateTime(currentYear,currentMonth,currentDay,h2,m2)));
                    adapter.setTaskovi(currentList);


                }
            });

        }
    }
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
                    Toast.makeText(Kalendar.this, R.string.priorityprob, Toast.LENGTH_SHORT).show();
                }else if(time==0){
                    Toast.makeText(Kalendar.this, R.string.timeprob, Toast.LENGTH_SHORT).show();
                }else if(durationN==0){
                    Toast.makeText(Kalendar.this, R.string.durationprob, Toast.LENGTH_SHORT).show();
                }else {
                    if (durationN == 7) {
                        duration = Integer.parseInt(enterTaskDuration.getText().toString());
                    }
                    if(taskName.isEmpty()){
                        taskName = getResources().getString(R.string.NewTask);
                    }
                    Interval tempI = std.CalcAiTask(new AiTask(taskName,durationN,priority,std.GetInterval(time,new DateTime(currentYear,currentMonth,currentDay,0,0))));
                    if(!std.isPossible()){
                        Toast.makeText(Kalendar.this, R.string.error, Toast.LENGTH_SHORT).show();
                    }else{
                        AfterCalculateBtn.setVisibility(View.VISIBLE);
                        ConfirmBtn.setVisibility(View.VISIBLE);
                        AiStartTime.setText(tempI.GetStartTime().ToStringTime());
                        AiEndTime.setText(tempI.GetEndTime().ToStringTime());
                        ConfirmBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                std.AcceptGuess();
                                Toast.makeText(Kalendar.this, R.string.settask, Toast.LENGTH_SHORT).show();
                                currentList = std.GetTasksInInterval(new Interval(new DateTime(currentYear,currentMonth,currentDay,0,0),new DateTime(currentYear,currentMonth,currentDay,23,59)));
                                adapter.setTaskovi(currentList);


                            }
                        });



                    }



                }

            }
        });


    }



    @Override
    protected void onPause() {
        super.onPause();
        Log.d("Stop","Saved in calendar");
        save();
        finish();
    }

    public void save(){
        String FILE_NAME="taskLists";
        String AI_TASKS = "aiTaskList";
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        Gson gson = new Gson();
        AiTaskList = new ArrayList<AiTask>();
        ArrayList<Task> t = std.getTasks();
        for(int i=0; i<t.size(); i++){
            if(t.get(i).getClass()==AiTask.class){
                AiTaskList.add((AiTask) t.get(i));
            }
        }
        t.removeAll(AiTaskList);
        String json = gson.toJson(t);
        String Aijson = gson.toJson(AiTaskList);
        editor.putString(FILE_NAME,json);
        editor.putString(AI_TASKS,Aijson);
        editor.apply();

    }

    public void load(){
        String FILE_NAME="taskLists";
        String AI_TASKS = "aiTaskList";
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(FILE_NAME,null);
        String Aijson = sharedPreferences.getString(AI_TASKS,null);
        Type type = new TypeToken<List<Task>>() {}.getType();
        Type AiType = new TypeToken<ArrayList<AiTask>>() {}.getType();
        mainList = gson.fromJson(json,type);
        if(mainList==null){
            mainList = new ArrayList<>();
        }
        AiTaskList = gson.fromJson(Aijson,AiType);
        if(AiTaskList==null){
            AiTaskList = new ArrayList<>();
        }
        mainList.addAll(AiTaskList);
        adapter.notifyDataSetChanged();

    }

    public void expandTaskList(View view) {
        currentList = std.GetTasksInInterval(new Interval(new DateTime(currentYear,currentMonth,currentDay,0,0),new DateTime(currentYear,currentMonth,currentDay,23,59)));
        adapter.setTaskovi(currentList);
        ListaItema.show();
    }


    @Override
    public void onTaskClick(int position, View itemView) {

    }

}