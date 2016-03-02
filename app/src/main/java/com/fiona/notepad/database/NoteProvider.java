package com.fiona.notepad.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class NoteProvider extends ContentProvider {

    private static final String AUTHORITIES = "com.fiona";      //清单文件中的authorities参数

    private static final String PATH = "note";                         //自己设置

    /**
     * URI 匹配
     */
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int NOTE = 1;
    private static final int NOTE_ID = 2;

    static {
        /**
         * 判断uri格式  不匹配返回 NO_MATCH (-1)
         */
        MATCHER.addURI(AUTHORITIES, "note", NOTE);
        MATCHER.addURI(AUTHORITIES, "note/#", NOTE_ID);
    }

    /**
     * Note 实体的标识
     * uri: content://com.example.fiona/note   整个note
     * uri: content://com.example.fiona/note/1 带参数
     */
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITIES + "/" + PATH);

    /**
     * 数据库 db
     */
    private SQLiteDatabase db;

    /**
     * 删除
     *
     * @param uri
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int n = 0;
        switch (MATCHER.match(uri)) {
            case NOTE:
                n = db.delete(Note.TABLE, selection, selectionArgs);
                break;
            case NOTE_ID:
                String id = uri.getLastPathSegment();
                n = db.delete(Note.TABLE, "_id = ?", new String[]{id});
                break;
            case UriMatcher.NO_MATCH:
                throw new IllegalArgumentException(uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri, null);      //通知数据改变
        return n;
    }

    /**
     * 获得uri的mime类型
     *
     * @param uri
     * @return 返回uri类型 或者  null
     */
    @Override
    public String getType(Uri uri) {
        switch (MATCHER.match(uri)) {
            case NOTE:
                return "vnd.android.cursor.dir" + "/" + "vnd.fiona.note";
            case NOTE_ID:
                return "vnd.android.cursor.item" + "/" + "vnd.fiona.note";
            default:
                return null;
        }
    }

    /**
     * 保存 Note 实例
     *
     * @param uri    资源标识
     * @param values 值（ORM）
     * @return uri 保存成功则返回新数据的uri，否则返回空
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri newUri = null;
        switch (MATCHER.match(uri)) {
            case NOTE:
                long id = db.insert(Note.TABLE, null, values);
                if (id == -1) {
                    return null;
                }
                newUri = Uri.withAppendedPath(uri, String.valueOf(id));    //追加id，返回插入的数据的uri
                break;
            default:
                throw new IllegalArgumentException(uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri, null);      //通知数据改变
        return newUri;
    }

    /**
     * 初始化数据库
     *
     * @return true 表示初始化成功
     */
    @Override
    public boolean onCreate() {
        db = new DbHelper(getContext()).getWritableDatabase();
        return null != db;
    }

    /**
     * 查询
     *
     * @param uri           资源标识
     *                      1：整体
     *                      2：个体
     * @param projection    查询的字段
     * @param selection     过滤
     * @param selectionArgs 过滤参数
     * @param sortOrder     排序方式
     * @return cursor
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch (MATCHER.match(uri)) {
            case NOTE:             //整体
                cursor = db.query(Note.TABLE, projection, selection, selectionArgs, null, sortOrder, null);
                break;
            case NOTE_ID:          //个体
                String id = uri.getLastPathSegment();
                cursor = db.query(Note.TABLE, projection, "_id = ?", new String[]{id}, null, null, null);
                break;
            default:
                throw new IllegalArgumentException(uri.toString());
        }
        return cursor;
    }

    /**
     * 更新
     *
     * @param uri
     * @param values
     * @param selection
     * @param selectionArgs
     * @return
     */
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int n = -1;
        switch (MATCHER.match(uri)) {
            case NOTE:
                n = db.update(Note.TABLE, values, selection, selectionArgs);
                break;
            case NOTE_ID:
                String id = uri.getLastPathSegment();
                n = db.update(Note.TABLE, values, "_id = ?", new String[]{id});
                break;
            default:
                throw new IllegalArgumentException(uri.toString());
        }
        getContext().getContentResolver().notifyChange(uri, null);      //通知数据改变
        return n;
    }
}