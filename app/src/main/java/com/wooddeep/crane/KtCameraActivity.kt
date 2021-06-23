package com.wooddeep.crane

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import android.widget.VideoView
import com.wooddeep.crane.persist.LogDbHelper
import com.wooddeep.crane.persist.entity.Camera
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope


class KtCameraActivity : AppCompatActivity(), CoroutineScope by MainScope(), View.OnTouchListener {

    private lateinit var videoView: VideoView
    private val uri: String = "rtsp://81.69.46.54:9090/camera"

    private lateinit var mSurfaceView: SurfaceView
    //private lateinit var mRtspClient: RtspClient


    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {//实现onTouch接口
//        when (view.id) {
//            R.id.videoView -> Toast.makeText(this, "ddd", Toast.LENGTH_LONG).show()
//        }
        return false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        LogDbHelper.getInstance(applicationContext).createTable(Camera::class.java)

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
