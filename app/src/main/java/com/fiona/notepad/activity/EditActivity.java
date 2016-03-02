package com.fiona.notepad.activity;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.fiona.notepad.R;
import com.fiona.notepad.database.Backup;
import com.fiona.notepad.database.Note;
import com.fiona.notepad.database.NoteProvider;
import com.fiona.notepad.view.EditTopView;
import com.readystatesoftware.systembartint.SystemBarTintManager;

public class EditActivity extends AppCompatActivity {
    EditTopView topView;
    EditText editText;
    Typeface typeface;
    long id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        typeface = Typeface.createFromAsset(getAssets(), "fonts/k.ttf");

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

        topView = (EditTopView) findViewById(R.id.edit_top_view);
        editText = (EditText) findViewById(R.id.editText);
        editText.setTypeface(typeface);

        id = getIntent().getLongExtra("id", -1);
        topView.setText("添加便签");
        if (id != -1) {
            Cursor cursor = getContentResolver().query(NoteProvider.CONTENT_URI,
                    null,
                    "_id = ?",
                    new String[]{String.valueOf(id)},
                    null);
            //导航栏标题改变
            int indexCreateTime = cursor.getColumnIndex(Note._CREATE_TIME);
            cursor.moveToFirst();
            long time = cursor.getLong(indexCreateTime);
            cursor.moveToFirst();
            topView.setText(String.valueOf(DateFormat.format("MM/dd", time)));

            //编辑框显示内容
            int indexContent = cursor.getColumnIndex(Note._CONTENT);
            cursor.moveToFirst();
            String content = cursor.getString(indexContent);
            editText.setText(content);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            insertToDb();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void insertToDb(){
        String content = String.valueOf(editText.getText());
        //插入数据库
        Log.d("debug", content);
        if (editText.getText().length() > 0) {
            if (id == -1) {
                getContentResolver().insert(NoteProvider.CONTENT_URI,
                        new Note(content).getValues());
            } else {
                getContentResolver().update(
                        Uri.withAppendedPath(NoteProvider.CONTENT_URI, String.valueOf(id)),
                        new Note(content).getValues(),
                        "_id = ?",
                        new String[]{String.valueOf(id)}
                );
            }
        } else {
            //输入为空时,有id则从数据库删除
            if (id != -1) {
                Log.d("debug", "输入为空");
                getContentResolver().delete(NoteProvider.CONTENT_URI,
                        "_id = ?",
                        new String[]{String.valueOf(id)});
            }
        }
        //备份数据库
        new BackupTask().execute();
    }

    /**
     * 备份数据库的异步任务
     */
    class BackupTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Backup.backup();
            return null;
        }
    }
}
