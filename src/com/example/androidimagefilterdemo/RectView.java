package com.example.androidimagefilterdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

/**
 * 1/5/15  10:13 AM
 * Created by JustinZhang.
 */
public class RectView extends View {
    public interface OnRectSelectedListener{
        void onRectSelected(Rect r);
    }
    private static final String TAG = "RectView ";
    private Context mContext;
    private Drawable mRectDrawable;
    private int initialRectWidth = 175;
    private Rect mLeftTop;
    private Rect mLeftBottom;
    private Rect mRightTop;
    private Rect mRightBottom;

    private int mViewWidth;
    private int mViewHeight;
    private ScaleGestureDetector mScaleDetcor;

    private OnRectSelectedListener mSelectedListener;


    public RectView(Context context) {
        super(context);
        shareConstructor(context, null);
    }

    public RectView(Context context, AttributeSet attrs) {
        super(context, attrs);
        shareConstructor(context, attrs);
    }

    public RectView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        shareConstructor(context, attrs);
    }

    private void shareConstructor(Context context, AttributeSet attrs) {

        mLeftTop = new Rect();
        mLeftBottom = new Rect();
        mRightTop = new Rect();
        mRightBottom = new Rect();
        mContext = context;
        mRectDrawable = getResources().getDrawable(R.drawable.dot_rect);
        mScaleDetcor = new ScaleGestureDetector(context, mScaleGestureListener);
    }

    private boolean mScale = false;
    private ScaleGestureDetector.OnScaleGestureListener mScaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            //Log.e(TAG, "scale factor:" + detector.getScaleFactor() + "   focus X:" + detector.getFocusX() + "   focus Y:" + detector.getFocusY());
            detector.getCurrentSpanX();
            scaleRectangle(mSelectedBound, detector.getScaleFactor());
            invalidate();
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            mScale = true;
            return true;

        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mScale = false;
            if(mSelectedListener!=null){
                mSelectedListener.onRectSelected(mSelectedBound);
            }
        }
    };

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        mViewWidth = widthSize;
        mViewHeight = heightSize;
        int left = widthSize / 2 - initialRectWidth / 2;
        int right = widthSize / 2 + initialRectWidth / 2;
        int top = heightSize / 2 - initialRectWidth / 2;
        int bottom = heightSize / 2 + initialRectWidth / 2;

        // mRectDrawable.setBounds(left, top, right, bottom);
        mSelectedBound.left = left;
        mSelectedBound.right = right;
        mSelectedBound.top = top;
        mSelectedBound.bottom = bottom;
        reviseCorners(mSelectedBound);
        //Log.e(TAG, mRectDrawable.getBounds().toString() + "" + " widthSize:" + widthSize + " heightSize:" + heightSize);
    }

    private void reviseCorners(Rect mSelectedBound) {
        reviseLeftTopCorner(mSelectedBound);
        reviseLeftBottomCorner(mSelectedBound);
        reviseRightTopCorner(mSelectedBound);
        reviseRightBottomCorner(mSelectedBound);
    }

    private void reviseRightBottomCorner(Rect mSelectedBound) {
        int x = mSelectedBound.right;
        int y = mSelectedBound.bottom;
        makeCorner(new Point(x, y), mRightBottom);
    }

    private void reviseRightTopCorner(Rect mSelectedBound) {
        int x = mSelectedBound.right;
        int y = mSelectedBound.top;
        makeCorner(new Point(x, y), mRightTop);
    }

    private void reviseLeftBottomCorner(Rect mSelectedBound) {
        int x = mSelectedBound.left;
        int y = mSelectedBound.bottom;
        makeCorner(new Point(x, y), mLeftBottom);
    }

    private void reviseLeftTopCorner(Rect mSelectedBound) {
        int x = mSelectedBound.left;
        int y = mSelectedBound.top;
        makeCorner(new Point(x, y), mLeftTop);
    }

    private void makeCorner(Point point, Rect rect) {
        rect.left = point.x - 30;
        rect.right = point.x + 30;
        rect.top = point.y - 30;
        rect.bottom = point.y + 30;
    }

    private Rect mSelectedBound = new Rect();

    private int mDownX;
    private int mDownY;


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getPointerCount() == 2) {
            //两个手指操作
            mScaleDetcor.onTouchEvent(event);
            return true;
        }
        if (!mScale) {

            //Log.e(TAG, "onTouchEvent");
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mDownX = float2int(event.getX());
                    mDownY = float2int(event.getY());
                    break;
                case MotionEvent.ACTION_MOVE:
                    if (mLeftTop.contains(mDownX, mDownY)) {
                        moveLeftTop(event.getX(), event.getY());

                    } else if (mLeftBottom.contains(mDownX, mDownY)) {
                        moveLeftBottom(event.getX(), event.getY());

                    } else if (mRightTop.contains(mDownX, mDownY)) {
                        moveRightTop(event.getX(), event.getY());

                    } else if (mRightBottom.contains(mDownX, mDownY)) {
                        moveRightBottom(event.getX(), event.getY());

                    } else if (mSelectedBound.contains(mDownX, mDownY)) {
                        float deltaX = event.getX() - mDownX;
                        float deltaY = event.getY() - mDownY;
                        moveSelectedBound(deltaX, deltaY);
                    }
                    mDownX = float2int(event.getX());
                    mDownY = float2int(event.getY());
                    reviseCorners(mSelectedBound);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    if(mSelectedListener!=null){
                        mSelectedListener.onRectSelected(mSelectedBound);
                    }
                    break;
            }
        }
        return true;
    }

    private void scaleRectangle(Rect rect, float factor) {

        float deltaWidth = rect.width() * (factor - 1) / 2;
        float deltaHeight = rect.height() * (factor - 1) / 2;
        //if(factor > 1 ){
        //扩大
        //左边-
        rect.left -= deltaWidth;
        // 上 -
        rect.top -= deltaHeight;
        //右+
        rect.right += deltaWidth;
        //下 +
        rect.bottom += deltaHeight;
        //越界恢复
        if (rect.width() < initialRectWidth || rect.height() < initialRectWidth||rect.left<=0||rect.top<=0||rect.right>=mViewWidth||rect.bottom>=mViewHeight) {
            rect.left += deltaWidth;
            // 上 -
            rect.top += deltaHeight;
            //右+
            rect.right -= deltaWidth;
            //下 +
            rect.bottom -= deltaHeight;


        }
    }

    private void moveSelectedBound(float x, float y) {

        mSelectedBound.offset(float2int(x), float2int(y));
        //Log.e(TAG, "------>" + mSelectedBound.right);
        if (mSelectedBound.left < 0 || mSelectedBound.right > mViewWidth || mSelectedBound.top < 0 || mSelectedBound.bottom > mViewHeight) {
            // mSelectedBound = tempRect;
            mSelectedBound.offset(-float2int(x), -float2int(y));
        }
    }

    //右下
    private void moveRightBottom(float x, float y) {
        //x>0 x< viewWidth x与左下的距离大于最小距离
        if (x > 0 && x < mViewWidth && x - mSelectedBound.left > initialRectWidth) {
            mSelectedBound.right = float2int(x);
        }
        //y >0 y < viewHeight y与右上的距离大于最小距离
        if (y > 0 && y < mViewHeight && y - mSelectedBound.top > initialRectWidth) {
            mSelectedBound.bottom = float2int(y);
        }
    }

    //右上
    private void moveRightTop(float x, float y) {
        //x > 0 x <viewWidth 与左上的距离大于最小距离
        if (x > 0 && x < mViewWidth && x - mSelectedBound.left > initialRectWidth) {
            mSelectedBound.right = float2int(x);
        }
        //y > 0 y < viewHeight 与右下的距离大于最小距离
        if (y > 0 && y < mViewHeight && mSelectedBound.bottom - y > initialRectWidth) {
            mSelectedBound.top = float2int(y);
        }
    }

    //左下
    private void moveLeftBottom(float x, float y) {
        //x > 0 ,与右下的距离大于最小距离，x< viewWidth
        if (mSelectedBound.right - x > initialRectWidth && x > 0 && x < mViewWidth) {
            mSelectedBound.left = float2int(x);
        }
        //y>0 y<viewHeight 与左上的距离大于最小距离
        if (y > 0 && y < mViewHeight && y - mSelectedBound.top > initialRectWidth)
            mSelectedBound.bottom = float2int(y);
    }

    //左上点
    private void moveLeftTop(float x, float y) {

        //x大于0 ，x与右上距离大于最小距离，x小于viewWidth
        if (mSelectedBound.right - x > initialRectWidth && x > 0 && x < mViewWidth) {
            mSelectedBound.left = float2int(x);
        }
        //y大于0 ， y与左下点距离大于最小距离，y小于viewHeight
        if (mSelectedBound.bottom - y > initialRectWidth && y > 0 && y < mViewHeight) {
            mSelectedBound.top = float2int(y);
        }

    }

    public static int float2int(float f) {
        return (int) Math.floor(f);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //Log.e(TAG, mSelectedBound + "");
        mRectDrawable.setBounds(mSelectedBound);
        mRectDrawable.draw(canvas);
        super.onDraw(canvas);
    }

    public void setmSelectedListener(OnRectSelectedListener mSelectedListener) {
        this.mSelectedListener = mSelectedListener;
        mSelectedListener.onRectSelected(mSelectedBound);
    }
}
