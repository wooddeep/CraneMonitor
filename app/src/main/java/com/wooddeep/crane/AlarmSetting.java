package com.wooddeep.crane;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wooddeep.crane.R;
import com.wooddeep.crane.persist.DatabaseHelper;
import com.wooddeep.crane.persist.dao.AlarmSetDao;
import com.wooddeep.crane.persist.dao.CalibrationDao;
import com.wooddeep.crane.persist.entity.AlarmSet;
import com.wooddeep.crane.persist.entity.Calibration;

import java.lang.reflect.Field;
import java.util.List;

@SuppressWarnings("unused")
public class AlarmSetting extends AppCompatActivity {

    class AlarmCell {
        public String propName;
        public int editTextId;
        public Object value;
        public AlarmCell(String pn, int eti, Object v) {
            this.propName = pn;
            this.editTextId = eti;
            this.value = v;
        }
    }

    AlarmCell [] alarmCells = new AlarmCell[] {
        new AlarmCell("t2tDistGear1", R.id.et_gear1, 0),
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_settting);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        confLoad(getApplicationContext());
        //setOnTouchListener();

    }

    private void confLoad(Context contex) {
        DatabaseHelper.getInstance(contex).createTable(AlarmSet.class);
        AlarmSetDao dao = new AlarmSetDao(contex);
        List<AlarmSet> paras = dao.selectAll();
        if (paras == null || paras.size() == 0) {
            dao.insert(new AlarmSet());
        }

        paras = dao.selectAll();
        if (paras.size() < 1) return;
        AlarmSet para = paras.get(0);

        /*
        for (int i = 0; i < ccs.length; i++) {
            CalibrationSetting.CalibrationCell cell = ccs[i];
            cell.setOnClickListener();

            String dimName = cell.dimValueName;
            String rateName = cell.rateValueName;
            String dataName = cell.uartDataName;

            try {
                Field field = Calibration.class.getDeclaredField(dimName);
                field.setAccessible(true);
                Object value = field.get(para);

                EditText et = (EditText) findViewById(cell.dimValueEditTextId);
                if (et != null) {
                    et.setText(value.toString());
                }

                if (rateName != null) {
                    TextView tv = (TextView) findViewById(cell.rateShowId);
                    field = Calibration.class.getDeclaredField(rateName);
                    field.setAccessible(true);
                    value = field.get(para);
                    tv.setText(value.toString());
                }

                if (dataName != null) {
                    TextView tv = (TextView) findViewById(cell.uartDataTextViewId);
                    field = Calibration.class.getDeclaredField(dataName);
                    field.setAccessible(true);
                    value = field.get(para);
                    tv.setText(value.toString());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        */
    }
}
