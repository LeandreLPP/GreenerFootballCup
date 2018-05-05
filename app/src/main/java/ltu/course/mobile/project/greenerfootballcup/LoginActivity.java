package ltu.course.mobile.project.greenerfootballcup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;

public class LoginActivity extends AppCompatActivity {

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
                if(current_year.getText().length() != 4){
                    LoginDatas.getInstance().setYear(null);
                    current_year.setError("syntax error");
                    if(btnToScreen2.isEnabled())
                        btnToScreen2.setEnabled(false);
                }else{
                    LoginDatas.getInstance().setYear(new Date(Integer.parseInt(current_year.getText().toString()),0,0));
                    if(LoginDatas.getInstance().isInitialized())
                        btnToScreen2.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

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
                Intent myIntent = new Intent(getApplicationContext(), TeamActivity.class);
                startActivity(myIntent);
            }
        });
    }
}
