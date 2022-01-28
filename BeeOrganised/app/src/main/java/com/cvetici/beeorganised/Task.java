package com.cvetici.beeorganised;

import java.util.ArrayList;
import java.util.HashMap;

public class Task
{
    public String title;
    public Interval time;
    public boolean done = false;
    public Routine routine = null;

    public String GetTitle() {
        return title;
    }
    public Interval GetTime() {
        return time;
    }
    public boolean GetDone() { return done; }
    public Routine GetRoutine() {
        return routine;
    }

    public void SetNewTime(Interval time) { this.time = time; }
    public void SetTitle(String title) { this.title = title; }
    public void SetRoutine(Routine newRoutine){
        this.routine = newRoutine;
    }

    public void CheckDone(){done = !done;}


    public Interval Intersect(Task other){
        return time.Intersect(other.time);
    }


    public ArrayList<Interval> UsedTime(Interval period){
        if(routine != null){
            return routine.UsedTime(period,this);
        }
        else {
            Interval i = time.Intersect(period);
            ArrayList<Interval> R = new ArrayList<>();
            if (i != null) {
                i.SetRefferedTask(this);
                R.add(i);
            }
            return R;
        }
    }

    public Interval UsedTimeF(Interval period){
        ArrayList<Interval> r = UsedTime(period);
        if(r.size()>0)
            return r.get(0);
        else{
            System.out.println("ERROR, NO USED TIME IN INTERVAL: " + period.ToString() + ". \nTASK: " + ToString());
            return null;
        }
    }

    public int GetPriority(){
        return 1;
    }

    public int ComparePriority(Task other){
        return GetPriority() - other.GetPriority();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Routine getRoutine() {
        return routine;
    }

    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public Task(){
        this.title = "New Task";
        this.time = new Interval();
    }

    public Task(String title, Interval time){
        this.title = title;
        this.time = time;
    }

    public Interval getTime() {
        return time;
    }

    public void setTime(Interval time) {
        this.time = time;
    }

    public String ToString()
    {
        return title + " P" + GetPriority()  + (routine!=null?" *R*":"") +  " " + time.ToString();
    }

    public String ToStringTime()
    {
        return time.ToStringTime();
    }
}

class AiTask extends Task
{
    protected int priority; //1-4
    protected Interval prefferedTime;

    @Override
    public int GetPriority(){
        return priority;
    }
    public Interval GetPrefferedInterval() { return prefferedTime; }

    @Override
    public void SetNewTime(Interval time){
        if(routine!=null){
            routine.SetSpecificDate(time.GetStartTime(), time);
        }
        else{
            this.time = time;
        }
    }

    public void SetPriority(int p){
        if(p<1) priority = 1;
        else if(p>4) priority = 4;
        else priority = p;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public Interval getPrefferedTime() {
        return prefferedTime;
    }

    public void setPrefferedTime(Interval prefferedTime) {
        this.prefferedTime = prefferedTime;
    }

    public AiTask(String title, Interval time, int priority, Interval prefferedTime){
        super(title,time);
        SetPriority(priority);
        this.prefferedTime = prefferedTime;
    }

    public AiTask(Task from, Interval prefferedTime){
        title = from.GetTitle();
        time = from.GetTime();
        this.prefferedTime = prefferedTime;
    }

    public AiTask(AiTask t){
        this.title = t.GetTitle();
        this.time = t.GetTime();
        this.prefferedTime = new Interval(t.prefferedTime);
        this.priority = t.GetPriority();
        this.done = t.GetDone();
    }

    private TimeSpan intToDur(int i){
        int min = 0;
        switch (i){
            case 1:
                min = 5;
                break;
            case 2:
                min = 15;
                break;
            case 3:
                min = 30;
                break;
            case 4:
                min = 60;
                break;
            case 5:
                min = 90;
                break;
            case 6:
                min = 120;
                break;
            default:
                min = i;
        }
        return new TimeSpan(min);
    }


    public AiTask(String title, int duration, int priority, Interval prefferedTime){
        super(title, new Interval(new DateTime(0,0), new TimeSpan(0)));
        time.SetDuration(intToDur(duration));

        this.SetPriority(priority);
        this.prefferedTime = prefferedTime;
    }
}

class Routine{
    protected boolean[] repeatDays;

    protected HashMap<String,Interval> changedDays = new HashMap<>();

    public Routine(boolean[] repeatDays){
        if(repeatDays.length == 7)
            this.repeatDays = repeatDays;
        else{
            System.out.println("NEDELJA NEMA SEDAM DANA");
        }
    }

    public boolean[] getRepeatDays() {
        return repeatDays;
    }

    public void setRepeatDays(boolean[] repeatDays) {
        this.repeatDays = repeatDays;
    }

    public HashMap<String, Interval> getChangedDays() {
        return changedDays;
    }

    public void setChangedDays(HashMap<String, Interval> changedDays) {
        this.changedDays = changedDays;
    }

    public Routine(int i){
        switch (i){
            case 1:
                repeatDays = new boolean[]{true,true,true,true,true,false,false};
                break;
            case 2:
                repeatDays = new boolean[]{false,false,false,false,false,true,true};
                break;
            case 3:
                repeatDays = new boolean[]{true,true,true,true,true,true,true};
                break;
            default:
                repeatDays = new boolean[]{false,false,false,false,false,false,false};
        }
    }

    public boolean SetRepeatDays(boolean[] repeatDays){
        if(repeatDays.length == 7) {
            this.repeatDays = repeatDays;
            return true;
        }
        else{
            System.out.println("NEDELJA NEMA SEDAM DANA");
            return false;
        }
    }

    public void SetSpecificDate(DateTime date, Interval interval){
        if(date.SameDate(interval.GetStartTime())){
            changedDays.put(date.ToStringDate(), interval);
        }
        else{
        }
    }

    protected boolean isDateUsed(DateTime date) { return repeatDays[date.GetDayInWeek()]; }

    public ArrayList<Interval> UsedTime(Interval period, Task task){
        Interval time = task.GetTime();
        ArrayList<Interval> r = new ArrayList<>();
        Interval potential = new Interval(new DateTime(period.GetStartTime(),time.GetStartTime()), time.GetDuration());

        while(potential.GetEndTime().Before(period.GetEndTime().AddDur(new TimeSpan(1,0,0)))){

            if( isDateUsed(potential.GetStartTime()) ) {
                Interval i = period.Intersect(potential);
                String key = potential.GetStartTime().ToStringDate();
                if(changedDays.containsKey(key)){
                    Interval specific = changedDays.get(key);
                    i = period.Intersect(specific);
                }
                if (i != null) {
                    i.SetRefferedTask(task);
                    r.add(i);
                }
            }
            potential.MoveByOffset(new TimeSpan(1,0,0));
        }

        return r;
    }

}