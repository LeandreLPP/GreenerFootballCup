package ltu.course.mobile.project.greenerfootballcup.Activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.Calendar;

import ltu.course.mobile.project.greenerfootballcup.R;
import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;

public class LoginActivity extends AppCompatActivity {

    private static final int minYear = 2000;
    private EditText admin_code ;
    private EditText admin_email;
    private EditText confirm_admin_email;
    private EditText current_year;
    private Button btnToScreen3;
    private EditText confirm_admin_code;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        admin_code = (EditText)findViewById(R.id.admin_code);
        admin_email = (EditText)findViewById(R.id.admin_email);
        confirm_admin_email = (EditText)findViewById(R.id.confirm_admin_email);
        current_year = (EditText)findViewById(R.id.current_year);
        btnToScreen3 = (Button)findViewById(R.id.btnToScreen3);
        confirm_admin_code = (EditText)findViewById(R.id.confirm_admin_code);

        current_year.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!current_year.getText().toString().equals("")) {
                    LoginDatas.getInstance().setYear(current_year.getText().toString());
                    if (isLoggedIn())
                        btnToScreen3.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        current_year.setKeyListener(null);
        //Open a date picker to choose the current year
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
                if(!confirm_admin_code.getText().toString().equals(admin_code.getText().toString())){
                    LoginDatas.getInstance().setAdminCode(null);
                    confirm_admin_code.setError(getResources().getString(R.string.passwords_do_not_match));
                    if(btnToScreen3.isEnabled())
                        btnToScreen3.setEnabled(false);
                }else{
                    LoginDatas.getInstance().setAdminCode(admin_code.getText().toString());
                    confirm_admin_code.setError(null);
                    if(isLoggedIn())
                        btnToScreen3.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        confirm_admin_code.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!confirm_admin_code.getText().toString().equals(admin_code.getText().toString())){
                    LoginDatas.getInstance().setAdminCode(null);
                    confirm_admin_code.setError(getResources().getString(R.string.passwords_do_not_match));
                    if(btnToScreen3.isEnabled())
                        btnToScreen3.setEnabled(false);
                }else{
                    LoginDatas.getInstance().setAdminCode(admin_code.getText().toString());
                    confirm_admin_code.setError(null);
                    if(isLoggedIn())
                        btnToScreen3.setEnabled(true);
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
                    confirm_admin_email.setError(getResources().getString(R.string.email_do_not_match));
                    if(btnToScreen3.isEnabled())
                        btnToScreen3.setEnabled(false);
                }else{
                    LoginDatas.getInstance().setEmailAddress(confirm_admin_email.getText().toString());
                    confirm_admin_email.setError(null);
                    if(isLoggedIn())
                        btnToScreen3.setEnabled(true);
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
                    confirm_admin_email.setError(getResources().getString(R.string.email_do_not_match));
                    if(btnToScreen3.isEnabled())
                        btnToScreen3.setEnabled(false);
                }else{
                    LoginDatas.getInstance().setEmailAddress(confirm_admin_email.getText().toString());
                    confirm_admin_email.setError(null);
                    if(isLoggedIn())
                        btnToScreen3.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnToScreen3.setOnClickListener(v -> {
            if(!admin_code.getText().toString().equals(LoginDatas.getInstance().getAdminCode())){
                admin_code.setError(getResources().getString(R.string.wrongAdminCode));
            }else{
                Intent myIntent = new Intent(getApplicationContext(), FieldActivity.class);
                startActivity(myIntent);
            }
        });

    }

    //Check if every informatino has been completed
    public boolean isLoggedIn(){
        CharSequence t = confirm_admin_email.getError();
        if(!admin_code.getText().toString().equals("") && confirm_admin_code.getError() == null && confirm_admin_email.getError() == null && !confirm_admin_email.getText().toString().equals("") && !current_year.getText().toString().equals(""))
            return true;
        return false;
    }

    public void openYearPicker() {

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.number_picker, null);

        //Setup the number picker since 2000 to the current year
        NumberPicker np = (NumberPicker) view.findViewById(R.id.numberPicker);
        String[] years = getYears(minYear,Calendar.getInstance().get(Calendar.YEAR));
        np.setDisplayedValues(years);
        np.setMaxValue(years.length-1);

        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this).setView(view)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        //Setup the buttons
        dialog.setOnShowListener(dialogInterface -> {

            Button btnPositive = ((android.app.AlertDialog) dialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
            btnPositive.setOnClickListener(v -> {
                current_year.setText(String.valueOf(years[np.getValue()]));
                dialog.dismiss();

            });

            Button btnNegative = ((android.app.AlertDialog) dialog).getButton(AlertDialog.BUTTON_NEGATIVE);
            btnNegative.setOnClickListener(v -> {
                dialog.dismiss();

            });
        });
        dialog.show();
    }

    //Return a string array containing all the years since 2000 to the current year
    public String[] getYears(int minimumInclusive, int maximumInclusive) {

        ArrayList<String> result = new ArrayList<String>();

        for(int i = maximumInclusive; i >= minimumInclusive; i--) {
            result.add(Integer.toString(i));
        }
        return result.toArray(new String[0]);
    }

}
