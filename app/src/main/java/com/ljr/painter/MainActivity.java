package com.ljr.painter;


import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.SeekBar;
import android.widget.Toast;

import com.ljr.painter.ui.MyView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "MainActivity";
    private FrameLayout mFrameLayout;
    private Toolbar mToolbar;
    private LinearLayout mUtils;
    private ImageView mPaint,mRubber,mColor,mSize,mRectangle,mCircle;
    private LinearLayout mUtilsColor;
    private View mBlack,mRed,mBlue,mYellow;
    private MyView mMyView;
    private SeekBar mPaintSize;
    private SeekBar mRubberSize;
    private LinearLayout mUtilsSize;
    private static final int DRAW_PATH = 0;//画线
    private static final int DRAW_CIRCLE = 1;//画圆
    private static final int DRAW_RECTANGLE = 2;//画矩形
    private int[] drawGraphicsStyle = new int[]{DRAW_PATH, DRAW_CIRCLE, DRAW_RECTANGLE};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        initView();
        initData();

    }

    private void initData() {
        Display defaultDisplay = getWindowManager().getDefaultDisplay();
        int screenWidth = defaultDisplay.getWidth();
        int screenHeight = defaultDisplay.getHeight() ;
        Log.d(TAG, "initData:"+screenWidth+"  "+screenHeight);
        mMyView = new MyView(this, screenWidth, screenHeight);
        mFrameLayout.addView(mMyView);
        mMyView.requestFocus();//获取焦点
    }

    private void initView() {
        mFrameLayout = (FrameLayout) findViewById(R.id.frameLayout);
        mUtils = (LinearLayout) findViewById(R.id.utils);
        mPaint = (ImageView) findViewById(R.id.paint);
        mRubber = (ImageView) findViewById(R.id.rubber);
        mColor = (ImageView) findViewById(R.id.color);
        mSize = (ImageView) findViewById(R.id.size);
        mRectangle = (ImageView) findViewById(R.id.rectangle);
        mCircle = (ImageView) findViewById(R.id.circle);
        mUtilsColor = (LinearLayout) findViewById(R.id.utils_color);
        mBlack = (View) findViewById(R.id.black);
        mRed = (View) findViewById(R.id.red);
        mBlue = (View) findViewById(R.id.blue);
        mYellow = (View) findViewById(R.id.yellow);
        mUtilsSize = (LinearLayout) findViewById(R.id.utils_size);

        mPaintSize = (SeekBar) findViewById(R.id.paintSize);
        mRubberSize = (SeekBar) findViewById(R.id.rubberSize);
        mPaintSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMyView.selectSize(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mMyView.selectSize(seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mUtilsSize.setVisibility(View.GONE);
            }
        });
        mRubberSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMyView.selectSize(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mMyView.selectSize(seekBar.getProgress());
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mUtilsSize.setVisibility(View.GONE);
            }
        });
        mPaint.setOnClickListener(this);
        mRubber.setOnClickListener(this);
        mColor.setOnClickListener(this);
        mSize.setOnClickListener(this);
        mRectangle.setOnClickListener(this);
        mCircle.setOnClickListener(this);

        mBlack.setOnClickListener(this);
        mRed.setOnClickListener(this);
        mBlue.setOnClickListener(this);
        mYellow.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.cancel:
                mMyView.cancel();
                Toast.makeText(this, "重置", Toast.LENGTH_SHORT).show();
                break;
            case R.id.back:
                mMyView.back();
                Toast.makeText(this, "撤销", Toast.LENGTH_SHORT).show();
                break;
            case R.id.to:
                mMyView.recover();
                Toast.makeText(this, "返回撤销", Toast.LENGTH_SHORT).show();
                break;
            case R.id.save:
                mMyView.save();
                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.paint :
                mUtilsColor.setVisibility(View.GONE);
                mUtilsSize.setVisibility(View.GONE);
                mMyView.selectPaint();
                   break;
            case R.id.rubber :
                mUtilsColor.setVisibility(View.GONE);
                mUtilsSize.setVisibility(View.GONE);
                mMyView.selectRubber();
                break;
            case R.id.color :
                mUtilsSize.setVisibility(View.GONE);
                mUtilsColor.setVisibility(View.VISIBLE);

                break;
            case R.id.size :
                mUtilsColor.setVisibility(View.GONE);
                mUtilsSize.setVisibility(View.VISIBLE);
                break;

            case R.id.rectangle :
                mUtilsColor.setVisibility(View.GONE);
                mUtilsSize.setVisibility(View.GONE);
                mMyView.drawGraphics(DRAW_RECTANGLE);
                break;

            case R.id.circle :
                mUtilsColor.setVisibility(View.GONE);
                mUtilsSize.setVisibility(View.GONE);

                mMyView.drawGraphics(DRAW_CIRCLE);
                break;





            case R.id.black :
                mMyView.selectPaintColor(0);
                mUtilsColor.setVisibility(View.GONE);
                break;
            case R.id.red :
                mMyView.selectPaintColor(1);
                mUtilsColor.setVisibility(View.GONE);
                break;
            case R.id.blue :
                mMyView.selectPaintColor(2);
                mUtilsColor.setVisibility(View.GONE);
                break;
            case R.id.yellow :
                mMyView.selectPaintColor(3);
                mUtilsColor.setVisibility(View.GONE);
                break;
        }

    }
}
