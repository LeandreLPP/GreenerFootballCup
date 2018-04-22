package ltu.course.mobile.project.greenerfootballcup;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.Toast;

import org.jsoup.nodes.Document;

import java.io.IOException;

import ltu.course.mobile.project.greenerfootballcup.utilities.Field;
import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;
import ltu.course.mobile.project.greenerfootballcup.utilities.ParserHTML;

public class FieldActivity extends AppCompatActivity {

    private GridLayout fieldGridLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field);

        fieldGridLayout = (GridLayout) findViewById(R.id.fieldGridLayout);
    }

    private void loadView(){
        String docUrl = ParserHTML.getURLofAllMatches(LoginDatas.getInstance().getYear());
        try
        {
            Document allMatchesPage = ParserHTML.getHTMLDocument(docUrl);
            Field[] fields = ParserHTML.extractFields(allMatchesPage);
            setupGrid(fields);
            fieldGridLayout.setOnClickListener(null);
        }
        catch (IOException | ParserHTML.WrongDocumentException e)
        {
            Toast.makeText(this, R.string.page_load_error, Toast.LENGTH_LONG).show();
            fieldGridLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loadView();
                }
            });
            e.printStackTrace();
        }
    }

    private void setupGrid(Field[] fields) {
        // TODO
    }
}
