package com.wooddeep.crane

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import com.arthenica.mobileffmpeg.Config
import com.arthenica.mobileffmpeg.FFmpeg
import com.arthenica.mobileffmpeg.FFmpeg.RETURN_CODE_CANCEL
import com.arthenica.mobileffmpeg.FFmpeg.RETURN_CODE_SUCCESS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import android.support.v4.app.SupportActivity
import android.support.v4.app.SupportActivity.ExtraData
import android.support.v4.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T




class KtFfmpegActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.ffmpeg_test)

        val startBtn = findViewById<Button>(R.id.start_ffmpeg)

        startBtn.setOnClickListener(View.OnClickListener {

            Thread(Runnable {
//                while (true) {
//                    println("hello world!")
//                    Thread.sleep(2000)
//                }

                when (val rc = FFmpeg.execute("-i /sdcard/test.mp4 -vcodec copy -acodec copy -f rtsp rtsp://81.69.46.54:9090/mp4")) {
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
