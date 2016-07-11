package com.beacon.transmitter.controllers.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.beacon.transmitter.MyApp;
import com.beacon.transmitter.controllers.util.NetworkUtil;
import com.beacon.transmitter.view.fragment.BaseFragment;


/**
 * Created by Yuriy on 2016-07-10 Ibeacon.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean status = NetworkUtil.isOnline();

        LocalBroadcastManager mgr = LocalBroadcastManager.getInstance(MyApp.appContext);
        Intent networkIntent = new Intent(BaseFragment.ACTION_NETWORK_STATUS);
        networkIntent.putExtra("status", status);
        mgr.sendBroadcast(networkIntent);
    }
}
