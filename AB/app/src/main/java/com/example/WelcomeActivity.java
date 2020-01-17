package com.example;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.MainActivity;
import com.example.R;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 欢迎界面
 */
public class WelcomeActivity extends AppCompatActivity {

    /**倒计时文本*/
    private TextView tvCountdown;

    private static final int MSG_COUNT_WHAT = 99;
    private static final int NUM = 5;
    private int countdownNum;//倒计时的秒数
    private static Timer timer;//计时器
    private MyHandler countdownHandle;//用于控制倒计时子线程
    private Runnable runnable;//倒计时子线程

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*==========设置全屏======必须在setContentView前面=======*/
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_welcome);

        //初始化控件
        initView();

        //初始化Handler和Runnable
        initThread();
    }


    /**
     * 初始化倒计时文本控件
     * */
    private void initView(){
        tvCountdown = (TextView) findViewById(R.id.tv_Countdown);
        //当点击文本控件是,停止倒计时,跳到主界面
        tvCountdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopThread();
                openNextActivity(WelcomeActivity.this);//打开下一个界面
            }
        });
    }
    /**
     * 初始化Handler和Runnable
     * */
    private void initThread(){
        //倒计时变量(初始化倒计时秒数)
        initCountdownNum();
        //handler对象
        countdownHandle = new MyHandler(this);
        //runnable
        runnable = new Runnable() {

            @Override
            public void run() {
                //执行倒计时代码
                timer = new Timer();
                TimerTask task = new TimerTask() {
                    public void run() {
                        countdownNum --;

                        Message msg = countdownHandle.obtainMessage();
                        msg.what = MSG_COUNT_WHAT;//message的what值
                        msg.arg1 = countdownNum;//倒计时的秒数

                        countdownHandle.sendMessage(msg);
                    }
                };
                timer.schedule(task,0,1000);
            }
        };
    }

    /**必须使用静态类*/
    private static class MyHandler extends Handler {
        private WeakReference<WelcomeActivity> mOuter;

        public MyHandler(WelcomeActivity activity) {
            mOuter = new WeakReference<WelcomeActivity>(activity);
        }
        @Override
        public void handleMessage(Message msg) {

            WelcomeActivity theActivity = mOuter.get();

            if (theActivity != null) {

                switch (msg.what) {
                    case MSG_COUNT_WHAT:
                        if(msg.arg1 == 0){//表示倒计时完成

                            //在这里执行的话，不会出现-1S的情况
                            if(timer != null){
                                timer.cancel();//销毁计时器
                            }

                            openNextActivity(theActivity);//打开下一个界面


                        }else{
                            theActivity.tvCountdown.setText(msg.arg1 +"跳过");
                        }
                        break;

                    default:
                        break;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        //开启线程
        countdownHandle.post(runnable);
        super.onResume();

    }

    @Override
    protected void onStop() {

        initCountdownNum();//初始化倒计时的秒数，这样按home键后再次进去欢迎界面，则会重新倒计时

        stopThread();

        super.onStop();
    }

    //停止倒计时
    private void stopThread(){
        //在这里执行的话，用户点击home键后，不会继续倒计时进入登录界面
        if(timer != null){
            timer.cancel();//销毁计时器
        }
        //将线程销毁掉
        countdownHandle.removeCallbacks(runnable);
    }

    //打开下一个界面
    private static void openNextActivity(Activity mActivity) {
        //跳转到登录界面并销毁当前界面
        Intent intent = new Intent(mActivity, MainActivity.class);
        mActivity.startActivity(intent);

        mActivity.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /*初始化倒计时的秒数*/
    private void initCountdownNum(){
        countdownNum = NUM;
    }
}