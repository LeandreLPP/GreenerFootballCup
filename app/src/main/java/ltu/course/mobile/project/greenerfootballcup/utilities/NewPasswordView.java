package ltu.course.mobile.project.greenerfootballcup.utilities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import ltu.course.mobile.project.greenerfootballcup.R;

public class NewPasswordView extends RelativeLayout {

    private EditText et_new_password;
    private EditText et_confirm_new_password;
    private Button btn_validate_new_password;
    private Dialog dialog;

    public NewPasswordView(Context context) {
        super(context);
        initialize();
    }

    public NewPasswordView(Context context, PopupWindow popupWindow) {
        super(context);
        initialize();
    }

    public NewPasswordView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public NewPasswordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void setDialog(Dialog d){
        dialog = d;
    }

    public void initialize(){
        inflate(getContext(), R.layout.new_password_view, this);
        et_new_password = findViewById(R.id.et_new_password);
        et_confirm_new_password = findViewById(R.id.et_confirm_new_password);
        btn_validate_new_password = findViewById(R.id.btn_validate_new_password);




        et_new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!et_confirm_new_password.getText().toString().equals(et_new_password.getText().toString())){
                    et_confirm_new_password.setError(getResources().getString(R.string.passwords_do_not_match));
                    if(btn_validate_new_password.isEnabled())
                        btn_validate_new_password.setEnabled(false);
                }else{
                    et_confirm_new_password.setError(null);
                    btn_validate_new_password.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        et_confirm_new_password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!et_confirm_new_password.getText().toString().equals(et_new_password.getText().toString())){
                    et_confirm_new_password.setError(getResources().getString(R.string.passwords_do_not_match));
                    if(btn_validate_new_password.isEnabled())
                        btn_validate_new_password.setEnabled(false);
                }else{
                    et_confirm_new_password.setError(null);
                    btn_validate_new_password.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btn_validate_new_password.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                LoginDatas.getInstance().setAdminCode(et_new_password.getText().toString());
                if(dialog != null)
                    dialog.dismiss();
            }});
    }
}
