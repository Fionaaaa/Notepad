package com.fiona.notepad.view;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.fiona.notepad.R;
import com.fiona.notepad.database.Note;

import java.util.ArrayList;

/**
 * 显示便签的游标适配器
 * Created by fiona on 15-12-22.
 */
public class NoteAdapter extends SimpleCursorAdapter {

    private Typeface typeface;
    private boolean isLongPress = false;
    public ArrayList<Integer> data = new ArrayList<>();

    public NoteAdapter(Context context, Cursor c) {
        super(context, R.layout.item_note, c, new String[]{Note._CONTENT, Note._CREATE_TIME}, new int[]{R.id.textView_content, R.id.textView_create_time}, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/k.ttf");
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        super.bindView(view, context, cursor);

        //设置字体风格
        ((TextView) view.findViewById(R.id.textView_content)).setTypeface(typeface);

        TextView textView = (TextView) view.findViewById(R.id.textView_create_time);
        SureCircleView sureCircleView = (SureCircleView) view.findViewById(R.id.sureCircleView);

        if (isLongPress) {
            textView.setVisibility(View.INVISIBLE);
            sureCircleView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.VISIBLE);
            sureCircleView.setVisibility(View.INVISIBLE);

            long time = cursor.getLong(cursor.getColumnIndex(Note._CREATE_TIME));
            textView.setText(DateFormat.format("MM/dd", time));
        }

        if (data.contains(cursor.getPosition())) {
            sureCircleView.setStyle(SureCircleView.FILL);
        } else {
            sureCircleView.setStyle(SureCircleView.STROKE);
        }
    }

    /**
     * 设置长按状态
     *
     * @param pressState
     */
    public void setPressState(boolean pressState) {
        isLongPress = pressState;
        if (!pressState) {
            data.removeAll(data);
        }
        notifyDataSetChanged();     //  通知数据集发生改变
    }

    /**
     * 设置选择状态
     *
     * @param position
     */
    public void setCheckStyle(int position) {
        if (!data.contains(position)) {
            data.add(position);
        } else {
            data.remove(data.indexOf(position));
        }
        notifyDataSetChanged();
    }

}
