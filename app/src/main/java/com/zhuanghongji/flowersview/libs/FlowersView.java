package com.zhuanghongji.flowersview.libs;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * 自定义 View : 繁华曲线
 *
 * @author zhuanghongji
 */
public class FlowersView extends View {

    private static final String TAG = "FlowersView";

    private static final float COMPLETE_DEVIATION = 10F;

    /** 画笔：辅助线 */
    private Paint mAssistPaint;
    /** 画笔：繁花曲线 */
    private Paint mTargetPaint;

    /** 繁花曲线的绘制路径 */
    private Path mTargetPath;

    /** 绘制区域的边长 */
    private int mSideLength;

    private float mRadian;
    private Point mPointFirstDraw;

    private float mRadiusA;
    private Point mPointA;

    private float mRadiusB;
    private Point mPointB;

    private float mRadiusC;
    private Point mPointC;

    private OnCompleteListener mOnCompleteListener;

    public FlowersView(Context context) {
        this(context, null);
    }

    public FlowersView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowersView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        initPoint();
        mTargetPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mSideLength = getMeasuredWidth();
        setMeasuredDimension(mSideLength, mSideLength);

        // （不变值）大圆半径为画布边长 1/4, 圆心在正中间
        mRadiusA = mSideLength / 2;
        mPointA.x = mSideLength / 2;
        mPointA.y = mSideLength / 2;

        // （默认值）小圆半径是边长 1/4, 圆心 x 轴是边长 1/4, y 轴是画布边长 1/2
        changeRadiusB(mSideLength / 4);

        mRadiusC = mSideLength / 8;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawTargetPath(canvas);
        drawCircleA(canvas);
        drawCircleB(canvas);
        drawLine(canvas);
    }

    private void drawTargetPath(Canvas canvas) {
        mTargetPath.reset();
        boolean isHaveMoved = false;

        double radian = 0;
        while (radian < mRadian) {
            radian += 0.01;
            Point tempPointC = getPointInChildCircle(mPointA,
                    mRadiusA, mRadiusB, mRadiusC, radian);

            if (!isHaveMoved) {
                mTargetPath.moveTo(tempPointC.getX(), tempPointC.getY());
                isHaveMoved = true;
            } else {
                mTargetPath.lineTo(tempPointC.getX(), tempPointC.getY());
            }
        }
        canvas.drawPath(mTargetPath, mTargetPaint);
    }

    private void drawLine(Canvas canvas) {
        if (mPointC.x == 0 && mPointC.y == 0) {
            return;
        }
        canvas.drawLine(mPointB.x, mPointB.y,
                mPointC.x, mPointC.y, mAssistPaint);

        mAssistPaint.setStrokeWidth(16);

        canvas.drawPoint(mPointC.x, mPointC.y, mAssistPaint);
        mAssistPaint.setStrokeWidth(2);
    }

    private void drawCircleB(Canvas canvas) {
        canvas.drawCircle(mPointB.x, mPointB.y, mRadiusB, mAssistPaint);
    }

    private void drawCircleA(Canvas canvas) {
        canvas.drawCircle(mPointA.x, mPointA.y, mRadiusA, mAssistPaint);
    }

    private void setupPointC() {
        if (mRadian == 0) {
            return;
        }
        mPointC = getPointInChildCircle(mPointA, mRadiusA, mRadiusB, mRadiusC, mRadian);
    }

    private void setupPointB() {
        mPointB = getPointInCircle(mPointA, mRadiusA - mRadiusB, mRadian);
    }

    private void initPoint() {
        mPointFirstDraw = new Point();

        mPointA = new Point();
        mPointB = new Point();
        mPointC = new Point();
    }

    private void initPaint() {
        mAssistPaint = new Paint();
        mAssistPaint.setColor(Color.BLUE);
        mAssistPaint.setStyle(Paint.Style.STROKE);
        mAssistPaint.setStrokeWidth(2);
        mAssistPaint.setStrokeCap(Paint.Cap.ROUND);
        mAssistPaint.setAntiAlias(true);

        mTargetPaint = new Paint();
        mTargetPaint.setColor(Color.RED);
        mTargetPaint.setStyle(Paint.Style.STROKE);
        mTargetPaint.setStrokeWidth(6);
        mTargetPaint.setAntiAlias(true);
    }

    public int getSideLength() {
        return mSideLength;
    }

    /**
     * 已知圆的圆心和半径，获取某弧度对应的圆上点的坐标
     * @param center 圆心
     * @param radius 半径
     * @param radian 弧度
     * @return 圆上点的坐标
     */
    private Point getPointInCircle(Point center, double radius, double radian) {
        float x = (float) (center.x + radius * Math.cos(radian));
        float y = (float) (center.y - radius * Math.sin(radian));
        return new Point(x, y);
    }

    private Point getPointInChildCircle(Point centerA, double radiusA,
                                        double radiusB, double radiusC, double radian) {
        Point centerB = getPointInCircle(centerA, radiusA - radiusB, radian);
        double radianC = 2.0 * Math.PI - ((radiusA / radiusB * radian) % (2.0 * Math.PI));
        return getPointInCircle(centerB, radiusC, radianC);
    }

    public void changeRadiusB(int radiusB) {
        mRadiusB = radiusB;
        setupPointB();
        setupPointC();

        mPointFirstDraw = getPointInChildCircle(mPointA, mRadiusA, mRadiusB, mRadiusC, 0.01);
    }

    public void changeRadiusC(int radiusC) {
        mRadiusC = radiusC;
        setupPointB();
        setupPointC();

        mPointFirstDraw = getPointInChildCircle(mPointA, mRadiusA, mRadiusB, mRadiusC, 0.01);
    }

    public void changeRadian(double radian) {
        mRadian = (float) radian;
        Log.d(TAG, "mRadian = " + mRadian);
        setupPointB();
        setupPointC();

        if (Math.abs(mPointFirstDraw.x - mPointC.x) < COMPLETE_DEVIATION
                && Math.abs(mPointFirstDraw.y - mPointC.y) < COMPLETE_DEVIATION
                && radian > 10) {
            if (mOnCompleteListener != null) {
                mOnCompleteListener.onComplete();
            }
        }
    }

    public void setOnCompleteListener(OnCompleteListener onCompleteListener) {
        mOnCompleteListener = onCompleteListener;
    }

}
