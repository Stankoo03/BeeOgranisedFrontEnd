package com.cvetici.beeorganised;

import java.util.ArrayList;

class ListClass {
    public String idChild;
    public String password;
    public ArrayList<Task> tasks;
    public ListClass(){
        tasks=null;
    }

    public ListClass(String id,ArrayList<Task> tasks){
        this.tasks = tasks;
        this.idChild = id;
    }
    public ListClass(String id,String password,ArrayList<Task> tasks){
        this.tasks = tasks;
        this.idChild = id;
        this.password = password;
    }

    public void setTasks(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public String getIdChild() {
        return idChild;
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }

    public void setIdChild(String id) {
        this.idChild = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
