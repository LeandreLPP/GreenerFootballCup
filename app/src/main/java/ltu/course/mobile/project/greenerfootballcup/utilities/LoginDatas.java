package ltu.course.mobile.project.greenerfootballcup.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;

import ltu.course.mobile.project.greenerfootballcup.R;
import ltu.course.mobile.project.greenerfootballcup.utilities.CustomView.NewPasswordView;

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
    // Maximum number of players in a team
    public static final String KEY_MAX_PLAYER = "maxPlayer";
    // Maximum number of overaged players in a team
    public static final String KEY_MAX_OVERAGED_PLAYER = "maxOveragedPlayer";
    // Age threshold
    public static final String KEY_AGE_THRESHOLD = "ageThreshold";


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
        setAgeThreshold(getAgeThreshold());
        setMaxOveragedPlayer(getMaxOveragedPlayer());
        setMaxPlayer(getMaxPlayer());
        signature = null;
        initialized = true;
    }


    //Get the year in the preferred file and then create a Date using it
    public Date getYear() { return new Date(Integer.parseInt(pref.getString(KEY_YEAR,null)), 0, 0); }

    //return the preferred admin_code
    public String getAdminCode() { return pref.getString(KEY_ADMIN_CODE,context.getString(R.string.default_password)); }

    //return the preferred email
    public String getEmailAddress() { return pref.getString(KEY_EMAIL,null); }

    public int getMaxPlayer(){ return pref.getInt(KEY_MAX_PLAYER,context.getResources().getInteger(R.integer.default_maxPlayer));}

    public int getMaxOveragedPlayer(){ return pref.getInt(KEY_MAX_OVERAGED_PLAYER,context.getResources().getInteger(R.integer.default_maxOveragedPlayer));}

    public int getAgeThreshold(){ return pref.getInt(KEY_AGE_THRESHOLD,context.getResources().getInteger(R.integer.default_ageThreshold));}

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

    public void setMaxPlayer(int value){
        editor.putInt(KEY_MAX_PLAYER,value);
        editor.commit();
    }

    public void setMaxOveragedPlayer(int value){
        editor.putInt(KEY_MAX_OVERAGED_PLAYER,value);
        editor.commit();
    }

    public void setAgeThreshold(int value){
        editor.putInt(KEY_AGE_THRESHOLD,value);
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
        alert.setPositiveButton(android.R.string.ok, null); //Set to null. We override the onclick
        alert.setNegativeButton(android.R.string.cancel, null);

        AlertDialog dialog = alert.create();

        EditText et_confirm_new_password = newPasswordView.findViewById(R.id.et_confirm_new_password);

        dialog.setOnShowListener(dialogInterface -> {

            Button btnPositive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnPositive.setOnClickListener(view -> {
                if(et_confirm_new_password.getError() == null && !et_confirm_new_password.getText().toString().equals("")){
                    LoginDatas.getInstance().setAdminCode(et_confirm_new_password.getText().toString());
                    dialog.dismiss();
                }
            });

            Button btnNegative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            btnNegative.setOnClickListener(view -> {
                //if it's the first utilisation of the application, the user cannot close this window without entering a new password
                if(!getAdminCode().equals(context.getResources().getString(R.string.default_password)))
                    dialog.dismiss();
            });

        });
        dialog.show();
    }

}
