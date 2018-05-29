package ltu.course.mobile.project.greenerfootballcup.utilities.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView  extends View {

    private Paint mPaint;
    private Path mPath;
    private boolean clearCanvas;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);;
        mPaint.setStrokeWidth(5);
        mPath = new Path();
        clearCanvas = false;
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(clearCanvas){
            mPath.reset();
            clearCanvas = false;
        }
        canvas.drawPath(mPath, mPaint);
        super.onDraw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                mPath.moveTo(event.getX(), event.getY());
                break;

            case MotionEvent.ACTION_MOVE:
                mPath.lineTo(event.getX(), event.getY());
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                break;
        }

        return true;
    }

    public void clearDrawing() {
        clearCanvas = true;
        invalidate();
    }

    public Bitmap getBitmap() throws NullSignatureException {
        if(mPath.isEmpty()){
            throw new NullSignatureException();
        }else{
            this.setDrawingCacheEnabled(true);
            this.buildDrawingCache();
            Bitmap bmp = Bitmap.createBitmap(this.getDrawingCache());
            this.setDrawingCacheEnabled(false);
            return bmp;
        }
    }

    public class NullSignatureException extends Exception{
        public NullSignatureException(){
            super("NullSignatureException");
        }
    }

}
