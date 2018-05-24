package ltu.course.mobile.project.greenerfootballcup.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
        alert.setPositiveButton(android.R.string.ok, null); //Set to null. We override the onclick
        alert.setNegativeButton(android.R.string.cancel, null);

        AlertDialog dialog = alert.create();

        EditText et_confirm_new_password = newPasswordView.findViewById(R.id.et_confirm_new_password);

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button btnPositive = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                btnPositive.setOnClickListener(view -> {
                    if(et_confirm_new_password.getError() == null){
                        LoginDatas.getInstance().setAdminCode(et_confirm_new_password.getText().toString());
                        dialog.dismiss();
                    }
                });

            }
        });
        dialog.show();
    }

    public void openVerifyPassword(Activity activity){
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.verify_password_view, null);

        EditText password = (EditText)view.findViewById(R.id.et_verify_password);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(password.getError() != null)
                    password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final AlertDialog dialog = new AlertDialog.Builder(activity).setView(view)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface dialogInterface) {

                Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                button.setOnClickListener(view -> {
                    if(password.getText().toString().equals(getAdminCode())){
                        dialog.dismiss();
                        openConfingPassword(activity);
                    }
                    else{
                        password.setError(activity.getResources().getString(R.string.wrongAdminCode));
                    }
                });
            }
        });
        dialog.show();
    }

}
