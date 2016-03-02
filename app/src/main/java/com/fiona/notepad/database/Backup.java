package com.fiona.notepad.database;

import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * 将数据库文件备份到SD卡中
 * Created by fiona on 15-12-23.
 */
public class Backup {

    public static final String BACKUP_PATH = "便签";
    public static final String DB_PATH = "data/data/com.fiona.notepad/databases/" + DbHelper.DB_NAME;

    /**
     * 备份数据库到SD卡
     */
    public static void backup() {
        String newPath = Environment.getExternalStorageDirectory() + "/" + BACKUP_PATH + "/" + DbHelper.DB_NAME;
        File file = new File(DB_PATH);
        if (file.exists()) {
            copyFile(file, newPath);
        }
    }

    /**
     * 复制文件
     *
     * @param oldFile 文件对象
     * @param newPath 文件路径
     */
    public static void copyFile(File oldFile, String newPath) {
        try {
            int size;
            File newFile = new File(newPath);
            if (!newFile.exists()) {
                newFile.createNewFile();
            }
            if (oldFile.exists()) {     //源文件存在时
                InputStream in = new FileInputStream(oldFile); // 读入原文件
                FileOutputStream out = new FileOutputStream(newPath);
                byte[] buffer = new byte[1024 * 4];
                while ((size = in.read(buffer)) != -1) {
                    out.write(buffer, 0, size);
                }
                in.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
        }
    }
}
