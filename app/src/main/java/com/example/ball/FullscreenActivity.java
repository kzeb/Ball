package com.example.ball;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import java.util.Random;


public class FullscreenActivity extends Activity implements SensorEventListener {
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private AnimatedView mAnimatedView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mAnimatedView = new AnimatedView(this);
        setContentView(mAnimatedView);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) { }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            mAnimatedView.onSensorEvent(event);
        }
    }

    public class AnimatedView extends View {
        private static final int CIRCLE_RADIUS = 100;
        private Paint mPaint;
        private Paint mPaint2;
        private Paint textPaint;
        private int x;
        private int y;
        private int viewWidth;
        private int viewHeight;
        private int modifierX=0;
        private int modifierY=0;
        private int score = 0;
        private int start = 11;
        private int stop = 5;

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while (!thread.isInterrupted()) {
                        Thread.sleep(2000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                               modifierX = new Random().nextInt(start)-stop;
                               modifierY = new Random().nextInt(start)-stop;
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        public AnimatedView(Context context) {
            super(context);
            mPaint = new Paint();
            mPaint.setColor(Color.rgb(0, 153, 255));
            mPaint2 = new Paint();
             mPaint2.setColor(Color.RED);
            textPaint = new Paint();
            textPaint.setColor(Color.WHITE);
            textPaint.setTextSize(100);
            thread.start();
        }

        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            viewWidth = w;
            viewHeight = h;
        }

        public void onSensorEvent (SensorEvent event) {
            if (score%100==1){
                start+=5;
                stop+=5;
            }
            if (score == 0){
                start = 11;
                stop = 5;
            }
            x = x - (int) event.values[0] * 8 + modifierX;
            y = y + (int) event.values[1] * 8 + modifierY;

            if (x <= CIRCLE_RADIUS) {
                x = CIRCLE_RADIUS;
                score-=10;
            }
            if (x >= viewWidth - CIRCLE_RADIUS) {
                x = viewWidth - CIRCLE_RADIUS;
                score-=10;
            }
            if (y <= CIRCLE_RADIUS) {
                y = CIRCLE_RADIUS;
                score-=10;
            }
            if (y >= viewHeight - CIRCLE_RADIUS) {
                y = viewHeight - CIRCLE_RADIUS;
                score-=10;
            }

            if (score<0){
                score = 0;
            }

            if((viewWidth/2)-100<=x&&(viewWidth/2)+100>=x&&(viewHeight/2)-100<=y&&(viewHeight/2)+100>=y){
                mPaint2.setColor(Color.rgb(0, 204, 0));
                score++;
            }
            else{ mPaint2.setColor(Color.RED); }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.drawCircle(viewWidth/2, viewHeight/2, CIRCLE_RADIUS*2, mPaint2);
            canvas.drawCircle(x, y, CIRCLE_RADIUS, mPaint);
            canvas.drawText("Score: " + score, 100, 200, textPaint);
            invalidate();
        }
    }
}