package com.cvetici.beeorganised;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

class ListaTaskovaAdapter extends RecyclerView.Adapter<ListaTaskovaAdapter.ViewHolder> {

    private ArrayList<Task> taskovi = new ArrayList<>();
    public ListaTaskovaAdapter() {

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_taskova,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtName.setText(taskovi.get(position).GetTitle());
    }

    @Override
    public int getItemCount() {
        return taskovi.size();
    }

    public void setTaskovi(ArrayList<Task> taskovi) {
        this.taskovi = taskovi;
        notifyDataSetChanged(); //Refresuje RC view
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.imeTaska);
        }
    }


}
