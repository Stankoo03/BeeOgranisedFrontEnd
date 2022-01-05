package com.cvetici.beeorganised;

import java.util.ArrayList;

public class SmartToDo{
    private ArrayList<Task> tasks;

    private Interval Morning;   //Default: Morning(7-12)
    private Interval Afternoon; //Default: Afternoon(12-17)
    private Interval Evening;   //Default: Evening(17-22)

    private float allowedOffset = 5f;

    //private List<Interval> UsedTime;

    public SmartToDo(float allowedOffset){
        tasks = new ArrayList<Task>();

        Morning   = new Interval(new DateTime(1,1,1, 7,0), new DateTime(1,1,1,12,0));
        Afternoon = new Interval(new DateTime(1,1,1,12,0), new DateTime(1,1,1,17,0));
        Evening   = new Interval(new DateTime(1,1,1,17,0), new DateTime(1,1,1,22,0));

        this.allowedOffset = allowedOffset;

        //UsedTime = new List<Interval>();
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

    public void AddTask(Task newTask){ //SHOULD BE PRIVATE
        //Check reccomended
        tasks.add(newTask);
        SortTasks(true);
        //UsedTime = CalcUsedTime(new Interval(DateTime.Today, new TimeSpan(30,0,0)));
    }

    public ArrayList<Interval> newTaskCheck(Task newTask){
        return CalcUsedTime(newTask.GetTime());
        //RETURNS list of USED times in preffered time if exists
    }

    public ArrayList<Interval> newTaskCheck(Task newTask, Interval preferedInterval){
        ArrayList<Interval> R = CalcFreeTime(preferedInterval);
        ArrayList<Interval> r = new ArrayList<Interval>();
        for(Interval i : R){
            if(i.GetDuration().GreaterThan((newTask.GetTime().GetDuration().Multiply((1f-allowedOffset/100f))))) {
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
}
