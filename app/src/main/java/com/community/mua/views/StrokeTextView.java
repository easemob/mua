package com.community.mua.views;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.community.mua.R;

/*
 * StrokeTextView的目标是给文字描边
 * 实现方法是两个TextView叠加,只有描边的TextView为底,实体TextView叠加在上面
 * 看上去文字就有个不同颜色的边框了
 */
@SuppressLint("AppCompatCustomView")
public class StrokeTextView extends TextView {
    private boolean hasStroke = false; // 默认不采用描边
    private Paint mTextPaint;
    private float borderWidth;
    private int borderColor;
    private ColorStateList textColor;

    public StrokeTextView(Context context) {
        this(context, null);
    }

    public StrokeTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StrokeTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mTextPaint = getPaint();
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.StrokeTextView);
        hasStroke = ta.getBoolean(R.styleable.StrokeTextView_hasBorder, false);
        borderWidth = ta.getDimension(R.styleable.StrokeTextView_borderWidth, 5);
        borderColor = ta.getColor(R.styleable.StrokeTextView_borderColor, 0xffffff);
        ta.recycle();
        textColor = getTextColors();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hasStroke) {
            // 描外层
            this.setTextColor(borderColor);
            mTextPaint.setStrokeWidth(borderWidth);  // 描边宽度
            mTextPaint.setStyle(Paint.Style.STROKE); //描边种类
            mTextPaint.setFakeBoldText(true); // 外层text采用粗体
            mTextPaint.setShadowLayer(1, 0, 0, 0); //字体的阴影效果，可以忽略
            super.onDraw(canvas);
            // 描内层，恢复原先的画笔
            this.setTextColor(textColor);
            mTextPaint.setStrokeWidth(0);
            mTextPaint.setStyle(Paint.Style.FILL);
            mTextPaint.setFakeBoldText(false);
            mTextPaint.setShadowLayer(0, 0, 0, 0);
        }
        super.onDraw(canvas);
    }

    private static final int duration = 4000;
    private int number;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        setText(String.format("%s", number));

    }

    public void runWithAnimation(int end) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofInt(
                this, "number", number, end);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        objectAnimator.start();
    }
}
