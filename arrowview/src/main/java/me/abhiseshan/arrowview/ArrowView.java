package me.abhiseshan.arrowview;

import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

public class ArrowView extends View {
    /**
     * Flag to denote the "downArrow" configuration
     */
    public static final int FLAG_STATE_DOWN = 0;
    /**
     * Flag to denote the "upArrow" configuration
     */
    public static final int FLAG_STATE_UP = 1;

    //Default Spin
    public static final int SPIN_OUTWARD = 0;

    public static final int SPIN_INWARD = 1;

    public static int spinType = 0;

    private float ARC_TOP_START = 225;
    private float ARC_TOP_ANGLE = 45f;
    private float ARC_BOTTOM_START = 90f;
    private float ARC_BOTTOM_ANGLE = 45f;
    private float ARC_LEFT_START = 315f;
    private float ARC_LEFT_ANGLE = -45f;
    private float ARC_RIGHT_ANGLE = -45f;
    private float ARC_RIGHT_START = 90f;

    private static final long ANIMATION_DURATION_MS = 300l;

    private static final int DEFAULT_COLOR = Color.BLACK;
    private float DEFAULT_STROKE_WIDTH = 8f;

    // Arcs that define the set of all points between which the two lines are drawn
    // Names (top, bottom, etc) are from the reference point of the "downArrow" configuration.
    private Path mArcTop;
    private Path mArcBottom;
    private Path mArcLeft;
    private Path mArcRight;

    // Pre-compute arc lengths when layout changes
    private float mArcLengthTop;
    private float mArcLengthBottom;
    private float mArcLengthLeft;
    private float mArcLengthRight;

    private Paint mPaint;
    private int mColor = DEFAULT_COLOR;
    private RectF mRect;
    private PathMeasure mPathMeasure;

    private float[] mFromXY;
    private float[] mToXY;

    /**
     * Internal state flag for the drawn appearance, downArrow or upArrow.
     * The default starting position is "downArrow". This represents the real configuration, whereas
     * {@code mPercent} holds the frame-by-frame position when animating between
     * the states.
     */
    private int mState = FLAG_STATE_DOWN;

    /**
     * The percent value upon the arcs that line endpoints should be found
     * when drawing.
     */
    private float mPercent = 1f;


    public ArrowView(Context context) {
        super(context);
    }

    public ArrowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        readXmlAttributes(context, attrs);
    }

    public ArrowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        readXmlAttributes(context, attrs);
    }

    private void readXmlAttributes(Context context, AttributeSet attrs) {
        // Size will be used for width and height of the icon, downArrow the space in between
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.ArrowView, 0, 0);
        try {
            mColor = a.getColor(R.styleable.ArrowView_av_lineColor, DEFAULT_COLOR);
            if (a.getString(R.styleable.ArrowView_av_spin).equals("inward")) {
                setSpin(SPIN_INWARD);
            }
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        setPointFromPercent(mArcTop, mArcLengthTop, mPercent, mFromXY);
        setPointFromPercent(mArcBottom, mArcLengthBottom, mPercent, mToXY);

        canvas.drawLine(mFromXY[0], mFromXY[1], mToXY[0], mToXY[1], mPaint);

        setPointFromPercent(mArcLeft, mArcLengthLeft, mPercent, mFromXY);
        setPointFromPercent(mArcRight, mArcLengthRight, mPercent, mToXY);

        canvas.drawLine(mFromXY[0], mFromXY[1], mToXY[0], mToXY[1], mPaint);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (changed) {
            init();
            invalidate();
        }
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable parcelable = super.onSaveInstanceState();
        if (parcelable == null) {
            parcelable = new Bundle();
        }

        ArrowViewState savedState = new ArrowViewState(parcelable);
        savedState.flagState = mState;
        return savedState;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if(!(state instanceof ArrowViewState)) {
            super.onRestoreInstanceState(state);
            return;
        }

        ArrowViewState ss = (ArrowViewState)state;
        mState = ss.flagState;
        if (mState != FLAG_STATE_DOWN && mState != FLAG_STATE_UP) {
            mState = FLAG_STATE_DOWN;
        }

        super.onRestoreInstanceState(ss.getSuperState());
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom);
        init();
    }

    @Override
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void setPaddingRelative(int start, int top, int end, int bottom) {
        super.setPaddingRelative(start, top, end, bottom);
        init();
    }

    public void setColor(int argb) {
        mColor = argb;
        if (mPaint == null) {
            mPaint = new Paint();
        }
        mPaint.setColor(argb);
        invalidate();
    }

    public void setSpin(int spin){
        if (spin == SPIN_OUTWARD){
            Log.d("outward spin", "outward spin");
            ARC_TOP_START = 225f;
            ARC_TOP_ANGLE = 45f;
            ARC_BOTTOM_START = 90f;
            ARC_BOTTOM_ANGLE = 45f;
            ARC_LEFT_START = 315f;
            ARC_LEFT_ANGLE = -45f; // sweep backwards
            ARC_RIGHT_START = 90f;
            ARC_RIGHT_ANGLE = -45f; // sweep backwards
            spinType = SPIN_OUTWARD;
            init();
        }
        else if (spin == SPIN_INWARD){
            Log.d("Inward spin", "Inward Spin");
            ARC_TOP_START = 225f;
            ARC_TOP_ANGLE = -90f;
            ARC_BOTTOM_START = 90f;
            ARC_BOTTOM_ANGLE =-180f;
            ARC_LEFT_START = 315f;
            ARC_LEFT_ANGLE = 90f; // sweep backwards
            ARC_RIGHT_START = 90f;
            ARC_RIGHT_ANGLE = 180f; // sweep backwards
            spinType = SPIN_INWARD;
            init();
        }
        else {
            String error = "SPIN TYPE NOT VALID.";
            throw new Error(error);
        }
    }

    public int getSpin(){
        return spinType;
    }

    public void setStroke(float stroke){
        DEFAULT_STROKE_WIDTH = stroke;
    }

    public float getStroke(){
        return DEFAULT_STROKE_WIDTH;
    }
    
    /**
     * Tell this view to switch states from upArrow to downArrow, or back, using the default animation duration.
     * @return an integer flag that represents the new state after toggling.
     *         This will be either {@link #FLAG_STATE_DOWN} or {@link #FLAG_STATE_UP}
     */
    public int toggle() {
        return toggle(ANIMATION_DURATION_MS);
    }

    /**
     * Tell this view to switch states from upArrow to downArrow, or back.
     * @param animationDurationMS duration in milliseconds for the toggle animation
     * @return an integer flag that represents the new state after toggling.
     *         This will be either {@link #FLAG_STATE_DOWN} or {@link #FLAG_STATE_UP}
     */
    public int toggle(long animationDurationMS) {
        mState = mState == FLAG_STATE_DOWN ? FLAG_STATE_UP : FLAG_STATE_DOWN;
        // invert percent, because state was just flipped
        mPercent = 1 - mPercent;
        ValueAnimator animator = ValueAnimator.ofFloat(mPercent, 1);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(animationDurationMS);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                setPercent(animation.getAnimatedFraction());
            }
        });

        animator.start();
        return mState;
    }

    public void upArrow() {
        upArrow(ANIMATION_DURATION_MS);
    }

    public void upArrow(long animationDurationMS) {
        if (mState == FLAG_STATE_UP) {
            return;
        }
        toggle(animationDurationMS);
    }

    public void downArrow() {
        downArrow(ANIMATION_DURATION_MS);
    }

    public void downArrow(long animationDurationMS) {
        if (mState == FLAG_STATE_DOWN) {
            return;
        }
        toggle(animationDurationMS);
    }

    private void setPercent(float percent) {
        mPercent = percent;
        invalidate();
    }

    /**
     * Perform measurements and pre-calculations.  This should be called any time
     * the view measurements or visuals are changed, such as with a call to {@link #setPadding(int, int, int, int)}
     * or an operating system callback like {@link #onLayout(boolean, int, int, int, int)}.
     */
    private void init() {
        mPaint = new Paint();
        mRect = new RectF();
        mRect.left = getPaddingLeft();
        mRect.right = getWidth() - getPaddingRight();
        mRect.top = getPaddingTop();
        mRect.bottom = getHeight() - getPaddingBottom();

        mPathMeasure = new PathMeasure();

        mArcTop = new Path();
        mArcTop.addArc(mRect, ARC_TOP_START, ARC_TOP_ANGLE);
        mPathMeasure.setPath(mArcTop, false);
        mArcLengthTop = mPathMeasure.getLength();

        mArcBottom = new Path();
        mArcBottom.addArc(mRect, ARC_BOTTOM_START, ARC_BOTTOM_ANGLE);
        mPathMeasure.setPath(mArcBottom, false);
        mArcLengthBottom = mPathMeasure.getLength();

        mArcLeft = new Path();
        mArcLeft.addArc(mRect, ARC_LEFT_START, ARC_LEFT_ANGLE);
        mPathMeasure.setPath(mArcLeft, false);
        mArcLengthLeft = mPathMeasure.getLength();

        mArcRight = new Path();
        mArcRight.addArc(mRect, ARC_RIGHT_START, ARC_RIGHT_ANGLE);
        mPathMeasure.setPath(mArcRight, false);
        mArcLengthRight = mPathMeasure.getLength();

        mPaint.setAntiAlias(true);
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.SQUARE);
        mPaint.setStrokeWidth(DEFAULT_STROKE_WIDTH);

        mFromXY = new float[]{0f, 0f};
        mToXY = new float[]{0f, 0f};
    }

    /**
     * Given some path and its length, find the point ([x,y]) on that path at
     * the given percentage of length.  Store the result in {@code points}.
     * @param path any path
     * @param length the length of {@code path}
     * @param percent the percentage along the path's length to find a point
     * @param points a float array of length 2, where the coordinates will be stored
     */
    private void setPointFromPercent(Path path, float length, float percent, float[] points) {
        float percentFromState = mState == FLAG_STATE_DOWN ? percent : 1 - percent;
        mPathMeasure.setPath(path, false);
        mPathMeasure.getPosTan(length * percentFromState, points, null);

    }

    /**
     * Internal saved state
     */
    static class ArrowViewState extends BaseSavedState {
        private int flagState;

        ArrowViewState(Parcelable superState) {
            super(superState);
        }

        private ArrowViewState(Parcel in) {
            super(in);
            this.flagState = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.flagState);
        }

        public static final Parcelable.Creator<ArrowViewState> CREATOR =
                new Parcelable.Creator<ArrowViewState>() {
                    public ArrowViewState createFromParcel(Parcel in) {
                        return new ArrowViewState(in);
                    }
                    public ArrowViewState[] newArray(int size) {
                        return new ArrowViewState[size];
                    }
                };
    }
}
