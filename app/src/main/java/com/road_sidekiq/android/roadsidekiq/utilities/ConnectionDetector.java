package com.road_sidekiq.android.roadsidekiq.utilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by rewin0087 on 4/18/15.
 */
public class ConnectionDetector {

    public static String HOST = "http://10.49.236.52:3000";

    private Context _context;

    public ConnectionDetector(Context context){
        this._context = context;
    }

    /*
     * Checking for all possible internet providers
     *
     * */
    public boolean isConnectingToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    public void showDialog(Context context) {
        AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
        localBuilder.setTitle("Internet Connection Error")
                .setMessage("Please connect to working Internet connection")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface paramAnonymous2DialogInterface, int paramAnonymous2Int) {
                        paramAnonymous2DialogInterface.cancel();
                    }
                });
        localBuilder.create().show();
    }
}