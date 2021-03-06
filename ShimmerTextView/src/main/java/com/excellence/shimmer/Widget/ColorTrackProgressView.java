package com.excellence.shimmer.Widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.excellence.shimmer.R;

/***
 * Created by ZhangWei on 2016/8/4.
 */
public class ColorTrackProgressView extends ProgressBar
{
	private static final String TAG = ColorTrackProgressView.class.getSimpleName();

	private Paint mPaint = null;
	private String mText = ColorTrackProgressView.class.getSimpleName();
	private int mTextSize = sp2px(30);
	private int mTextOriginColor = Color.BLACK;
	private int mTextChangeColor = Color.WHITE;
	private Rect mTextBound = new Rect();

	private int mTextWidth;
	private int mViewWidth;
	private int mTextStartX = 0;
	private float mMiniProgress = 0;

	public ColorTrackProgressView(Context context)
	{
		this(context, null);
	}

	public ColorTrackProgressView(Context context, AttributeSet attrs)
	{
		this(context, attrs, android.R.attr.progressBarStyleHorizontal);
	}

	public ColorTrackProgressView(Context context, AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ColorTrackProgressView);
		mText = typedArray.getString(R.styleable.ColorTrackProgressView_txt);
		mTextSize = typedArray.getDimensionPixelOffset(R.styleable.ColorTrackProgressView_txt_size, mTextSize);
		mTextOriginColor = typedArray.getColor(R.styleable.ColorTrackProgressView_txt_origin_color, mTextOriginColor);
		mTextChangeColor = typedArray.getColor(R.styleable.ColorTrackProgressView_txt_change_color, mTextChangeColor);
		typedArray.recycle();
		if (mText == null)
			mText = ColorTrackProgressView.class.getSimpleName();
		setTextProgress();
		// 抗锯齿
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	}

	private void setTextProgress()
	{
		mMiniProgress = (float) getProgress() / getMax();
	}

	@Override
	protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		measureText();
	}

	private void measureText()
	{
		mPaint.setTextSize(mTextSize);

		// 横向
		mPaint.getTextBounds(mText, 0, mText.length(), mTextBound);
		mTextWidth = (int) mPaint.measureText(mText);
		mTextStartX = getMeasuredWidth() / 2 - mTextWidth / 2;
		mViewWidth = getMeasuredWidth();
	}

	@Override
	protected synchronized void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		int rx = (int) (mMiniProgress * mViewWidth);
		drawChangeX(canvas, rx);
		drawOriginX(canvas, rx);
	}

	private void drawChangeX(Canvas canvas, int r)
	{
		if (r >= mTextStartX && r <= mTextStartX + mTextWidth)
			drawTextX(canvas, mTextChangeColor, mTextStartX, r);
	}

	private void drawOriginX(Canvas canvas, int r)
	{
		if (r >= mTextStartX && r <= mTextStartX + mTextWidth)
			drawTextX(canvas, mTextOriginColor, r, mViewWidth);
		else if (r > mTextStartX + mTextWidth)
			drawTextX(canvas, mTextChangeColor, mTextStartX, mTextStartX + mTextWidth);
		else
			drawTextX(canvas, mTextOriginColor, mTextStartX, mTextStartX + mTextWidth);
	}

	private void drawTextX(Canvas canvas, int color, int startX, int endX)
	{
		mPaint.setColor(color);
		// 需要还原Clip
		// save()先把画布的数据保存了(如matrix等)，最后绘制完后再restore()则把中间对画布坐标等操作forget掉
		canvas.save(Canvas.CLIP_SAVE_FLAG);
		// clipRect()截取画布中的一个区域
		canvas.clipRect(startX, 0, endX, getMeasuredHeight());
		canvas.drawText(mText, mTextStartX, getMeasuredHeight() / 2 - ((mPaint.descent() + mPaint.ascent()) / 2), mPaint);
		// restore()最后要将画布回复原来的数据（记住save()跟restore()要配对使用）
		canvas.restore();
	}

	private int sp2px(int size)
	{
		float scale = getContext().getResources().getDisplayMetrics().scaledDensity;
		return (int) (size * scale + 0.5f);
	}

	@Override
	public synchronized void setProgress(int progress)
	{
		super.setProgress(progress);
		setTextProgress();
	}

	public String getText()
	{
		return mText;
	}

	public void setText(String text)
	{
		mText = text;
		measureText();
		invalidate();
	}

	public void setText(int strId)
	{
		mText = getResources().getString(strId);
		measureText();
		invalidate();
	}
}
