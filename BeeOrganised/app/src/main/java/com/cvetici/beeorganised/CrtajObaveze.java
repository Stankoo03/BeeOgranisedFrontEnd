package com.cvetici.beeorganised;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.List;
import java.util.Random;

public class CrtajObaveze extends View {
    private int height,width=0;
    private int radius=0;
    private Paint paint;
    private boolean isInit=false;
    private RectF oval;
    private List<Task> listaTaskova;
    Random rand = new Random();
    private int[] boje = {
            getResources().getColor(R.color.UserChosing),
            getResources().getColor(R.color.BeeText),
            getResources().getColor(R.color.DateColor)
    };


    public CrtajObaveze(Context context) {
        super(context);
    }
    public CrtajObaveze(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CrtajObaveze(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    private void initClock(){

        oval = new RectF();
        height = getHeight();
        width = getWidth();
        int min = Math.min(height,width);
        radius = min/2;
        paint = new Paint();
        isInit=true;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!isInit){
            initClock();
        }
        if(listaTaskova!=null) {

            for(Task item:listaTaskova) {
                paint.setColor(boje[rand.nextInt(boje.length-1)]);
                float hour1 =item.GetTime().GetStartTime().GetHour();
                float minute1 = item.GetTime().GetStartTime().GetMinute();
                hour1 = hour1>12?hour1-12:hour1;
                float loc1 = (hour1+minute1/60);
                float angle1 = (float) ((Math.PI/6)*loc1-Math.PI/2);
                float hour2 =item.GetTime().GetEndTime().GetHour();
                hour2 = hour2>12?hour2-12:hour2;
                float minute2 =item.GetTime().GetEndTime().GetMinute();
                float loc2 = (hour2+minute2/60);
                float angle2 = (float) ((Math.PI/6)*loc2-Math.PI/2)-angle1;
                canvas.drawArc(80, 80, width - 80, height - 80, (float) (180*angle1/Math.PI), (float)(180*angle2/Math.PI) , true, paint);

            }

        }

    }
    public void drawLists(List<Task> listaTaskova){
        this.listaTaskova = listaTaskova;
    }
    public void Refreshuj(){
        postInvalidateDelayed(1000);
        invalidate();

    }

}
