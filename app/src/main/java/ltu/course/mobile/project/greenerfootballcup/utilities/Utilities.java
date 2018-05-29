package ltu.course.mobile.project.greenerfootballcup.utilities;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

public class Utilities {

    public static synchronized void checkInternetConnection(Activity activity) {
        ConnectivityManager cm = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = cm != null && cm.getActiveNetworkInfo() != null;
        if (!connected)
        {
            RequirePermissionDialogFragment dialogFragment = new RequirePermissionDialogFragment();
            dialogFragment.show(activity.getFragmentManager(), "need_internet");
        }
    }

    public static class Result{
        public String errorMessage;
        public boolean success;
    }
}
