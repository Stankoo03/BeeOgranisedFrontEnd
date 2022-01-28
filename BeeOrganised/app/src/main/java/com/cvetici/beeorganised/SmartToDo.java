package com.cvetici.beeorganised;

import java.util.ArrayList;
import java.util.Random;

public class SmartToDo{
    private ArrayList<Task> tasks;

    public Interval Morning;   //Default: Morning(7-12)
    public Interval Afternoon; //Default: Afternoon(12-17)
    public Interval Evening;   //Default: Evening(17-22)
    public Interval LateNight; //Default: LateNight(22-3)

    private final float allowedOffset;

    private AiTask pick;
    private boolean possible = false;
    private boolean changes = false;

    private int currentInterval = 1;

    private ArrayList<Task> newTasks;
    private ArrayList<Task> ejected;
    private ArrayList<Change> makedChanges;

    private float GetMulOffset(boolean plusSign){
        if (plusSign) return (1f+allowedOffset/100f);
        else return (1f-allowedOffset/100f);
    }
    public void setTasks(ArrayList<Task> tasks){
        ArrayList<Task> neww = new ArrayList<>();
        for (Task t :
                tasks) {
            if(t.GetTime().GetEndTime().After(new DateTime(DateTime.Now(),0,0))){
                neww.add(t);
            }
        }
        this.tasks = neww;
    }
    public ArrayList<Task> getTasks(){
        return tasks;
    }
    public ArrayList<Change> getMakedChanges() {return makedChanges;}

    public boolean isPossible(){ return possible; }


    public SmartToDo(float allowedOffset){
        tasks = new ArrayList<>();
        makedChanges = new ArrayList<>();

        Morning   = new Interval(new DateTime(2020,1,1, 7,0),
                                 new DateTime(2020,1,1,12,0));

        Afternoon = new Interval(new DateTime(2020,1,1,12,0),
                                 new DateTime(2020,1,1,17,0));

        Evening   = new Interval(new DateTime(2020,1,1,17,0),
                                 new DateTime(2020,1,1,22,0));

        LateNight = new Interval(new DateTime(2020,1,1,22,0),
                                 new DateTime(2020,1,2, 3,0));

        this.allowedOffset = allowedOffset;

        pick = null;

    }


    public Task FindByTitle(ArrayList<Task> list, String title){
        for (Task t :
                list) {
            if (t.GetTitle().equals(title))
                return t;
        }
        return null;
    }




    public void RemoveTask(int index){
        tasks.remove(index);
    }

    public void RemoveTask(Task t){
        RemoveTask(t.GetTitle());
    }

    public void RemoveTask(String title){
        for (int i = 0; i < tasks.size(); i++) {
            if(tasks.get(i).GetTitle().equals(title)){
                RemoveTask(i);
                return;
            }
        }
    }

    private void RemoveTaskFNT(String title){
        for (int i = 0; i < newTasks.size(); i++) {
            if(newTasks.get(i).GetTitle().equals(title)){
                newTasks.remove(i);
                return;
            }
        }
    }

    public ArrayList<Task> GetTasksInInterval(Interval i){
        ArrayList<Task> R = new ArrayList<>();
        for(Task t : tasks) {
            ArrayList<Interval> r = t.UsedTime(i);
            for (Interval in : r){
                R.add(in.GetRefferedTask());
            }
        }

        R.sort((task, t1) -> {
            if(task.GetDone() && !t1.GetDone()){
                return 1;
            }
            else if(!task.GetDone() && t1.GetDone()){
                return -1;
            }
            else{
                if(task.GetTime().GetStartTime().After(t1.GetTime().GetStartTime())){
                    return 1;
                }
                else if (task.GetTime().GetStartTime().GetLongMinutes() == t1.GetTime().GetStartTime().GetLongMinutes()){
                    return 0;
                }
                else{
                    return -1;
                }
            }
        });

        return R;
    }

    private ArrayList<Integer> ContainsInIntervals(Interval i){
        ArrayList<Integer> r = new ArrayList<>();
        if(new Interval(Morning, i.GetStartTime()).Intersect(i)  != null) r.add(1);
        if(new Interval(Afternoon, i.GetStartTime()).Intersect(i)!= null) r.add(2);
        if(new Interval(Evening, i.GetStartTime()).Intersect(i)  != null) r.add(3);
        if(new Interval(LateNight, i.GetStartTime()).Intersect(i)!= null) r.add(4);

        return r;
    }

    public boolean AddTask(Task newTask){
        boolean flag1 = true;
        while(flag1){
            flag1 = TitleExist(newTask.GetTitle());
            if (flag1) newTask.SetTitle(newTask.GetTitle() + " i");
        }
        ArrayList<Interval> times = newTaskCheck(newTask);
        if(times == null || times.size() == 0) {
            tasks.add(newTask);
            SortTasks(true);
            return true;
        }
        else {
            boolean flag = false;
            for (Interval in : times){
                if(in.GetRefferedTask().GetPriority() == 1) flag = true;
            }
            if(flag) {
                return false;
            }
            else{
                int maxTries = 150;

                ArrayList<Integer> intervals = ContainsInIntervals(newTask.GetTime());

                if(intervals.size() == 1){
                    Interval prefferedInterval = GetInterval(intervals.get(0),newTask.getTime().GetStartTime());
                    MakeChanges(new AiTask(newTask, prefferedInterval), prefferedInterval, maxTries, true, true);
                    if(possible){
                        tasks = newTasks;
                        tasks.add(newTask);
                        SortTasks(true);
                        return true;
                    }
                    else{
                        Eject(CalcUsedTime(prefferedInterval), new AiTask(newTask,prefferedInterval), prefferedInterval, maxTries, true, true);

                        if(possible){
                            tasks = newTasks;
                            tasks.add(newTask);
                            SortTasks(true);
                            return true;
                        }
                        else
                            return false;
                    }
                }
                else if(intervals.size() == 2){
                    return false;
                }
                else{
                    return false;
                }

            }

        }
    }

    private boolean TitleExist(String title){
        for (Task t :
                tasks) {
            if(t.GetTitle().equals(title)) return true;
        }
        return false;
    }

    private void AddFluidTask(AiTask newTask){
        //Check recommended
        boolean flag = true;
        while(flag){
            flag = TitleExist(newTask.GetTitle());
            if (flag) newTask.SetTitle(newTask.GetTitle() + " i");
        }
        tasks.add(newTask);
        SortTasks(true);
    }

    private Interval LowestPriorityI(ArrayList<Interval> l){
        if(l.isEmpty()){
            Interval interval = new Interval();
            interval.SetRefferedTask(new AiTask("",1,2,new Interval()));
            return interval;
        }
        Interval t = l.get(0);

        for (int i = 1; i < l.size(); i++) {
            if(l.get(i).GetRefferedTask().GetPriority() > t.GetRefferedTask().GetPriority()){
                t = l.get(i);
            }
        }
        return t;
    }

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

        ArrayList<Interval> possibleIns = new ArrayList<>();
        for (Interval i :
                times) {
            if(i.GetRefferedTask().GetPriority() >= (threeIncluded?3:4))
                possibleIns.add(i);
        }

        possibleIns.sort((interval, t1) -> Integer.compare(t1.GetDuration().GetLongMinutes(), interval.GetDuration().GetLongMinutes()));

        TimeSpan br = new TimeSpan(0);

        for (Interval i: possibleIns){
            r.add(i);
            br.Add(i.GetDuration());
            if(br.GreaterThan(span))
                return r;
        }

        return r;
    }


    private boolean IsMovable(Task t, Interval prefferedInterval){

        return t instanceof AiTask && t.GetPriority()>1 && !t.UsedTime(prefferedInterval).isEmpty();
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
                    return true;
                }
            }
        }
        return false;
    }

    private ArrayList<Change> FindChanges(ArrayList<Task> oldd, ArrayList<Task> neww, Interval prefferedInterval){
        ArrayList<Change> R = new ArrayList<>();
        for (Task t :
                oldd) {
            if(t instanceof AiTask){
                AiTask a = (AiTask) FindByTitle(neww, t.GetTitle());
                if(a == null) a = (AiTask) t;
                if(!a.UsedTimeF(Interval.WholeDay(prefferedInterval)).Equals(t.UsedTimeF(Interval.WholeDay(prefferedInterval)))){
                    R.add(new Change(t.GetTitle(), a.UsedTimeF(Interval.WholeDay(prefferedInterval)), t.UsedTimeF(Interval.WholeDay(prefferedInterval))));
                }
            }
        }
        return R;
    }

    private void MakeChanges(AiTask newTask, Interval prefferedInterval, int maxTries, boolean reset, boolean fixed){

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
                guess = tt.GetTime().MoveNextTo(newTasks.get(rtt2).UsedTimeF(prefferedInterval), r.nextBoolean());
                while (  ( guess == null || guess.Intersect(prefferedInterval).GetIntersectType() != 6 || HaveOverlays(CalcUsedTime(newTasks, prefferedInterval), guess)  ) && localTries < maxTries) {

                    localTries++;
                    rtt2 = r.nextInt(newTasks.size());
                    guess = tt.GetTime().MoveNextTo(newTasks.get(rtt2).UsedTimeF(prefferedInterval), r.nextBoolean());
                }
            }


            if(localTries < maxTries-1) {
                tt.SetNewTime(guess);

                if(fixed){
                    ArrayList<Interval> c = CalcUsedTime(newTasks, prefferedInterval);
                    boolean flag = true;
                    for( Interval in: c ){
                        if(in.Intersect(newTask.GetTime()) != null)
                            flag = false;
                    }
                    if(flag){
                        possible = true;
                        changes = true;
                        return;
                    }
                }
                else {
                    Interval c = CanFitInterval(CalcFreeTime(newTasks, prefferedInterval), newTask.GetTime());


                    if (c != null) {
                        if (c.GetDuration().GreaterThan(newTask.GetTime().GetDuration().Multiply(GetMulOffset(true)))) {
                            pick = new AiTask(newTask.GetTitle(), new Interval(c.GetStartTime(), newTask.GetTime().GetDuration()), newTask.GetPriority(), newTask.prefferedTime);
                        } else {
                            pick = new AiTask(newTask.GetTitle(), c, newTask.GetPriority(), newTask.prefferedTime);
                        }

                        possible = true;
                        changes = true;
                        return; //possible
                    }
                }
            }
        }
    }

    private void Eject(ArrayList<Interval> inPI, AiTask newTask, Interval prefferedInterval, int maxTries, boolean threeIncluded, boolean fixed){
        newTasks = CopyArray(tasks);

        TimeSpan ts = SumOfDurations(inPI, threeIncluded).Adding( SumOfDurations(CalcFreeTime(prefferedInterval)) );

        if(ts.GreaterThan(newTask.GetTime().GetDuration().Multiply(GetMulOffset(false)))){
            ArrayList<Interval> toEject = FindToEject(inPI,threeIncluded, newTask.GetTime().GetDuration().Multiply(GetMulOffset(false)));

            ejected = ConvertIntervalToTaskArray(CopyArrayInterval(toEject));

            for (Interval in :
                    toEject) {
                RemoveTaskFNT(in.GetRefferedTask().GetTitle());
            }

            if(toEject.size() == 1 && !fixed){
                Interval in = toEject.get(0);
                if(in.GetDuration().GreaterThan(newTask.GetTime().GetDuration().Multiply(GetMulOffset(true)))){
                    in = new Interval(in.GetStartTime(), newTask.GetTime().GetDuration());
                }
                changes = true;
                pick = new AiTask(newTask.GetTitle(),in, newTask.GetPriority(), newTask.GetPrefferedInterval());
                possible = true;
            }

            if(!possible)
                MakeChanges(newTask, prefferedInterval, maxTries, false, fixed);

            if(possible) {
                if (!PlaceEjected(prefferedInterval.GetStartTime())) {
                    possible = false;
                    changes = false;
                } else {
                    newTasks.addAll(ejected);
                }
            }
        }
    }

    private boolean PlaceEjected(DateTime date) {
        int inter = currentInterval;

        Random r= new Random();
        boolean success = true;

        for (Task a : ejected) {
            boolean flag = false;
            int pivot = inter + 1;
            while (pivot < 5 && !flag) {
                ArrayList<Interval> possible = newTaskCheck(newTasks, a, GetInterval(pivot,date));
                if(!possible.isEmpty()){
                    Interval localPick = possible.get(r.nextInt(possible.size()));
                    if(localPick.GetDuration().GreaterThan(a.GetTime().GetDuration().Multiply(GetMulOffset(true)))){
                        a.SetNewTime(new Interval(localPick.GetStartTime(),a.GetTime().GetDuration()));
                    }
                    else{
                        a.SetNewTime(localPick);
                    }
                    flag = true;
                }
            }
            pivot = inter - 1;
            while (pivot > 0 && !flag) {
                ArrayList<Interval> possible = newTaskCheck(newTasks, a, GetInterval(pivot,date));
                if(!possible.isEmpty()){
                    Interval localPick = possible.get(r.nextInt(possible.size()));
                    if(localPick.GetDuration().GreaterThan(a.GetTime().GetDuration().Multiply(GetMulOffset(true)))){
                        a.SetNewTime(new Interval(localPick.GetStartTime(),a.GetTime().GetDuration()));
                    }
                    else{
                        a.SetNewTime(localPick);
                    }
                    flag = true;
                }
            }

            success = success && flag;
        }
        return success;
    }

    private void ChangesToFreeTime(AiTask newTask, Interval prefferedInterval){

        int maxTries = 150;

        ArrayList<Interval> inPI = CalcUsedTime(prefferedInterval);

        int lpiinpi = LowestPriorityI(inPI).GetRefferedTask().GetPriority();

        if(lpiinpi <= 1){

            return; //not possible
        }
        else if(lpiinpi <= 2){

            if(!SumOfDurations(CalcFreeTime(prefferedInterval)).GreaterThan(newTask.GetTime().GetDuration().Multiply(GetMulOffset(false)))){
                return; //not possible
            }
            else{
                MakeChanges(newTask, prefferedInterval, maxTries, true, false);
            }

        }
        else if(lpiinpi <= 3){

            MakeChanges(newTask, prefferedInterval, maxTries, true, false);
            if(!possible){
                Eject(inPI,newTask,prefferedInterval,maxTries, true, false);
            }

        }
        else{ // lpiinpi <= 4
            MakeChanges(newTask, prefferedInterval, maxTries, true, false);

            if(!possible){
                Eject(inPI,newTask,prefferedInterval,maxTries, false, false);
            }

            if(!possible){
                Eject(inPI,newTask,prefferedInterval,maxTries, true, false);
            }

        }

        if(changes)
            makedChanges = FindChanges(ConvertIntervalToTaskArray(CalcUsedTime(prefferedInterval)),newTasks, prefferedInterval);
        else{
            makedChanges.clear();
        }

    }

    public Interval CalcAiTask(AiTask newTask){

        possible = false;
        changes = false;

        Interval prefferedInterval = newTask.GetPrefferedInterval();
        System.out.println(prefferedInterval.ToString());
        ArrayList<Interval> times = newTaskCheck(newTask, prefferedInterval);
        if(times.size() == 0){

            ChangesToFreeTime(newTask, prefferedInterval);
            if(possible)
                return pick.GetTime();
            else
                return null;
        }
        else
        {
            Random r = new Random();
            Interval localPick = times.get(r.nextInt(times.size()));

            if(localPick.GetDuration().GreaterThan(newTask.GetTime().GetDuration().Multiply(GetMulOffset(true)))){
                if(r.nextBoolean()){
                    localPick = new Interval(localPick.GetStartTime(), newTask.GetTime().GetDuration());
                }
                else {
                    localPick = new Interval(newTask.GetTime().GetDuration(), localPick.GetEndTime());
                }
            }
            pick = new AiTask(newTask.GetTitle(),localPick, newTask.GetPriority(), newTask.GetPrefferedInterval());
            possible = true; //possible
            changes = false;
            return localPick;
        }
    }

    public void AcceptGuess(){
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
        ArrayList<Interval> r = new ArrayList<>();
        for(Interval i : R){
            if(i.GetDuration().GreaterThan((newTask.GetTime().GetDuration().Multiply(GetMulOffset(false))))) {
                r.add(i);
            }
        }
        return r;
        //RETURNS list of FREE times in prefferedInterval which can fit newTask
    }

    public ArrayList<Interval> newTaskCheck(ArrayList<Task> tasks, Task newTask, Interval preferedInterval){
        ArrayList<Interval> R = CalcFreeTime(tasks, preferedInterval);
        ArrayList<Interval> r = new ArrayList<>();
        for(Interval i : R){
            if(i.GetDuration().GreaterThan((newTask.GetTime().GetDuration().Multiply(GetMulOffset(false))))) {
                r.add(i);
            }
        }
        return r;
        //RETURNS list of FREE times in prefferedInterval which can fit newTask
    }

    private ArrayList<Interval> CalcUsedTime(Interval period){
        ArrayList<Interval> R = new ArrayList<>();
        for(int i = 0; i<tasks.size(); i++){
            ArrayList<Interval> newTimes = tasks.get(i).UsedTime(period);
            R.addAll(newTimes);
        }
        R = Interval.SortByStartTime(R);
        return R;
    }
    private ArrayList<Interval> CalcUsedTime(ArrayList<Task> tasks, Interval period){
        ArrayList<Interval> R = new ArrayList<>();
        for(int i = 0; i<tasks.size(); i++){
            ArrayList<Interval> newTimes = tasks.get(i).UsedTime(period);
            R.addAll(newTimes);
        }
        R = Interval.SortByStartTime(R);
        return R;
    }

    public Interval GetInterval(int i, DateTime dateFrom){
        currentInterval = i;
        switch (i){
            case 1:
                return new Interval(Morning, dateFrom);
            case 2:
                return new Interval(Afternoon, dateFrom);
            case 3:
                return new Interval(Evening, dateFrom);
            case 4:
                return new Interval(LateNight, dateFrom);
            default:
                currentInterval = 1;
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

    private ArrayList<Task> CopyArray(ArrayList<Task> lista){
        ArrayList<Task> a = new ArrayList<>();

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
            if(in.GetRefferedTask() instanceof AiTask){
                a.add(new AiTask((AiTask) in.GetRefferedTask()));
            }
            else{
                a.add(in.GetRefferedTask());
            }
        }

        return a;
    }
}

class Change{
    private String title; // task to be changed
    private Interval oldTime, newTime;
    private boolean ejected;

    public String getTitle() {
        return title;
    }

    public Interval getOldTime() {
        return oldTime;
    }

    public Interval getNewTime() {
        return newTime;
    }

    public Change(String title, Interval oldTime, Interval newTime) {
        this.title = title;
        this.oldTime = new Interval(oldTime);
        this.newTime = new Interval(newTime);
    }

    public String ToString(){
        return title + ":  " + oldTime.ToString() + " -> " + newTime.ToString();
    }
}