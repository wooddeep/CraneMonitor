package com.wooddeep.crane.views;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.text.InputType;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.wooddeep.crane.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by niuto on 2019/9/10.
 */

public class FixedTitleTable {
    private Activity activity;
    private ScrollView contentVerticle, firstColumn;
    private HorizontalScrollView firstRowView, contentHorizontalView;

    private int screenWidth;

    public FixedTitleTable(int sw) {
        this.screenWidth = sw;
    }

    public int textSize = 24;


    @TargetApi(Build.VERSION_CODES.M)
    public void init(Activity activity, List<Integer> widthList) {
        this.activity = activity;
        contentVerticle = (ScrollView) activity.findViewById(R.id.content_verticle);
        firstColumn = (ScrollView) activity.findViewById(R.id.first_column);
        contentVerticle.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                firstColumn.scrollTo(scrollX, scrollY);
            }
        });
        firstColumn.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                contentVerticle.scrollTo(scrollX, scrollY);
            }
        });

        firstRowView = (HorizontalScrollView) activity.findViewById(R.id.first_row);
        firstRowView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                contentHorizontalView.scrollTo(scrollX, scrollY);
            }
        });

        contentHorizontalView = (HorizontalScrollView) activity.findViewById(R.id.content_horizontal);
        contentHorizontalView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                firstRowView.scrollTo(scrollX, scrollY);
            }
        });


    }

    private int fisrtColWidth = 400;

    public List<View> getColumn(int index) {
        List<View> viewList = new ArrayList<>();
        TableLayout contentTable = (TableLayout) activity.findViewById(R.id.content_talble);

        for (int col = 0; col < contentTable.getChildCount(); col++) {
            TableRow row = (TableRow) contentTable.getChildAt(col); // 遍历行
            viewList.add(row.getChildAt(index)); // 取列值
        }
        return viewList;
    }

    public int getColNum() {
        TableLayout contentTable = (TableLayout) activity.findViewById(R.id.content_talble);
        TableRow row = (TableRow) contentTable.getChildAt(0); // 遍历行
        return row.getChildCount();
    }

    public void setMainColumn(int id) {
        TableRow firstRow = (TableRow) activity.findViewById(R.id.first_row_row);
        for (int i = 0; i < firstRow.getChildCount(); i++) {
            TextView textView = (TextView) firstRow.getChildAt(i);
            if (id == textView.getId()) {
                TextPaint paint = textView.getPaint();
                paint.setFakeBoldText(true);
                textView.setTextColor(Color.RED);
            } else {
                TextPaint paint = textView.getPaint();
                paint.setFakeBoldText(false);
                textView.setTextColor(Color.DKGRAY);
            }
        }
    }

    public void setFirstRow(List<TableCell> colNames, List<Integer> idList, List<Integer> widthList) {
        float colWidth = 200;
        float spareWidth = this.screenWidth - fisrtColWidth;
        float avgWidth = (float) spareWidth / (colNames.size() - 1);
        if (avgWidth >= 200) {
            colWidth = avgWidth;
        }

        TableLayout tableTitle = (TableLayout) activity.findViewById(R.id.title_table);

        TextView title = (TextView) activity.findViewById(R.id.title_tv);
        title.setTextSize(textSize);
        title.setText(colNames.get(0).value);

        //title.setWidth(fisrtColWidth); // TODO
        if (widthList == null) {
            title.setWidth(fisrtColWidth);
        } else {
            title.setWidth(widthList.get(0));
        }

        TextPaint tp = title.getPaint();
        tp.setFakeBoldText(true);
        TableLayout firstRowTable = (TableLayout) activity.findViewById(R.id.first_row_table);
        TableRow firstRow = (TableRow) activity.findViewById(R.id.first_row_row);
        firstRow.removeAllViews();

        for (int i = 1; i < colNames.size(); i++) {
            TextView view = (TextView) LayoutInflater.from(activity).inflate(R.layout.table_title_row_cell_tv, null);
            //view.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
            view.setId(idList.get(i));
            view.setText(colNames.get(i).value);
            view.setHeight(60);
            view.setTextSize(textSize);

            //view.setWidth((int) colWidth);
            if (widthList == null) {
                view.setWidth((int) colWidth);
            } else {
                view.setWidth(widthList.get(i));
            }

            view.setBackgroundColor(Color.rgb(176, 196, 222));
            view.setGravity(Gravity.CENTER);
            view.setTextColor(Color.DKGRAY);
            view.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            if (colNames.get(i).clickListener != null)
                view.setOnClickListener(colNames.get(i).clickListener);
            firstRow.addView(view); // 先添加控件
            TableRow.LayoutParams lp = (TableRow.LayoutParams) view.getLayoutParams();
            lp.setMargins(0, 0, 2, 0); // 再设置margin
        }
        tableTitle.setVisibility(View.VISIBLE);
        firstRowTable.setVisibility(View.VISIBLE);
    }

    /*
    public void setFirstRow(List<TableCell> colNames, List<Integer> idList, List<Integer> widthList) {
        TableLayout tableTitle = (TableLayout) activity.findViewById(R.id.title_table);
        TextView title = (TextView) activity.findViewById(R.id.title_tv);
        title.setTextSize(textSize);
        title.setText(colNames.get(0).value);

        title.setWidth(widthList.get(0)); // TODO

        TextPaint tp = title.getPaint();
        tp.setFakeBoldText(true);
        TableLayout firstRowTable = (TableLayout) activity.findViewById(R.id.first_row_table);
        TableRow firstRow = (TableRow) activity.findViewById(R.id.first_row_row);
        firstRow.removeAllViews();

        for (int i = 1; i < colNames.size(); i++) {
            TextView view = (TextView) LayoutInflater.from(activity).inflate(R.layout.table_title_row_cell_tv, null);
            //view.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));//加粗
            view.setId(idList.get(i));
            view.setText(colNames.get(i).value);
            view.setHeight(60);
            view.setTextSize(textSize);

            view.setWidth((int) widthList.get(i));
            view.setBackgroundColor(Color.rgb(176, 196, 222));
            view.setGravity(Gravity.CENTER);
            view.setTextColor(Color.DKGRAY);
            view.setTypeface(Typeface.MONOSPACE, Typeface.BOLD);
            if (colNames.get(i).clickListener != null)
                view.setOnClickListener(colNames.get(i).clickListener);
            firstRow.addView(view); // 先添加控件
            TableRow.LayoutParams lp = (TableRow.LayoutParams) view.getLayoutParams();
            lp.setMargins(0, 0, 2, 0); // 再设置margin
        }
        tableTitle.setVisibility(View.VISIBLE);
        firstRowTable.setVisibility(View.VISIBLE);
    }
    */

    public void clearAll() {
        TableLayout firstColTable = (TableLayout) activity.findViewById(R.id.first_column_table);
        TableLayout contentTable = (TableLayout) activity.findViewById(R.id.content_talble);
        firstColTable.removeAllViews();
        contentTable.removeAllViews();
    }


    public List<View> addDataRow(List<TableCell> cells, List<Integer> widthList, boolean visible) {
        float colWidth = 200;
        float spareWidth = this.screenWidth - fisrtColWidth;
        float avgWidth = (float) spareWidth / (cells.size() - 1);
        if (avgWidth >= 200) {
            colWidth = avgWidth;
        }

        List<View> outList = new ArrayList<View>();

        TableLayout firstColTable = (TableLayout) activity.findViewById(R.id.first_column_table);
        // 添加一行的第一个单元
        TableRow firstRow = (TableRow) LayoutInflater.from(activity).inflate(R.layout.table_row, null);
        firstRow.setBackgroundColor(Color.WHITE);
        TextView view = (TextView) LayoutInflater.from(activity).inflate(R.layout.table_title_row_cell_tv, null);
        view.setText(cells.get(0).value);
        view.setTextSize(textSize);
        view.setHeight(60);

        if (widthList == null) {
            view.setWidth(fisrtColWidth);
        } else {
            view.setWidth(widthList.get(0));
        }

        view.setBackgroundColor(0x00000000);
        view.setGravity(Gravity.CENTER);
        view.setTextColor(Color.DKGRAY);
        view.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
        firstRow.addView(view); // 先添加控件
        firstColTable.addView(firstRow);
        TableLayout.LayoutParams lp = (TableLayout.LayoutParams) firstRow.getLayoutParams();
        lp.setMargins(2, 0, 2, 2); // 再设置margin

        // 添加其他单元格
        TableLayout contentTable = (TableLayout) activity.findViewById(R.id.content_talble);
        TableRow contentRow = (TableRow) LayoutInflater.from(activity).inflate(R.layout.table_row, null);
        if (visible) {
            contentRow.setVisibility(View.VISIBLE);
            firstRow.setVisibility(View.VISIBLE);
        }

        for (int i = 1; i < cells.size(); i++) {
            TableCell cell = cells.get(i);

            switch (cell.type) {
                case 0:
                    TextView contentView = (TextView) LayoutInflater.from(activity).inflate(R.layout.table_title_row_cell_tv, null);
                    outList.add(contentView);
                    contentView.setHeight(60);
                    //contentView.setWidth((int) colWidth);

                    if (widthList == null) {
                        contentView.setWidth((int) colWidth);
                    } else {
                        contentView.setWidth(widthList.get(i));
                    }

                    contentView.setBackgroundColor(Color.WHITE);
                    contentView.setGravity(Gravity.CENTER);
                    contentView.setTextColor(Color.DKGRAY);
                    contentView.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
                    contentRow.addView(contentView); // 先添加控件
                    TableRow.LayoutParams rowLp = (TableRow.LayoutParams) contentView.getLayoutParams();
                    rowLp.setMargins(0, 0, 2, 0); // 再设置margin
                    contentView.setText(cell.value);
                    contentView.setTextSize(textSize);
                    if (cell.clickListener != null)
                        contentView.setOnClickListener(cell.clickListener);
                    break;
                case 1:
                    EditText editor = (EditText) LayoutInflater.from(activity).inflate(R.layout.table_title_row_cell_et, null);
                    outList.add(editor);
                    editor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_TEXT);
                    editor.setHeight(60);
                    //editor.setWidth((int) colWidth);

                    if (widthList == null) {
                        editor.setWidth((int) colWidth);
                    } else {
                        editor.setWidth(widthList.get(i));
                    }

                    editor.setBackgroundColor(Color.WHITE);
                    editor.setTextColor(Color.DKGRAY);
                    editor.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
                    contentRow.addView(editor); // 先添加控件
                    rowLp = (TableRow.LayoutParams) editor.getLayoutParams();
                    rowLp.setMargins(0, 0, 2, 0); // 再设置margin
                    editor.setText(cell.value);
                    editor.setTextSize(textSize);

                    if (cell.clickListener != null) editor.setOnClickListener(cell.clickListener);
                    break;
                case 2:
                    TextView button = (TextView) LayoutInflater.from(activity).inflate(R.layout.table_title_row_cell_tv, null);
                    outList.add(button);
                    button.setHeight(60);
                    //button.setWidth((int) colWidth);
                    if (widthList == null) {
                        button.setWidth((int) colWidth);
                    } else {
                        button.setWidth(widthList.get(i));
                    }

                    button.setBackgroundColor(Color.WHITE);
                    button.setGravity(Gravity.CENTER);
                    button.setTextColor(Color.DKGRAY);
                    button.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
                    contentRow.addView(button); // 先添加控件
                    button.setTag(cell.getPrivData()); // 存储私有数据
                    rowLp = (TableRow.LayoutParams) button.getLayoutParams();
                    rowLp.setMargins(0, 0, 2, 0); // 再设置margin
                    JSONArray opts = cell.privData.optJSONArray("options");
                    button.setText(opts.optString(Integer.parseInt(cell.value)));
                    int index = Integer.parseInt(cell.value);
                    button.setId(index);
                    button.setTextSize(textSize);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            int newId = (id + 1) % opts.length();
                            String value = opts.optString(newId);
                            ((TextView) v).setText(value);
                            ((TextView) v).setTextSize(textSize);
                            v.setId(newId);
                        }
                    });

                    break;

                case 3:
                    editor = (EditText) LayoutInflater.from(activity).inflate(R.layout.table_title_row_cell_et, null);
                    outList.add(editor);
                    editor.setHeight(60);
                    //editor.setWidth((int) colWidth);
                    if (widthList == null) {
                        editor.setWidth((int) colWidth);
                    } else {
                        editor.setWidth(widthList.get(i));
                    }

                    editor.setBackgroundColor(Color.WHITE);
                    //editor.setGravity(Gravity.CENTER);
                    editor.setTextColor(Color.DKGRAY);
                    editor.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
                    contentRow.addView(editor); // 先添加控件
                    rowLp = (TableRow.LayoutParams) editor.getLayoutParams();
                    rowLp.setMargins(0, 0, 2, 0); // 再设置margin
                    editor.setText(cell.value);
                    editor.setTextSize(textSize);

                    if (cell.clickListener != null)
                        editor.setOnClickListener(cell.clickListener);
                    break;

                default:
                    break;
            }
        }
        contentRow.setBackgroundColor(Color.LTGRAY);
        contentTable.addView(contentRow);
        lp = (TableLayout.LayoutParams) contentRow.getLayoutParams();
        lp.setMargins(0, 0, 2, 2); // 再设置margin
        return outList;
    }

    /*
    public void addDataRow(List<TableCell> cells, boolean visible, List<Integer> widthList) {
        TableLayout firstColTable = (TableLayout) activity.findViewById(R.id.first_column_table);
        // 添加一行的第一个单元
        TableRow firstRow = (TableRow) LayoutInflater.from(activity).inflate(R.layout.table_row, null);
        firstRow.setBackgroundColor(Color.WHITE);
        TextView view = (TextView) LayoutInflater.from(activity).inflate(R.layout.table_title_row_cell_tv, null);
        view.setText(cells.get(0).value);
        view.setTextSize(textSize);
        view.setHeight(60);
        view.setWidth(widthList.get(0));

        view.setBackgroundColor(0x00000000);
        view.setGravity(Gravity.CENTER);
        view.setTextColor(Color.DKGRAY);
        view.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
        firstRow.addView(view); // 先添加控件
        firstColTable.addView(firstRow);
        TableLayout.LayoutParams lp = (TableLayout.LayoutParams) firstRow.getLayoutParams();
        lp.setMargins(2, 0, 2, 2); // 再设置margin

        // 添加其他单元格
        TableLayout contentTable = (TableLayout) activity.findViewById(R.id.content_talble);
        TableRow contentRow = (TableRow) LayoutInflater.from(activity).inflate(R.layout.table_row, null);

        if (visible) {
            contentRow.setVisibility(View.VISIBLE);
            firstRow.setVisibility(View.VISIBLE);
        }

        for (int i = 1; i < cells.size(); i++) {
            TableCell cell = cells.get(i);

            switch (cell.type) {
                case 0:
                    TextView contentView = (TextView) LayoutInflater.from(activity).inflate(R.layout.table_title_row_cell_tv, null);
                    contentView.setHeight(60);
                    contentView.setWidth((int) widthList.get(i));
                    contentView.setBackgroundColor(Color.WHITE);
                    contentView.setGravity(Gravity.CENTER);
                    contentView.setTextColor(Color.DKGRAY);
                    contentView.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
                    contentRow.addView(contentView); // 先添加控件
                    TableRow.LayoutParams rowLp = (TableRow.LayoutParams) contentView.getLayoutParams();
                    rowLp.setMargins(0, 0, 2, 0); // 再设置margin
                    contentView.setText(cell.value);
                    contentView.setTextSize(textSize);
                    if (cell.clickListener != null)
                        contentView.setOnClickListener(cell.clickListener);
                    break;
                case 1:
                    EditText editor = (EditText) LayoutInflater.from(activity).inflate(R.layout.table_title_row_cell_et, null);
                    editor.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_NUMBER_FLAG_SIGNED);
                    //editor.setInputType(InputType.TYPE_CLASS_TEXT);
                    editor.setHeight(60);
                    editor.setWidth((int) widthList.get(i));
                    editor.setBackgroundColor(Color.WHITE);
                    editor.setGravity(Gravity.CENTER);
                    editor.setTextColor(Color.DKGRAY);
                    editor.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
                    contentRow.addView(editor); // 先添加控件
                    rowLp = (TableRow.LayoutParams) editor.getLayoutParams();
                    rowLp.setMargins(0, 0, 2, 0); // 再设置margin
                    editor.setText(cell.value);
                    editor.setTextSize(textSize);
                    if (cell.clickListener != null)
                        editor.setOnClickListener(cell.clickListener);
                    break;
                case 2:
                    TextView button = (TextView) LayoutInflater.from(activity).inflate(R.layout.table_title_row_cell_tv, null);
                    button.setHeight(60);
                    button.setWidth((int) widthList.get(i));
                    button.setBackgroundColor(Color.WHITE);
                    button.setGravity(Gravity.CENTER);
                    button.setTextColor(Color.DKGRAY);
                    button.setTypeface(Typeface.MONOSPACE, Typeface.NORMAL);
                    contentRow.addView(button); // 先添加控件
                    button.setTag(cell.getPrivData()); // 存储私有数据
                    rowLp = (TableRow.LayoutParams) button.getLayoutParams();
                    rowLp.setMargins(0, 0, 2, 0); // 再设置margin
                    JSONArray opts = cell.privData.optJSONArray("options");
                    button.setText(opts.optString(Integer.parseInt(cell.value)));
                    int index = Integer.parseInt(cell.value);
                    button.setId(index);
                    button.setTextSize(textSize);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int id = v.getId();
                            int newId = (id + 1) % opts.length();
                            String value = opts.optString(newId);
                            ((TextView) v).setText(value);
                            ((TextView) v).setTextSize(textSize);
                            v.setId(newId);
                        }
                    });
                    break;
                default:
                    break;
            }
        }
        contentRow.setBackgroundColor(Color.LTGRAY);
        contentTable.addView(contentRow);
        lp = (TableLayout.LayoutParams) contentRow.getLayoutParams();
        lp.setMargins(0, 0, 2, 2); // 再设置margin
    }
    */

    public List<List<String>> getCurrData() {
        List<List<String>> out = new ArrayList();

        TableLayout firstRowTable = (TableLayout) activity.findViewById(R.id.first_row_table);

        TableRow titleRow = (TableRow) firstRowTable.getChildAt(0);
        List<String> idList = new ArrayList();
        for (int i = 0; i < titleRow.getChildCount(); i++) {
            TextView tv = (TextView) titleRow.getChildAt(i);
            idList.add(String.valueOf(tv.getId())); // 对应每一列数据的ID
        }
        out.add(idList);

        TableLayout contentTable = (TableLayout) activity.findViewById(R.id.content_talble);
        int rows = contentTable.getChildCount();
        for (int i = 0; i < rows; i++) {
            TableRow row = (TableRow) contentTable.getChildAt(i);
            List<String> valList = new ArrayList();

            int cols = row.getChildCount();
            for (int j = 0; j < cols; j++) {
                TextView tv = (TextView) row.getChildAt(j);
                Object tag = tv.getTag();
                if (tag != null && tag instanceof JSONObject) {
                    String showValue = tv.getText().toString();
                    JSONArray options = ((JSONObject) tag).optJSONArray("options");
                    for (int k = 0; k < options.length(); k++) {
                        if ((options).optString(k).equals(showValue)) {
                            valList.add(String.valueOf(k));
                            break;
                        }
                    }
                } else {
                    valList.add(tv.getText().toString());
                }
            }
            out.add(valList);
        }

        return out;
    }
}
