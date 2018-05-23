package ltu.course.mobile.project.greenerfootballcup;


import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TableRow;
import android.widget.TextView;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ltu.course.mobile.project.greenerfootballcup.utilities.Field;
import ltu.course.mobile.project.greenerfootballcup.utilities.Match;
import ltu.course.mobile.project.greenerfootballcup.utilities.ParserHTML;
import ltu.course.mobile.project.greenerfootballcup.utilities.Team;

public class MatchActivity extends AppCompatActivity {
    private Field field;
    private Match match;
    private ListView matcheList;
    private List<Match> allMatch;
    private Button registerTeam1;
    private Button registerTeam2;
    private Button registerResult;
    private Document HTMLdocument;
    private String url = "http://teamplaycup.se/cup/?games&home=kurirenspelen/17&scope=all&arena=A%2011-manna%20(Gstad)&field=";




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);


        TextView title = (TextView)findViewById(R.id.title);
        String FieldName = field.getDisplayName();
        title.setText("All matches for field" + FieldName);

        allMatch = new ArrayList<>();
       // match = new Match();
        matcheList = (ListView) findViewById(R.id.matches);

        registerTeam1 = (Button) findViewById(R.id.team1);
        registerTeam2 = (Button) findViewById(R.id.team2);
        registerResult = (Button) findViewById(R.id.register);


        registerTeam1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                //TODO: opens Screen: Teamconfiguration Team1
            }
        });

        registerTeam2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                //TODO: opens Screen: Teamconfiguration Team2
            }
        });

        registerResult.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick (View v){
                //TODO: opens Screen: Register Results
            }
        });




    }

    public void setField (Field newfield){
        this.field = field;
    }

    private class extractInformation extends AsyncTask<String, Long, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... strings) {
            try {
                HTMLdocument = ParserHTML.getHTMLDocument(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //find correct Elements, to extract Data from
            Elements allH4elements = HTMLdocument.select("h4");
            Element matchBucket = null;
            for (Element header : allH4elements){
                    if (header.text().equals("Fr 2017-08-04")){
                        matchBucket = header;
                        break;
                    }
            }

            //select all matches/games
            Elements allGames = matchBucket.nextElementSibling().select("table").select("tbody").select("tr");

            //extract information of each game
            for (Element game : allGames){
                Elements data = game.select("td");
                //allMatch.add(new Match(data.get(0).text(), data.get(1).text(), data.get(1).attr("href"),data.get(2).text(), data.get(3).text()));
            }



            return null;
        }

        @Override
        protected void onProgressUpdate(Long... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }



}
