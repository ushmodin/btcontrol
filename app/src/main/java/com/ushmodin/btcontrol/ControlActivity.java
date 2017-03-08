package com.ushmodin.btcontrol;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

public class ControlActivity extends AppCompatActivity {
    private Point point;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new Draw(this));
    }

    class Draw extends View {
        public Draw(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            int height = getHeight();
            int width = getWidth();
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.GRAY);
            paint.setStrokeWidth(2);
            canvas.drawLine(width / 2, 0, width / 2, height, paint);
            canvas.drawLine(0, height / 2, width, height / 2, paint);

            Point point = ControlActivity.this.point;
            if (point != null) {
                Paint paint1 = new Paint();
                paint1.setColor(Color.RED);
                canvas.drawCircle(point.x, point.y, 50, paint1);
            }
            super.onDraw(canvas);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            int centerX = getWidth() / 2;
            int centerY = getHeight() / 2;
            float driveX = (x - centerX) / centerX;
            float driveY = (centerY - y) / centerY;
            ControlService.drive(driveX, driveY);
//            Log.d("CURSOR", "X = " + x + " Y = " + y);
//            Log.d("CURSOR", "centerX = " + centerX + " centerY = " + centerY);
//            Log.d("CURSOR", "driveX = " + driveX + " driveY = " + driveY);
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    point = new Point();
                    point.set((int)x, (int)y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:
                    point.set((int)x, (int)y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    point = null;
                    ControlService.stop();
                    invalidate();
                    break;
            }
            return true;
        }

    }
}
