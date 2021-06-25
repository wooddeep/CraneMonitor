package com.wooddeep.crane.agent

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.net.Uri
import android.util.DisplayMetrics
import android.view.MotionEvent
import android.view.SurfaceView
import android.view.View
import android.widget.LinearLayout
import com.wooddeep.crane.R
import com.wooddeep.crane.persist.DatabaseHelper
import com.wooddeep.crane.persist.dao.CameraDao
import com.wooddeep.crane.persist.entity.Camera
import com.wooddeep.crane.views.CraneView
import org.videolan.libvlc.LibVLC
import org.videolan.libvlc.Media
import org.videolan.libvlc.MediaPlayer
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.set


private fun createOneLine(activity: Activity, context: Context, rows: Int): LinearLayout {

    val dm = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(dm)
    val height = dm.heightPixels

    val linearLayout = LinearLayout(context)
    val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            height / rows, 1f)
    linearLayout.orientation = LinearLayout.HORIZONTAL
    linearLayout.layoutParams = layoutParams
    return linearLayout
}

private val viewMap = HashMap<View, Float>()

@SuppressLint("ClickableViewAccessibility")
private fun createSurface(activity: Activity, context: Context, config: Camera): SurfaceView {
    val surface = SurfaceView(activity)
    var surfaceParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f)
    surface.layoutParams = surfaceParams

    val mediaPlayer: MediaPlayer
    val options = ArrayList<String>()
    val libVLC = LibVLC(activity.getApplication(), options)
    mediaPlayer = MediaPlayer(libVLC)
    //String url = getString(R.string.http_video11_qtv_com_cn_qtv1_sd_manifest_m3u8);
    //String _url = "rtsp://192.168.141.98:8554/camera";
    //String url = "file:///sdcard/lihan.mp4";
    mediaPlayer.vlcVout.setVideoSurface(surface.holder.surface, surface.holder)

    val ip = config.ip
    val port = config.port
    val route = config.route
    val url = "rtsp://${ip.trim()}:${port.trim()}/${route.trim()}"
    println("## url:$url")

    //播放前还要调用这个方法
    mediaPlayer.vlcVout.attachViews()
    val media = Media(libVLC, Uri.parse(url))
    mediaPlayer.media = media

    surface.setOnClickListener { v ->
        println("## start camera")
        mediaPlayer.play()
        true
    }

    val onTouchListener = View.OnTouchListener { v, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            if (viewMap[v]!!.minus(event.x) > 100) {
                activity.findViewById<LinearLayout>(R.id.main_cameras).visibility = View.GONE
                activity.findViewById<LinearLayout>(R.id.main_main_win).visibility = View.VISIBLE
            }

            if (event.x - viewMap[v]!! > 100) {
                activity.findViewById<LinearLayout>(R.id.main_cameras).visibility = View.GONE
                activity.findViewById<LinearLayout>(R.id.main_main_win).visibility = View.VISIBLE
                activity.findViewById<CraneView>(R.id.crane).visibility = View.GONE
                activity.findViewById<SurfaceView>(R.id.mini_camera).visibility = View.VISIBLE
            }
        }
        if (event.action == MotionEvent.ACTION_DOWN) {
            viewMap[v] = event.x
        }
        false
    }

    surface.setOnTouchListener(onTouchListener)


    return surface
}


private fun createSplitLine(activity: Activity, context: Context, isHoz: Boolean): View {

    return if (!isHoz) {
        val view = View(activity)
        var viewParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(1,
                LinearLayout.LayoutParams.MATCH_PARENT)

        view.layoutParams = viewParams
        view.setBackgroundColor(Color.GRAY)
        view
    } else {
        val view = View(activity)
        var viewParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                1)
        view.layoutParams = viewParams
        view.setBackgroundColor(Color.GRAY)
        view
    }
}


private val surfaceLayout = arrayOf(
        arrayOf(1, 1),
        arrayOf(1, 2),
        arrayOf(2, 2),
        arrayOf(2, 2),
        arrayOf(2, 3),
        arrayOf(2, 3),
        arrayOf(3, 3),
        arrayOf(3, 3),
        arrayOf(3, 3)
)

fun createCameraContainers(activity: Activity, context: Context) {
    DatabaseHelper.getInstance(context).createTable(Camera::class.java)
    val cameraDao = CameraDao(activity)
    val config = cameraDao.selectAll()

    if (config.size <= 0) return

    val mainLayout = activity.findViewById<LinearLayout>(R.id.main_cameras)
    mainLayout.removeAllViews()
    val rows = surfaceLayout[config.size - 1][0]
    var cols = surfaceLayout[config.size - 1][1]

    for (row in 0 until rows) {
        if (row > 0) {
            val hoz = createSplitLine(activity, context, true)
            mainLayout.addView(hoz)
        }

        val linearLayout = createOneLine(activity, context, rows)
        mainLayout.addView(linearLayout)

        for (col in 0 until cols) {
            if (col > 0) {
                val vertical = createSplitLine(activity, context, false)
                linearLayout.addView(vertical)
            }
            val surfaceRight = createSurface(activity, context, config[row * cols + col])
            linearLayout.addView(surfaceRight)
        }
    }

}