package com.wooddeep.crane.message;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.wooddeep.crane.persist.dao.log.SwitchRecDao;
import com.wooddeep.crane.persist.entity.log.SwitchRec;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by niuto on 2019/9/30.
 */

public class BootUpReceiver extends BroadcastReceiver {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SwitchRec switchRec = new SwitchRec();

    @Override
    public void onReceive(Context context, Intent intent) {

        SwitchRecDao switchRecDao = new SwitchRecDao(context);

        switchRec.setTime(sdf.format(new Date()));

        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            switchRec.setAction("power on");
            switchRecDao.insert(switchRec);
            System.out.println("## power on");
        }

        if(intent.getAction().equals(Intent.ACTION_SHUTDOWN)){
            switchRec.setAction("power off");
            switchRecDao.insert(switchRec);
            System.out.println("## power off");
        }
    }
}
