package ltu.course.mobile.project.greenerfootballcup.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

import java.util.ArrayList;

import java.util.List;

import ltu.course.mobile.project.greenerfootballcup.R;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Field;
import ltu.course.mobile.project.greenerfootballcup.utilities.CustomView.LoadingView;
import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;
import ltu.course.mobile.project.greenerfootballcup.utilities.ParserHTML;
import ltu.course.mobile.project.greenerfootballcup.utilities.Utilities;

public class FieldActivity extends AppCompatActivity {

    public static final int GRID_LAYOUT_WIDTH = 4;
    public static final String FIELD_ARGUMENT_ID = "FIELD";

    private LoadingView loadingView;

    private GridLayout fieldGridLayout;
    private Handler handlerActivity;
    private LoadViewAsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);

        fieldGridLayout = findViewById(R.id.fieldGridLayout);
        fieldGridLayout.setVisibility(View.INVISIBLE);
        loadingView = findViewById(R.id.loadingView);
        loadingView.setMaxProgress(5);

        handlerActivity = new Handler();

        task = new LoadViewAsyncTask();
        task.execute();
    }

    /**
     * Avoid to come back to the LoginActivity using the back button
     */
    @Override
    public void onBackPressed() {
        openVerifyPassword();
        return;
    }

    /**
     * Use to back to the previous activity
     */
    public void backToPreviousActivity(){
        finish();
    }

    /**
     * Fill the central GridLayout with buttons representing the fields.
     *
     * @param fieldList The sorted and split list of the fields to connect to each button.
     */
    private void fillGridLayout(List<List<Field>> fieldList) {
        int x, y;
        x = 0;
        for (List<Field> row : fieldList)
        {
            y = 0;
            for (final Field field : row)
            {
                Button button = new Button(this);
                button.setText(field.getDisplayName());
                button.setOnClickListener(v -> selectField(field));
                GridLayout.LayoutParams params = new GridLayout.LayoutParams(GridLayout.spec(x),
                                                                             GridLayout.spec(y));
                fieldGridLayout.addView(button, params);
                fieldGridLayout.setColumnCount(GRID_LAYOUT_WIDTH);
                y++;
                if (y >= GRID_LAYOUT_WIDTH)
                {
                    y = 0;
                    x++;
                }
            }
            x++;
        }
    }

    /**
     * Select a field and pass its URL argument to the Match activity with
     *
     * @param field The field selected
     */
    private void selectField(Field field) {
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra(FIELD_ARGUMENT_ID, field.getUrlArgument());
        startActivity(intent);
    }

    /**
     * Sort alphabetically the Fields and split them into sublist for each letter.
     *
     * @param fields The array of fields to sort and split.
     * @return A list of sublist containing the Fields sorted and split up.
     * @see Field
     */
    public static List<List<Field>> sortAndSplitFieldList(Field[] fields) {
        List<Field> firstSort = new ArrayList<>();
        for(Field f : fields)
        {
            int i = 0;
            while(i<firstSort.size() && f.getFullName().compareTo(firstSort.get(i).getFullName()) > 0)
                i++;
            firstSort.add(i,f);
        }

        List<List<Field>> ret = new ArrayList<>();
        ret.add(new ArrayList<>());
        int i = -1;
        char c = ' ';
        for (Field f : firstSort)
        {
            char c1 = f.getDisplayName().charAt(0);
            if (i == -1 || c1 != c)
            {
                i++;
                ret.add(new ArrayList<>());
                c = c1;
            }
            ret.get(i).add(f);
        }
        return ret;
    }


    private class LoadViewAsyncTask extends AsyncTask<Void, Integer, Utilities.Result> {
        private List<List<Field>> fieldList;

        @Override
        protected void onPreExecute() {
            loadingView.setOnClickListener(null);
            loadingView.setLoadingText(R.string.loading);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            loadingView.updateBar(progress[0]);
        }

        @Override
        protected Utilities.Result doInBackground(Void... voids) {
            Utilities.Result result = new Utilities.Result();
            result.success = false;
            try
            {
                publishProgress(0);
                handlerActivity.post(() -> { Utilities.checkInternetConnection(FieldActivity.this); });
                publishProgress(1);
                String docUrl = ParserHTML.getURLofAllMatches(LoginDatas.getInstance().getYear());
                publishProgress(2);
                Document allMatchesPage = ParserHTML.getHTMLDocument(docUrl);
                publishProgress(3);
                Field[] fields = ParserHTML.extractFields(allMatchesPage);
                if(fields.length <= 0)
                {
                    result.errorMessage = getString(R.string.no_field_error);
                    return result;
                }
                publishProgress(4);
                fieldList = sortAndSplitFieldList(fields);
                publishProgress(5);
                result.success = true;
            }
            catch (HttpStatusException httpException)
            {
                result.errorMessage = getString(R.string.website_down_error);
            }
            catch (Exception e)
            {
                result.errorMessage = getString(R.string.loading_failed);
            }
            return result;
        }

        @Override
        protected void onPostExecute(Utilities.Result result) {
            if (result.success)
            {
                fillGridLayout(fieldList);
                loadingView.setVisibility(View.INVISIBLE);
                fieldGridLayout.setVisibility(View.VISIBLE);
            }
            else{
                loadingView.displayError(result.errorMessage);
                loadingView.setOnClickListener((c) -> (FieldActivity.this.task = new LoadViewAsyncTask()).execute());
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            loadingView.displayError(getString(R.string.loading_failed));
        }

    }


    /**
     * Open a AlertDialog asking the user the admin code
     * If it's the right admin code, then the application go back to the Login Screen
     *
     */
    public void openVerifyPassword(){
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.verify_password_view, null);

        EditText password = (EditText)view.findViewById(R.id.et_verify_password);

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(password.getError() != null)
                    password.setError(null);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        final android.app.AlertDialog dialog = new android.app.AlertDialog.Builder(this).setView(view)
                .setPositiveButton(android.R.string.ok, null) //Set to null. We override the onclick
                .setNegativeButton(android.R.string.cancel, null)
                .create();

        dialog.setOnShowListener(dialogInterface -> {

            Button button = ((android.app.AlertDialog) dialog).getButton(android.app.AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view1 -> {
                if(password.getText().toString().equals(LoginDatas.getInstance().getAdminCode())){
                    dialog.dismiss();
                    backToPreviousActivity();
                }
                else{
                    password.setError(getResources().getString(R.string.wrongAdminCode));
                }
            });
        });
        dialog.show();
    }

}
