package ltu.course.mobile.project.greenerfootballcup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class MatchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener((c)->{
            Intent i = new Intent(this, TeamActivity.class);
            startActivity(i);
        });
    }
}
