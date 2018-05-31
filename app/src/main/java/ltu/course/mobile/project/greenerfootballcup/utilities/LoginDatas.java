package ltu.course.mobile.project.greenerfootballcup.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.widget.Button;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ltu.course.mobile.project.greenerfootballcup.R;
import ltu.course.mobile.project.greenerfootballcup.utilities.CustomView.NewPasswordView;

import static android.content.Context.MODE_PRIVATE;

public class LoginDatas {

    private static final LoginDatas ourInstance = new LoginDatas();
    // Current year (make variable public to access from outside)
    private static final String KEY_YEAR = "year";
    // Email address (make variable public to access from outside)
    private static final String KEY_EMAIL = "email";
    // Admin_code (make variable public to access from outside)
    private static final String KEY_ADMIN_CODE = "admin_code";
    // File name (make variable public to access from outside)
    private static final String KEY_FILE = "login";
    // Maximum number of players in a team
    private static final String KEY_MAX_PLAYER = "maxPlayer";
    // Maximum number of overaged players in a team
    private static final String KEY_MAX_OVERAGED_PLAYER = "maxOveragedPlayer";
    // Age threshold
    private static final String KEY_AGE_THRESHOLD = "ageThreshold";

    private Bitmap signature;

    // Shared Preferences
    private SharedPreferences pref;
    // Editor for Shared preferences
    private SharedPreferences.Editor editor;
    // Context
    private Context context;

    private LoginDatas() {
    }

    public static LoginDatas getInstance() {
        return ourInstance;
    }

    public void initialize(Context ctx) {
        context = ctx;
        pref = context.getSharedPreferences(KEY_FILE, MODE_PRIVATE);
        editor = pref.edit();
        setAgeThreshold(getAgeThreshold());
        setMaxOveragedPlayer(getMaxOveragedPlayer());
        setMaxPlayer(getMaxPlayer());
        signature = null;
        editor.apply();
    }

    //Get the year in the preferred file and then create a Date using it
    public Date getYear() {
        DateFormat format = new SimpleDateFormat("yyyy", Locale.getDefault());
        Date ret;
        try
        {
            ret = format.parse(pref.getString(KEY_YEAR, null));
        }
        catch (ParseException e)
        {
            ret = Calendar.getInstance().getTime();
            e.printStackTrace();
        }
        return ret;
    }

    //return the preferred admin_code
    public String getAdminCode() {
        return pref.getString(KEY_ADMIN_CODE, context.getString(R.string.default_password));
    }

    //return the preferred email
    public String getEmailAddress() {
        return pref.getString(KEY_EMAIL, null);
    }

    public int getMaxPlayer() {
        return pref.getInt(KEY_MAX_PLAYER,
                           context.getResources().getInteger(R.integer.default_maxPlayer));
    }

    public int getMaxOveragedPlayer() {
        return pref.getInt(KEY_MAX_OVERAGED_PLAYER,
                           context.getResources().getInteger(R.integer.default_maxOveragedPlayer));
    }

    public int getAgeThreshold() {
        return pref.getInt(KEY_AGE_THRESHOLD,
                           context.getResources().getInteger(R.integer.default_ageThreshold));
    }

    public void setYear(String year) {
        editor.putString(KEY_YEAR, year);
        editor.commit();
    }

    public void setAdminCode(String adminCode) {
        editor.putString(KEY_ADMIN_CODE, adminCode);
        editor.commit();
    }

    public void setEmailAddress(String emailAddress) {
        editor.putString(KEY_EMAIL, emailAddress);
        editor.commit();
    }

    public void setMaxPlayer(int value) {
        editor.putInt(KEY_MAX_PLAYER, value);
        editor.commit();
    }

    public void setMaxOveragedPlayer(int value) {
        editor.putInt(KEY_MAX_OVERAGED_PLAYER, value);
        editor.commit();
    }

    public void setAgeThreshold(int value) {
        editor.putInt(KEY_AGE_THRESHOLD, value);
        editor.commit();
    }

    public void setSignature(Bitmap bitmap) {
        signature = bitmap;
    }

    public Bitmap getSignature() {
        return signature;
    }
}
