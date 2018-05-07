package ltu.course.mobile.project.greenerfootballcup;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.HttpStatusException;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import ltu.course.mobile.project.greenerfootballcup.utilities.Field;
import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;
import ltu.course.mobile.project.greenerfootballcup.utilities.ParserHTML;

public class FieldActivity extends AppCompatActivity {

    public static final int GRID_LAYOUT_WIDTH = 4;
    public static final String FIELD_ARGUMENT_ID = "FIELD";
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1234;

    private ConstraintLayout loadingLayout;
    private TextView loadingText;
    private ProgressBar progressBar;
    private GridLayout fieldGridLayout;
    private Handler handlerActivity;
    private LoadViewAsyncTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);

        loadingLayout = findViewById(R.id.loadingLayout);
        loadingText = findViewById(R.id.loadingText);
        progressBar = findViewById(R.id.progressBar);
        fieldGridLayout = findViewById(R.id.fieldGridLayout);

        handlerActivity = new Handler();
        task = new LoadViewAsyncTask();
        task.execute();
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
        List<Field> firstSort = new ArrayList<>(Arrays.asList(fields));
        firstSort.sort(Comparator.comparing(Field::getFullName));

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

    private synchronized void checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean connected = cm != null && cm.getActiveNetworkInfo() != null;
        if (!connected)
        {
            RequirePermissionDialogFragment dialogFragment = new RequirePermissionDialogFragment();
            dialogFragment.show(getFragmentManager(), "need_internet");
        }
    }

    public static class RequirePermissionDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setMessage(R.string.need_internet_message)
                   .setPositiveButton(R.string.ok, (dialog, id) -> ActivityCompat.requestPermissions(
                           getActivity(),
                           new String[]{Manifest.permission.INTERNET},
                           MY_PERMISSIONS_REQUEST_READ_CONTACTS));
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

    private class LoadViewAsyncTask extends AsyncTask<Void, Integer, Result> {
        private List<List<Field>> fieldList;

        @Override
        protected void onPreExecute() {
            loadingLayout.setOnClickListener(null);
            loadingText.setText(R.string.loading);
            loadingText.setTextColor(getResources().getColor(R.color.colorPrimaryDark, null));
            progressBar.setEnabled(true);
            progressBar.setProgress(0);
            progressBar.setMax(5);
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            progressBar.setProgress(progress[0]);
        }

        @Override
        protected Result doInBackground(Void... voids) {
            Result result = new Result();
            result.success = false;
            try
            {
                publishProgress(0);
                handlerActivity.post(FieldActivity.this::checkInternetConnection);
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
        protected void onPostExecute(Result result) {
            if (result.success)
            {
                loadingLayout.setOnClickListener(null);
                loadingLayout.setVisibility(ConstraintLayout.INVISIBLE);
                fillGridLayout(fieldList);
            }
            else displayError(result.errorMessage);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            displayError(getString(R.string.loading_failed));
        }

        void displayError(String errorMessage) {
            loadingLayout.setOnClickListener((c) -> (FieldActivity.this.task = new LoadViewAsyncTask()).execute());
            loadingText.setText(errorMessage);
            loadingText.setTextColor(getResources().getColor(R.color.colorTextError, null));
            progressBar.setEnabled(false);
        }
    }

    private static class Result{
        public String errorMessage;
        public boolean success;
    }
}
