package com.cvetici.beeorganised;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.shapes.ArcShape;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CrtajObaveze extends View {
    private int height,width=0;
    private int radius=0;
    private Paint paint,paint1,bela;
    private boolean isInit=false;
    private RectF oval;
    private List<Task> listaTaskova;
    Random rand = new Random();
    private boolean dan=true;
    private RectF osnovaKruga;
    private boolean IsTouched;
    private float touchAngle;
    private TextView taskName,startingTime,endingTime;
    private RelativeLayout taskChangerLayout;
    private Animation animacijaOtvaranja,animacijaZatvaranja;
    Context context;
    private ImageButton bin;
    private int[] boje = {
            getResources().getColor(R.color.UserChosing)
    };
    private View WorkerActivity;
    Dialog dialogdel;


    public CrtajObaveze(Context context) {
        super(context);
        this.context=context;
    }
    public CrtajObaveze(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
    }

    public CrtajObaveze(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;



    }
    private void initClock(){
        dialogdel = new Dialog(context);
        taskName = ((Activity)context).findViewById(R.id.imeTaska);
        startingTime=((Activity)context).findViewById(R.id.startingTime);
        endingTime = ((Activity)context).findViewById(R.id.endingTime);
        height = getHeight();
        width = getWidth();
        int min = Math.min(height,width);
        radius = min/2;
        paint = new Paint();
        paint1 = new Paint();
        bela = new Paint();
        bela.setColor(getResources().getColor(R.color.ProzirnaDate));
        taskChangerLayout = ((Activity)context).findViewById(R.id.changeTask);
        isInit=true;
        osnovaKruga = new RectF(80, 80, width - 80, height - 80);
        IsTouched = false;
        animacijaOtvaranja = AnimationUtils.loadAnimation(context,R.anim.polako_pojavi);
        animacijaZatvaranja = AnimationUtils.loadAnimation(context,R.anim.polako_zatvori);
        bin = (ImageButton) ((Activity)context).findViewById(R.id.delete);
        opendelete();

    }

    private void opendelete(){
        bin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogdel.setContentView(R.layout.deletetask);
                dialogdel.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialogdel.show();

            }
        });

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(!isInit){
            initClock();
        }
        if(listaTaskova!=null) {

            for(Task item:listaTaskova) {
                paint.setColor(boje[0]);
                paint1.setColor(Color.RED);
                float hour1 =item.GetTime().GetStartTime().GetHour();
                float minute1 = item.GetTime().GetStartTime().GetMinute();
                float hour2 =item.GetTime().GetEndTime().GetHour();
                float minute2 =item.GetTime().GetEndTime().GetMinute();
                float loc1,loc2;
                loc1 = (hour1+minute1/60);
                loc2 = (hour2+minute2/60);
                if (dan == true && (hour1 >= 12 && hour2 >= 12)) {
                    hour1 = hour1 -12;
                    hour2 = hour2 -12;
                    loc1 = (hour1+minute1/60);
                    loc2 = (hour2+minute2/60);
                    float angle1 = (float) ((Math.PI / 6) * loc1 - Math.PI / 2);
                    float angle2 = (float) ((Math.PI / 6) * loc2 - Math.PI / 2) - angle1;
                    angle2 = (float) (180 * angle2 / Math.PI);
                    angle1 = (float) (180 * angle1 / Math.PI);
                    canvas.drawArc(osnovaKruga, angle1, angle2 , true, paint);
                    canvas.drawArc(osnovaKruga,angle1,1,true,bela);
                    canvas.drawArc(osnovaKruga,angle2+angle1-1,1,true,bela);
                    clickListener(angle1,angle2,canvas,item);


                }if(loc1 < 12 && loc2 >= 12){
                    proveriPrelom(loc1,hour2-12,minute2,canvas,item);

                }
                if (dan == false && loc1 < 12 && loc2 < 12) {
                    float angle1 = (float) ((Math.PI / 6) * loc1 - Math.PI / 2);
                    float angle2 = (float) ((Math.PI / 6) * loc2 - Math.PI / 2) - angle1;
                    angle2 = (float) (180 * angle2 / Math.PI);
                    angle1 = (float) (180 * angle1 / Math.PI);
                    canvas.drawArc(osnovaKruga, angle1 , angle2 , true, paint);
                    canvas.drawArc(osnovaKruga,angle1,1,true,bela);
                    canvas.drawArc(osnovaKruga,angle2+angle1-1,1,true,bela);
                    clickListener(angle1,angle2,canvas,item);

                }
                if(IsTouched&&taskChangerLayout.getVisibility()==VISIBLE){
                    taskChangerLayout.startAnimation(animacijaZatvaranja);
                    taskChangerLayout.setVisibility(INVISIBLE);

                }

            }

        }

    }

    private void proveriPrelom(float loc1,float hour3,float minute3,Canvas canvas,Task item) {
        if(dan==true){
            float angle1 = (float) (-Math.PI / 2);
            float loc3 = (hour3+minute3/60);
            float angle3 = (float) ((Math.PI / 6) * loc3 - Math.PI / 2)-angle1;
            canvas.drawArc(osnovaKruga, (float) (180 * angle1 / Math.PI), (float) (180 * angle3 / Math.PI), true, paint);
            canvas.drawArc(osnovaKruga, (float) (180 * (angle3+angle1) / Math.PI)-1, 1f, true, bela);
            clickListener((float)(180 * angle1 / Math.PI),(float)(180 * angle3 / Math.PI),canvas,item);
        }else {
            float angle1 = (float) ((Math.PI / 6) * loc1 - Math.PI / 2);
            float angle3 = (float) ((Math.PI / 6) * 11.99f - Math.PI / 2)-angle1;
            canvas.drawArc(osnovaKruga, (float) (180 * angle1 / Math.PI), (float) (180 * angle3 / Math.PI), true, paint);
            canvas.drawArc(osnovaKruga, (float) (180 * angle1 / Math.PI), (float) 1, true, bela);
            clickListener((float)(180 * angle1 / Math.PI),(float)(180 * angle3 / Math.PI),canvas,item);
        }
    }

    public void drawLists(List<Task> listaTaskova){
        this.listaTaskova = listaTaskova;
    }
    public void Refreshuj(){
        invalidate();

    }
    public void CrtajDan(boolean dan){
        this.dan = dan;
        Refreshuj();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX,touchY;
        if(event.getAction()==MotionEvent.ACTION_DOWN){
               IsTouched = true;
                touchX = event.getX()-width/2;
                touchY = event.getY()-height/2;
                 touchAngle= (float) Math.toDegrees(Math.atan2(touchY,touchX)+Math.PI/2);
                 touchAngle-=90;
                if (touchAngle < 0) {
                    touchAngle += 360;
                }

                invalidate();
        }
        return super.onTouchEvent(event);
    }


    public void clickListener(float angle1, float angle2,Canvas canvas,Task current){
        if(angle1<0){
            angle1+=360;
        }
        if(angle2<0){
            angle2+=360;
        }
        if(IsTouched&&angle1<=touchAngle&&touchAngle<=angle2+angle1 ){
            IsTouched = false;
            canvas.drawArc(osnovaKruga,(float) angle1, (float) angle2,true,paint1);
            taskName.setText(current.GetTitle());
            startingTime.setText(current.GetTime().GetStartTime().ToStringTime());
            endingTime.setText(current.GetTime().GetEndTime().ToStringTime());

            changeTaskColor();
        }


    }

    public void changeTaskColor(){
        if(taskChangerLayout.getVisibility()==INVISIBLE) {
            taskChangerLayout.setVisibility(VISIBLE);
            taskChangerLayout.startAnimation(animacijaOtvaranja);
        }
    }
}
