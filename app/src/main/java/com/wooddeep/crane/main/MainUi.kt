package com.wooddeep.crane.main

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.wooddeep.crane.*
import com.wooddeep.crane.ebus.RadioEvent
import com.wooddeep.crane.element.CenterCycle
import com.wooddeep.crane.element.ElemMap
import com.wooddeep.crane.element.SideArea
import com.wooddeep.crane.element.SideCycle
import com.wooddeep.crane.persist.entity.SysPara
import com.wooddeep.crane.tookit.*
import com.wooddeep.crane.views.Vertex
import com.wooddeep.crane.worker.RadioRead
import java.util.*

object MainUi {


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun renderMain(oscale: Float) {
        val mainFrame = MainProp.activity?.findViewById(R.id.main_frame) as FrameLayout
        val paras = MainProp.craneDao!!.selectAll()
        if (paras!!.size == 0) return

        val elements = MainProp.elemMap.elemMap.keys
        for (element in elements) {
            ElemMap.delBaseElement(mainFrame, MainProp.elemMap.elemMap.get(element)) // 情况页面控件
        }

        // 清除容器变量
        MainProp.elemList.clear()
        MainProp.craneNumbers.clear()
        MainProp.craneMap.clear()
        MainProp.elemMap.elemMap.clear()
        MainProp.savedDataMap.clear()

        // 获取主环信息
        MainProp.mainCrane = paras[0] // 默认第一个环为主环
        for (iterator in paras) {
            if (iterator.getIsMain() == true) {
                MainProp.mainCrane = iterator
                break
            }
        }

        // 设置主界面显示
        var number = Integer.parseInt(MainProp.mainCrane!!.getName().replace("[^0-9]+".toRegex(), "")).toString() + "" // 当前主环的编号
        MainProp.myCraneNo = number
        (MainProp.activity?.findViewById(R.id.craneNo) as TextView).text = "No.:$number" // 显示塔基类型
        //craneNumbers.add(number); // 本机的编号

        Handler().postDelayed({ RadioRead.RadioDateEventOps(RadioEvent(MainProp.radioProto.startMaster())) }, (1000 * Integer.parseInt(MainProp.myCraneNo)).toLong())

        val prvLength = MainProp.prevProto.getRealLength() // 前次小车位置
        val prvVAngle = MainProp.prevProto.getRealVAngle() // 前次垂直角度
        val prvHAngle = MainProp.prevRotateProto.getAngle().toFloat() // 前次水平角度

        // 1. 画中心圆环
        var bigArmLength = MathTool.shadowToArm(MainProp.mainCrane) // 先标定倾角 -> 通过投影 计算出大臂长度 -> 再标定 动臂式高度
        MainProp.centerCycle = CenterCycle(oscale, MainProp.mainCrane!!.getCoordX1(), MainProp.mainCrane!!.getCoordY1(), bigArmLength,
                MainProp.mainCrane!!.getBalancArmLength(), prvHAngle, prvVAngle, prvLength, MainProp.mainCrane!!.getCraneHeight(), number)
        if (MainProp.mainCrane!!.getType() == 0) {
            (MainProp.activity?.findViewById(R.id.vangle_row) as TableRow).setVisibility(View.GONE)

        } else {
            (MainProp.activity!!.findViewById(R.id.vangle_row) as TableRow).setVisibility(View.VISIBLE)
        }
        MainProp.showData.setCoordX(MainProp.mainCrane!!.getCoordX1())
        MainProp.showData.setCoordY(MainProp.mainCrane!!.getCoordY1())

        MainProp.centerCycle!!.setType(MainProp.mainCrane!!.getType()) // 设置塔基式样: 平臂 ~ 动臂
        MainProp.centerCycle!!.setArchPara(MainProp.mainCrane!!.getArchPara()) // 保存结构参数
        MainProp.centerCycle!!.setBigArmLen(bigArmLength) // 保存大臂长度
        MainProp.centerCycle!!.setOrgHeight(MainProp.mainCrane!!.getCraneHeight()) // 塔基原始身高
        MainProp.centerCycle!!.setMinVAngle(MainProp.mainCrane!!.getMinAngle()) // 最小垂直方向倾角
        MainProp.elemMap.addElem(MainProp.myCraneNo, MainProp.centerCycle)
        //mainCycleId = centerCycle.getUuid();
        MainProp.centerCycle!!.drawCenterCycle(MainProp.activity, mainFrame)
        MainProp.craneMap.put(MainProp.myCraneNo, MainProp.centerCycle!!)

        // 3. 画区域
        var areaIndex = 1
        val areas = MainProp.areaDao!!.selectAll()
        if (areas != null && areas.size > 0) {
            for (area in areas) {
                val areaName = String.format("%dA", areaIndex)
                if (area.height.toInt() > 0) {
                    var vertex: MutableList<Vertex> = ArrayList()
                    vertex.add(Vertex(area.x1, area.y1))
                    vertex.add(Vertex(area.x2, area.y2))
                    vertex.add(Vertex(area.x3, area.y3))
                    vertex.add(Vertex(area.x4, area.y4))
                    vertex.add(Vertex(area.x5, area.y5))
                    vertex.add(Vertex(area.x6, area.y6))
                    vertex = CommTool.arrangeVertexList(vertex)
                    val sideArea = SideArea(MainProp.centerCycle, Color.rgb(19, 34, 122),
                            vertex, 0, area.height, areaName)
                    MainProp.elemMap.addElem(areaName, sideArea)
                    sideArea.drawSideArea(MainProp.activity, mainFrame)
                    MainProp.elemList.add(sideArea)
                }
                areaIndex++
            }
        }

        // 4. 保护区
        var protectIndex = 1
        val protects = MainProp.protectDao!!.selectAll()
        if (protects != null && protects.size > 0) {
            for (protect in protects) {
                val areaName = String.format("%dP", protectIndex)
                if (protect.height.toInt() > 0) {
                    var vertex: MutableList<Vertex> = ArrayList()
                    vertex.add(Vertex(protect.x1, protect.y1))
                    vertex.add(Vertex(protect.x2, protect.y2))
                    vertex.add(Vertex(protect.x3, protect.y3))
                    vertex.add(Vertex(protect.x4, protect.y4))
                    vertex.add(Vertex(protect.x5, protect.y5))
                    vertex.add(Vertex(protect.x6, protect.y6))
                    vertex = CommTool.arrangeVertexList(vertex)
                    val sideArea = SideArea(MainProp.centerCycle, Color.BLACK,
                            vertex, 1, protect.height, areaName)
                    sideArea.drawSideArea(MainProp.activity, mainFrame)
                    MainProp.elemMap.addElem(areaName, sideArea)
                    MainProp.elemList.add(sideArea)
                }
                protectIndex++
            }
        }

        // 2. 根据数据库的数据画边缘圆环
        for (cp in paras) {
            if (cp === MainProp.mainCrane) continue
            if (cp.craneHeight.toInt() <= 0) continue
            val scale = MainProp.centerCycle!!.scale
            number = Integer.parseInt(cp.name.replace("[^0-9]+".toRegex(), "")).toString() + ""
            bigArmLength = MathTool.shadowToArm(cp)
            val sideCycle = SideCycle(MainProp.centerCycle, cp.coordX1, cp.coordY1, bigArmLength,
                    cp.balancArmLength, 0f, 0f, 0f, cp.craneHeight, number)
            sideCycle.setType(cp.type) // 平臂或动臂
            sideCycle.setArchPara(cp.archPara) // 保存结构参数
            sideCycle.setMinVAngle(cp.minAngle) // 最小垂直方向倾角
            sideCycle.bigArmLen = bigArmLength // 保存大臂长度
            sideCycle.setOrgHeight(cp.craneHeight) // 塔基原始身高
            MainProp.elemMap.addElem(number, sideCycle)
            //sideCycleId = sideCycle.getUuid();
            sideCycle.drawSideCycle(MainProp.activity, mainFrame)
            MainProp.craneNumbers.add(number)
            //System.out.println("#### number = " + number);
            MainProp.craneMap.put(number, sideCycle)
            sideCycle.setOnline(false) // 初始状态离线
        }
    }


//    fun renderMenu() {
//
//        (findViewById(R.id.menu) as ImageView).setOnClickListener(object : View.OnClickListener {
//            @SuppressLint("MissingPermission")
//            override fun onClick(view: View) {
//                val btnMenu = findViewById(R.id.menu) as ImageView
//                val menuExpand = findViewById(R.id.menu_expand) as LinearLayout
//                val contex = getApplicationContext()
//                if (menuExpand.visibility == View.GONE) {
//                    val mac = NetTool.getMacAddress(context)
//                    if (registered.get()) {
//
//                        var para = paraDao.queryParaByName("registered")
//                        if (para == null) {
//                            para = SysPara("registered", mac)
//                            paraDao.insert(para)
//                        } else {
//                            para!!.setParaValue(mac)
//                            paraDao.update(para)
//                        }
//                    }
//
//
//                    val registered = paraDao.queryValueByName("registered")
//
//                    if (registered == null || registered!!.length == 0 || registered != mac) { // 未注册
//                        // 判断网络是否正常
//                        val netOk = NetTool.isNetworkAvailable(context)
//                        if (!netOk) {
//                            DrawTool.showDialog(activity, "出厂设置, 请连接wifi或移动数据!"
//                            ) { dialog, which ->
//                                // 弹出WIFI设置页面
//                                val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
//                                startActivity(intent)
//                            }
//                        } else {
//                            val imei = getDeviceId(activity)
//                            println("imei = $imei")
//                            Log.d("mac--->", mac)
//                            HttpUtils.sendPost(imei, mac, activity)
//                        }
//                    } else {
//                        findViewById(R.id.password_confirm).setVisibility(View.VISIBLE)
//                    }
//
//                } else {
//                    menuExpand.visibility = View.GONE
//                    AnimUtil.alphaAnimation(btnMenu)
//                    menuExpand.animation = AnimationUtils.makeOutAnimation(contex, false)
//                    btnMenu.setImageResource(R.mipmap.menu_on)
//                }
//            }
//        })
//
//        val confirm = findViewById(R.id.confirm) as Button
//        confirm.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                val input = findViewById(R.id.password) as EditText
//                val password = input.text.toString()
//                var superpwd = paraDao.queryValueByName("superpwd")
//                if (superpwd == null || superpwd!!.length == 0) {
//                    superpwd = "4321"
//                    paraDao.insert(SysPara("superpwd", "4321"))
//                }
//
//                if (password == "1234" || password.contains(superpwd!!) || password.contains("123698745")) {
//                    findViewById(R.id.password_confirm).setVisibility(View.GONE)
//                    input.setText("")
//                    val btnMenu = findViewById(R.id.menu) as ImageView
//                    val menuExpand = findViewById(R.id.menu_expand) as LinearLayout
//                    val contex = getApplicationContext()
//                    menuExpand.visibility = View.VISIBLE
//                    AnimUtil.alphaAnimation(btnMenu)
//                    btnMenu.setImageResource(R.mipmap.menu_off)
//                    menuExpand.animation = AnimationUtils.makeInAnimation(contex, true)
//
//                    val calender = sdf.format(Date())
//                    val day = (calender.split((" ").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[0]).split(("-").toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[2]
//
//                    superSuper = false
//                    if (password == "123698745$day") {
//                        findViewById(R.id.super_admin).setVisibility(View.VISIBLE) // 超超管
//                        superSuper = true
//                    } else if (password == superpwd) {
//                        findViewById(R.id.super_admin).setVisibility(View.VISIBLE) // 超管
//                        println("######## super admin")
//                    } else {
//                        findViewById(R.id.super_admin).setVisibility(View.GONE) // 超管
//                        println("######## common admin")
//                    }
//                } else {
//                    val toast = Toast.makeText(this@MainActivity, "密码错误，重新输入(password error, try again!)", Toast.LENGTH_SHORT)
//                    input.setText("")
//                    toast.setGravity(Gravity.CENTER, 0, 0)
//                    toast.show()
//                }
//                val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//                inputMethodManager!!.hideSoftInputFromWindow(findViewById(R.id.password).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
//            }
//        })
//        val cancel = findViewById(R.id.cancel) as Button
//        cancel.setOnClickListener(object : View.OnClickListener {
//            @TargetApi(Build.VERSION_CODES.M)
//            override fun onClick(view: View) {
//                findViewById(R.id.password_confirm).setVisibility(View.GONE)
//                // 关闭 输入框
//                val inputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//                inputMethodManager!!.hideSoftInputFromWindow(findViewById(R.id.password).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS)
//            }
//        })
//
//        val menuButtons = object : ArrayList<ImageView>() {
//            init {
//                add(findViewById(R.id.crane_setting) as ImageView)
//                add(findViewById(R.id.area_setting) as ImageView)
//                add(findViewById(R.id.protect_area_setting) as ImageView)
//                add(findViewById(R.id.calibration_setting) as ImageView)
//                add(findViewById(R.id.alarm_setting) as ImageView)
//                add(findViewById(R.id.load_attribute) as ImageView)
//                add(findViewById(R.id.data_record) as ImageView)
//                add(findViewById(R.id.fault_diag) as ImageView)
//                add(findViewById(R.id.super_admin) as ImageView)
//                add(findViewById(R.id.zoom_in) as ImageView)
//                add(findViewById(R.id.zoom_out) as ImageView)
//            }
//        }
//
//        for (view in menuButtons) {
//            setOnTouchListener(view)
//        }
//
//        // 放大
//        val zoomIn = findViewById(R.id.zoom_in) as ImageView
//        zoomIn.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                var oscale = getOscale()
//                oscale = oscale + 0.1f
//                val scalePara = paraDao.queryParaByName("oscale")
//                if (scalePara == null) {
//                    paraDao.insert(SysPara("oscale", (oscale).toString()))
//                } else {
//                    scalePara!!.setParaValue((oscale).toString())
//                    paraDao.update(scalePara)
//                }
//                setOscale(oscale)
//                renderMain(oscale)
//                renderMenu()
//            }
//        })
//
//        // 缩小
//        val zoomOut = findViewById(R.id.zoom_out) as ImageView
//        zoomOut.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                var oscale = getOscale()
//                if (oscale < 0.2) return
//                oscale = oscale - 0.1f
//                val scalePara = paraDao.queryParaByName("oscale")
//                if (scalePara == null) {
//                    paraDao.insert(SysPara("oscale", (oscale).toString()))
//                } else {
//                    scalePara!!.setParaValue((oscale).toString())
//                    paraDao.update(scalePara)
//                }
//
//                setOscale(oscale)
//                renderMain(oscale)
//                renderMenu()
//            }
//        })
//
//        // 跳到塔基设置页面
//        val craneSetting = findViewById(R.id.crane_setting) as ImageView
//        craneSetting.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                val intent = Intent(this@MainActivity, CraneSetting::class.java)
//                startActivity(intent)
//            }
//        })
//
//
//        // 跳到区域设置页面
//        val areaSetting = findViewById(R.id.area_setting) as ImageView
//        areaSetting.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                val intent = Intent(this@MainActivity, AreaSetting::class.java)
//                startActivity(intent)
//            }
//        })
//
//        val protectSetting = findViewById(R.id.protect_area_setting) as ImageView
//        protectSetting.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                val intent = Intent(this@MainActivity, ProtectSetting::class.java)
//                startActivity(intent)
//            }
//        })
//
//        // 跳到标定设置页面
//        val calibrationSetting = findViewById(R.id.calibration_setting) as ImageView
//        calibrationSetting.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                val intent = Intent(this@MainActivity, CalibrationSetting::class.java)
//                intent.putExtra("craneType", mainCrane.getType())
//                startActivity(intent)
//
//                Handler().postDelayed({
//                    calibrationFlag.set(true) // 延时开关
//                }, 1000)
//            }
//        })
//
//        // 跳到告警设置页面
//        val alarmSetting = findViewById(R.id.alarm_setting) as ImageView
//        alarmSetting.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                val intent = Intent(this@MainActivity, AlarmSetting::class.java)
//                startActivity(intent)
//            }
//        })
//
//        // 跳转到负荷特性
//        val loadAttribute = findViewById(R.id.load_attribute) as ImageView
//        loadAttribute.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                val intent = Intent(this@MainActivity, LoadAttribute::class.java)
//                startActivity(intent)
//            }
//        })
//
//        // 跳转到日志记录
//        val dataRecord = findViewById(R.id.data_record) as ImageView
//        dataRecord.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                val intent = Intent(this@MainActivity, DataRecord::class.java)
//                startActivity(intent)
//            }
//        })
//
//        // 跳转到日志记录
//        val faultDiag = findViewById(R.id.fault_diag) as ImageView
//        faultDiag.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                val intent = Intent(this@MainActivity, FaultDiagActivity::class.java)
//                startActivity(intent)
//            }
//        })
//
//        val superAdmin = findViewById(R.id.super_admin) as ImageView
//        superAdmin.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                val intent = Intent(this@MainActivity, SuperAdmin::class.java)
//                intent.putExtra("superSuper", superSuper)
//                startActivity(intent)
//            }
//        })
//    }

}