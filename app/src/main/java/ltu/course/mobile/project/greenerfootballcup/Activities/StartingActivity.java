package ltu.course.mobile.project.greenerfootballcup.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import ltu.course.mobile.project.greenerfootballcup.R;
import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Match;


public class StartingActivity extends AppCompatActivity {

    Button btnToSecondScreen;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        btnToSecondScreen = (Button)findViewById(R.id.btnToSecondScreen);

        btnToSecondScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Force the user to initialise his password at the first utilisation

                Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(myIntent);

            }
        });

        //Very important, need to initialize the LoginData with the applicationContext to be able to use SharedPreference file
       // LoginDatas.getInstance().initialize(getApplicationContext());

    }

}
