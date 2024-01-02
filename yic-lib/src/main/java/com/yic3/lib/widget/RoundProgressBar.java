package com.yic3.lib.widget;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.ColorUtils;
import com.yic3.lib.R;


/**
 * @author Venn
 * create at 2018/10/12
 * description: 圆形进度条
 */
public class RoundProgressBar extends View {

    private Paint mBackgroundPaint = new Paint();
    private Paint mFullPaint = new Paint();
    private Paint mRoundPaint = new Paint();
    private Paint mThumbOutPaint = new Paint();
    private Paint mCircePaint = new Paint();

    private float radius = 100;
    private float thumbRadius = 15;
    private float thumbOutRadius = 20;

    private float currentProgress = 100;
    private int maxProgress = 100;

    private float bgProgressWidth = 10;
    private float fullProgressWidth = 10;

    private float startAngle = 0;

    private RectF rectF;

    private int colorStart = ColorUtils.getColor(R.color.theme_color);

    // private int colorCenter = Color.parseColor("#93D8FF");
    private int colorEnd = ColorUtils.getColor(R.color.theme_color);

    private int colorFull = colorStart;

    private int colorThumbOut = Color.WHITE;
    private int colorThumb = ColorUtils.getColor(R.color.theme_color);

    public void setColorThumb(int colorThumb) {
        this.colorThumb = colorThumb;
        invalidate();
    }

    private int colorBg = Color.parseColor("#D9D9D9");

    private ArgbEvaluator evaluator;


    public RoundProgressBar(Context context) {
        this(context, null);

    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStyle(context, attrs, defStyleAttr);
    }

    private void initStyle(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        if (attrs == null) {
            return;
        }
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RoundProgressBar);
        colorStart = array.getColor(R.styleable.RoundProgressBar_round_start_color, colorStart);
        // colorCenter = array.getColor(R.styleable.RoundProgressBar_round_center_color, colorCenter);
        colorEnd = array.getColor(R.styleable.RoundProgressBar_round_end_color, colorEnd);
        colorBg = array.getColor(R.styleable.RoundProgressBar_round_bg_color, colorBg);
        colorThumb = array.getColor(R.styleable.RoundProgressBar_round_thumb_color, colorThumb);
        colorThumbOut = array.getColor(R.styleable.RoundProgressBar_round_thumb_out_color, colorThumbOut);
        thumbRadius = array.getDimension(R.styleable.RoundProgressBar_round_thumb_radius, thumbRadius);
        thumbOutRadius = array.getDimension(R.styleable.RoundProgressBar_round_thumb_out_radius, thumbOutRadius);
        radius = array.getDimension(R.styleable.RoundProgressBar_round_radius, radius);
        currentProgress = array.getFloat(R.styleable.RoundProgressBar_round_progress, 0.0f);
        maxProgress = array.getInteger(R.styleable.RoundProgressBar_round_max_progress, maxProgress);
        bgProgressWidth = array.getDimension(R.styleable.RoundProgressBar_round_bg_progress_width, bgProgressWidth);
        fullProgressWidth = array.getDimension(R.styleable.RoundProgressBar_round_full_progress_width, fullProgressWidth);
        startAngle = array.getFloat(R.styleable.RoundProgressBar_round_start_angle, startAngle);
        array.recycle();
    }

    private void initPaint() {
        evaluator = new ArgbEvaluator();
        mBackgroundPaint.setColor(colorBg);
        mBackgroundPaint.setStyle(Paint.Style.STROKE);
        mBackgroundPaint.setStrokeWidth(bgProgressWidth);
        mBackgroundPaint.setAntiAlias(true);

        mFullPaint.setStyle(Paint.Style.STROKE);
        mFullPaint.setStrokeWidth(fullProgressWidth);
        mFullPaint.setAntiAlias(true);

        mCircePaint.setColor(colorStart);

        mRoundPaint.setColor(colorThumb);
        mRoundPaint.setAntiAlias(true);

        mThumbOutPaint.setColor(colorThumbOut);
        mThumbOutPaint.setAntiAlias(true);

        rectF = getRectF();
        mFullPaint.setShader(getGradient());
        mFullPaint.setStrokeCap(Paint.Cap.ROUND);

    }

    private RectF getRectF() {
        float x = thumbOutRadius < thumbRadius ? thumbRadius : thumbOutRadius;
        float y = x;
        return new RectF(x, y, x + (radius * 2), y + (radius * 2));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (evaluator != null) {
            drawFull(canvas);
        }
    }

    private void drawFull(Canvas canvas) {
        float angle = getCurrentRotation();
        canvas.drawArc(rectF, startAngle, 360, false, mBackgroundPaint);
        canvas.drawArc(rectF, startAngle, angle, false, mFullPaint);

        float radian = getRadiansAngle(angle, startAngle);

        float x1 = getAngleX(rectF.centerX(), radian, (int) radius);
        float y1 = getAngleY(rectF.centerY(), radian, (int) radius);

        mRoundPaint.setColor(colorThumb);
        canvas.drawCircle(x1, y1, thumbOutRadius, mThumbOutPaint);
        canvas.drawCircle(x1, y1, thumbRadius, mRoundPaint);

    }

    public void setProgressColor(int colorStart, int colorEnd) {
        this.colorStart = colorStart;
        this.colorEnd = colorEnd;
        invalidate();
    }

    private LinearGradient getGradient(){
        return new LinearGradient(0, 0, getWidth(), 0, new int[]{colorStart, colorEnd}, null, LinearGradient.TileMode.CLAMP);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width;
        int height;
        int specMode = MeasureSpec.getMode(widthMeasureSpec);
        int specSize = MeasureSpec.getSize(widthMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            width = specSize;
        } else {
            width = (int) ((2 * radius) + bgProgressWidth + (thumbRadius * 2));
        }
        specMode = MeasureSpec.getMode(heightMeasureSpec);
        specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            height = specSize;
        } else {
            height = (int) ((2 * radius) + bgProgressWidth + (thumbRadius * 2));
        }
        setMeasuredDimension(width, height);
        initPaint();
    }

    private float getCurrentRotation() {
        return 360 * (currentProgress / maxProgress);
    }

    private float getRadiansAngle(float angle, float startAngle) {
        float radiansAngle = angle + startAngle;
        return (float) Math.toRadians(radiansAngle > 360 ? radiansAngle - 360 : radiansAngle);
    }

    private float getAngleX(float centerX, float radian, float radius) {
        return (float) (centerX + Math.cos(radian) * radius);
    }

    private float getAngleY(float centerY, float radian, float radius) {
        return (float) (centerY + Math.sin(radian) * radius);
    }

    public void setCurrentProgress(float currentProgress) {
        this.currentProgress = currentProgress;
        invalidate();
    }
}