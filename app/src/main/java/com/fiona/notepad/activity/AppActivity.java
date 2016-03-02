package com.fiona.notepad.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.fiona.notepad.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.TimerTask;

public class AppActivity extends AppCompatActivity {

    Button button;
    boolean isSkip = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);
        button = (Button) findViewById(R.id.button_skip);

        /**
         * 设置状态栏背景色
         */
        if (android.os.Build.VERSION.SDK_INT > 18) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            // 创建状态栏的管理实例
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            // 激活状态栏设置
            tintManager.setStatusBarTintEnabled(true);
            // 激活导航栏设置
            tintManager.setNavigationBarTintEnabled(true);
            // 设置一个颜色给系统栏
            tintManager.setTintColor(Color.parseColor("#9933FA"));
        }
    }

    Handler handler = new Handler();

    @Override
    protected void onStart() {
        super.onStart();
        final int[] i = new int[]{3};
        new Thread() {
            @Override
            public void run() {
                while (i[0] > 0) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            button.setText(i[0]-- + "s 跳过>>");
                        }
                    });
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                //返回活动
                if (isSkip) {
                    finish();
                }
            }
        }.start();
    }

    public void skip(View view) {
        isSkip = false;
        finish();
    }
}
