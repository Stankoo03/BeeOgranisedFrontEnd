package com.cvetici.beeorganised;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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

public class Kalendar extends AppCompatActivity implements CalendarAdapter.OnItemListener, TimePickerDialog.OnTimeSetListener  {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectDate;
    private ExtendedFloatingActionButton task;
    private Animation fromButton,toButton;
    private int currentDay,currentMonth,currentYear;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private RadioGroup RG;

    private EditText enterTask;
    private Button CalculateBtn,SetBtn;
    private LinearLayout ManualTimeLayout,AiLayout,AfterCalculateBtn;
    private Spinner prioritySp,timeSp,durationSp;

    private Button FromTime,ToTime;
    private int h1=-1,h2=-1,m1=-1,m2=-1;
    private SmartToDo std;

    private List<Task> currentList;


    @RequiresApi(api = Build.VERSION_CODES.O)
    //istrazi sta je ovo Requires API
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalendar);
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


        bottomSheetDialog = new BottomSheetDialog(
                Kalendar.this, R.style.BottomSheetDialogTheme
        );
        bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(
                R.layout.layout_bottom_sheet,(LinearLayout)findViewById(R.id.bottomSheetContainer)
        );
        bottomSheetDialog.setContentView(bottomSheetView);

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
    public void onItemClick(int position, String dayText) {
        if(!dayText.equals(" ")){
            if(task.getVisibility()!=View.VISIBLE) {
                task.setVisibility(View.VISIBLE);
                TaskVisibilityAnimation();
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

        }else{
            if(task.getVisibility()==View.VISIBLE) {
                task.setVisibility(View.INVISIBLE);
                TaskVisibilityAnimation();
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
        if(dateTo!=null) {
            FromTime.setText("Starting time: " + hourOfDay+ ":" + minute);
            h1=hourOfDay;
            m1=minute;
            dateTo=null;

        }else{
            h2=hourOfDay;
            m2=minute;
            ToTime.setText("Ending Time: "+ hourOfDay+ ":" + minute);

        }
        ShowSetSimpleTaskBtn();

    }

    private void ShowSetSimpleTaskBtn() {
        if(h1!=-1 && h2!=-1 && m1!=-1 && m2 !=-1){
            SetBtn.setVisibility(View.VISIBLE);
            SetBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Task temp = new Task(enterTask.getText().toString(),new Interval(new DateTime(currentYear,currentMonth,currentDay,h1,m1),new DateTime(currentYear,currentMonth,currentDay,h2,m2)));
                    std.AddTask(temp);
                    load(currentDay,currentMonth,currentYear);
                    //currentList = std.GetTasksInInterval(new Interval(new DateTime(Year,Month,Danas,0,1),new DateTime(Year,Month,Danas,23,59)));
                    currentList.add(temp);
                    Toast.makeText(Kalendar.this, "Task uspesno zadat za vreme "+currentDay+"."+currentMonth+"."+currentYear, Toast.LENGTH_SHORT).show();
                    //TODO Pogledaj ovo andrijo
                    save(currentDay,currentMonth,currentYear);
                }
            });
        }
    }

    public void save(int dan, int mesec, int godina){
        String FILE_NAME=dan+"_"+mesec+"_"+godina;
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        SharedPreferences.Editor  editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(currentList);
        editor.putString(FILE_NAME,json);
        editor.apply();
    }
    public void load(int dan,int mesec,int godina){
        String FILE_NAME=dan+"_"+mesec+"_"+godina;
        SharedPreferences sharedPreferences = getSharedPreferences(FILE_NAME,MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(FILE_NAME,null);
        Type type = new TypeToken<List<Task>>() {}.getType();
        currentList = gson.fromJson(json,type);
        if(currentList==null){
            currentList = new ArrayList<>();
        }

    }


}