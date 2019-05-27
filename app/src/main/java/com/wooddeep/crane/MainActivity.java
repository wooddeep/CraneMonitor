package com.wooddeep.crane;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.test.suitebuilder.annotation.Suppress;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

import com.wooddeep.crane.ebus.UserEvent;
import com.wooddeep.crane.element.BaseElem;
import com.wooddeep.crane.element.CenterCycle;
import com.wooddeep.crane.element.ElemMap;
import com.wooddeep.crane.element.SideArea;
import com.wooddeep.crane.element.SideCycle;
import com.wooddeep.crane.views.Vertex;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


// 启动mumu之后, 输入：
// adb connect 127.0.0.1:7555
// 然后再调试, 就ok了

// eventbus for android
// https://www.jianshu.com/p/428a5257839c

/*
dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation 'com.android.support:appcompat-v7:26.0.0-alpha1'
    implementation 'com.android.support.constraint:constraint-layout:1.0.1'
    implementation 'org.locationtech.jts:jts-core:1.15.0'
    implementation 'org.greenrobot:eventbus:3.0.0'
    }
*/

/**
 * 获取主界面FrameLayout的坐标及长宽
 **/
/*
@Override
public void onWindowFocusChanged (boolean hasFocus) {
    FrameLayout mainFrame = findViewById(R.id.main_frame);
    int[] location = new int[2];
    mainFrame.getLocationInWindow(location);
    Context context = getApplicationContext();
    int width = mainFrame.getMeasuredWidth(); // 获取组件宽度
    int height = mainFrame.getMeasuredHeight(); // 获取组件高度
    Log.i(TAG, String.format("## MainFrame: x0:%d, y0:%d, width:%d, height:%d\n", location[0], location[1], width, height));
}
*/

// 圆环设置
/*
SuperCircleView mSuperCircleView;
mSuperCircleView = findViewById(R.id.superview);
mSuperCircleView.setValue(100);
mSuperCircleView.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        //随机设定圆环大小
        int i = new Random().nextInt(100) + 1;
        //int i = 2;
        Log.i(TAG, "onClick: i::" + i);
        mSuperCircleView.setValue(i);
    }
});
*/

// 进度条
/*
final SimpleProgressbar spb = (SimpleProgressbar) findViewById(R.id.spb);
final int max = spb.getMax();
new Thread(new Runnable() {
    @Override
    public void run() {
        int progress = spb.getProgress();
        while ((progress + 1) <= max) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            spb.setProgress(progress + 1);
            progress = progress + 1;
        }
    }
}).start();

Coordinate pointer0 = new Coordinate(0, 0);
Coordinate pointer1 = new Coordinate(0, 10);
double distance = pointer0.distance(pointer1);
try {
    Geometry g1 = new WKTReader().read("LINESTRING (0 10, 10 0)");
    Geometry g2 = new WKTReader().read("POINTSTRING (0 0)");
    double d = g1.distance(g2);
    Log.d(TAG, "### this distance = " + d);
} catch (ParseException e) {
    e.printStackTrace();
}

*/

@SuppressWarnings("unused")
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final ElemMap elemMap = new ElemMap();
    private String mainCycleId = null; // 主环的uuid
    private String sideCycleId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);
    }

    // 定义处理接收的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(UserEvent userEvent) {
        try {
            elemMap.alarmJudge(mainCycleId);
            Float distance = elemMap.distanceMap.get(sideCycleId);
            EditText distanceEditText = findViewById(R.id.distance);
            distanceEditText.setText(String.format("%.2f", distance).toString());
            CenterCycle cc = (CenterCycle)elemMap.getElem(mainCycleId);
            EditText limitEditText = findViewById(R.id.alarm_limit);
            try {
                float limit = Float.parseFloat(limitEditText.getText().toString());
                if (distance <= limit) {
                    elemMap.getElem(mainCycleId);
                    cc.setAlarm(!cc.getAlarm());
                } else {
                    cc.setAlarm(false);
                }

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(
                            new UserEvent("Mr.sorrow", "123456", mainCycleId, null)
                        );
                    }
                }, 500);


            } catch (Exception e) {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        FrameLayout mainFrame = (FrameLayout) findViewById(R.id.main_frame);
        final CenterCycle centerCycle = new CenterCycle(1.0f, 100, 100, 80 / 2, 10, 45, 30);
        elemMap.addElem(centerCycle.getUuid(), centerCycle);
        mainCycleId = centerCycle.getUuid();
        centerCycle.drawCenterCycle(this, mainFrame);

        float scale = centerCycle.scale; //DrawTool.DrawCenterCycle(this, mainFrame, 1.0f, 80 / 2, 10, 45, 30);
        SideCycle sideCycle = new SideCycle(scale, 100, 100, 130, 130, 60 / 2, 10, -45, 0);
        elemMap.addElem(sideCycle.getUuid(), sideCycle);
        sideCycleId = sideCycle.getUuid();
        sideCycle.drawSideCycle(this, mainFrame);

        List<Vertex> vertex1 = new ArrayList<Vertex>() {{
            add(new Vertex(35, 75));
            add(new Vertex(35, 125));
            add(new Vertex(60, 100));
        }};

        SideArea sideArea = new SideArea(Color.GREEN, scale, 100, 100, vertex1);
        elemMap.addElem(sideArea.getUuid(), sideArea);
        sideArea.drawSideArea(this, mainFrame);

        List<Vertex> vertex2 = new ArrayList<Vertex>() {{
            add(new Vertex(160, 130));
            add(new Vertex(175, 160));
            add(new Vertex(190, 130));
        }};

        SideArea sideArea1 = new SideArea(Color.RED, scale, 100, 100, vertex2);
        elemMap.addElem(sideArea1.getUuid(), sideArea1);
        sideArea1.drawSideArea(this, mainFrame);

        findViewById(R.id.adjust_hangle_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 发送消息
                centerCycle.hAngleAdd(1);
                EventBus.getDefault().post(
                    new UserEvent("Mr.sorrow", "123456", centerCycle.getUuid(), null)
                );
            }
        });

        findViewById(R.id.adjust_hangle_sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerCycle.hAngleSub(1);
                EventBus.getDefault().post(
                    new UserEvent("Mr.sorrow", "123456", centerCycle.getUuid(), null)
                );

            }
        });

        findViewById(R.id.adjust_vangle_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerCycle.vAngleAdd(1);
            }
        });

        findViewById(R.id.adjust_vangle_sub).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                centerCycle.vAngleSub(1);
            }
        });

    }

}
