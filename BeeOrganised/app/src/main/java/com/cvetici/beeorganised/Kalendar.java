package com.cvetici.beeorganised;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class Kalendar extends AppCompatActivity implements CalendarAdapter.OnItemListener  {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectDate;
    private ExtendedFloatingActionButton task;
    private Animation fromButton,toButton;
    private int currentDay,currentMonth,currentYear;
    private BottomSheetDialog bottomSheetDialog;
    private View bottomSheetView;
    private RadioGroup RG;


    @RequiresApi(api = Build.VERSION_CODES.O)
    //istrazi sta je ovo Requires API
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kalendar);
        initWigets();
        selectDate = LocalDate.now();
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
        task.shrink();
        setMonthView();

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
            task.setVisibility(View.VISIBLE);
            task.startAnimation(fromButton);
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
            Toast.makeText(this, currentDay+" "+currentMonth+" "+currentYear, Toast.LENGTH_SHORT).show();
        }else{
            task.setVisibility(View.INVISIBLE);
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
/*
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
*/
    }



}