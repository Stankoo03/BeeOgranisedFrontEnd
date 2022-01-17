package com.cvetici.beeorganised;

import android.graphics.Color;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

class ListaTaskovaAdapter extends RecyclerView.Adapter<ListaTaskovaAdapter.ViewHolder> {

    private List<Task> taskovi = new ArrayList<>();
    private OnTaskListener onTaskListener;
    ViewHolder holder;
    public ListaTaskovaAdapter(OnTaskListener onTaskListener) {
        this.onTaskListener = onTaskListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.lista_taskova,parent,false);
        holder = new ViewHolder(view,onTaskListener);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtName;
        private TextView vreme;
        OnTaskListener onTaskListener;
        public ViewHolder(@NonNull View itemView,OnTaskListener onTaskListener) {
            super(itemView);
            txtName = itemView.findViewById(R.id.imeTaska);
            vreme = itemView.findViewById(R.id.vremeTaska);
            this.onTaskListener = onTaskListener;
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            onTaskListener.onTaskClick(getAdapterPosition(),itemView);
        }
    }
    public interface OnTaskListener{
        void onTaskClick(int position,View itemView);

    }

}
