package com.wooddeep.crane.agent

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.wooddeep.crane.*
import com.wooddeep.crane.persist.entity.SysPara
import com.wooddeep.crane.tookit.AnimUtil
import com.wooddeep.crane.tookit.DrawTool
import com.wooddeep.crane.tookit.HttpUtils
import com.wooddeep.crane.tookit.NetTool
import java.util.*

fun setOnDoubleClick(activity: Activity) {
    val gridView = activity.findViewById<FrameLayout>(R.id.main_frame)
    gridView.setOnLongClickListener { v ->
        createCameraContainers(activity, activity.applicationContext)
        activity.findViewById<LinearLayout>(R.id.main_cameras).visibility = View.VISIBLE
        activity.findViewById<LinearLayout>(R.id.main_main_win).visibility = View.GONE


        true
    }

    activity.findViewById<LinearLayout>(R.id.main_cameras).setOnLongClickListener { v ->
        activity.findViewById<LinearLayout>(R.id.main_cameras).visibility = View.GONE
        activity.findViewById<LinearLayout>(R.id.main_main_win).visibility = View.VISIBLE
        true
    }

}

/*
private fun renderMenu(context: Context, activity: Activity) {

    (activity.findViewById(R.id.menu) as ImageView).setOnClickListener(object : View.OnClickListener {
        @SuppressLint("MissingPermission")
        override fun onClick(view: View) {
            val btnMenu = activity.findViewById(R.id.menu) as ImageView
            val menuExpand = activity.findViewById(R.id.menu_expand) as LinearLayout
            val contex = activity.getApplicationContext()
            if (menuExpand.visibility == View.GONE) {
                val mac = NetTool.getMacAddress(context)
                if (registered.get()) {
                    //EdbTool.extTableExec("forever.db", "syspara", String.format("update syspara set paraValue='%s' where paraName='registered'", mac));
                    var para = paraDao.queryParaByName("registered")
                    if (para == null) {
                        para = SysPara("registered", mac)
                        paraDao.insert(para)
                    } else {
                        para!!.setParaValue(mac)
                        paraDao.update(para)
                    }
                }

                //JSONArray out = EdbTool.extTableQuery("forever.db", "syspara", "select paraValue from syspara where paraName='registered'");
                val registered = paraDao.queryValueByName("registered")
                /*
                                   if (out.length() > 0) try {
                                       registered = out.getJSONObject(0).optString("paraValue", "");
                                   } catch (JSONException e) {
                                       e.printStackTrace();
                                   }
                                   */

                if (registered == null || registered!!.length == 0 || registered != mac) { // 未注册
                    // 判断网络是否正常
                    val netOk = NetTool.isNetworkAvailable(context)
                    if (!netOk) {
                        DrawTool.showDialog(activity, "出厂设置, 请连接wifi或移动数据!"
                        ) { dialog, which ->
                            // 弹出WIFI设置页面
                            val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
                            startActivity(intent)
                        }
                    } else {
                        val imei = getDeviceId(activity)
                        println("imei = $imei")
                        Log.d("mac--->", mac)
                        HttpUtils.sendPost(imei, mac, activity)
                    }
                } else {
                    activity.findViewById(R.id.password_confirm).setVisibility(View.VISIBLE)
                }

            } else {
                menuExpand.visibility = View.GONE
                AnimUtil.alphaAnimation(btnMenu)
                menuExpand.animation = AnimationUtils.makeOutAnimation(contex, false)
                btnMenu.setImageResource(R.mipmap.menu_on)
            }
        }
    })

    val confirm = activity.findViewById(R.id.confirm) as Button
    confirm.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View) {
            val input = activity.findViewById(R.id.password) as EditText
            val password = input.text.toString()
            var superpwd = paraDao.queryValueByName("superpwd")
            if (superpwd == null || superpwd!!.length == 0) {
                superpwd = "4321"
                paraDao.insert(SysPara("superpwd", "4321"))
            }

            if (password == "1234" || password.contains(superpwd!!) || password.contains("123698745")) {
                activity.findViewById(R.id.password_confirm).setVisibility(View.GONE)
                input.setText("")
                val btnMenu = activity.findViewById(R.id.menu) as ImageView
                val menuExpand = activity.findViewById(R.id.menu_expand) as LinearLayout
                val contex = activity.getApplicationContext()
                menuExpand.visibility = View.VISIBLE
                AnimUtil.alphaAnimation(btnMenu)
                btnMenu.setImageResource(R.mipmap.menu_off)
                menuExpand.animation = AnimationUtils.makeInAnimation(contex, true)

                val calender = sdf.format(Date())
                val day = (calender.split((" ").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]).split(("-").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[2]

                superSuper = false
                if (password == "123698745$day") {
                    activity.findViewById(R.id.super_admin).setVisibility(View.VISIBLE) // 超超管
                    superSuper = true
                } else if (password == superpwd) {
                    activity.findViewById(R.id.super_admin).setVisibility(View.VISIBLE) // 超管
                    println("######## super admin")
                } else {
                    activity.findViewById(R.id.super_admin).setVisibility(View.GONE) // 超管
                    println("######## common admin")
                }
            } else {
                val toast = Toast.makeText(this@MainActivity, "密码错误，重新输入(password error, try again!)", Toast.LENGTH_SHORT)
                input.setText("")
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
            }
            val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager!!.hideSoftInputFromWindow(activity.findViewById(R.id.password).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
        }
    })
    val cancel = activity.findViewById(R.id.cancel) as Button
    cancel.setOnClickListener(object : View.OnClickListener {
        @TargetApi(Build.VERSION_CODES.M)
        override fun onClick(view: View) {
            activity.findViewById(R.id.password_confirm).setVisibility(View.GONE)
            // 关闭 输入框
            val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager!!.hideSoftInputFromWindow(activity.findViewById(R.id.password).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
        }
    })

    val menuButtons = object : ArrayList<ImageView>() {
        init {
            add(findViewById(R.id.crane_setting) as ImageView)
            add(findViewById(R.id.area_setting) as ImageView)
            add(findViewById(R.id.protect_area_setting) as ImageView)
            add(findViewById(R.id.calibration_setting) as ImageView)
            add(findViewById(R.id.alarm_setting) as ImageView)
            add(findViewById(R.id.load_attribute) as ImageView)
            add(findViewById(R.id.data_record) as ImageView)
            //add((ImageView) findViewById(R.id.fault_diag));
            add(findViewById(R.id.camera_conf) as ImageView)
            add(findViewById(R.id.super_admin) as ImageView)
            add(findViewById(R.id.zoom_in) as ImageView)
            add(findViewById(R.id.zoom_out) as ImageView)
        }
    }

    for (view in menuButtons) {
        setOnTouchListener(view)
    }

    // 放大
    val zoomIn = findViewById(R.id.zoom_in) as ImageView
    zoomIn.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View) {
            var oscale = getOscale()
            oscale = oscale + 0.1f
            val scalePara = paraDao.queryParaByName("oscale")
            if (scalePara == null) {
                paraDao.insert(SysPara("oscale", (oscale).toString()))
            } else {
                scalePara!!.setParaValue((oscale).toString())
                paraDao.update(scalePara)
            }
            setOscale(oscale)
            renderMain(oscale)
            renderMenu()
        }
    })

    // 缩小
    val zoomOut = findViewById(R.id.zoom_out) as ImageView
    zoomOut.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View) {
            var oscale = getOscale()
            if (oscale < 0.2) return
            oscale = oscale - 0.1f
            val scalePara = paraDao.queryParaByName("oscale")
            if (scalePara == null) {
                paraDao.insert(SysPara("oscale", (oscale).toString()))
            } else {
                scalePara!!.setParaValue((oscale).toString())
                paraDao.update(scalePara)
            }

            setOscale(oscale)
            renderMain(oscale)
            renderMenu()
        }
    })

    // 跳到塔基设置页面
    val craneSetting = findViewById(R.id.crane_setting) as ImageView
    craneSetting.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View) {
            val intent = Intent(this@MainActivity, CraneSetting::class.java)
            startActivity(intent)
        }
    })


    // 跳到区域设置页面
    val areaSetting = findViewById(R.id.area_setting) as ImageView
    areaSetting.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View) {
            val intent = Intent(this@MainActivity, AreaSetting::class.java)
            startActivity(intent)
        }
    })

    val protectSetting = findViewById(R.id.protect_area_setting) as ImageView
    protectSetting.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View) {
            val intent = Intent(this@MainActivity, ProtectSetting::class.java)
            startActivity(intent)
        }
    })

    // 跳到标定设置页面
    val calibrationSetting = findViewById(R.id.calibration_setting) as ImageView
    calibrationSetting.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View) {
            val intent = Intent(this@MainActivity, CalibrationSetting::class.java)
            intent.putExtra("craneType", mainCrane.getType())
            startActivity(intent)

            Handler().postDelayed({
                calibrationFlag.set(true) // 延时开关
            }, 1000)
        }
    })

    // 跳到告警设置页面
    val alarmSetting = findViewById(R.id.alarm_setting) as ImageView
    alarmSetting.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View) {
            val intent = Intent(this@MainActivity, AlarmSetting::class.java)
            startActivity(intent)
        }
    })

    // 跳转到负荷特性
    val loadAttribute = findViewById(R.id.load_attribute) as ImageView
    loadAttribute.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View) {
            val intent = Intent(this@MainActivity, LoadAttribute::class.java)
            startActivity(intent)
        }
    })

    // 跳转到日志记录
    val dataRecord = findViewById(R.id.data_record) as ImageView
    dataRecord.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View) {
            val intent = Intent(this@MainActivity, DataRecord::class.java)
            startActivity(intent)
        }
    })


    val CamerConf = findViewById(R.id.camera_conf) as ImageView
    CamerConf.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View) {
            val intent = Intent(this@MainActivity, CameraActivity::class.java)
            startActivity(intent)
        }
    })

    val superAdmin = findViewById(R.id.super_admin) as ImageView
    superAdmin.setOnClickListener(object : View.OnClickListener {
        override fun onClick(view: View) {
            val intent = Intent(this@MainActivity, SuperAdmin::class.java)
            intent.putExtra("superSuper", superSuper)
            startActivity(intent)
        }
    })
}
 */