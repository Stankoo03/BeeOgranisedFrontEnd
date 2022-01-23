package com.cvetici.beeorganised;

import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicReference;

public class SmartToDo{
    private ArrayList<Task> tasks;

    public Interval Morning;   //Default: Morning(7-12)
    public Interval Afternoon; //Default: Afternoon(12-17)
    public Interval Evening;   //Default: Evening(17-22)
    public Interval LateNight; //Default: LateNight(22-3)

    private float allowedOffset = 5f;
/*
    private ArrayList<Change> changes;*/
    private AiTask pick;
    private boolean possible = false;
    private boolean changes = false;

    private ArrayList<Task> newTasks;
    private ArrayList<Task> ejected;

    private float GetMulOffset(boolean plusSign){
        if (plusSign) return (1f+allowedOffset/100f);
        else return (1f-allowedOffset/100f);
    }


    public boolean isPossible(){ return possible; }

    //private List<Interval> UsedTime;

    public SmartToDo(float allowedOffset){
        tasks = new ArrayList<Task>();

        Morning   = new Interval(new DateTime(2020,1,1, 7,0),
                                 new DateTime(2020,1,1,12,0));

        //System.out.println("mornign" + Morning.ToString());

        Afternoon = new Interval(new DateTime(2020,1,1,12,0),
                                 new DateTime(2020,1,1,17,0));

        Evening   = new Interval(new DateTime(2020,1,1,17,0),
                                 new DateTime(2020,1,1,22,0));

        LateNight = new Interval(new DateTime(2020,1,1,22,0),
                                 new DateTime(2020,1,2, 3,0));

        this.allowedOffset = allowedOffset;

        //changes = new ArrayList<>();
        pick = null;

        //UsedTime = new List<Interval>();
    }
    
    public void PrintAll(String title){
        System.out.println("Г--" + title + "################--Г");
        //System.out.println(title);
        for (Task t :
                tasks) {
            System.out.println("|| " + t.ToString());
        }
        System.out.println("L--" + title + "****************--L");
    }


    public void PrintAll(String title, ArrayList<Task> tasks){
        System.out.println("Г--" + title + "################--Г");
        //System.out.println(title);
        for (Task t :
                tasks) {
            System.out.println("|| " + t.ToString());
        }
        System.out.println("L--" + title + "****************--L");
    }

    public void PrintAllTimes(String title, ArrayList<Interval> times){
        System.out.println("Г--" + title + "################--Г");
        //System.out.println(title);
        for (Interval t :
                times) {
            System.out.println("|| " + t.ToString());
        }
        System.out.println("L--" + title + "****************--L");
    }

    public void RemoveTask(int index){
        tasks.remove(index);
    }

    public void RemoveTask(Task t){
        tasks.remove(t);
    }

    public boolean RemoveTask(String title){
        for (int i = 0; i < tasks.size(); i++) {
            if(tasks.get(i).GetTitle() == title){
                RemoveTask(i);
                return true;
            }
        }
        return false;
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

    private Interval LowestPriorityI(ArrayList<Interval> l){
        if(l.isEmpty()){
            Interval seks = new Interval();
            seks.SetRefferedTask(new AiTask("",1,2,new Interval()));
            return seks;
        }
        Interval t = l.get(0);

        for (int i = 1; i < l.size(); i++) {
            if(l.get(i).GetRefferedTask().GetPriority() > t.GetRefferedTask().GetPriority()){
                t = l.get(i);
            }
        }
        return t;
    }

    private Task LowestPriorityT(ArrayList<Task> l){
        Task t = l.get(0);

        for (int i = 1; i < l.size(); i++) {
            if(l.get(i).GetPriority() < t.GetPriority()){
                t = l.get(i);
            }
        }
        return t;
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

    private TimeSpan SumOfDurations(ArrayList<Interval> times, boolean threeIncluded){
        TimeSpan r = new TimeSpan(0);
        for (Interval i :
                times) {
            if(i.GetRefferedTask().GetPriority() >= (threeIncluded?3:4))
                r.Add(i.GetDuration());
        }
        return r;
    }



    private ArrayList<Interval> FindToEject(ArrayList<Interval> times, boolean threeIncluded, TimeSpan span){
        ArrayList<Interval> r = new ArrayList<>();
        /*
        for(Task t : tasks) {
            ArrayList<Interval> R = t.UsedTime(i);
            for (Interval in : r){
                r.add(in.GetRefferedTask());
            }
            //...
        }*/
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

    private boolean IsMovable(Task t, Interval prefferedInterval, int newTaskPriority){
        return t instanceof AiTask && t.GetPriority()>1 && t.GetTime().Intersect(prefferedInterval).GetIntersectType()==6;
    }

    private boolean IsEjectable(Task t, boolean threeIncluded){
        return t instanceof AiTask && t.GetPriority()>=(threeIncluded?3:4);
    }

    private Interval CanFitInterval(ArrayList<Interval> times, Interval interval){
        for (Interval i:
                times) {
            if(i.GetDuration().GreaterThan(interval.GetDuration().Multiply(GetMulOffset(false)))){
                if(i.GetDuration().GreaterThan(interval.GetDuration().Multiply(GetMulOffset(true)))){
                    return new Interval(i.GetStartTime(), interval.GetDuration());
                }
                else{
                    return i;
                }
            }
        }
        return null;
    }

    private boolean HaveOverlays(ArrayList<Interval> times, Interval interval){
        for (Interval i :
                times) {
            Interval in = i.Intersect( interval);
            if(in!=null) {
                if(in.GetDuration().GreaterThan(new TimeSpan(1))) {
                    //Debug.log("## INtersect" + in.ToString());
                    return true;
                }
            }
        }
        return false;
    }

    private void MakeChanges(AiTask newTask, Interval prefferedInterval, int maxTries, boolean reset){

        if(reset) {
            newTasks = CopyArray(tasks);
            possible = false;
            changes = false;
        }
        Random r = new Random();
        int tries = 0;
        while(!possible && tries<maxTries){
            tries++;
            int localTries = 0;

            int rtt = r.nextInt(newTasks.size());
            Task tt = newTasks.get(rtt);
            while(!IsMovable(tt,prefferedInterval, newTask.GetPriority()) && localTries<maxTries){
                localTries++;
                rtt = r.nextInt(newTasks.size());
                tt = newTasks.get(rtt);
            }

            Interval guess = new Interval();
            boolean found = false;
            if(r.nextBoolean()){
                if(r.nextBoolean()){
                    guess = new Interval(prefferedInterval.GetStartTime(), tt.GetTime().GetDuration());
                }
                else {
                    guess = new Interval(tt.GetTime().GetDuration(), prefferedInterval.GetEndTime());
                }

                if(!HaveOverlays(CalcUsedTime(newTasks, prefferedInterval), guess)){
                    found = true;
                }
            }

            if(!found) {
                int rtt2 = r.nextInt(newTasks.size());
                guess = tt.GetTime().MoveNextTo(newTasks.get(rtt2).GetTime(), r.nextBoolean());
                while ((guess.Intersect(prefferedInterval).GetIntersectType() != 6 || HaveOverlays(CalcUsedTime(newTasks, prefferedInterval), guess)) && localTries < maxTries) {

                    //
                    Debug.log("intersect t - " + guess.Intersect(prefferedInterval).GetIntersectType());

                    PrintAllTimes("Try" + tries + " Local" + localTries + " ", CalcUsedTime(newTasks,prefferedInterval));
                    Debug.log("Guessssss: " + guess.ToString());


                    localTries++;
                    rtt2 = r.nextInt(newTasks.size());
                    guess = tt.GetTime().MoveNextTo(newTasks.get(rtt2).GetTime(), r.nextBoolean());
                }
            }

            Debug.log("local tris " + localTries);

            if(localTries < maxTries-1) {
                tt.SetNewTime(guess);
                if(tries < 10)
                    PrintAll("ASDDS",newTasks);
                Interval c = CanFitInterval(CalcFreeTime(newTasks, prefferedInterval), newTask.GetTime());
                if(c!=null){
                    Debug.log("-*-*-KURCINAA-*-**-*-*-*-*-*-*-*-*-*-*-");
                    if(c.GetDuration().GreaterThan(newTask.GetTime().GetDuration().Multiply(GetMulOffset(true)))){
                        pick = new AiTask(newTask.GetTitle(), new Interval(c.GetStartTime(), newTask.GetTime().GetDuration()), newTask.GetPriority(), newTask.prefferedTime);
                    }
                    else{
                        pick = new AiTask(newTask.GetTitle(), c, newTask.GetPriority(), newTask.prefferedTime);
                    }
                    possible = true;
                    changes = true;
                    return; //possible
                }
            }

        }

        Debug.log("TRIES " + tries);
    }

    private void Eject(ArrayList<Interval> inPI, AiTask newTask, Interval prefferedInterval, int maxTries, boolean threeIncluded){
        newTasks = CopyArray(tasks);

        if(SumOfDurations(inPI, threeIncluded).GreaterThan(newTask.GetTime().GetDuration().Multiply(GetMulOffset(false)))){
            ArrayList<Interval> toEject = FindToEject(inPI,threeIncluded, newTask.GetTime().GetDuration().Multiply(GetMulOffset(false)));

            ejected = ConvertIntervalToTaskArray(CopyArrayInterval(toEject));
            for (Interval in :
                    toEject) {
                newTasks.remove(in.GetRefferedTask());
            }

            if(toEject.size() == 1){
                Interval in = toEject.get(0);
                if(in.GetDuration().GreaterThan(newTask.GetTime().GetDuration().Multiply(GetMulOffset(true)))){
                    in = new Interval(in.GetStartTime(), newTask.GetTime().GetDuration());
                }
                changes = true;
                pick = new AiTask(newTask.GetTitle(),in, newTask.GetPriority(), newTask.GetPrefferedInterval());
                possible = true;
            }

            MakeChanges(newTask, prefferedInterval, maxTries, false);
        }
    }

    private void ChangesToFreeTime(AiTask newTask, Interval prefferedInterval){

        //should set global variables "pick", "newTasks" and "possible"


        int maxTries = 1000;
        Random r = new Random();

        ArrayList<Interval> inPI = CalcUsedTime(prefferedInterval);


        System.out.println(prefferedInterval.ToString());

        int lpiinpi = LowestPriorityI(inPI).GetRefferedTask().GetPriority();

        Debug.log("lppni" + lpiinpi);

        if(lpiinpi <= 1){

            return; //not possible
        }
        else if(lpiinpi <= 2){

            if(!SumOfDurations(CalcFreeTime(prefferedInterval)).GreaterThan(newTask.GetTime().GetDuration().Multiply(GetMulOffset(false)))){
                Debug.log("Velika kurcina  " + SumOfDurations(CalcFreeTime(prefferedInterval)).ToString());
                Debug.log("jos veca kurcina  " + prefferedInterval.ToString());
                return; //not possible
            }
            else{
                MakeChanges(newTask, prefferedInterval, maxTries, true);
                /*
                int tries = 0;
                while(!possible && tries<maxTries){
                    tries++;
                    int localTries = 0;

                    int rtt = r.nextInt(newTasks.size());
                    Task tt = newTasks.get(rtt);
                    while(!IsMovable(tt,prefferedInterval) && localTries<maxTries){
                        localTries++;
                        rtt = r.nextInt(newTasks.size());
                        tt = newTasks.get(rtt);
                    }

                    Interval guess = new Interval();
                    boolean found = false;
                    if(r.nextBoolean()){
                        if(r.nextBoolean()){
                            guess = new Interval(prefferedInterval.GetStartTime(), tt.GetTime().GetDuration());
                        }
                        else {
                            guess = new Interval(tt.GetTime().GetDuration(), prefferedInterval.GetEndTime());
                        }

                        if(!HaveOverlays(CalcUsedTime(newTasks, prefferedInterval), guess)){
                            found = true;
                        }
                    }

                    if(!found) {
                        int rtt2 = r.nextInt(newTasks.size());
                        guess = tt.GetTime().MoveNextTo(newTasks.get(rtt2).GetTime(), r.nextBoolean());
                        while ((guess.Intersect(prefferedInterval).GetIntersectType() != 6 || HaveOverlays(CalcUsedTime(newTasks, prefferedInterval), guess)) && localTries < maxTries) {

                            //
                            //Debug.log("intersect t - " + guess.Intersect(prefferedInterval).GetIntersectType());

                            //PrintAllTimes("Try" + tries + " Local" + localTries + " ", CalcUsedTime(newTasks,prefferedInterval));
                            //Debug.log("Guessssss: " + guess.ToString());


                            localTries++;
                            rtt2 = r.nextInt(newTasks.size());
                            guess = tt.GetTime().MoveNextTo(newTasks.get(rtt2).GetTime(), r.nextBoolean());
                        }
                    }

                    //Debug.log("local tris " + localTries);

                    if(localTries < maxTries-1) {
                        tt.SetNewTime(guess);
                        if(tries < 10)
                        PrintAll("ASDDS",newTasks);
                        Interval c = CanFitInterval(CalcFreeTime(newTasks, prefferedInterval), newTask.GetTime());
                        if(c!=null){
                            Debug.log("-*-*-KURCINAA-*-**-*-*-*-*-*-*-*-*-*-*-");
                            if(c.GetDuration().GreaterThan(newTask.GetTime().GetDuration().Multiply(GetMulOffset(true)))){
                                pick = new AiTask(newTask.GetTitle(), new Interval(c.GetStartTime(), newTask.GetTime().GetDuration()), newTask.GetPriority(), newTask.prefferedTime);
                            }
                            else{
                                pick = new AiTask(newTask.GetTitle(), c, newTask.GetPriority(), newTask.prefferedTime);
                            }
                            possible = true;
                            changes = true;
                            return; //possible
                        }
                    }

                }

                Debug.log("TRIES " + tries);*/
            }

        }
        else if(lpiinpi <= 3){

            MakeChanges(newTask, prefferedInterval, maxTries, true);

            if(!possible){
                Eject(inPI,newTask,prefferedInterval,maxTries, true);
            }

        }
        else{ // lpiinpi <= 4
            MakeChanges(newTask, prefferedInterval, maxTries, true);

            if(!possible){
                Eject(inPI,newTask,prefferedInterval,maxTries, false);
            }

            if(!possible){
                Eject(inPI,newTask,prefferedInterval,maxTries, true);
            }

        }

        //TODO implement
    }

    public Interval CalcAiTask(AiTask newTask){
        Interval prefferedInterval = newTask.GetPrefferedInterval();
        System.out.println(prefferedInterval.ToString());
        ArrayList<Interval> times = newTaskCheck(newTask, prefferedInterval);
        if(times.size() == 0){

            ChangesToFreeTime(newTask, prefferedInterval);
            if(possible)
                return pick.GetTime();
            else
                return null;

            //return null; //temporary
        }
        else
        {
            Random r = new Random();
            Interval localPick = times.get(r.nextInt(times.size()));

            if(localPick.GetDuration().GreaterThan(newTask.GetTime().GetDuration().Multiply(GetMulOffset(true)))){
                if(r.nextBoolean() || true){ //TODO delete "|| true"
                    localPick = new Interval(localPick.GetStartTime(), newTask.GetTime().GetDuration());
                }
                else {
                    localPick = new Interval(newTask.GetTime().GetDuration(), localPick.GetEndTime());
                }
            }
            pick = new AiTask(newTask.GetTitle(),localPick, newTask.GetPriority(), newTask.GetPrefferedInterval());
            possible = true; //possible
            return localPick;
        }
    }

    public void AcceptGuess(){
        //ApplyAllChanges();
        if(possible) {
            if (changes)
                tasks = newTasks;
            System.out.println(pick.ToString());
            AddFluidTask(pick);
            possible = false;
        }
        else{
            System.out.println("not possible");
        }
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
    private ArrayList<Interval> CalcUsedTime(ArrayList<Task> tasks, Interval period){
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

    public Interval GetInterval(int i, DateTime dateFrom){
        switch (i){
            case 1:
                System.out.println(Morning.ToString());
                return new Interval(Morning, dateFrom);
            case 2:
                return new Interval(Afternoon, dateFrom);
            case 3:
                return new Interval(Evening, dateFrom);
            case 4:
                return new Interval(LateNight, dateFrom);
            default:
                return new Interval(Morning, dateFrom);
        }
    }

    private ArrayList<Interval> CalcFreeTime(Interval period){
        return Interval.Invert(CalcUsedTime(period), period);
    }
    private ArrayList<Interval> CalcFreeTime(ArrayList<Task> tasks, Interval period){
        return Interval.Invert(CalcUsedTime(tasks, period), period);
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
/*
    private void ApplyAllChanges(){
        for (Change c: changes) {
            c.ApplyChange();
        }
        changes.clear();
    }
*/
    private ArrayList<Task> CopyArray(ArrayList<Task> lista){
        ArrayList<Task> a = new ArrayList<Task>();

        for (Task t: lista) {
            if(t instanceof AiTask){
                a.add(new AiTask((AiTask) t));
            }
            else{
                a.add(t);
            }
        }

        return a;
    }

    private ArrayList<Interval> CopyArrayInterval(ArrayList<Interval> lista){
        ArrayList<Interval> a= new ArrayList<>();
        for (Interval in: lista){
            a.add(new Interval(in));
        }

        return a;
    }

    private ArrayList<Task> ConvertIntervalToTaskArray(ArrayList<Interval> lista){
        ArrayList<Task> a= new ArrayList<>();
        for (Interval in: lista){
            a.add(in.GetRefferedTask());
        }

        return a;
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