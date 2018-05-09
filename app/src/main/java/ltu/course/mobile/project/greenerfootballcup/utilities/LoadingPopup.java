package ltu.course.mobile.project.greenerfootballcup.utilities;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import ltu.course.mobile.project.greenerfootballcup.R;

public class LoadingPopup extends PopupWindow {

    private TextView loadingText;
    private ProgressBar progressBar;
    private Activity activity;
    private LayoutInflater layoutInflater;
    private View popupView;

    public LoadingPopup(Activity activity){
        this.activity = activity;

        layoutInflater = activity.getLayoutInflater();
        popupView = layoutInflater.inflate(R.layout.loading_view, null);
        loadingText = popupView.findViewById(R.id.loadingText);
        progressBar = popupView.findViewById(R.id.progressBar);

        loadingText.setText(R.string.loading);
        loadingText.setTextColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        progressBar.setEnabled(true);
        progressBar.setProgress(0);
        progressBar.setMax(5);
    }

    public void updateBar(int progress){
        progressBar.setProgress(progress);
    }

    public void displayError(String errorMessage) {
        loadingText.setText(errorMessage);
        loadingText.setTextColor(activity.getResources().getColor(R.color.colorTextError));
        progressBar.setEnabled(false);
    }
}
