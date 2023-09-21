package com.example.speakcalproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.Calendar;

public class CircularProgressBar extends View {
    private Paint paint;
    private Paint mTextPaint;
    private RectF oval;
    private float progress = 0f;
    private String progressText = "";
    private int textSize = 100;
    public CircularProgressBar(Context context) {
        super(context);
        init();
    }

    public CircularProgressBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public CircularProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setStyle((Paint.Style.STROKE));
        paint.setStrokeWidth(100);
        paint.setAntiAlias(true);
        oval = new RectF();
        mTextPaint = new Paint();
        mTextPaint.setColor(Color.BLACK);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
    }

    public void setProgress(float progress, String progressText) {
        this.progress = progress;
        this.progressText = progressText;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width,height);
        int padding = 50;
        int stroke = 100;

        oval.set(padding+(stroke/2),padding+(stroke/2),size-padding-(stroke/2),size-padding-(stroke/2));
        float angle = 360*progress/100;
        paint.setColor(Color.GRAY);
        canvas.drawArc(oval,135,275,false,paint);

        if(angle <= 275) {
            paint.setColor(Color.BLACK);
            canvas.drawArc(oval, 135, angle, false, paint);
        } else {
            paint.setColor(Color.RED);
            canvas.drawArc(oval, 135, 275, false, paint);
        }

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH)+1;
        String dateTime = String.format("%04d-%02d-%02d", year, month, day);

        canvas.drawText(progressText+" Kcal",width/2,(height-mTextPaint.ascent())/2,mTextPaint);
        canvas.drawText(dateTime,width/2,(height-mTextPaint.ascent())/2+textSize,mTextPaint);






    }
}
