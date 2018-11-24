package com.zhuanghongji.flowersview;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zhuanghongji.flowersview.libs.FlowersView;
import com.zhuanghongji.flowersview.libs.OnCompleteListener;

/**
 * 主页面：测试 FlowersView
 */
public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    public static final int HANDLER_WHAT_START = 0;
    public static final int HANDLER_WHAT_STOP = 1;

    private static final int AUTO_INTERVAL = 1000;

    private FlowersView mFlowersView;

    private AppCompatSeekBar sbRadiusB;
    private TextView tvRadiusB;

    private AppCompatSeekBar sbRadiusC;
    private TextView tvRadiusC;

    private AppCompatSeekBar sbRadian;
    private TextView tvRadian;

    private int mRadian = 0;
    private boolean mIsStart = false;
    private TextView tvRadianChange;

    private Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == HANDLER_WHAT_START) {
                mHandler.post(mAutoIncreaseRadian);
                return true;
            }
            if (msg.what == HANDLER_WHAT_STOP) {
                mHandler.removeCallbacksAndMessages(null);
                return true;
            }
            return false;
        }
    });

    private Runnable mAutoIncreaseRadian = new Runnable() {
        @Override
        public void run() {
            mRadian++;
            sbRadian.setProgress(mRadian);
            mHandler.postDelayed(this, AUTO_INTERVAL);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();

        mFlowersView.post(new Runnable() {
            @Override
            public void run() {
                int radiusBMax = mFlowersView.getSideLength() / 2;
                sbRadiusB.setMax(radiusBMax);
                sbRadiusC.setMax(radiusBMax);

                // int radiusCMax = mFlowersView.getSideLength() / 2;
                // sbRadiusB.setMax(radiusCMax);

                int bigRadianMax = 10000;
                sbRadian.setMax(bigRadianMax);
                tvRadian.setText(String.valueOf(bigRadianMax));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initEvent() {
        sbRadiusB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRadiusB.setText(String.valueOf(progress));

                int sbc = sbRadiusC.getProgress();
                sbRadiusC.setMax(progress);
                sbRadiusC.setProgress(sbc);

                mFlowersView.changeRadiusB(progress);
                mFlowersView.postInvalidate();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sbRadiusC.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRadiusC.setText(String.valueOf(progress));

                mFlowersView.changeRadiusC(progress);
                mFlowersView.postInvalidate();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        sbRadian.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvRadian.setText(String.valueOf(progress));

                mFlowersView.changeRadian(progress * 1.0 / 10);
                mFlowersView.postInvalidate();
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        tvRadianChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mIsStart) {
                    mRadian = 0;
                    mIsStart = true;
                    mHandler.sendEmptyMessage(HANDLER_WHAT_START);
                    return;
                }
                mIsStart = false;
                mHandler.sendEmptyMessage(HANDLER_WHAT_STOP);
            }
        });

        mFlowersView.setOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete() {
                Log.i(TAG, "onComplete");
                mHandler.sendEmptyMessage(HANDLER_WHAT_STOP);
            }
        });
    }

    private void initView() {
        mFlowersView = findViewById(R.id.flowers_view);

        sbRadiusB = findViewById(R.id.sb_radius_b);
        tvRadiusB = findViewById(R.id.tv_radius_b);

        sbRadiusC = findViewById(R.id.sb_radius_c);
        tvRadiusC = findViewById(R.id.tv_radius_c);

        sbRadian = findViewById(R.id.sb_radian);
        tvRadian = findViewById(R.id.tv_radian);

        tvRadianChange = findViewById(R.id.tv_radian_change);
    }
}
