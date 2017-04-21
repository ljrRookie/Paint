package com.ljr.painter.ui;

import android.content.Context;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;

import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.View;


import com.ljr.painter.bean.DrawPath;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * Created by LinJiaRong on 2017/4/20.
 * TODO：
 */

public class MyView extends View {
    private Context mContext;
    private int mWidth;
    private int mHeight;//画板宽高
    private int[] mPaintColor;//颜色集合
    private static ArrayList<DrawPath> mSavePath;
    private static ArrayList<DrawPath> mDeletePath;
    private int mCurrentStyle = 1;
    private int mCurrentPaintSize = 5;
    private int mCurrentRubberSize = 50;
    private int mCurrentColor = Color.BLACK;
    private Paint mBitmapPaint;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private DrawPath mDrawPath;
    private float mX, mY;//临时坐标点
    //设置画图样式
    private static final int DRAW_PATH = 01;
    private static final int DRAW_CIRCLE = 02;
    private static final int DRAW_RECTANGLE = 03;
    private int[] graphics = new int[]{DRAW_PATH, DRAW_CIRCLE, DRAW_RECTANGLE};
    private int mCurrentDrawGraphics = graphics[0];//默认画线
    private static final String TAG = "MyView";

    public MyView(Context context, int w, int h) {
        super(context);
        this.mContext = context;
        mWidth = w;
        mHeight = h;
        mPaintColor = new int[]{Color.BLACK, Color.RED, Color.BLUE,
                Color.YELLOW};
        //设置默认样式，去除dis-in的黑色方框以及clear模式的黑线效果
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        initCanvas();
        mSavePath = new ArrayList<DrawPath>();
        mDeletePath = new ArrayList<DrawPath>();

    }

    //初始化画笔样式
    private void setPaintStyle() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);//设置外边缘
        mPaint.setStrokeCap(Paint.Cap.ROUND);//形状
        mPaint.setAntiAlias(true);//抗锯齿，平滑
        mPaint.setDither(true);
        if (mCurrentStyle == 1) {
            mPaint.setStrokeWidth(mCurrentPaintSize);
            mPaint.setColor(mCurrentColor);
        } else {//橡皮擦
            mPaint.setAlpha(0);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
            mPaint.setColor(Color.TRANSPARENT);
            mPaint.setStrokeWidth(mCurrentRubberSize);
            mCurrentDrawGraphics = DRAW_PATH;

        }
    }

    private void initCanvas() {
        setPaintStyle();
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);
        //画布大小
        mBitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.ARGB_8888);
        mBitmap.eraseColor(Color.argb(0, 0, 0, 0));
        //所有画的东西都保存在mBitmap里
        mCanvas = new Canvas(mBitmap);
        mCanvas.drawColor(Color.TRANSPARENT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //将画的东西显示出来
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        //实时显示
        if (mPath != null) {
            canvas.drawPath(mPath, mPaint);
        }
    }

    private float startX;
    private float startY;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = event.getX();
                startY = event.getY();
                //每次触摸屏幕重新new一个Path
                mPath = new Path();
                //每次都记录一次画的路径
                mDrawPath = new DrawPath();
                mDrawPath.path = mPath;
                mDrawPath.paint = mPaint;
                mPath.moveTo(x, y);
                mX = x;
                mY = y;
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - mX);
                float dy = Math.abs(mY - y);
                if (dx >= 4 || dy >= 4) {
                    if (mCurrentDrawGraphics == DRAW_PATH) {
                        mPath.lineTo(mX, mY);
                    } else if (mCurrentDrawGraphics == DRAW_CIRCLE) {
                        mPath.reset();//清空以前的路径，否则会出现无数条从起点到末位置的线
                        RectF rectF = new RectF(startY,startX, x, y);
                        mPath.addOval(rectF, Path.Direction.CCW);
                    } else if (mCurrentDrawGraphics == DRAW_RECTANGLE) {
                        mPath.reset();
                        RectF rectF = new RectF(startX, startY, x, y);
                        mPath.addRect(rectF, Path.Direction.CCW);
                    }

                    mX = x;
                    mY = y;
                }
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                if(mCurrentDrawGraphics == DRAW_PATH){
                    mPath.lineTo(mX, mY);
                }

                mCanvas.drawPath(mPath, mPaint);
                mSavePath.add(mDrawPath);
                mPath = null;
                invalidate();
                break;

        }
        return true;
    }

    /**
     * 重置：删除所有已保存的路径即可
     */
    public void cancel() {
        if (mSavePath != null && mSavePath.size() > 0) {
            mSavePath.clear();
            initCanvas();
            Iterator<DrawPath> iterator = mSavePath.iterator();
            while (iterator.hasNext()) {
                DrawPath drawPath = iterator.next();
                mCanvas.drawPath(drawPath.path, drawPath.paint);
            }
            invalidate();
        }
    }

    /**
     * 撤销：清空画布，删除最后一条路径，重新将路径集合绘制在画布上。(保存最后的路径用于恢复撤销)
     */
    public void back() {
        if (mSavePath != null && mSavePath.size() > 0) {
            DrawPath deletePath = mSavePath.get(mSavePath.size() - 1);
            mDeletePath.add(deletePath);
            mSavePath.remove(mSavePath.size() - 1);
            initCanvas();
            Iterator<DrawPath> iterator = mSavePath.iterator();
            while (iterator.hasNext()) {
                DrawPath drawPath = iterator.next();
                mCanvas.drawPath(drawPath.path, drawPath.paint);
            }
            invalidate();
        }
    }

    /**
     * 恢复撤销：将删除的路径重新添加
     */
    public void recover() {
        if (mDeletePath.size() > 0) {
            DrawPath drawPath = mDeletePath.get(mDeletePath.size() - 1);
            mSavePath.add(drawPath);
            mCanvas.drawPath(drawPath.path, drawPath.paint);
            mDeletePath.remove(mDeletePath.size() - 1);
            invalidate();

        }
    }

    /**
     * 保存到sd卡
     */
    public void save() {
/*        File filesDir = mContext.getFilesDir();
        //获取系统时间，并以时间命名
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date(System.currentTimeMillis());
        String str = format.format(date) + "paint.png";
        File drawFile = new File("sdcard/"+ str);
        Log.d(TAG, "save: "+drawFile.getName()+"========================="+drawFile.getPath());
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(drawFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        //图片压缩
        mBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);*/
    }

    public void selectPaint() {
        if (mCurrentStyle == 1) {
            setPaintStyle();
        } else {
            mCurrentStyle = 1;
            setPaintStyle();
        }
    }

    public void selectRubber() {
        if (mCurrentStyle != 1) {
            setPaintStyle();
        } else {
            mCurrentStyle = 2;
            setPaintStyle();
        }
    }

    public void selectPaintColor(int colorId) {
        mCurrentColor = mPaintColor[colorId];
        setPaintStyle();
    }


    public void selectSize(int Size) {
        mCurrentPaintSize = Size;
        mCurrentRubberSize = Size;
        setPaintStyle();
    }

    //画线，圆，矩形，
    public void drawGraphics(int which) {
        mCurrentDrawGraphics = graphics[which];
    }
}
