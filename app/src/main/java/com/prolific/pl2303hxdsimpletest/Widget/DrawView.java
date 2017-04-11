package com.prolific.pl2303hxdsimpletest.Widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.view.View;

/**
 * Created by asus on 2015/8/3.
 */
public  class DrawView extends View {




    public DrawView(Context context){super(context);}
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);




        canvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1000);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.YELLOW);

        Shader shader = new RadialGradient(0,0,135, Color.TRANSPARENT, Color.YELLOW, Shader.TileMode.MIRROR);
        paint.setShader(shader);
        canvas.drawLine(0, 180, 2000, 180, paint);
        //canvas.drawRect(0,0,300,250,paint);
    }

}
