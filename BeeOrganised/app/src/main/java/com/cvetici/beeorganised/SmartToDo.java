package com.cvetici.beeorganised;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

public class SmartToDo{
    private ArrayList<Task> tasks;

    public Interval Morning;   //Default: Morning(7-12)
    public Interval Afternoon; //Default: Afternoon(12-17)
    public Interval Evening;   //Default: Evening(17-22)
    public Interval LateNight; //Default: LateNight(22-3)

    private float allowedOffset = 5f;

    private ArrayList<Change> changes;
    private AiTask pick;

    private float GetMulOffset(boolean plusSign){
        if (plusSign) return (1f+allowedOffset/100f);
        else return (1f-allowedOffset/100f);
    }

    //private List<Interval> UsedTime;

    public SmartToDo(float allowedOffset){
        tasks = new ArrayList<Task>();

        Morning   = new Interval(new DateTime(1,1,1, 7,0), new DateTime(1,1,1,12,0));
        Afternoon = new Interval(new DateTime(1,1,1,12,0), new DateTime(1,1,1,17,0));
        Evening   = new Interval(new DateTime(1,1,1,17,0), new DateTime(1,1,1,22,0));
        LateNight = new Interval(new DateTime(1,1,1,22,0), new DateTime(1,1,2, 3,0));

        this.allowedOffset = allowedOffset;

        changes = new ArrayList<>();
        pick = null;

        //UsedTime = new List<Interval>();
    }
    public void SetTaskList(ArrayList<Task> task){
        tasks = task;
    }

    public void RemoveTask(int index){
        tasks.remove(index);
    }

    public void RemoveTask(Task t){
        tasks.remove(t);
    }

    public ArrayList<Task> GetTasksInInterval(Interval i){
        ArrayList<Task> R = new ArrayList<>();
        for(Task t : tasks) {
            ArrayList<Interval> r = t.UsedTime(i);
            for (Interval in : r){
                R.add(in.GetRefferedTask());
            }
        }
        return R;
    }

    public boolean AddTask(Task newTask){
        ArrayList<Interval> times = newTaskCheck(newTask);
        if(times == null || times.size() == 0) {
            tasks.add(newTask);
            SortTasks(true);
            return true;
        }
        else {
            return false;
        }
    }

    private void AddFluidTask(AiTask newTask){
        //Check recommended
        tasks.add(newTask);
        SortTasks(true);
    }

    private int LowestPriority(ArrayList<Interval> l){
        Interval t = l.get(0);

        for (int i = 1; i < l.size(); i++) {
            if(l.get(i).GetRefferedTask().GetPriority() < t.GetRefferedTask().GetPriority()){
                t = l.get(i);
            }
        }
        return t.GetRefferedTask().GetPriority();
    }
/*
    private ArrayList<Interval> FilterWithHighestPriority(ArrayList<Interval> times, int highestPriority){
        ArrayList<Interval> r = new ArrayList<>();

        for (Interval i :
                times) {
            if(i.GetRefferedTask().GetPriority() >= highestPriority){
                r.add(i);
            }
        }
        return r;
    }

    private ArrayList<Interval> FilterWithLowestPriority(ArrayList<Interval> times, int lowestPriority){
        ArrayList<Interval> r = new ArrayList<>();

        for (Interval i :
                times) {
            if(i.GetRefferedTask().GetPriority() <= lowestPriority){
                r.add(i);
            }
        }
        return r;
    }
*/
    private TimeSpan SumOfDurations(ArrayList<Interval> times){
        TimeSpan r = new TimeSpan(0);
        for (Interval i :
                times) {
            r.Add(i.GetDuration());
        }
        return r;
    }

    /*private void Rearange(){

    }*/

    private int EmptyTime(ArrayList<Interval> times){
        for (int i = 0; i < times.size()-1; i++) {
            if(times.get(i).GetEndTime().Before(times.get(i+1).GetStartTime())){
                return i;
            }
        }
        return -1;
    }

    private void ChangesToFreeTime(ArrayList<Interval> times, AiTask newTask, Interval prefferedInterval){
/*
        //should set global variable "pick" and "changes"
        Random r = new Random();

        if(r.nextBoolean()){
            for (int i = 0; i < times.size(); i++) {
                if()
            }
        }
        else{

        }

/*
        int middle = EmptyTime(times);
        if(middle == -1) middle = times.size()/2;


        if(r.nextBoolean()){
            for()
        }
        else{

        }
*/



        //TODO implement
    }

    public Interval CalcAiTask(AiTask newTask, Interval prefferedInterval){
        ArrayList<Interval> times = newTaskCheck(newTask, prefferedInterval);
        if(times == null || times.size() == 0){

            ChangesToFreeTime(CalcUsedTime(prefferedInterval), newTask, prefferedInterval);
            //return pick.GetTime();

            return null; //temporary
        }
        else
        {
            Random r = new Random();
            Interval localPick = times.get(r.nextInt(times.size()));

            if(localPick.GetDuration().GreaterThan(newTask.GetTime().GetDuration().Multiply(GetMulOffset(true)))){
                if(r.nextBoolean()){
                    return new Interval(localPick.GetStartTime(), newTask.GetTime().GetDuration());
                }
                else {
                    return new Interval(newTask.GetTime().GetDuration(), localPick.GetEndTime());
                }
            }
            else return localPick;
        }
    }

    public void AcceptGuess(){
        ApplyAllChanges();
        AddFluidTask(pick);
    }

    public ArrayList<Interval> newTaskCheck(Task newTask){
        return CalcUsedTime(newTask.GetTime());
        //RETURNS list of USED times in newTask.time if exists
    }

    public ArrayList<Interval> newTaskCheck(Task newTask, Interval preferedInterval){
        ArrayList<Interval> R = CalcFreeTime(preferedInterval);
        ArrayList<Interval> r = new ArrayList<Interval>();
        for(Interval i : R){
            if(i.GetDuration().GreaterThan((newTask.GetTime().GetDuration().Multiply(GetMulOffset(false))))) {
                r.add(i);
            }
        }
        return r;
        //RETURNS list of FREE times in prefferedInterval which can fit newTask
    }
    private ArrayList<Interval> CalcUsedTime(Interval period){
        ArrayList<Interval> R = new ArrayList<Interval>();
        for(int i = 0; i<tasks.size(); i++){
            ArrayList<Interval> newTimes = tasks.get(i).UsedTime(period);
            for(Interval time : newTimes){
                R.add(time);
            }
        }
        R = Interval.SortByStartTime(R);
        return R;
    }

    public Interval IntToPTime(int i){
        switch (i){
            case 1:
                return Morning;
            case 2:
                return Afternoon;
            case 3:
                return Evening;
            case 4:
                return LateNight;
            default:
                return Morning;
        }
    }

    private ArrayList<Interval> CalcFreeTime(Interval period){
        return Interval.Invert(CalcUsedTime(period), period);
    }

    public void SortTasks(boolean asc){
        //times.Sort();  TODO research
        for(int i = 0; i<tasks.size()-1; i++){
            for(int j = i+1; j<tasks.size(); j++){
                if(tasks.get(i).GetTime().GetStartTime().After(tasks.get(j).GetTime().GetStartTime()) == asc){
                    Task T = tasks.get(i);
                    tasks.set(i, tasks.get(j));
                    tasks.set(j, T);
                }
            }
        }
    }

    private void ApplyAllChanges(){
        for (Change c: changes) {
            c.ApplyChange();
        }
        changes.clear();
    }
}

class Change{
    private AiTask task; // task to be changed
    private Interval newTime;

    public Change(AiTask task, Interval newTime) {
        this.task = task;
        this.newTime = newTime;
    }

    public void ApplyChange(){
        task.SetNewTime(newTime);
    }
}