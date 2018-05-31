package ltu.course.mobile.project.greenerfootballcup.utilities.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.TableLayout;

import ltu.course.mobile.project.greenerfootballcup.R;
import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;

public class ConfigurationView extends TableLayout {

    EditText edt_max_age;
    EditText edt_max_overaged_player;
    EditText edt_max_number_player;

    public ConfigurationView(Context context) {
        super(context);
        initialize();
    }

    public ConfigurationView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public void initialize(){
        inflate(getContext(), R.layout.configuration_view, this);

        edt_max_age = findViewById(R.id.edt_max_age);
        edt_max_overaged_player = findViewById(R.id.edt_max_overaged_player);
        edt_max_number_player = findViewById(R.id.edt_max_number_player);

        //Initialize the text value with the preferred value
        edt_max_age.setText(String.valueOf(LoginDatas.getInstance().getAgeThreshold()));
        edt_max_number_player.setText(String.valueOf(LoginDatas.getInstance().getMaxPlayer()));
        edt_max_overaged_player.setText(String.valueOf(LoginDatas.getInstance().getMaxOveragedPlayer()));
    }
}
