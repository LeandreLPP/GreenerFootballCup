package ltu.course.mobile.project.greenerfootballcup.utilities.CustomView;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ltu.course.mobile.project.greenerfootballcup.R;

public class LoadingView extends RelativeLayout {

    private TextView loadingText;
    private ProgressBar progressBar;

    public LoadingView(Context context) {
        super(context);
        initialize();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void initialize(){
        inflate(getContext(), R.layout.loading_view, this);
        loadingText = findViewById(R.id.loadingText);
        progressBar = findViewById(R.id.progressBar);
        loadingText.setText(R.string.loading);
        loadingText.setTextColor(getResources().getColor(R.color.LoadingBlue));
        progressBar.setEnabled(true);
        progressBar.setProgress(0);
    }

    public void updateBar(int progress){
        progressBar.setProgress(progress);
    }

    public void setMaxProgress(int max){
        progressBar.setMax(max);
    }

    public void setLoadingText(int text){
        loadingText.setText(text);
        loadingText.setTextColor(getResources().getColor(R.color.LoadingBlue));
    }

    public void setLoadingText(String text){
        loadingText.setText(text);
    }

    public void displayError(String errorMessage) {
        loadingText.setText(errorMessage);
        loadingText.setTextColor(getResources().getColor(R.color.colorTextError));
        progressBar.setEnabled(false);
    }

}
