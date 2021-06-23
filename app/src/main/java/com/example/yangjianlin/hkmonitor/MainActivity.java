// 截图:
// https://blog.csdn.net/wljs17/article/details/92979250
// http://www.voidcn.com/article/p-evnwluuk-qc.html

package com.example.yangjianlin.hkmonitor;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;

import com.googlecode.tesseract.android.TessBaseAPI;
import com.hikvision.netsdk.ExceptionCallBack;
import com.hikvision.netsdk.HCNetSDK;
import com.hikvision.netsdk.NET_DVR_DEVICEINFO_V30;
import com.hikvision.netsdk.NET_DVR_PREVIEWINFO;
import com.hikvision.netsdk.PTZCommand;
import com.hikvision.netsdk.RealPlayCallBack;
import com.wooddeep.crane.R;
import com.wooddeep.crane.views.MainEntry;

import org.MediaPlayer.PlayM4.Player;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * <pre>
 *  ClassName  DemoActivity Class
 * </pre>
 *
 * @author zhuzhenlei
 * @version V1.0
 * @modificationHistory
 */
public class MainActivity extends Activity implements Callback, IVLCVout.Callback {

    private HashMap<String, Integer> ocrMap = new HashMap() {{
        put("001", 1);
        put("002", 2);
        put("003", 3);
        put("004", 4);
        put("005", 5);
        put("006", 6);
        put("007", 7);
        put("00'?", 7);
        put("008", 8);
        put("00B", 8);
        put("009", 9);
        put("00S", 9);
        put("010", 10);
        put("011", 11);
        put("012", 12);
        put("013", 13);
        put("014", 14);
        put("015", 15);
        put("016", 16);
        put("017", 17);
        put("018", 18);
        put("01B", 18);
        put("019", 19);
        put("020", 20);
        put("021", 21);
        put("022", 22);
    }};

    private Button m_oLoginBtn = null;
    private Button m_oPreviewBtn = null;
    private Button m_oPTZBtn = null;
    private Button m_stopSave = null;

    private Button settingBtn = null;

    private SurfaceView m_osurfaceView = null;

    private Button m_zoom_in = null;
    private Button m_zoom_out = null;
    private Button m_shoot_cut = null;
    private String m_oIPAddr = "192.168.0.77";
    private String m_oPort = "8000";
    private String m_oUser = "admin";
    private String m_oPsd = "admin";


    private NET_DVR_DEVICEINFO_V30 m_oNetDvrDeviceInfoV30 = null;

    private int m_iLogID = -1; // return by NET_DVR_Login_v30
    private int m_iPlayID = -1; // return by NET_DVR_RealPlay_V30
    private int m_iPlaybackID = -1; // return by NET_DVR_PlayBackByTime

    private int m_iPort = -1; // play port
    private int m_iStartChan = 0; // start channel no
    private int m_iChanNum = 0; // channel number
    //private static PlaySurfaceView[] playView = new PlaySurfaceView[4];

    private final String TAG = "DemoActivity";

    private boolean m_bTalkOn = false;
    private boolean m_bPTZL = false;
    private boolean m_bMultiPlay = false;

    private boolean m_bNeedDecode = true;
    private boolean m_bSaveRealData = false;
    private boolean m_bStopPlayback = false;

    private VideoView videoView;

    public static String[] PERMISSIONS_STORAGE = {
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public static void requestPermissions(Activity context) {
        //用于Android6.0以后的操作系统，动态申请存储的读写权限
        int permission = ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(context, PERMISSIONS_STORAGE, 1);
        }
    }

    private String mFilePath;
    private Uri uri;

    // display surface
    private SurfaceView mSurface;
    private SurfaceHolder holder;

    // media player
    private LibVLC libvlc;
    private MediaPlayer mMediaPlayer = null;

    private void bindViews() {
        mSurface = (SurfaceView) findViewById(R.id.surface);
        holder = mSurface.getHolder();
        uri = Uri.parse("file:///sdcard/lihan.mp4");

        try {
            // Create LibVLC
            // TODO: make this more robust, and sync with audio demo
            ArrayList<String> options = new ArrayList<String>();
            //options.add("--subsdec-encoding <encoding>");
            options.add("--aout=opensles");
            options.add("--audio-time-stretch"); // time stretching
            options.add("-vvv"); // verbosity
            libvlc = new LibVLC(this, options);
//            libvlc.setOnHardwareAccelerationError(this);
            holder.setKeepScreenOn(true);

            // Create media player
            mMediaPlayer = new MediaPlayer(libvlc);
            //mMediaPlayer.setEventListener(mPlayerListener);

            // Set up video output
            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(mSurface);
            //vout.setSubtitlesView(mSurfaceSubtitles);
            vout.addCallback(this);
            vout.attachViews();

            Media m = new Media(libvlc, uri);
            mMediaPlayer.setMedia(m);
            mMediaPlayer.play();
        } catch (Exception e) {
            Toast.makeText(this, "Error creating player!", Toast.LENGTH_LONG).show();
        }


    }

    void startCamera() {
        m_oIPAddr = "192.168.0.77";
        //m_oPort = "8200";
        try {
            if (m_iLogID < 0) {
                // login on the device
                m_iLogID = loginDevice();
                if (m_iLogID < 0) {
                    Log.e(TAG, "This device logins failed!");
                    return;
                } else {
                    System.out.println("m_iLogID=" + m_iLogID);
                }
                // get instance of exception callback and set
                ExceptionCallBack oexceptionCbf = getExceptiongCbf();
                if (oexceptionCbf == null) {
                    Log.e(TAG, "ExceptionCallBack object is failed!");
                    return;
                }

                //HCNetSDK.getInstance().NET_DVR_CaptureJPEGPicture();

                if (!HCNetSDK.getInstance().NET_DVR_SetExceptionCallBack(
                    oexceptionCbf)) {
                    Log.e(TAG, "NET_DVR_SetExceptionCallBack is failed!");
                    return;
                }

                m_oLoginBtn.setText("Logout");
                Log.i(TAG,
                    "Login sucess ****************************1***************************");
            } else {
                // whether we have logout
                if (!HCNetSDK.getInstance().NET_DVR_Logout_V30(m_iLogID)) {
                    Log.e(TAG, " NET_DVR_Logout is failed!");
                    return;
                }
                m_oLoginBtn.setText("西南摄像机");
                m_iLogID = -1;
            }
        } catch (Exception err) {
            Log.e(TAG, "error: " + err.toString());
        }
    }

    void startPreview() {
        try {
            if (m_iLogID < 0) {
                Log.e(TAG, "please login on device first");
                return;
            }
            if (m_bNeedDecode) {
                if (m_iChanNum > 1)// preview more than a channel
                {
                    if (!m_bMultiPlay) {
                        m_bMultiPlay = true;
                        m_oPreviewBtn.setText("停止");
                    } else {
                        stopMultiPreview();
                        m_bMultiPlay = false;
                        m_oPreviewBtn.setText("播放");
                    }
                } else // preivew a channel
                {
                    if (m_iPlayID < 0) {
                        startSinglePreview();
                    } else {
                        stopSinglePreview();
                        m_oPreviewBtn.setText("播放");
                    }
                }
            } else {

            }
        } catch (Exception err) {
            Log.e(TAG, "error: " + err.toString());
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23) {
            requestPermissions(this);
        }

        setContentView(R.layout.video_main);

        if (!initeSdk()) {

        }

        ScreenShotFb.init(this);

        m_oLoginBtn = (Button) findViewById(R.id.btn_Login);
        m_oPreviewBtn = (Button) findViewById(R.id.btn_Preview);
        m_oPTZBtn = (Button) findViewById(R.id.btn_PTZ);
        m_stopSave = (Button) findViewById(R.id.btn_stopSave);
        m_osurfaceView = (SurfaceView) findViewById(R.id.Sur_Player);

        m_zoom_in = (Button) findViewById(R.id.btn_zoom_in);
        m_zoom_out = (Button) findViewById(R.id.btn_zoom_out);
        m_shoot_cut = (Button) findViewById(R.id.btn_cut);

        //m_osurfaceView.getDisplay();

        baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);

        baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);

        m_oLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCamera();
            }
        });


        m_zoom_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.ZOOM_IN, 0)) {
                        Log.e(TAG,
                            "start PAN_LEFT failed with error code: "
                                + HCNetSDK.getInstance()
                                .NET_DVR_GetLastError());
                    } else {

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                Log.i(TAG, "start ZOOM_IN succ");
                                HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.ZOOM_IN, 1);
                            }
                        }, 100);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        m_zoom_out.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.ZOOM_OUT, 0)) {
                        Log.e(TAG,
                            "start PAN_LEFT failed with error code: "
                                + HCNetSDK.getInstance()
                                .NET_DVR_GetLastError());
                    } else {

                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                Log.i(TAG, "start ZOOM_OUT succ");
                                HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.ZOOM_OUT, 1);
                            }
                        }, 100);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        m_shoot_cut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //captureFrameOnMemroy();
                zoomAdjust(100, 5);
            }
        });

        m_oPreviewBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startPreview();
            }
        });

        m_oPTZBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                //int clibrary = HCNetSDKJNAInstance.getInstance().NET_DVR_SaveRealData_V30(m_iPlayID, 2, "/sdcard/test.mp4");

                Log.i(TAG, "[1]NetSdk m_iPlayID = **************************************************" + m_iPlayID);

                if (!HCNetSDK.getInstance().NET_DVR_SaveRealData(m_iPlayID, "/sdcard/lihan.mp4")) {
                    System.out.println("NET_DVR_SaveRealData failed! error: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    return;
                } else {
                    System.out.println("NET_DVR_SaveRealData succ!");
                }

            }
        });

        m_stopSave.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.i(TAG, "stop save = **************************************************" + m_iPlayID);

                if (!HCNetSDK.getInstance().NET_DVR_StopSaveRealData(m_iPlayID)) {
                    System.out.println("NET_DVR_StopSaveRealData failed! error: " + HCNetSDK.getInstance().NET_DVR_GetLastError());
                    return;
                } else {
                    System.out.println("NET_DVR_StopSaveRealData succ!");
                }


            }
        });

        m_osurfaceView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                int visibility = findViewById(R.id.tookit).getVisibility();
                if (visibility == View.VISIBLE) {
                    visibility = View.GONE;
                } else {
                    visibility = View.VISIBLE;
                }
                findViewById(R.id.tookit).setVisibility(visibility);
            }
        });

        // 扩张按钮
        findViewById(R.id.expand).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                ImageView expand = (ImageView) findViewById(R.id.expand);
                RelativeLayout container = (RelativeLayout) findViewById(R.id.total);
                RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) m_osurfaceView.getLayoutParams();
                if ((int) (linearParams.width * 10 / linearParams.height) == 13) { // 扩张到屏幕尺寸
                    linearParams.height = container.getHeight();
                    linearParams.width = container.getWidth();
                    m_osurfaceView.setLayoutParams(linearParams);
                    expand.setImageResource(R.drawable.shrink);
                } else {
                    float rate = (float) container.getWidth() / container.getHeight();
                    if (rate > (4.0 / 3)) { // 太宽了，以高度为基准
                        linearParams.height = container.getHeight();// 控件的高强制设成20
                        linearParams.width = (int) (container.getHeight() * 4 / 3.0f);// 控件的宽强制设成30
                    } else {
                        linearParams.width = container.getWidth();// 控件的宽强制设成30
                        linearParams.height = (int) (container.getWidth() * 3.0f / 4);
                    }
                    m_osurfaceView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件
                    expand.setImageResource(R.drawable.expand);
                }

            }
        });

        // 设置, 弹出设置框
        findViewById(R.id.setting).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int visibility = findViewById(R.id.setpad).getVisibility();
                if (visibility == View.VISIBLE) {
                    findViewById(R.id.setpad).setVisibility(View.GONE);
                    findViewById(R.id.history).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.setpad).setVisibility(View.VISIBLE);
                }

            }
        });

        findViewById(R.id.series).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int visibility = findViewById(R.id.history).getVisibility();
                if (visibility == View.VISIBLE) {
                    findViewById(R.id.setpad).setVisibility(View.GONE);
                    findViewById(R.id.history).setVisibility(View.GONE);
                } else {
                    findViewById(R.id.history).setVisibility(View.VISIBLE);
                    bindViews();
                }
            }
        });

        new Handler().postDelayed(new Runnable() {
            public void run() {
                startCamera();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        startPreview();
                    }
                }, 2000);
            }
        }, 2000);

        MainEntry mainEntry = new MainEntry();
        mainEntry.onCreate(this); // 塔基主函数
    }

    // @Override
    public void surfaceCreated(SurfaceHolder holder) {
        m_osurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        Log.i(TAG, "surface is created" + m_iPort);
        if (-1 == m_iPort) {
            return;
        }
        Surface surface = holder.getSurface();
        if (true == surface.isValid()) {
            if (false == Player.getInstance()
                .setVideoWindow(m_iPort, 0, holder)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

    // @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    // @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "Player setVideoWindow release!" + m_iPort);
        if (-1 == m_iPort) {
            return;
        }
        if (true == holder.getSurface().isValid()) {
            if (false == Player.getInstance().setVideoWindow(m_iPort, 0, null)) {
                Log.e(TAG, "Player setVideoWindow failed!");
            }
        }
    }

    private boolean initeSdk() {
        // init net sdk
        if (!HCNetSDK.getInstance().NET_DVR_Init()) {
            Log.e(TAG, "HCNetSDK init is failed!");
            return false;
        }
        HCNetSDK.getInstance().NET_DVR_SetLogToFile(3, "/mnt/sdcard/sdklog/",
            true);
        return true;
    }

    private void startSinglePreview() {
        if (m_iPlaybackID >= 0) {
            Log.i(TAG, "Please stop palyback first");
            return;
        }
        RealPlayCallBack fRealDataCallBack = getRealPlayerCbf();
        if (fRealDataCallBack == null) {
            Log.e(TAG, "fRealDataCallBack object is failed!");
            return;
        }
        Log.i(TAG, "m_iStartChan:" + m_iStartChan);

        NET_DVR_PREVIEWINFO previewInfo = new NET_DVR_PREVIEWINFO();
        previewInfo.lChannel = m_iStartChan;
        previewInfo.dwStreamType = 0; // substream
        previewInfo.bBlocked = 1;

        m_iPlayID = HCNetSDK.getInstance().NET_DVR_RealPlay_V40(m_iLogID,
            previewInfo, fRealDataCallBack);
        if (m_iPlayID < 0) {
            Log.e(TAG, "NET_DVR_RealPlay is failed!Err:"
                + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        //HCNetSDK.getInstance().NET_DVR_CaptureJPEGPicture();


        Log.i(TAG, "NetSdk Play sucess ***********************3***************************");
        m_oPreviewBtn.setText("Stop");
    }


    private void stopMultiPreview() {
        int i = 0;
        for (i = 0; i < 4; i++) {
            //playView[i].stopPreview();
        }
        m_iPlayID = -1;
    }

    private void stopSinglePreview() {
        if (m_iPlayID < 0) {
            Log.e(TAG, "m_iPlayID < 0");
            return;
        }

        // net sdk stop preview
        if (!HCNetSDK.getInstance().NET_DVR_StopRealPlay(m_iPlayID)) {
            Log.e(TAG, "StopRealPlay is failed!Err:"
                + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return;
        }

        m_iPlayID = -1;
        stopSinglePlayer();
    }

    private void stopSinglePlayer() {
        Player.getInstance().stopSound();
        // player stop play
        if (!Player.getInstance().stop(m_iPort)) {
            Log.e(TAG, "stop is failed!");
            return;
        }

        if (!Player.getInstance().closeStream(m_iPort)) {
            Log.e(TAG, "closeStream is failed!");
            return;
        }
        if (!Player.getInstance().freePort(m_iPort)) {
            Log.e(TAG, "freePort is failed!" + m_iPort);
            return;
        }
        m_iPort = -1;
    }

    /**
     * @return login ID
     * @fn loginNormalDevice
     * @author zhuzhenlei
     * @brief login on device
     * [out]
     */
    private int loginNormalDevice() {
        // get instance
        m_oNetDvrDeviceInfoV30 = new NET_DVR_DEVICEINFO_V30();
        if (null == m_oNetDvrDeviceInfoV30) {
            Log.e(TAG, "HKNetDvrDeviceInfoV30 new is failed!");
            return -1;
        }
        String strIP = m_oIPAddr;
        int nPort = Integer.parseInt(m_oPort);
        String strUser = m_oUser;
        String strPsd = m_oPsd;
        // call NET_DVR_Login_v30 to login on, port 8000 as default
        int iLogID = HCNetSDK.getInstance().NET_DVR_Login_V30(strIP, nPort,
            strUser, strPsd, m_oNetDvrDeviceInfoV30);
        if (iLogID < 0) {
            Log.e(TAG, "NET_DVR_Login is failed!Err:"
                + HCNetSDK.getInstance().NET_DVR_GetLastError());
            return -1;
        }
        if (m_oNetDvrDeviceInfoV30.byChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byChanNum;
        } else if (m_oNetDvrDeviceInfoV30.byIPChanNum > 0) {
            m_iStartChan = m_oNetDvrDeviceInfoV30.byStartDChan;
            m_iChanNum = m_oNetDvrDeviceInfoV30.byIPChanNum
                + m_oNetDvrDeviceInfoV30.byHighDChanNum * 256;
        }

        Log.i(TAG, "NET_DVR_Login is Successful!");

        return iLogID;
    }

    private int loginDevice() {
        int iLogID = -1;

        iLogID = loginNormalDevice();

        return iLogID;
    }

    private ExceptionCallBack getExceptiongCbf() {
        ExceptionCallBack oExceptionCbf = new ExceptionCallBack() {
            public void fExceptionCallBack(int iType, int iUserID, int iHandle) {
                System.out.println("recv exception, type:" + iType);
            }
        };
        return oExceptionCbf;
    }


    private RealPlayCallBack getRealPlayerCbf() {
        RealPlayCallBack cbf = new RealPlayCallBack() {
            public void fRealDataCallBack(int iRealHandle, int iDataType,
                                          byte[] pDataBuffer, int iDataSize) {

                processRealData(1, iDataType, pDataBuffer,
                    iDataSize, Player.STREAM_REALTIME);


            }
        };
        return cbf;
    }


    public void processRealData(int iPlayViewNo, int iDataType, byte[] pDataBuffer, int iDataSize, int iStreamMode) {

        if (!m_bNeedDecode) {
            // Log.i(TAG, "iPlayViewNo:" + iPlayViewNo + ",iDataType:" +
            // iDataType + ",iDataSize:" + iDataSize);
        } else {
            if (HCNetSDK.NET_DVR_SYSHEAD == iDataType) {
                if (m_iPort >= 0) {
                    return;
                }
                m_iPort = Player.getInstance().getPort();
                if (m_iPort == -1) {
                    Log.e(TAG, "getPort is failed with: "
                        + Player.getInstance().getLastError(m_iPort));
                    return;
                }
                Log.i(TAG, "getPort succ with: " + m_iPort);
                if (iDataSize > 0) {
                    if (!Player.getInstance().setStreamOpenMode(m_iPort,
                        iStreamMode)) // set stream mode
                    {
                        Log.e(TAG, "setStreamOpenMode failed");
                        return;
                    }
                    if (!Player.getInstance().openStream(m_iPort, pDataBuffer,
                        iDataSize, 2 * 1024 * 1024)) // open stream
                    {
                        Log.e(TAG, "openStream failed");
                        return;
                    }
                    if (!Player.getInstance().play(m_iPort,
                        m_osurfaceView.getHolder())) {
                        Log.e(TAG, "play failed");
                        return;
                    }
                    if (!Player.getInstance().playSound(m_iPort)) {
                        Log.e(TAG, "playSound failed with error code:"
                            + Player.getInstance().getLastError(m_iPort));
                        return;
                    }
                }
            } else {
                //System.out.printf("@@@ pDataBuffer.length = %d, iDataSize = %d\n", pDataBuffer.length, iDataSize);
                Player player = Player.getInstance();
                boolean input = player.inputData(m_iPort, pDataBuffer, iDataSize);
                if (!input) {
                    for (int i = 0; i < 4000 && m_iPlaybackID >= 0
                        && !m_bStopPlayback; i++) {
                        if (Player.getInstance().inputData(m_iPort,
                            pDataBuffer, iDataSize)) {
                            break;

                        }

                        if (i % 100 == 0) {
                            Log.e(TAG, "inputData failed with: "
                                + Player.getInstance()
                                .getLastError(m_iPort) + ", i:" + i);
                        }

                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();

                        }
                    }
                }

            }
        }

    }

    final String TESSBASE_PATH = "/sdcard/";
    //System.out.println(TESSBASE_PATH);
    //识别语言英文
    final String DEFAULT_LANGUAGE = "eng";
    //创建Tess
    final TessBaseAPI baseApi = new TessBaseAPI();

    public int ocr(Bitmap bmp) {
        //训练数据路径，tessdata

        baseApi.setImage(bmp);

        String text = baseApi.getUTF8Text();

        baseApi.setVariable("tessedit_char_whitelist", "01234567B9");

        System.out.printf("### text = %s, times = %d\n", text, ocrMap.get(text));

        return ocrMap.get(text);
        //初始化OCR的训练数据路径与语言
        //baseApi.init(TESSBASE_PATH, DEFAULT_LANGUAGE);
        //设置识别模式
        //baseApi.setPageSegMode(TessBaseAPI.PageSegMode.PSM_SINGLE_LINE);
        //设置要识别的图片
        //baseApi.setImage(bmp);
        //设置字典白名单
        //baseApi.setVariable("tessedit_char_whitelist", "0123456789Xx");

        //收尾
        //baseApi.clear();
        //baseApi.end();

    }

    private Player.MPInteger stWidth;
    private Player.MPInteger stHeight;
    private Player.MPInteger stSize;

    /**
     * 将彩色图转换为灰度图
     *
     * @param img 位图
     * @return 返回转换好的位图
     */
    public Bitmap convertGreyImg(Bitmap img) {
        int width = img.getWidth();         //获取位图的宽
        int height = img.getHeight();       //获取位图的高

        int[] pixels = new int[width * height]; //通过位图的大小创建像素点数组

        img.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];

                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);

                grey = (int) ((float) red * 0.3 + (float) green * 0.59 + (float) blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        result.setPixels(pixels, 0, width, 0, 0, width, height);
        return result;
    }

    public void captureFrameOnMemroy() {
        try {
            long start = System.currentTimeMillis();
            // mPicCapturedListener = picCapturedListener;
            if (stWidth == null) {
                stWidth = new Player.MPInteger();
            }
            if (stHeight == null) {
                stHeight = new Player.MPInteger();
            }
            if (!Player.getInstance().getPictureSize(m_iPort, stWidth, stHeight)) {
                Log.e(TAG, "获取图片尺寸失败---> error code:" + Player.getInstance().getLastError(m_iPort));
                return;
            }
            int nSize = 5 * stWidth.value * stHeight.value;
            byte[] picBuf = new byte[nSize];
            if (stSize == null) {

                stSize = new Player.MPInteger();
            }
            if (!Player.getInstance().getBMP(m_iPort, picBuf, nSize, stSize)) {
                Log.e(TAG, "获取位图失败----> error code:" + Player.getInstance().getLastError(m_iPort));
                return;
            }

            Bitmap bitmap = BitmapFactory.decodeByteArray(picBuf, 0, picBuf.length);

            Bitmap zero = Bitmap.createBitmap(bitmap, 15, stHeight.value - 30, 46, 30);

            ocr(zero);

        } catch (Exception err) {
            Log.e(TAG, "error: " + err.toString());
        }
        //return null;
    }

    public int getCurrentTime() {
        try {
            //long start = System.currentTimeMillis();
            // mPicCapturedListener = picCapturedListener;
            if (stWidth == null) {
                stWidth = new Player.MPInteger();
            }
            if (stHeight == null) {
                stHeight = new Player.MPInteger();
            }
            if (!Player.getInstance().getPictureSize(m_iPort, stWidth, stHeight)) {
                Log.e(TAG, "获取图片尺寸失败---> error code:" + Player.getInstance().getLastError(m_iPort));
                return -1;
            }
            int nSize = 5 * stWidth.value * stHeight.value;
            byte[] picBuf = new byte[nSize];
            if (stSize == null) {

                stSize = new Player.MPInteger();
            }
            if (!Player.getInstance().getBMP(m_iPort, picBuf, nSize, stSize)) {
                Log.e(TAG, "获取位图失败----> error code:" + Player.getInstance().getLastError(m_iPort));
                return -1;
            }

            Bitmap bitmap = BitmapFactory.decodeByteArray(picBuf, 0, picBuf.length);

            Bitmap zero = Bitmap.createBitmap(bitmap, 15, stHeight.value - 30, 46, 30);

            return ocr(zero);

        } catch (Exception err) {
            Log.e(TAG, "error: " + err.toString());
        }
        return -1;
    }


    /**
     * 获取主界面FrameLayout的坐标及长宽
     **/
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.Play);
        RelativeLayout.LayoutParams linearParams = (RelativeLayout.LayoutParams) m_osurfaceView.getLayoutParams();

        float rate = (float) container.getWidth() / container.getHeight();
        if (rate > (4.0 / 3)) { // 太宽了，以高度为基准
            linearParams.height = container.getHeight();// 控件的高强制设成20
            linearParams.width = (int) (container.getHeight() * 4 / 3.0f);// 控件的宽强制设成30
        } else {
            linearParams.width = container.getWidth();// 控件的宽强制设成30
            linearParams.height = (int) (container.getWidth() * 3.0f / 4);
        }

        m_osurfaceView.setLayoutParams(linearParams); //使设置好的布局参数应用到控件

    }

    /**
     * @currHeight: 当前高度
     * @rate: 每变一倍，需要变换多少米
     **/
    void zoomAdjust(final float currHeight, final float rate) {
        int currTimes = getCurrentTime();
        int desireTimes = Math.min(22, (int) (currHeight / rate));

        if (desireTimes > currTimes) { // 需要放大
            int step = desireTimes - currTimes;

            try {
                if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.ZOOM_IN, 0)) {
                    Log.e(TAG,
                        "start PAN_LEFT failed with error code: "
                            + HCNetSDK.getInstance()
                            .NET_DVR_GetLastError());
                } else {

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Log.i(TAG, "start ZOOM_IN succ");
                            HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.ZOOM_IN, 1);

                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    Log.i(TAG, "stop ZOOM_IN succ");
                                    HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.ZOOM_IN, 1);

                                    zoomAdjust(currHeight, rate);
                                }
                            }, 4000); // 延时两秒判断是否完成变焦
                        }
                    }, 100 * step);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (desireTimes < currTimes) { // 需要放大
            int step = currTimes - desireTimes;

            try {
                if (!HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.ZOOM_OUT, 0)) {
                    Log.e(TAG,
                        "start PAN_LEFT failed with error code: "
                            + HCNetSDK.getInstance()
                            .NET_DVR_GetLastError());
                } else {

                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Log.i(TAG, "start ZOOM_out succ");
                            HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.ZOOM_OUT, 1);

                            new Handler().postDelayed(new Runnable() {
                                public void run() {
                                    Log.i(TAG, "stop ZOOM_OUT succ");
                                    HCNetSDK.getInstance().NET_DVR_PTZControl_Other(m_iLogID, m_iStartChan, PTZCommand.ZOOM_OUT, 1);

                                    zoomAdjust(currHeight, rate);
                                }
                            }, 4000); // 延时两秒判断是否完成变焦
                        }
                    }, 100 * step);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onNewLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {

    }

    @Override
    public void onSurfacesCreated(IVLCVout vlcVout) {

    }

    @Override
    public void onSurfacesDestroyed(IVLCVout vlcVout) {

    }

    @Override
    public void onHardwareAccelerationError(IVLCVout vlcVout) {

    }
}