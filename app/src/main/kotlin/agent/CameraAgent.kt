package com.wooddeep.crane.agent

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.view.SurfaceView
import android.view.View
import android.widget.LinearLayout
import com.wooddeep.crane.R
import com.wooddeep.crane.persist.DatabaseHelper
import com.wooddeep.crane.persist.dao.CameraDao
import com.wooddeep.crane.persist.entity.Camera

private fun createOneLine(activity: Activity, context: Context): LinearLayout {
    val linearLayout = LinearLayout(context)
    val layoutParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
    linearLayout.orientation = LinearLayout.HORIZONTAL
    linearLayout.layoutParams = layoutParams
    return linearLayout
}

private fun createSurface(activity: Activity, context: Context): SurfaceView {
    val surface = SurfaceView(activity)
    var surfaceParams: LinearLayout.LayoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            1f)
    surface.layoutParams = surfaceParams
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

    val mainLayout = activity.findViewById<LinearLayout>(R.id.main_cameras)

    val rows = surfaceLayout[config.size - 1][0]
    var cols = surfaceLayout[config.size - 1][1]

    for (row in 0 until rows) {
        if (row > 0) {
            val hoz = createSplitLine(activity, context, true)
            mainLayout.addView(hoz)
        }

        val linearLayout = createOneLine(activity, context)
        mainLayout.addView(linearLayout)

        for (col in 0 until cols) {
            if (col > 0) {
                val vertical = createSplitLine(activity, context, false)
                linearLayout.addView(vertical)
            }
            val surfaceRight = createSurface(activity, context)
            linearLayout.addView(surfaceRight)
        }
    }

}