package android.coolweather.com.viewtwo;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by Mao on 2018/2/8.
 * 自定义View：圆环交替效果
 */

public class CustomView extends View {
    //第一圈颜色
    private int mFirstColor;
    //第二圈颜色
    private int mSecondColor;
    //圆环宽度
    private int mCircleWidth;
    //速度
    private int mSpeed;
    //进度
    private int progress;
    //是否改变颜色
    private boolean isChange=false;
    //画笔
    private Paint mPaint;

    public CustomView(Context context) {
        this(context,null);
    }

    public CustomView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint=new Paint();
        /*
        * 获取自定义的属性值
        * */
        TypedArray typedArray=context.getTheme().obtainStyledAttributes(attrs,R.styleable.CustomView,defStyleAttr,0);
        int count=typedArray.getIndexCount();
        for (int i=0;i<count;i++){
            int attr=typedArray.getIndex(i);
            switch (attr){
                case R.styleable.CustomView_firstColor:
                    mFirstColor=typedArray.getColor(attr, Color.GRAY);
                    break;
                case R.styleable.CustomView_secondColor:
                    mSecondColor=typedArray.getColor(attr,Color.BLUE);
                    break;
                case R.styleable.CustomView_circleWidth:
                    mCircleWidth=typedArray.getDimensionPixelSize(attr,
                            (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,24,getResources().getDisplayMetrics()));
                    break;
                case R.styleable.CustomView_speed:
                    mSpeed=typedArray.getInt(attr,20);
                    break;
            }
            /*
            * 创建子线程改变进度值
            * */
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        progress++;
                        Log.d("MVE","progress:"+progress);
                        if (progress==360){
                            isChange=!isChange;
                            progress=0;
                        }
                        /*
                        * Android提供了Invalidate方法实现界面刷新，但是Invalidate不能直接在线程中调用
                        * 使用postInvalidate可以直接在工作线程中更新界面
                        * */
                        postInvalidate();
                        try {
                            Thread.sleep(mSpeed);
                            Log.d("MVE","mSpeed:"+mSpeed);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int circleX=getWidth()/2;//圆心的X坐标
        int circleR=circleX-mCircleWidth/2;//半径
        /*
        * 圆弧的外切矩形，定义了圆弧的形状和大小的边界
        * */
        RectF rectF=new RectF(circleX-circleR,circleX-circleR,circleX+circleR,circleX+circleR);
        //去锯齿
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mCircleWidth);
        /*
        * 绘制弧：drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint)
        * oval:指定圆弧的外轮廓矩形区域
        * startAngle:圆弧起始角度，单位为度
        * weepAngle:圆弧扫过的角度，顺时针方向，单位为度
        * useCenter:如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形
        * paint:绘制风格
        * */
        if (isChange){
            mPaint.setColor(mFirstColor);
            canvas.drawArc(rectF,-90,360,false,mPaint);
            mPaint.setColor(mSecondColor);
            canvas.drawArc(rectF,-90,progress,false,mPaint);
        }else {
            mPaint.setColor(mSecondColor);
            canvas.drawArc(rectF,-90,360,false,mPaint);
            mPaint.setColor(mFirstColor);
            canvas.drawArc(rectF,-90,progress,false,mPaint);
        }
    }
}
