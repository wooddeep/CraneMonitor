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
import android.widget.ScrollView
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

private val surfaceList = ArrayList<SurfaceView>()

private val mediaPlayerList = ArrayList<MediaPlayer>()

private fun createMediaPlayer(surface: SurfaceView, activity: Activity, context: Context, config: Camera): MediaPlayer {
    val mediaPlayer: MediaPlayer
    val options = ArrayList<String>()
    val libVLC = LibVLC(activity.getApplication(), options)
    mediaPlayer = MediaPlayer(libVLC)

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
    return mediaPlayer
}


@SuppressLint("ClickableViewAccessibility")
private fun createSurface(activity: Activity, context: Context, config: Camera): SurfaceView {
    val surface = SurfaceView(activity)

    val mediaPlayer: MediaPlayer
    val options = ArrayList<String>()
    val libVLC = LibVLC(activity.getApplication(), options)
    mediaPlayer = MediaPlayer(libVLC)
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
            if (viewMap[v]!!.minus(event.x) > 50) { // 向左滑
                if (activity.findViewById<LinearLayout>(R.id.main_cameras).visibility == View.VISIBLE) { // 当前是摄像头大屏
                    activity.findViewById<LinearLayout>(R.id.main_cameras).visibility = View.GONE // 摄像头大屏隐藏
                    activity.findViewById<LinearLayout>(R.id.main_main_win).visibility = View.VISIBLE // 显示主窗口
                    //activity.findViewById<LinearLayout>(R.id.mini_camera_container).removeAllViews()
                }
                activity.findViewById<CraneView>(R.id.crane).visibility = View.VISIBLE
                activity.findViewById<ScrollView>(R.id.mini_camera).visibility = View.GONE
                activity.findViewById<LinearLayout>(R.id.mini_camera_container).removeAllViews()
            }

            if (event.x - viewMap[v]!! > 50) { // 向右滑, 显示右上角滑动小窗
                if (activity.findViewById<ScrollView>(R.id.mini_camera).visibility == View.GONE) {
                    createMiniCameraContainers(activity, context)
                }
                activity.findViewById<LinearLayout>(R.id.main_cameras).visibility = View.GONE
                activity.findViewById<LinearLayout>(R.id.main_main_win).visibility = View.VISIBLE
                activity.findViewById<CraneView>(R.id.crane).visibility = View.GONE
                activity.findViewById<ScrollView>(R.id.mini_camera).visibility = View.VISIBLE
                activity.findViewById<LinearLayout>(R.id.main_cameras).removeAllViews()
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

    val currentSize = surfaceList.size
    val targetSize = config.size

    if (currentSize < targetSize) {
        for (index in currentSize until targetSize) {
            val surfaceRight = createSurface(activity, context, config[index])
            surfaceList.add(surfaceRight)
        }
    }

    val mainLayout = activity.findViewById<LinearLayout>(R.id.main_cameras)
    for (index in 0 until mainLayout.childCount) {
        val view = mainLayout.getChildAt(index)
        if (view is LinearLayout) {
            view.removeAllViews()
        }
    }
    mainLayout.removeAllViews()

    val miniLayout = activity.findViewById<LinearLayout>(R.id.mini_camera_container)
    miniLayout.removeAllViews()

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
            val surfaceRight = surfaceList[row * cols + col]


            var surfaceParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f)
            surfaceRight.layoutParams = surfaceParams


            linearLayout.addView(surfaceRight)
        }
    }

}

fun createMiniCameraContainers(activity: Activity, context: Context) {

    DatabaseHelper.getInstance(context).createTable(Camera::class.java)
    val cameraDao = CameraDao(activity)
    val config = cameraDao.selectAll()

    if (config.size <= 0) return

    val currentSize = surfaceList.size
    val targetSize = config.size

    if (currentSize < targetSize) {
        for (index in currentSize until targetSize) {
            val surfaceRight = createSurface(activity, context, config[index])
            surfaceList.add(surfaceRight)
        }
    }

    val mainLayout = activity.findViewById<LinearLayout>(R.id.main_cameras)
    for (index in 0 until mainLayout.childCount) {
        val view = mainLayout.getChildAt(index)
        if (view is LinearLayout) {
            view.removeAllViews()
        }
    }
    mainLayout.removeAllViews()

    val miniLayout = activity.findViewById<LinearLayout>(R.id.mini_camera_container)
    miniLayout.removeAllViews()

    for (index in 0 until surfaceList.size) {
        if (index > 0) {
            val hoz = createSplitLine(activity, context, true)
            miniLayout.addView(hoz)
        }
        val surfaceRight = surfaceList[index]

        var surfaceParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                200)

        surfaceRight.layoutParams = surfaceParams

        val mediaPlayer = createMediaPlayer(surfaceRight, activity, context, config[index])
        surfaceRight.setOnClickListener { v ->
            println("## start camera")
            mediaPlayer.play()
            true
        }

        miniLayout.addView(surfaceRight)
    }

}

