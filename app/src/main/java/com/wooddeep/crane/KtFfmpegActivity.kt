package com.wooddeep.crane

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.annotation.RequiresApi
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.MediaController
import android.widget.Toast
import android.widget.VideoView
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFmpeg.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.FFmpeg.RETURN_CODE_SUCCESS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope

import android.view.SurfaceView


class KtFfmpegActivity : AppCompatActivity(), CoroutineScope by MainScope(), View.OnTouchListener {

    private lateinit var videoView: VideoView
    private val uri: String = "rtsp://81.69.46.54:9090/camera"

    fun onCreate__(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ffmpeg_test)
        videoView = findViewById(R.id.videoView)

        //findViewById()获取在布局中定义了的元素，返回的是一个View对象，需要向下转型
        var mediaController: MediaController = MediaController(this)
        videoView.setVideoURI(Uri.parse(uri))
        videoView.setMediaController(mediaController)
        videoView.start()
        videoView.setOnTouchListener(this)
    }


    private lateinit var mSurfaceView: SurfaceView
    //private lateinit var mRtspClient: RtspClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ffmpeg_test)

        mSurfaceView = findViewById(R.id.surfaceView)
        val host = "rtsp://81.69.46.54:9090/camera"
        play(host)

    }

    fun play(host: String) {
        //创建client，需要传入一个SurfaceView作为显示
//        mRtspClient = RtspClient(host)
//        mRtspClient.setSurfaceView(mSurfaceView);
//        mRtspClient.start();
    }

    override fun onDestroy() {
        //if (mRtspClient != null) mRtspClient.shutdown()
        super.onDestroy()
    }

    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {//实现onTouch接口
        when (view.id) {
            R.id.videoView -> Toast.makeText(this, "ddd", Toast.LENGTH_LONG).show()
        }
        return false
    }


    fun onCreate_(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ffmpeg_test)

        val startBtn = findViewById<Button>(R.id.start_ffmpeg)

        startBtn.setOnClickListener(View.OnClickListener {

            // -re: 正常帧率
            Thread(Runnable {
                when (val rc = FFmpeg.execute("-re -i /sdcard/test.mp4 -vcodec copy -acodec copy -f rtsp rtsp://81.69.46.54:9090/mp4")) {
                    RETURN_CODE_SUCCESS -> Log.i(Config.TAG, "Command execution completed successfully.")
                    RETURN_CODE_CANCEL -> Log.i(Config.TAG, "Command execution cancelled by user.")
                    else -> {
                        Log.i(Config.TAG, String.format("Command execution failed with rc=%d and the output below.", rc))
                    }
                }
            }).start()
        })

        // 触发判断本机是否为主机
        Handler().postDelayed({
            println("postDelayed!")
        }, 10)

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {

    }

    override fun onBackPressed() {
        //按返回键返回桌面
        moveTaskToBack(true)
    }


}
