package com.example.chaiy.circlecheckbox;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * Created by Chaiy on 7/4/2016.
 */
public class CircleCheckBox extends View {

    private static final String TAG = CircleCheckBox.class.getSimpleName();
    public static final int INNER_CIRCLE_ANIM_DURATION = 1000;

    int borderColor = Color.GRAY;
    int circleColor = Color.RED;
    int tickMarkColor = Color.WHITE;

    float outerCircleRadius = 200;
    float innerCircleRadius = 188;
    int innerCircleAlpha = 255;

    int circlePadding = 20;

    int width;
    int height;

    int borderThickness = 2;
    int tickMarkThickness = 6;

    Paint borderPaint;
    Paint innerCirclePaint;
    Paint tickMarkPaint;

    Path tickPath1 = new Path();
    Path tickPath2 = new Path();

    private PathMeasure tickPath1Measure;
    private PathMeasure tickPath2Measure;

    final float[] tick1StartPosition = new float[2];
    final float[] tick1OffsetPosition = new float[2];

    final float[] tick2StartPosition = new float[2];
    final float[] tick2OffsetPosition = new float[2];

    boolean isChecked;
    boolean isAnimationInProgress;
    boolean animateInnerCircleRadius = false;
    boolean isTick2Started = false;
    boolean isTick1Started = false;

    public CircleCheckBox(Context context) {
        super(context);
        init();
    }

    public CircleCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }


    private void init() {
        outerCircleRadius = (int) DisplayUtils.convertDpToPixel(100, getContext());

        Log.v(TAG, " setMinimumWidth() :" + outerCircleRadius * 2);
        computeViewDimensions();
        computeInnerCircleRadius();

        setMinimumWidth(width);
        setMinimumHeight(height);

        borderPaint = new Paint();
        borderPaint.setColor(borderColor);
        borderPaint.setStrokeWidth(borderThickness);
        borderPaint.setStyle(Paint.Style.STROKE);

        if (isChecked()) {
            innerCircleAlpha = 255;
        } else {
            innerCircleAlpha = 0;
        }
        innerCirclePaint = new Paint();
        innerCirclePaint.setStyle(Paint.Style.FILL);
        innerCirclePaint.setColor(circleColor);
        innerCirclePaint.setAlpha(innerCircleAlpha);


        tickMarkPaint = new Paint();
        tickMarkPaint.setStyle(Paint.Style.STROKE);
        tickMarkPaint.setStrokeWidth(tickMarkThickness);
        tickMarkPaint.setColor(tickMarkColor);

        computeTickPath();

    }

    private void computeInnerCircleRadius() {
        innerCircleRadius = outerCircleRadius - borderThickness;
    }

    private void computeViewDimensions() {

        height = width = (int) ((outerCircleRadius * 2) + circlePadding);
        Log.v(TAG, " height: " + height + " width: " + width);
    }

    private void computeTickPath() {
        computeTick1Path();
        computeTick12Path();
    }

    private void computeTick1Path() {

        float startX = 0 - (innerCircleRadius / 2);
        float startY = 0;

        float endX = 0;
        float endY = 0 + (innerCircleRadius / 2);

        tickPath1.moveTo(startX, startY);
        tickPath1.lineTo(endX, endY);

        tickPath1Measure = new PathMeasure(tickPath1, false);

        tick1StartPosition[0] = startX;
        tick1StartPosition[1] = startY;
    }

    private void computeTick12Path() {

        float startX = 0;
        float startY = 0 + (innerCircleRadius / 2);

        float endX = 0 + (innerCircleRadius / 2);
        float endY = 0 - (innerCircleRadius / 2);

        tickPath2.moveTo(startX, startY);
        tickPath2.lineTo(endX, endY);

        tickPath2Measure = new PathMeasure(tickPath2, false);

        tick2StartPosition[0] = startX;
        tick2StartPosition[1] = startY;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.v(TAG, " onMeasure() :" + height);
        super.onMeasure(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.v(TAG, " getWidth() :" + getWidth());
        canvas.translate(getWidth() / 2f, getHeight() / 2f);
        drawBorder(canvas);
        if (isChecked() || isAnimationInProgress) {
            drawInnerCircle(canvas);
            drawTickMark1(canvas);
            drawTickMark2(canvas);
        }
    }

    private void drawBorder(Canvas canvas) {

        canvas.drawCircle(0, 0, outerCircleRadius, borderPaint);

    }

    private void drawInnerCircle(Canvas canvas) {
        //if (isChecked() || isAnimationInProgress) {
        Log.v(TAG, " drawInnerCircle :" + getInnerCircleRadius());
        Log.v(TAG, " innerCircleAlpha :" + getInnerCircleAlpha());
        innerCirclePaint.setAlpha(getInnerCircleAlpha());
        canvas.drawCircle(0, 0, getInnerCircleRadius(), innerCirclePaint);
        // }
    }

    private void drawTickMark1(Canvas canvas) {

        if (isTick1Started) {
            tickMarkPaint.setAlpha(getInnerCircleAlpha());
            Log.v(TAG, " drawTickMark1 tick1OffsetPosition : pos[0] : " + tick1OffsetPosition[0] + " pos[1]: " + tick1OffsetPosition[1]);
            canvas.drawLine(tick1StartPosition[0], tick1StartPosition[1], tick1OffsetPosition[0], tick1OffsetPosition[1], tickMarkPaint);
        }

    }

    private void drawTickMark2(Canvas canvas) {

        if (isTick2Started) {
            tickMarkPaint.setAlpha(getInnerCircleAlpha());
            Log.v(TAG, " drawTickMark2 tick2OffsetPosition : pos[0] : " + tick2OffsetPosition[0] + " pos[1]: " + tick2OffsetPosition[1]);
            canvas.drawLine(tick2StartPosition[0], tick2StartPosition[1], tick2OffsetPosition[0], tick2OffsetPosition[1], tickMarkPaint);
        }


    }

    private void updateCheckedState(boolean isCircleChecked) {
        isChecked = isCircleChecked;
    }

    public void setChecked(boolean isChecked) {
        Log.v(TAG, " setChecked :" + isChecked);
        Log.v(TAG, " isAnimationInProgress :" + isAnimationInProgress);
        if (!isAnimationInProgress)
            animateInnerCircle(isChecked);

    }

    private float getInnerCircleRadius() {
        return innerCircleRadius;
    }

    private void setInnerCircleRadius(float innerCircleRadius) {
        this.innerCircleRadius = innerCircleRadius;
    }

    private int getInnerCircleAlpha() {
        return innerCircleAlpha;
    }

    private void setInnerCircleAlpha(int innerCircleAlpha) {
        this.innerCircleAlpha = innerCircleAlpha;
    }

    private void animateInnerCircle(final boolean isCircleChecked) {

        AnimatorSet circleAnimationSet = new AnimatorSet();
        circleAnimationSet.playTogether(createAlphaAnimator(isCircleChecked));
        if (animateInnerCircleRadius)
            circleAnimationSet.playTogether(createRadiusAnimator(isCircleChecked));
        if (isCircleChecked)
            circleAnimationSet.playSequentially(createTickAnimation(isCircleChecked));
        circleAnimationSet.setDuration(INNER_CIRCLE_ANIM_DURATION);
        circleAnimationSet.setInterpolator(new AccelerateDecelerateInterpolator());

        circleAnimationSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                Log.v(TAG, " onAnimationStart :");
                isAnimationInProgress = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {
                Log.v(TAG, " onAnimationEnd :");
                Log.v(TAG, " isCircleChecked :" + isCircleChecked);
                updateCheckedState(isCircleChecked);
                isAnimationInProgress = false;
                if (!isCircleChecked) {
                    isTick2Started = false;
                    isTick1Started = false;
                }
                postInvalidate();

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        circleAnimationSet.start();


    }

    private ObjectAnimator createRadiusAnimator(final boolean isCircleChecked) {
        Log.v(TAG, " init createRadiusAnimator :" + innerCircleRadius);

        final ObjectAnimator innerCircleRadiusAnimator;
        if (isCircleChecked) {
            computeInnerCircleRadius();
            innerCircleRadiusAnimator = ObjectAnimator.ofFloat(this, "innerCircleRadius", 0, innerCircleRadius);
        } else {
            innerCircleRadiusAnimator = ObjectAnimator.ofFloat(this, "innerCircleRadius", innerCircleRadius, 0);
        }

        innerCircleRadiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Log.v(TAG, " onAnimationUpdate innerCircleRadiusAnimator:" + innerCircleRadius);
                Log.v(TAG, "after onAnimationUpdate innerCircleRadiusAnimator:" + valueAnimator.getAnimatedValue());
                Log.v(TAG, "after onAnimationUpdate innerCircleRadius :" + innerCircleRadius);
                postInvalidate();
            }
        });

        return innerCircleRadiusAnimator;
    }

    private ObjectAnimator createAlphaAnimator(final boolean isCircleChecked) {

        Log.v(TAG, " init createAlphaAnimator :" + innerCirclePaint.getAlpha());

        final ObjectAnimator innerCircleAlphaAnimator;

        if (isCircleChecked) {
            innerCircleAlphaAnimator = ObjectAnimator.ofInt(this, "innerCircleAlpha", 0, 255);
        } else {
            innerCircleAlphaAnimator = ObjectAnimator.ofInt(this, "innerCircleAlpha", 255, 0);
        }

        innerCircleAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Log.v(TAG, " onAnimationUpdate innerCircleAlphaAnimator:" + innerCircleAlpha);
                Log.v(TAG, "after onAnimationUpdate alpha :" + valueAnimator.getAnimatedValue());
                Log.v(TAG, "after onAnimationUpdate alpha :" + innerCirclePaint.getAlpha());
                postInvalidate();
            }
        });

        return innerCircleAlphaAnimator;
    }


    private AnimatorSet createTickAnimation(final boolean isCircleChecked) {

        AnimatorSet tickAnimation = new AnimatorSet();

        ValueAnimator tick1Animation;
        // if (isCircleChecked)
        tick1Animation = ObjectAnimator.ofFloat(0, 1);
        tick1Animation.setStartDelay(INNER_CIRCLE_ANIM_DURATION / 4);
        //else
        //  tick1Animation = ObjectAnimator.ofFloat(1, 0);

        tick1Animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final float distance = valueAnimator.getAnimatedFraction() * tickPath1Measure.getLength();
                Log.v(TAG, " tick1Animation :" + distance);
                tickPath1Measure.getPosTan(distance, tick1OffsetPosition, null);
                Log.v(TAG, " tick1 pos[0] :" + tick1OffsetPosition[0] + " pos[1] :" + tick1OffsetPosition[1]);
                invalidate();
            }
        });
        tick1Animation.setDuration(INNER_CIRCLE_ANIM_DURATION / 4);
        tick1Animation.setInterpolator(new AccelerateDecelerateInterpolator());
        tick1Animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isTick1Started = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        ValueAnimator tick2Animation;
        //if (isCircleChecked)
        tick2Animation = ObjectAnimator.ofFloat(0, 1);
        tick2Animation.setDuration(INNER_CIRCLE_ANIM_DURATION / 4);
        tick2Animation.setInterpolator(new AccelerateDecelerateInterpolator());

        //else
        //  tick2Animation = ObjectAnimator.ofFloat(1, 0);
        tick2Animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                final float distance = valueAnimator.getAnimatedFraction() * tickPath2Measure.getLength();
                Log.v(TAG, " tick2Animation :" + distance);
                tickPath2Measure.getPosTan(distance, tick2OffsetPosition, null);
                Log.v(TAG, " tick2 pos[0] :" + tick2OffsetPosition[0] + " pos[1] :" + tick2OffsetPosition[1]);
                invalidate();
            }
        });
        tick2Animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                isTick2Started = true;
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
        tickAnimation.playSequentially(tick1Animation, tick2Animation);

        return tickAnimation;
    }

    public boolean isChecked() {
        return isChecked;
    }
}
