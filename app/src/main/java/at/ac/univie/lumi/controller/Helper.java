package at.ac.univie.lumi.controller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

/**
 * Created by phili on 6/19/2017.
 *
 * This class provides methods which are used in some activities. The class is used for code reusing.
 */

public final class Helper {

    /**
     * This method is responisble to open a specific website.
     * @param url The url of the website.
     */

    public static void goToURL(Activity activity, String url){
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        activity.startActivity(intent);
    }

    /**
     * This method checks if an internet/network connection is available and return true or false.
     * @return Returns true or false depending on the internet/network connection.
     */

    public static boolean isNetworkAvailable(Activity activity) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
