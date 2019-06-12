package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.example.x6.serial.SerialPort;
import com.wooddeep.crane.ebus.MessageEvent;
import com.wooddeep.crane.ebus.UserEvent;
import com.wooddeep.crane.element.CenterCycle;
import com.wooddeep.crane.element.ElemMap;
import com.wooddeep.crane.element.SideArea;
import com.wooddeep.crane.element.SideCycle;
import com.wooddeep.crane.persist.dao.CraneParaDao;
import com.wooddeep.crane.persist.entity.CranePara;
import com.wooddeep.crane.tookit.AnimUtil;
import com.wooddeep.crane.tookit.DrawTool;
import com.wooddeep.crane.views.GridLineView;
import com.wooddeep.crane.views.Vertex;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


// 启动mumu之后, 输入：
// adb connect 127.0.0.1:7555
// 然后再调试, 就ok了

// eventbus for android
// https://www.jianshu.com/p/428a5257839c

// set 按钮的前景 后台
// https://blog.csdn.net/oathevil/article/details/23707359

// FrameLayout 分层权重问题
// https://www.jianshu.com/p/c1d17a39bc09

// 仿qq弹出菜单
// https://github.com/Zhaoss/HintPopupWindow

// 仿苹果对话框
// https://github.com/saiwu-bigkoo/Android-AlertView

// 自定义按钮
// https://www.cnblogs.com/Free-Thinker/p/5571876.html

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

    private float oscale = 1.0f;

    private String mainCycleId = null; // 主环的uuid
    private String sideCycleId = null;

    Timer timer = new Timer();
    TimerTask task = new TimerTask() {
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new MessageEvent("Mr.sorrow", "123456"));
                }
            });
        }
    };

    public float getOscale() {
        return oscale;
    }

    public void setOscale(float oscale) {
        this.oscale = oscale;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EventBus.getDefault().register(this);

        timer.schedule(task, 1000, 500);

        try {
            SerialPort serialttyS1 =  new SerialPort( new File( "/dev/ttyS1"),115200,0);
            InputStream ttyS1InputStream =  serialttyS1.getInputStream();
            OutputStream ttyS1OutputStream =  serialttyS1.getOutputStream();
        }  catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 定义处理接收的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void messageEventBus(MessageEvent userEvent) {
        //fanRotate();
        elemMap.alramFlink();
    }

    // 定义处理接收的方法, MAIN方法: 事件处理放在main方法中
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void userEventBus(UserEvent userEvent) {
        try {
            //EditText limitEditText = (EditText) findViewById(R.id.alarm_limit);
            //float limit = Float.parseFloat(limitEditText.getText().toString());
            float limit = 5;
            elemMap.alarmJudge(mainCycleId, limit); // 告警判断
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setOnTouchListener(View view) {
        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ObjectAnimator oa = ObjectAnimator.ofFloat(view,
                    "scaleX", 0.93f, 1f);
                    oa.setDuration(500);
                    ObjectAnimator oa2 = ObjectAnimator.ofFloat(view,
                    "scaleY", 0.93f, 1f);
                    oa2.setDuration(700);
                    oa.start();
                    oa2.start();
                }
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ObjectAnimator oa = ObjectAnimator.ofFloat(view,
                    "scaleX", 1f, 0.93f);
                    oa.setDuration(500);
                    ObjectAnimator oa2 = ObjectAnimator.ofFloat(view,
                    "scaleY", 1f, 0.93f);
                    oa2.setDuration(700);
                    oa.start();
                    oa2.start();
                }
                return false;
            }
        };
        view.setOnTouchListener(onTouchListener);
    }

    private void fanRotate() {
        ImageView windSpeedLog = (ImageView) findViewById(R.id.wind_speed_log);
        Log.i(TAG, "## start rotate!!!");
        //上述参数解释分别为：旋转起始角度，旋转结束角度，相对与自身，x轴方向的一半，相对于自身，y轴方向的一半

        RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.54f);
        rotateAnimation.setDuration(500);
        windSpeedLog.setAnimation(rotateAnimation);
        windSpeedLog.startAnimation(rotateAnimation);
    }


    private void setAdjustButton() {
        /*
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
        */
    }

    private List<CranePara> confLoad(Context contex) {
        CraneParaDao dao = new CraneParaDao(contex);
        List<CranePara> paras = dao.getAllCranePara();
        return paras;
    }

    private void renderMain(float oscale) {
        FrameLayout mainFrame = (FrameLayout) findViewById(R.id.main_frame);
        DrawTool.drawGrid(this, mainFrame);
        List<CranePara> paras = confLoad(getApplicationContext());
        elemMap.delElems(mainFrame);

        // 画中心圆环，目前暂时以此环
        final CenterCycle centerCycle = new CenterCycle(oscale, 150, 150, 80 / 2, 10, 45, 30);
        elemMap.addElem(centerCycle.getUuid(), centerCycle);
        mainCycleId = centerCycle.getUuid();
        centerCycle.drawCenterCycle(this, mainFrame);

        // 根据数据库的数据画图
        for (CranePara cp : paras) {
            float scale = centerCycle.scale; //DrawTool.DrawCenterCycle(this, mainFrame, 1.0f, 80 / 2, 10, 45, 30);
            SideCycle sideCycle = new SideCycle(scale, 150, 150, cp.getCoordX1(),
                cp.getCoordY1(), cp.getBigArmLength(), 10, -45, 0);

            elemMap.addElem(sideCycle.getUuid(), sideCycle);
            sideCycleId = sideCycle.getUuid();
            sideCycle.drawSideCycle(this, mainFrame);
        }

        /*
        float scale = centerCycle.scale; //DrawTool.DrawCenterCycle(this, mainFrame, 1.0f, 80 / 2, 10, 45, 30);
        SideCycle sideCycle = new SideCycle(scale, 100, 100, 130, 130, 60 / 2, 10, -45, 0);
        elemMap.addElem(sideCycle.getUuid(), sideCycle);
        sideCycleId = sideCycle.getUuid();
        sideCycle.drawSideCycle(this, mainFrame);
        */

        /*
        List<Vertex> vertex1 = new ArrayList<Vertex>() {{
            add(new Vertex(0, 25));
            add(new Vertex(55, 25));
            add(new Vertex(95, 75));
            add(new Vertex(75, 99));
            add(new Vertex(25, 50));
        }};

        SideArea sideArea = new SideArea(Color.GRAY, scale, 100, 100, vertex1);
        elemMap.addElem(sideArea.getUuid(), sideArea);
        sideArea.drawSideArea(this, mainFrame);
        */

        findViewById(R.id.menu).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView btnMenu = (ImageView) findViewById(R.id.menu);
                LinearLayout menuExpand = (LinearLayout) findViewById(R.id.menu_expand);
                Context contex = getApplicationContext();
                if (menuExpand.getVisibility() == View.GONE) {
                    menuExpand.setVisibility(View.VISIBLE);
                    AnimUtil.alphaAnimation(btnMenu);
                    btnMenu.setImageResource(R.mipmap.menu_off);
                    menuExpand.setAnimation(AnimationUtils.makeInAnimation(contex, true));
                } else {
                    menuExpand.setVisibility(View.GONE);
                    AnimUtil.alphaAnimation(btnMenu);
                    menuExpand.setAnimation(AnimationUtils.makeOutAnimation(contex, false));
                    btnMenu.setImageResource(R.mipmap.menu_on);
                }
                Log.i(TAG, "## click!");
            }
        });

        List<ImageView> menuButtons = new ArrayList<ImageView>() {{
            add((ImageView) findViewById(R.id.crane_setting));
            add((ImageView) findViewById(R.id.area_setting));
            add((ImageView) findViewById(R.id.alarm_setting));
            add((ImageView) findViewById(R.id.zoom_in));
            add((ImageView) findViewById(R.id.zoom_out));
        }};

        for (ImageView view : menuButtons) {
            setOnTouchListener(view);
        }

        // 放大
        ImageView zoomIn = (ImageView) findViewById(R.id.zoom_in);
        zoomIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float oscale = getOscale();
                oscale = oscale + 0.1f;
                setOscale(oscale);
                renderMain(oscale);
            }
        });

        // 缩小
        ImageView zoomOut = (ImageView) findViewById(R.id.zoom_out);
        zoomOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                float oscale = getOscale();
                oscale = oscale - 0.1f;
                setOscale(oscale);
                renderMain(oscale);
            }
        });

        // 调到塔基设置页面
        ImageView craneSetting = (ImageView) findViewById(R.id.crane_setting);
        craneSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, CraneSetting.class);
                startActivity(intent);
            }
        });

    }

    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        renderMain(oscale);
    }
}
