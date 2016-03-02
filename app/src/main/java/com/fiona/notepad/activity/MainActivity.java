package com.fiona.notepad.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.fiona.notepad.App;
import com.fiona.notepad.R;
import com.fiona.notepad.database.NoteProvider;
import com.fiona.notepad.view.DeleteView;
import com.fiona.notepad.view.NewNoteView;
import com.fiona.notepad.view.NoteAdapter;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    public Cursor cursor;
    public NoteAdapter noteAdapter;
    public boolean isLongPress = false;

    public DeleteView deleteView;
    public NewNoteView newNoteView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //圣诞快乐
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            Date date = sdf.parse("2015-12-25 23:59:59");
            Log.d("test", date.toLocaleString());
            if (System.currentTimeMillis() <= date.getTime()) {
                startActivity(new Intent(this, AppActivity.class));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

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
            tintManager.setTintColor(Color.parseColor("#6699ff"));
        }

        deleteView = (DeleteView) findViewById(R.id.deleteView);
        deleteView.setContext(this);        //使DeleteView获得活动引用
        newNoteView = (NewNoteView) findViewById(R.id.newNoteView);
    }

    /**
     * 加载便签列表
     */
    @Override
    protected void onStart() {
        super.onStart();
        listView = (ListView) findViewById(R.id.listView_note);
        cursor = getContentResolver().query(NoteProvider.CONTENT_URI, null, null, null, null);
        noteAdapter = new NoteAdapter(this, cursor);
        listView.setAdapter(noteAdapter);
        listView.setVerticalScrollBarEnabled(false);        //进度条隐藏

        //点击列表项
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("debug", "点击了 id=" + id);
                if (isLongPress) {
                    noteAdapter.setCheckStyle(position);
                } else {
                    Intent intent = new Intent(getApplication(), EditActivity.class);
                    intent.putExtra("id", id);
                    startActivity(intent);
                }
            }
        });

        //长按列表项
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("debug", "长按");
                if (!isLongPress) {
                    isLongPress = true;
                    //时间不可见,选择框可见
                    noteAdapter.setPressState(true);
                    //添加按钮不可点击，删除按钮可见
                    newNoteView.setEnabled(false);
                    deleteView.setVisibility(View.VISIBLE);
                    new Thread() {
                        @Override
                        public void run() {
                            deleteView.setY(deleteView.getHeight() + deleteView.getY());
                            while (deleteView.getY() > listView.getY() + listView.getHeight()) {
                                Log.d("debug", "删除按钮慢慢出现");
                                deleteView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        deleteView.setY(deleteView.getY() - 20);
                                    }
                                });
                                try {
                                    Thread.sleep(20);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            if (deleteView.getY() + deleteView.getHeight() < newNoteView.getY() + newNoteView.getHeight()) {
                                deleteView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        deleteView.setY(deleteView.getY() + newNoteView.getY() + newNoteView.getHeight() - deleteView.getY() - deleteView.getHeight());
                                    }
                                });
                            }
                        }
                    }.start();
                }
                return false;
            }
        });
    }

    /**
     * 返回键的监听
     *
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (isLongPress) {
                noteAdapter.setPressState(false);
                isLongPress = false;      //退出长按模式

                new Thread() {
                    @Override
                    public void run() {
                        while (deleteView.getY() <= newNoteView.getY() + newNoteView.getHeight()) {
                            newNoteView.post(new Runnable() {
                                @Override
                                public void run() {
                                    deleteView.setY(deleteView.getY() + 20);
                                }
                            });
                            try {
                                Thread.sleep(20);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
