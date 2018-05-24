package ltu.course.mobile.project.greenerfootballcup.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.view.Gravity;
import android.view.View;
import android.widget.PopupWindow;

import java.util.Date;

import ltu.course.mobile.project.greenerfootballcup.R;

import static android.content.Context.MODE_PRIVATE;

public class LoginDatas {

    private static final LoginDatas ourInstance = new LoginDatas();
    // Current year (make variable public to access from outside)
    public static final String KEY_YEAR = "year";
    // Email address (make variable public to access from outside)
    public static final String KEY_EMAIL = "email";
    // Admin_code (make variable public to access from outside)
    public static final String KEY_ADMIN_CODE = "admin_code";
    // File name (make variable public to access from outside)
    public static final String KEY_FILE = "login";

    private boolean initialized;

    private Bitmap signature;

    // Shared Preferences
    private SharedPreferences pref;
    // Editor for Shared preferences
    private SharedPreferences.Editor editor;
    // Context
    private Context context;


    private LoginDatas(){ initialized = false;}

    public static LoginDatas getInstance() {
        return ourInstance;
    }

    public void initialize(Context ctx)
    {
        context = ctx;
        pref = context.getSharedPreferences(KEY_FILE, MODE_PRIVATE);
        editor = pref.edit();
        signature = null;
        initialized = true;
    }


    //Get the year in the preferred file and then create a Date using it
    public Date getYear() { return new Date(Integer.parseInt(pref.getString(KEY_YEAR,null)), 0, 0); }

    //return the preferred admin_code
    public String getAdminCode() { return pref.getString(KEY_ADMIN_CODE,context.getString(R.string.default_password)); }

    //return the preferred email
    public String getEmailAddress() { return pref.getString(KEY_EMAIL,null); }


    public void setYear(String year){
        editor.putString(KEY_YEAR,year);
        editor.commit();
    }

    public void setAdminCode(String adminCode){
        editor.putString(KEY_ADMIN_CODE,adminCode);
        editor.commit();
    }

    public void setEmailAddress(String emailAddress){
        editor.putString(KEY_EMAIL,emailAddress);
        editor.commit();
    }

    public void setSignature(Bitmap bitmap){
        signature = bitmap;
    }

    public Bitmap getSignature() {
        return signature;
    }

    public void openConfingPassword(Activity activity){
        AlertDialog.Builder alert = new AlertDialog.Builder(activity);
        NewPasswordView newPasswordView = new NewPasswordView(activity);
        // this is set the view from XML inside AlertDialog
        alert.setView(newPasswordView);
        // disallow cancel of AlertDialog on click of back button and outside touch
        alert.setCancelable(false);

        AlertDialog dialog = alert.create();
        newPasswordView.setDialog(dialog);
        dialog.show();
    }

}
