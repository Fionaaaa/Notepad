package com.fiona.notepad;

import android.app.Application;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.fiona.notepad.activity.AppActivity;
import com.fiona.notepad.database.Backup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by fiona on 15-12-23.
 */
public class App extends Application {

    public boolean isFirst;

    @Override
    public void onCreate() {
        Log.d("App", "程序开启");
        super.onCreate();

        /**
         * 程序第一次执行
         */
        isFirst = getSharedPreferences("notepad", MODE_PRIVATE).getBoolean("isFirst", true);
        if (isFirst) {
            //初始化
            getSharedPreferences("notepad", MODE_PRIVATE).edit().putBoolean("isFirst", false).commit();
            createReadme();     //创建说明书
            importBackup();     //导入数据库
        }
    }

    /**
     * 导入数据库
     */
    void importBackup() {
        Log.d("debug", "从sd导入数据库");
        File fileSd = new File(Environment.getExternalStorageDirectory() + "/" + Backup.BACKUP_PATH + "/notepad.db");
        if (fileSd.exists()) {
            //如果SD卡存在数据库，则导入外部数据库
            Backup.copyFile(fileSd, Backup.DB_PATH);
        } else {
            try {
                fileSd.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //如果不存在外部数据库，则导入程序自带的数据库,并且备份一份到sd卡
            try {
                InputStream in = getAssets().open("notepad.db");
                File dbFile = new File(Backup.DB_PATH);
                if (!dbFile.exists()) {
                    dbFile.createNewFile();
                }
                FileOutputStream out = new FileOutputStream(dbFile);
                FileOutputStream outToSd = new FileOutputStream(fileSd);
                byte[] buf = new byte[1024 * 4];
                int size;
                while ((size = in.read(buf)) != -1) {
                    out.write(buf, 0, size);      //  写到程序
                    outToSd.write(buf, 0, size);  //写到sd卡
                }
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 程序安装后创建说明书：便签使用说明.txt
     */
    void createReadme() {
        Log.d("debug", "创建readme文件");
        File folder = new File(Environment.getExternalStorageDirectory() + "/" + Backup.BACKUP_PATH);
        if (!folder.exists()) {
            folder.mkdir();
        }
        File file = new File(folder, "便签使用说明.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            file.delete();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            InputStream in = getAssets().open("readme.txt");
            FileOutputStream out = new FileOutputStream(file);
            int size;
            byte[] buf = new byte[1024 * 4];
            while ((size = in.read(buf)) != -1) {
                out.write(buf, 0, size);
            }
            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
