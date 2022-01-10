package com.cvetici.beeorganised;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

class ListaTaskovaAdapter extends RecyclerView.Adapter<ListaTaskovaAdapter.ViewHolder> {

    private List<Task> taskovi = new ArrayList<>();
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
        holder.vreme.setText(taskovi.get(position).ToStringTime());
    }

    @Override
    public int getItemCount() {
        return taskovi.size();
    }

    public void setTaskovi(List<Task> taskovi) {
        this.taskovi = taskovi;
        notifyDataSetChanged(); //Refresuje RC view
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView txtName;
        private TextView vreme;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.imeTaska);
            vreme = itemView.findViewById(R.id.vremeTaska);
        }
    }


}
