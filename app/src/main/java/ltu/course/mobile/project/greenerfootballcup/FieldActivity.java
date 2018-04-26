package ltu.course.mobile.project.greenerfootballcup;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

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

    private ConstraintLayout loadingLayout;
    private TextView loadingText;
    private ProgressBar progressBar;
    private GridLayout fieldGridLayout;
    private ScrollView scrollView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);

        loadingLayout = findViewById(R.id.loadingLayout);
        loadingText = findViewById(R.id.loadingText);
        progressBar = findViewById(R.id.progressBar);
        scrollView = findViewById(R.id.scrollView);
        fieldGridLayout = findViewById(R.id.fieldGridLayout);

        (new LoadViewAsyncTask()).execute();
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
     * @param field
     */
    private void selectField(Field field) {
        Intent intent = new Intent(this, MatchActivity.class);
        intent.putExtra(FIELD_ARGUMENT_ID, field.getUrlArgument());
        startActivity(intent);
    }

    /**
     * Sort alphabetically the Fields and split them into sublist for each letter.
     *
     * @see Field
     *
     * @param fields The array of fields to sort and split.
     * @return A list of sublist containing the Fields sorted and split up.
     */
    public static List<List<Field>> sortAndSplitFieldList(Field[] fields) {
        List<Field> firstSort = new ArrayList<Field>(Arrays.asList(fields));
        firstSort.sort(Comparator.comparing(Field::getFullName));

        List<List<Field>> ret = new ArrayList<>();
        ret.add(new ArrayList<>());
        int i = 0;
        char c = firstSort.get(0).getDisplayName().charAt(0);
        for (Field f : firstSort)
        {
            char c1 = f.getDisplayName().charAt(0);
            if (c1 != c)
            {
                i++;
                ret.add(new ArrayList<>());
                c = c1;
            }
            ret.get(i).add(f);
        }
        return ret;
    }


    private boolean isInternetEnabled() {
        return false;
    }

    private class LoadViewAsyncTask extends AsyncTask<Void, Progress, Boolean>
    {
        private List<List<Field>> fieldList;

        @Override
        protected void onPreExecute() {
            if(!isInternetEnabled())
                this.cancel(true);
            loadingLayout.setOnClickListener(null);
            loadingText.setText("Loading, please wait...");
            loadingText.setTextColor(getResources().getColor(R.color.colorPrimaryDark,null));
            progressBar.setEnabled(true);
            progressBar.setProgress(0);
            progressBar.setMax(4);
        }

        @Override
        protected void onProgressUpdate(Progress... progress) {
            loadingText.setText(progress[0].getText());
            progressBar.setProgress(progress[0].getProgress());
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean success = false;
            try
            {
                publishProgress(new Progress("Compiling URL...", 0));
                String docUrl = ParserHTML.getURLofAllMatches(LoginDatas.getInstance().getYear());
                publishProgress(new Progress("Retrieving HTML page...", 1));
                Document allMatchesPage = ParserHTML.getHTMLDocument(docUrl);
                publishProgress(new Progress("Extracting information...", 2));
                Field[] fields = ParserHTML.extractFields(allMatchesPage);
                publishProgress(new Progress("Sorting fields...", 3));
                fieldList = sortAndSplitFieldList(fields);
                publishProgress(new Progress("Building UI...", 4));
                success = true;
            }
            catch (Exception e)
            { }
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(success)
            {
                loadingLayout.setOnClickListener(null);
                loadingLayout.setVisibility(ConstraintLayout.INVISIBLE);
                fillGridLayout(fieldList);
            }
            else displayError();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            displayError();
        }

        protected void displayError(){
            loadingLayout.setOnClickListener((c)->(new LoadViewAsyncTask()).execute());
            loadingText.setText("Could not load the list of play fields.\nTouch to retry.");
            loadingText.setTextColor(getResources().getColor(R.color.colorTextError,null));
            progressBar.setEnabled(false);
        }
    }
    private class Progress {
        private String text;
        private int progress;

        private Progress(String text, int progress) {
            this.text = text;
            this.progress = progress;
        }

        public int getProgress() {
            return progress;
        }

        public String getText() {
            return text;
        }
    }
}
