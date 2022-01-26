package com.cvetici.beeorganised;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectDate;
    private ExtendedFloatingActionButton task;
    private Animation fromButton,toButton;
    private int currentDay,currentMonth,currentYear;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private RadioGroup RG;
    private ImageView hexagonIV,prevHexagon;

    private EditText enterTask;
    private Button CalculateBtn,SetBtn;
    private LinearLayout ManualTimeLayout,AiLayout,AfterCalculateBtn;
    private Spinner prioritySp,timeSp,durationSp;

    private Button FromTime,ToTime;
    private int h1=-1,h2=-1,m1=-1,m2=-1;
    private SmartToDo std;
    private BottomSheetDialog ListaItema;
    private View ListView;
    private RecyclerView ListaTaskova;
    private ListaTaskovaAdapter adapter = new ListaTaskovaAdapter(this::onTaskClick);

    private List<Task> currentList, mainList;
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

        std = new SmartToDo(5);
        task.shrink();
        load();
        std.setTasks((ArrayList<Task>) mainList);
        adapter.setTaskovi(new ArrayList<Task>());

        ListaTaskova.setAdapter(adapter);


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
            load();
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
                        CalculateBtn.setVisibility(View.GONE);
                        durationSp.setVisibility(View.GONE);

                        break;
                    case R.id.AiTime:
                        if(ManualTimeLayout.getVisibility()==View.VISIBLE){
                            ManualTimeLayout.setVisibility(View.GONE);
                        }
                        AiLayout.setVisibility(View.VISIBLE);
                        durationSp.setVisibility(View.VISIBLE);
                        CalculateBtn.setVisibility(View.VISIBLE);

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

    private void ShowSetSimpleTaskBtn() {
        if(h1!=-1 && h2!=-1 && m1!=-1 && m2 !=-1){
            SetBtn.setVisibility(View.VISIBLE);
            SetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Task temp = new Task(enterTask.getText().toString(),new Interval(new DateTime(currentYear,currentMonth,currentDay,h1,m1),new DateTime(currentYear,currentMonth,currentDay,h2,m2)));
                    load();
                    std.setTasks((ArrayList<Task>) mainList);
                    if(std.AddTask(temp)){
                        Toast.makeText(Kalendar.this, R.string.settask, Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(Kalendar.this, R.string.setproblem, Toast.LENGTH_LONG).show();
                    }
                    save();
                }
            });
        }
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