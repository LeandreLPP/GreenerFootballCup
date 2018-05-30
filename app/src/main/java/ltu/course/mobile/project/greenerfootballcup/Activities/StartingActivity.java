package ltu.course.mobile.project.greenerfootballcup.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.io.File;

import ltu.course.mobile.project.greenerfootballcup.R;
import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;

public class StartingActivity extends AppCompatActivity {

    Button btnToSecondScreen;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);

        btnToSecondScreen = (Button) findViewById(R.id.btnToSecondScreen);

        btnToSecondScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Delete previously take report pictures
                File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

                File fileImgResult = new File(dir, "pictureResults.jpg");
                File fileImgFairplay = new File(dir, "pictureFairplay.jpg");
                File backupFile = new File(dir, "backup.jpg");

                fileImgResult.delete();
                fileImgFairplay.delete();
                backupFile.delete();

                //Force the user to initialise his password at the first utilisation
                Intent myIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(myIntent);
            }
        });

        //Very important, need to initialize the LoginData with the applicationContext to be able to use SharedPreference file
        LoginDatas.getInstance().initialize(getApplicationContext());
    }
}
