package pl.edu.pwr.asystenttreningu;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by michalos on 10.11.14.
 */

public class CheckInternetConnection {

    public static Boolean checkConnection(Context mContext) {

        NetworkInfo info = ((ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (info == null || !info.isConnected())
        {
            return false;
        }
        return true;

    }
}
