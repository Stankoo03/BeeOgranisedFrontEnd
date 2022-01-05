package com.cvetici.beeorganised;

public class TimeSpan {


    private int minutes; //Core data of class TimeSpan

    final int hoursInDays = 24;
    final int minutesInHour = 60;
    final int minutesInDay = minutesInHour*hoursInDays;

    public int GetLongMinutes() { return minutes; }
    public int GetDay() { return minutes/minutesInDay; }
    public int GetHour(){ return (minutes%minutesInDay)/minutesInHour; }
    public int GetMinute(){ return minutes%minutesInHour; }

    public TimeSpan(int days, int hours, int mins){
        int min = 0;
        min += days*minutesInDay;
        min += hours*minutesInHour;
        min += mins;
        minutes = min;
    }

    public TimeSpan(int longMinutes){
        minutes = longMinutes;
    }

    public TimeSpan Multiply(float mul){
        return new TimeSpan( Math.round(minutes*mul) );
    }

    public boolean GreaterThan(TimeSpan other){
        return minutes > other.minutes;
    }

    public String ToString(){
        return GetDay() + "days " + GetHour() + "hours " + GetMinute() + "minutes ";
    }
}
