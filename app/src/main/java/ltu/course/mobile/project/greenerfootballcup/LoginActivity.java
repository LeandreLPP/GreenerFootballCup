package ltu.course.mobile.project.greenerfootballcup;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.KeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;

public class LoginActivity extends AppCompatActivity {

    private static final int minYear = 2000;
    private EditText admin_code ;
    private EditText confirm_admin_Code;
    private EditText admin_email;
    private EditText confirm_admin_email;
    private EditText current_year;
    private Button btnToScreen2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        admin_code = (EditText)findViewById(R.id.admin_code);
        confirm_admin_Code = (EditText)findViewById(R.id.confirm_admin_code);
        admin_email = (EditText)findViewById(R.id.admin_email);
        confirm_admin_email = (EditText)findViewById(R.id.confirm_admin_email);
        current_year = (EditText)findViewById(R.id.current_year);
        btnToScreen2 = (Button)findViewById(R.id.btnToScreen2);

        current_year.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!current_year.getText().toString().equals("")) {
                    LoginDatas.getInstance().setYear(new Date(Integer.parseInt(current_year.getText().toString()), 0, 0));
                    if (LoginDatas.getInstance().isInitialized())
                        btnToScreen2.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        current_year.setKeyListener(null);
        current_year.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYearPicker();
            }
        });

        admin_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!confirm_admin_Code.getText().toString().equals(admin_code.getText().toString())){
                    LoginDatas.getInstance().setAdminCode(null);
                    confirm_admin_Code.setError("admin code error");
                    if(btnToScreen2.isEnabled())
                        btnToScreen2.setEnabled(false);
                }else{
                    LoginDatas.getInstance().setAdminCode(confirm_admin_Code.getText().toString());
                    confirm_admin_Code.setError(null);
                    if(LoginDatas.getInstance().isInitialized())
                        btnToScreen2.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirm_admin_Code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!confirm_admin_Code.getText().toString().equals(admin_code.getText().toString())){
                    LoginDatas.getInstance().setAdminCode(null);
                    confirm_admin_Code.setError("admin code error");
                    if(btnToScreen2.isEnabled())
                        btnToScreen2.setEnabled(false);
                }else{
                    LoginDatas.getInstance().setAdminCode(confirm_admin_Code.getText().toString());
                    confirm_admin_Code.setError(null);
                    if(LoginDatas.getInstance().isInitialized())
                        btnToScreen2.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        admin_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!confirm_admin_email.getText().toString().equals(admin_email.getText().toString())){
                    LoginDatas.getInstance().setEmailAddress(null);
                    confirm_admin_email.setError("admin email error");
                    if(btnToScreen2.isEnabled())
                        btnToScreen2.setEnabled(false);
                }else{
                    LoginDatas.getInstance().setEmailAddress(confirm_admin_email.getText().toString());
                    confirm_admin_email.setError(null);
                    if(LoginDatas.getInstance().isInitialized())
                        btnToScreen2.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirm_admin_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!confirm_admin_email.getText().toString().equals(admin_email.getText().toString())){
                    LoginDatas.getInstance().setEmailAddress(null);
                    confirm_admin_email.setError("admin email error");
                    if(btnToScreen2.isEnabled())
                        btnToScreen2.setEnabled(false);
                }else{
                    LoginDatas.getInstance().setEmailAddress(confirm_admin_email.getText().toString());
                    confirm_admin_email.setError(null);
                    if(LoginDatas.getInstance().isInitialized())
                        btnToScreen2.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnToScreen2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(getApplicationContext(), FieldActivity.class);
                startActivity(myIntent);
            }
        });
    }

    public void openYearPicker() {

        final Dialog dialogPicker = new Dialog(LoginActivity.this);
        dialogPicker.setTitle(R.string.title_year_picker);
        dialogPicker.setContentView(R.layout.number_picker);
        Button confirm_year = (Button) dialogPicker.findViewById(R.id.confirm_year);
        Button cancel_year = (Button) dialogPicker.findViewById(R.id.cancel_year);
        NumberPicker np = (NumberPicker) dialogPicker.findViewById(R.id.numberPicker);

        String[] years = getYears(minYear,Calendar.getInstance().get(Calendar.YEAR));
        np.setDisplayedValues(years);
        np.setMaxValue(years.length-1);

        confirm_year.setOnClickListener(v -> {
            current_year.setText(String.valueOf(years[np.getValue()]));
            dialogPicker.dismiss();
        });


        cancel_year.setOnClickListener(v -> dialogPicker.dismiss());
        dialogPicker.show();

    }

    public String[] getYears(int minimumInclusive, int maximumInclusive) {

        ArrayList<String> result = new ArrayList<String>();

        for(int i = maximumInclusive; i >= minimumInclusive; i--) {
            result.add(Integer.toString(i));
        }
        return result.toArray(new String[0]);
    }
}
