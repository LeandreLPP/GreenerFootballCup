package ltu.course.mobile.project.greenerfootballcup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.PopupWindow;

import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;
import ltu.course.mobile.project.greenerfootballcup.utilities.NewPasswordView;


public class StartingActivity extends Activity {

    Button btnToSecondScreen;
    PopupWindow popupWindow;

    private Handler myHandler = new Handler();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        btnToSecondScreen = (Button)findViewById(R.id.btnToSecondScreen);

        btnToSecondScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Force the user to initialise his password at the first utilisation
                if(LoginDatas.getInstance().getAdminCode().equals(getString(R.string.default_password)))
                    LoginDatas.getInstance().openConfingPassword(StartingActivity.this);
                else {
                    Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(myIntent);
                }
            }
        });

        //Very important, need to initialize the LoginData with the applicationContext to be able to use SharedPreference file
        LoginDatas.getInstance().initialize(getApplicationContext());

    }

}
