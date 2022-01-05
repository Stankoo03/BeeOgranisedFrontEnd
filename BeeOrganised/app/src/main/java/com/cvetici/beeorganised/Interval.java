package com.cvetici.beeorganised;

import java.util.ArrayList;

public class Interval{
    private DateTime startTime, endTime;
    private int intesectType;
    private Task refferedTask;

    public DateTime GetStartTime() {
        return startTime;
    }
    public DateTime GetEndTime() {
        return endTime;
    }
    public TimeSpan GetDuration() {
        return  endTime.Difference(startTime);
    }
    public int GetIntersectType() {
        return intesectType;
    }
    public Task GetRefferedTask() {
        return refferedTask;
    }

    public void SetStartTime(DateTime sTime) {
        if(sTime.After(endTime)) {
            //System.Console.WriteLine(sTime + "  --  " + endTime); //DEBUG
            //throw new Exception("Error! New StartTime is AFTER current EndTime!");
            System.out.println("Error! New StartTime is AFTER current EndTime!");
        }
        else startTime = sTime;
    }
    public void SetEndTime(DateTime eTime) {
        if(eTime.Before(startTime)) {
            //System.Console.WriteLine(startTime + "  --  " + eTime); //DEBUG
            //throw new Exception("Error! New EndTime is BEFORE current StartTime!");
            System.out.println("Error! New EndTime is BEFORE current StartTime!");
        }
        else endTime = eTime;
    }
    public void SetDuration(TimeSpan dur) {
        /*if(dur < new TimeSpan(0))
            System.out.println("Error! New Duration is NEGATIVE!");
            //throw new Exception("Error! New Duration is NEGATIVE!");*/
        endTime = startTime.AddDur(dur);
    }
    private void SetIntersectType(int type){
        if(type == 2 || type == 3 || type == 5 || type == 6)
            intesectType = type;
        else
            System.out.println("Invalid intersect type provided: " + type);
    }
    public void SetRefferedTask(Task rTask) {refferedTask = rTask;}

    public Interval() { DateTime now = new DateTime(); startTime = now; endTime = now; }
    public Interval(DateTime time) { startTime = time; SetEndTime(time); }
    public Interval(DateTime sTime, DateTime eTime) { startTime = sTime; SetEndTime(eTime); }
    public Interval(DateTime sTime, DateTime eTime, int intersectType) { startTime = sTime; SetEndTime(eTime); SetIntersectType(intersectType); }
    public Interval(DateTime sTime, DateTime eTime, Task refferedTask) { startTime = sTime; SetEndTime(eTime); this.refferedTask = refferedTask; }
    public Interval(DateTime sTime, TimeSpan dur) { startTime = sTime; SetDuration(dur); }
    public Interval(Interval toCopy) { startTime = toCopy.GetStartTime(); SetEndTime(toCopy.GetEndTime()); intesectType = toCopy.intesectType; refferedTask = toCopy.refferedTask; }
    public Interval(DateTime date, int sHour, int sMin, int eHour, int eMin){
        startTime = new DateTime(date.GetYear(), date.GetMonth(), date.GetDay(), sHour, sMin);
        SetEndTime(new DateTime(date.GetYear(), date.GetMonth(), date.GetDay(), eHour, eMin));
    }

    public String ToString()
    {
        return /*"("+refferedTask.GetTitle()+")"+*/ startTime.ToString() + " <--> " + endTime.ToString();
    }

    public boolean NoDuration() {return GetDuration().GetLongMinutes() == 0;}

    public void MoveByOffset(TimeSpan offset){
        startTime = startTime.AddDur(offset);
        endTime = endTime.AddDur(offset);
    }

    public Interval Intersect(Interval other){
        if(GetStartTime().Before(other.GetStartTime())){
            if(GetEndTime().Before(other.GetStartTime())){
                //1st
                return null;
            }
            else{
                if(GetEndTime().Before(other.GetEndTime())){
                    //2nd
                    return new Interval(other.GetStartTime(), GetEndTime(), 2);
                }
                else{
                    //3rd
                    return new Interval(other.GetStartTime(), other.GetEndTime(), 3);
                }
            }
        }
        else{
            if(other.GetEndTime().Before(GetStartTime())){
                //4th
                return null;
            }
            else{
                if(other.GetEndTime().Before(GetEndTime())){
                    //5th
                    return new Interval(GetStartTime(), other.GetEndTime(), 5);
                }
                else{
                    //6th
                    return new Interval(GetStartTime(), GetEndTime(), 6);
                }
            }
        }
    }

    public Interval Unify(Interval other){
        Interval intersect = Intersect(other);

        if(intersect == null) return null;

        switch(intersect.GetIntersectType()){
            case 2:
                return new Interval(GetStartTime(), other.GetEndTime(), 2);

            case 3:
                return new Interval(GetStartTime(), GetEndTime(), 3);

            case 5:
                return new Interval(other.GetStartTime(), GetEndTime(), 5);

            case 6:
                return new Interval(other.GetStartTime(), other.GetEndTime(), 6);

            default:
                return null;
        }

    }
    /*
        public static List<Interval> UnifyAll(List<Interval> times){
            if(times.Count() < 2) return times;

            return times;

            for(int i = 0; i<times.Count()-1; i++){
                for(int j = i+1; j<times.Count(); j++){
                    if(times[i].Intersect(times[j]) != null);
                    //TODO implement...
                }
            }
            return null; //delete this line
        }
    */
    public static ArrayList<Interval> SortByStartTime(ArrayList<Interval> times){
        //times.Sort();  TODO research
        //Debug.Log("Sorting..."); //DEBUG
        for(int i = 0; i<times.size()-1; i++){
            for(int j = i+1; j<times.size(); j++){
                if(times.get(i).GetStartTime().After(times.get(j).GetStartTime())){
                    Interval T = times.get(i);
                    times.set(i, times.get(j));
                    times.set(j, T);
                }
            }
        }
        return times;
    }

    public static ArrayList<Interval> Crop(ArrayList<Interval> times, Interval toInterval){
        //times = UnifyAll(times);
        ArrayList<Interval> R = new ArrayList<>();
        for(int i = 0; i<times.size(); i++){
            Interval intersect = times.get(i).Intersect(toInterval);
            if(intersect != null){
                R.add(intersect);
            }
        }
        return R;
    }
    /*
        //TODO check correctness
        private static bool isSorted(List<Interval> times){
            for(int i = 1; i<times.Count(); i++){
                if(times[i-1].GetStartTime() > times[i].GetStartTime())
                    return false;
            }
            return true;
        }
    */
    //DEBUG
    private static void PrintList(ArrayList<Interval> times, String pre){
        for(Interval i : times){
            System.out.println(pre + " " + i.ToString());
        }
    }

    public static ArrayList<Interval> Invert(ArrayList<Interval> times, Interval inInterval){
        if(times.size()>0){
            times = Crop(times, inInterval);
            //(!isSorted(times)){
            times = SortByStartTime(times);
            //}

            PrintList(times,"INVERT* ");

            ArrayList<Interval> R = new ArrayList<Interval>();

            if(inInterval.GetStartTime().Before(times.get(0).GetStartTime())){
                R.add(new Interval(inInterval.GetStartTime(),
                        times.get(0).GetStartTime()));
            }

            for(int i = 1; i<times.size(); i++){
                if(times.get(i-1).GetEndTime().Before(times.get(i).GetStartTime())){
                    //Debug.Log(new Interval(times[i-1].GetEndTime(), times[i].GetStartTime()).ToString()); //DEBUG
                    R.add(new Interval(times.get(i-1).GetEndTime(), times.get(i).GetStartTime()));
                }
            }

            if(times.get(times.size()-1).GetEndTime().Before(inInterval.GetEndTime())){
                R.add(new Interval(times.get(times.size()-1).GetEndTime(),
                        inInterval.GetEndTime()));
            }
            return R;
        }
        else{
            return new ArrayList<Interval>();
        }
    }

    public static DateTime ConstructDT(DateTime dateFrom, DateTime timeFrom){
        return new DateTime(dateFrom.GetYear(), dateFrom.GetMonth(), dateFrom.GetDay(),
                            timeFrom.GetHour(), timeFrom.GetMinute());
    }
/*
    public static DateOnly DateFromDT(DateTime dateFrom){
        return new DateOnly(dateFrom.GetYear(), dateFrom.GetMonth(), dateFrom.GetDay());
    }

    public static TimeOnly TimeFromDT(DateTime timeFrom){
        return new TimeOnly(timeFrom.Hour, timeFrom.Minute, timeFrom.Second, timeFrom.Millisecond);
    }*/
}
