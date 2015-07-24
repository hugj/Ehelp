package com.ehelp.send;

import android.app.Service;
import android.content.Intent;
import android.os.Vibrator;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ehelp.R;
import com.ehelp.Test;
import com.github.jlmd.animatedcircleloadingview.AnimatedCircleLoadingView;

public class CountNum extends ActionBarActivity {

    private AnimatedCircleLoadingView animatedCircleLoadingView; // 动画视图控制
    Thread thread;
    Vibrator mVibrator; // 震动控制器
    // 后台线程负责调用动画视图
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                Thread.sleep(1850);
                for (int i = 3; i >= 1; i--) {
                    changePercent(i);// 将后台执行结果显示到UI
                    Thread.sleep(2600);
                }
                Thread.sleep(1000);
                //
                if (!CountNum.this.isFinishing()) {
                    getIntoSOS(); // 跳转到求救页面
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_count_num);
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mVibrator.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_count_num, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // 若取消求救则返回主界面并销毁当前avtivity
    public void backToHome(View view) {
        CountNum.this.finish();
    }

    // 创建该activity的各种操作
    private void init() {
        animatedCircleLoadingView = (AnimatedCircleLoadingView) findViewById(R.id.circle_loading_view);
        startLoading();
        startPercentMockThread();
        mVibrator = (Vibrator) getSystemService(Service.VIBRATOR_SERVICE);
        long[] pattern = {1850, 1400, 1200, 1400, 1200, 1400, 1200}; //依次为停顿时长，震动时长，停顿时长，震动时长 ...
        mVibrator.vibrate(pattern,-1);
    }

    private void startLoading() {
        animatedCircleLoadingView.startDeterminate();
    }

    private void startPercentMockThread() {
        thread = new Thread(runnable);
        thread.start();
    }

    // 将后台执行结果显示到UI
    private void changePercent(final int percent) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                animatedCircleLoadingView.setPercent(percent);
            }
        });
    }

    // 跳转到求救页面
    private void getIntoSOS() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(CountNum.this, SendSOS.class);
                startActivity(intent);
                CountNum.this.finish();
            }
        });
    }

}
