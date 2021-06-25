package com.wooddeep.crane;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.bigkoo.alertview.AlertView;
import com.wooddeep.crane.persist.dao.CameraDao;
import com.wooddeep.crane.persist.entity.Camera;
import com.wooddeep.crane.views.FixedTitleTable;
import com.wooddeep.crane.views.TableCell;

import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CameraActivity extends AppCompatActivity {

    private Activity activity;

    private FixedTitleTable table;

    private CameraDao cameraDao;

    private ImageView addButton;

    private ImageView delButton;

    private ImageView saveButton;

    private MediaPlayer _mediaPlayer = null;
    //private SurfaceView  srfc;

    private LibVLC libVLC = null;

    private Button start;
    private Button stop;

    private SurfaceView srfc; // = findViewById(R.id.srfc);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_conf);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        activity = this;
        cameraDao = new CameraDao(getApplicationContext());
        addButton = findViewById(R.id.add_logo);
        delButton = findViewById(R.id.minus_logo);
        saveButton = findViewById(R.id.save_logo);
        srfc = findViewById(R.id.srfc);
        //cameraDao.deleteAll();
        ArrayList<String> options = new ArrayList<>();
        libVLC = new LibVLC(getApplication(), options);

        //initMediaPlayer("");

        // add camera
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCamera();
            }
        });

        // delete camera
        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCamera();
            }
        });

        // save change
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveCameraInfo(activity);
            }
        });

        findViewById(R.id.btn_play_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.camera_show).setVisibility(View.GONE);
            }
        });

        //在Activity中可以为按钮增加事件
        start = findViewById(R.id.btn_play_start);
        stop = findViewById(R.id.btn_play_stop);

        start.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                //_mediaPlayer.getMedia().release();
                //_mediaPlayer.release();
                //Media media = new Media(libVLC, Uri.parse("rtsp://192.168.141.98:8554/camera"));

                try {
                    if (_mediaPlayer != null) {
                        _mediaPlayer.getVLCVout().detachViews();
                    }
                    initMediaPlayer("");

                    //_mediaPlayer.setMedia(media);
                    _mediaPlayer.play();
                } catch (Exception e) {

                }
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //mediaPlayer.pause();
            }
        });

    }

    public void initMediaPlayer(String url) {

        try {
//            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
//                mediaPlayer.stop();
//                mediaPlayer.release();
//                mediaPlayer = null;
//            }
            MediaPlayer mediaPlayer;

            mediaPlayer = new MediaPlayer(libVLC);
            //String url = getString(R.string.http_video11_qtv_com_cn_qtv1_sd_manifest_m3u8);
            String _url = "rtsp://192.168.141.98:8554/camera";
            //String url = "file:///sdcard/lihan.mp4";
            mediaPlayer.getVLCVout().setVideoSurface(srfc.getHolder().getSurface(), srfc.getHolder());


            //播放前还要调用这个方法
            mediaPlayer.getVLCVout().attachViews();
            Media media = new Media(libVLC, Uri.parse(_url));
            mediaPlayer.setMedia(media);
            //mediaPlayer.play();
            _mediaPlayer = mediaPlayer;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void hideKeyboard() {
        try {
            View rootview = this.getWindow().getDecorView();
            InputMethodManager inputMethodManager = (InputMethodManager) getApplicationContext()
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(rootview.findFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
    }

    private int getIdByNumber(String number) {
        List<Camera> all = cameraDao.selectAll();
        int id = -1;
        for (Camera cell : all) {
            if (cell.getNumber().equals(number)) {
                return cell.getId();
            }
        }
        return id;
    }

    private void saveCameraInfo(Activity activity) {
        List<List<String>> gTable = table.getCurrData();
        AlertView alertView = new AlertView("保存摄像头参数(save camera parameter)", "确定保存(save confirm)?", null,
            new String[]{"确定(yes)", "取消(no)"}, null, activity,
            AlertView.Style.Alert, (o, position) -> {
            if (position == 0 && gTable != null) { // 确认
                for (int j = 1; j < gTable.size(); j++) { // 去除掉第一行
                    Camera camera = new Camera();
                    String number = String.valueOf(j);
                    int id = getIdByNumber(number);
                    camera.setId(id);
                    camera.setNumber(number);
                    camera.setIp(gTable.get(j).get(0).trim());
                    camera.setPort(gTable.get(j).get(1).trim());
                    camera.setRoute(gTable.get(j).get(2).trim());
                    camera.setUsername(gTable.get(j).get(3).trim());
                    camera.setPassword(gTable.get(j).get(4).trim());
                    cameraDao.update(camera);
                    hideKeyboard();
                }
            }
        });
        alertView.show();
    }

    private void initCameraTest(View v) {
        String number = String.valueOf(viewMap.get(v));
        List<List<String>> gTable = table.getCurrData();
        List<String> currValus = gTable.get(Integer.parseInt(number));

        String ip = currValus.get(0);
        String port = currValus.get(1);
        String route = currValus.get(2);
        String user = currValus.get(3);
        String pass = currValus.get(4);

        String url = String.format("rtsp://%s:%s/%s", ip.trim(), port.trim(), route.trim());
        System.out.println("## url:" + url);

        findViewById(R.id.camera_show).setVisibility(View.VISIBLE);

        //initMediaPlayer(url);
    }

    private List<Integer> widthList = new ArrayList() {{
        add(200);
        add(300);
        add(200);
        add(250);
        add(200);
        add(250);
        add(250);
    }};

    private void addCamera() {
        List<List<String>> gTable = table.getCurrData();

        ArrayList<TableCell> row = new ArrayList<TableCell>();
        row.add(new TableCell(0, String.valueOf(gTable.size())));
        row.add(new TableCell(1, "0.0.0.0"));
        row.add(new TableCell(1, "8554"));
        row.add(new TableCell(3, ""));
        row.add(new TableCell(1, ""));
        row.add(new TableCell(1, ""));

        View.OnClickListener cl = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initCameraTest(v);
            }
        };

        row.add(new TableCell(0, "打开", cl));
        List<View> viewList = table.addDataRow(row, widthList, true);
        viewMap.put(viewList.get(5), gTable.size()); // 设置每个被点击view的行所在的编号, 6 是 "打开" 按钮的下标

        Camera camera = new Camera();
        camera.setNumber(String.valueOf(gTable.size()));
        camera.setIp("0.0.0.0");
        camera.setPort("8554");
        cameraDao.insert(camera);

    }

    private HashMap<View, Object> viewMap = new HashMap();

    public void deleteCamera() {
        List<List<String>> gTable = table.getCurrData();
        AlertView alertView = new AlertView("删除摄像头参数(delete camera)", "确定删除(delete confirm)?", null,
            new String[]{"确定(yes)", "取消(no)"}, null, activity,
            AlertView.Style.Alert, (o, position) -> {
            if (position == 0 && gTable != null) { // 确认
                Camera camera = new Camera();
                String number = String.valueOf(gTable.size() - 1);
                camera.setId(getIdByNumber(number));
                cameraDao.delete(camera);
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                showCameraInfo(dm.widthPixels);
            }
        });
        alertView.show();
    }

    public void showCameraInfo(int width) {
        table.init(this, widthList);
        table.clearAll();

        // 头部信息
        ArrayList<TableCell> colNames = new ArrayList<TableCell>() {{
            add(new TableCell(0, "编号"));
            add(new TableCell(0, "IP地址"));
            add(new TableCell(0, "端口"));
            add(new TableCell(0, "路径"));
            add(new TableCell(0, "用户名"));
            add(new TableCell(0, "密码"));
            add(new TableCell(0, "测试"));
        }};
        List<Integer> idList = new ArrayList() {{
            add(-1);
            add(-1);
            add(-1);
            add(-1);
            add(-1);
            add(-1);
            add(-1);
        }};

        table.setFirstRow(colNames, idList, widthList);

        List<Camera> paras = cameraDao.selectAll();
        for (Camera camera : paras) {

            ArrayList<TableCell> row = new ArrayList<TableCell>();
            row.add(new TableCell(0, camera.getNumber()));
            row.add(new TableCell(1, camera.getIp()));
            row.add(new TableCell(1, camera.getPort()));
            row.add(new TableCell(3, camera.getRoute()));
            row.add(new TableCell(1, camera.getUsername()));
            row.add(new TableCell(1, camera.getPassword()));
            View.OnClickListener cl = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initCameraTest(v);
                }
            };

            row.add(new TableCell(0, "打开", cl));
            List<View> viewList = table.addDataRow(row, widthList, true);
            viewMap.put(viewList.get(5), Integer.parseInt(camera.getNumber())); // 设置每个被点击view的行所在的编号

        }
    }

    private void setOnTouchListener(View view) {
        View.OnTouchListener onTouchListener = (v, event) -> {
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
        };
        view.setOnTouchListener(onTouchListener);
    }

    @SuppressWarnings("unused")
    private AlertView gAlertView = null;

    private void setOnClickListener(View view) {
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.close_logo) {
                    finish();
                }
            }
        };

        view.setOnClickListener(onClickListener);
    }

    private void setOnTouchListener() {
        List<ImageView> menuButtons = new ArrayList<ImageView>() {{
            add((ImageView) findViewById(R.id.close_logo));
        }};

        for (ImageView view : menuButtons) {
            setOnTouchListener(view);
            setOnClickListener(view);
        }
    }


    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        table = new FixedTitleTable(dm.widthPixels); // 输入屏幕宽度

        try {
            showCameraInfo(dm.widthPixels);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOnTouchListener();
    }
}
