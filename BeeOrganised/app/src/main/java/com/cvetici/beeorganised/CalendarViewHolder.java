package com.cvetici.beeorganised;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.menu.MenuView;
import androidx.recyclerview.widget.RecyclerView;

import java.text.BreakIterator;

public class CalendarViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public final TextView dayOfMonth;
    public final ImageView hexagonImage;
    private final CalendarAdapter.OnItemListener onItemListener;

    public CalendarViewHolder(@NonNull View itemView, CalendarAdapter.OnItemListener onItemListener) {
        super(itemView);
        dayOfMonth = itemView.findViewById(R.id.cellDayText);
        hexagonImage = itemView.findViewById(R.id.hexagon);
        this.onItemListener = onItemListener;
        itemView.setOnClickListener(this);
    }



    public void onClick(View view){
        onItemListener.onItemClick(getAdapterPosition(),(String) dayOfMonth.getText(),hexagonImage);
    }


}
