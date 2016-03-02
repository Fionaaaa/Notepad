package com.fiona.notepad.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.fiona.notepad.R;
import com.fiona.notepad.activity.MainActivity;
import com.fiona.notepad.database.Backup;
import com.fiona.notepad.database.Note;
import com.fiona.notepad.database.NoteProvider;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by fiona on 15-12-24.
 */
public class DeleteView extends View {

    Paint p;
    float w = 0;
    float h = 0;
    Bitmap bitmap;
    float x;
    float y;

    MainActivity context;

    public DeleteView(Context context) {
        this(context, null);
    }

    public DeleteView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_delete_white_36dp);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (w == 0) {
            w = getWidth();
            h = getHeight();
            x = bitmap.getWidth();
            y = bitmap.getHeight();
        }
        canvas.drawBitmap(bitmap, w / 2 - x / 2, h / 2 - y / 2, p);
    }

    /**
     * 点击删除按钮
     *
     * @param event
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("debug_1", "点击删除按钮");
        float x = event.getX();
        float y = event.getY();
        if (x >= w / 2 - this.x / 2 && x <= w / 2 + this.x / 2 && y >= h / 2 - this.y / 2 && y <= h / 2 + this.y / 2 && context != null) {
            new Thread() {
                @Override
                public void run() {
                    while (context.deleteView.getY() <= context.newNoteView.getY() + context.newNoteView.getHeight()) {
                        post(new Runnable() {
                            @Override
                            public void run() {
                                context.deleteView.setY(context.deleteView.getY() + 20);
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
            /**
             * 删除操作
             */
            Cursor c = context.getContentResolver().query(NoteProvider.CONTENT_URI, null, null, null, null);
            ArrayList<Integer> index = context.noteAdapter.data;
            while (c.moveToNext()) {
                for (int position : index) {
                    if (c.getPosition() == position) {
                        long id = c.getLong(c.getColumnIndex(Note._ID));
                        context.getContentResolver().delete(NoteProvider.CONTENT_URI, "_id =?", new String[]{String.valueOf(id)});
                        Log.d("debug", "删除了" + id);
                    }
                }
            }

            if (index.size() > 0) {
                Snackbar.make(context.deleteView, "正在删除...", Snackbar.LENGTH_LONG)
                        .setAction("撤销", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(context, "已撤销删除", Toast.LENGTH_SHORT).show();
                                //撤销删除：从备份数据库找回
                                new AsyncTask<Void, Void, Void>() {

                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        File file = new File(Environment.getExternalStorageDirectory() + "/" + Backup.BACKUP_PATH + "/notepad.db");
                                        if (file.exists()) {
                                            Backup.copyFile(file, Backup.DB_PATH);
                                        }
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        context.cursor.requery();   //  重新查询
                                    }
                                }.execute();
                            }
                        })
                        .show();
                context.cursor.requery();   //  重新查询
            }

            //退出长按模式
            context.noteAdapter.setPressState(false);
            context.isLongPress = false;
        }
        return true;
    }

    /**
     * 获得活动引用
     *
     * @param obj
     */
    public void setContext(Object obj) {
        context = (MainActivity) obj;
    }
}
