package ltu.course.mobile.project.greenerfootballcup.Activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ltu.course.mobile.project.greenerfootballcup.R;
import ltu.course.mobile.project.greenerfootballcup.utilities.Adapter.MatchAdapter;
import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;
import ltu.course.mobile.project.greenerfootballcup.utilities.MatchData;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Field;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Match;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Team;
import ltu.course.mobile.project.greenerfootballcup.utilities.ParserHTML;
import ltu.course.mobile.project.greenerfootballcup.utilities.Utilities;

public class MatchActivity extends AppCompatActivity {

    public static final String CODE_TEAM = "team_code";
    public static final int CODE_TEAM_A = 1234;
    public static final int CODE_TEAM_B = 5678;
    private ListView matchListView;
    private ArrayList<Match> matchList;
    private Button registerTeam1;
    private Button registerTeam2;
    private Button registerResult;
    private TextView title;
    private Document HTMLdocument;
    private String url = "";
    private String fieldArg;
    private String Fieldname;
    private static MatchAdapter adapter;
    private Match selectedMatch;

    public static final String TEAM_URL = "FIELD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        title = (TextView) findViewById(R.id.title);
        fieldArg = getIntent().getStringExtra(FieldActivity.FIELD_ARGUMENT_ID);

        matchList = new ArrayList<>();
        matchListView = (ListView) findViewById(R.id.matches);

        selectedMatch = null;

        registerTeam1 = (Button) findViewById(R.id.team1);
        registerTeam2 = (Button) findViewById(R.id.team2);
        registerResult = (Button) findViewById(R.id.register);

        final fillList fillMatchList = new fillList();
        fillMatchList.execute();

        //TODO: if team configured, disable button
        registerTeam1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMatch != null)
                {
                    Intent intent = new Intent(getApplicationContext(), TeamActivity.class);
                    intent.putExtra(TEAM_URL, selectedMatch.getFirstTeamURL());
                    intent.putExtra(CODE_TEAM, CODE_TEAM_A);
                    startActivityForResult(intent, CODE_TEAM_A);
                }
            }
        });
        registerTeam1.setEnabled(false);

        //TODO: if team configured, disable button
        registerTeam2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedMatch != null)
                {
                    Intent intent = new Intent(getApplicationContext(), TeamActivity.class);
                    //passing the whole URL of the selected team
                    intent.putExtra(TEAM_URL, selectedMatch.getSecondTeamURL());
                    intent.putExtra(CODE_TEAM, CODE_TEAM_B);
                    startActivityForResult(intent, CODE_TEAM_B);
                }
            }
        });
        registerTeam2.setEnabled(false);

        //TODO: what information to pass to the next activity ?
        registerResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
                MatchData.getInstance().setField(new Field(fieldArg));
                MatchData.getInstance().setMatch(selectedMatch);

                startActivity(intent);
            }
        });
        registerResult.setEnabled(false);
    }

    private class fillList extends AsyncTask<String, Integer, Utilities.Result> {

        @Override
        protected Utilities.Result doInBackground(String... strings) {
            Utilities.Result result = new Utilities.Result();
            result.success = false;
            try
            {
                DateFormat format = new SimpleDateFormat("yy", Locale.getDefault());
                String yearStr = format.format(LoginDatas.getInstance().getYear());
                url = ("http://www.teamplaycup.se/cup/?games&home=kurirenspelen/" + yearStr + "&scope=all&arena=" + fieldArg + "&field=");
                //the bottom url is a test URL - upper URL is for the finished App
                //url = "http://teamplaycup.se/cup/?games&home=kurirenspelen/17&scope=all&arena=A%2011-manna%20(Gstad)&field=";
                HTMLdocument = ParserHTML.getHTMLDocument(url);
                Elements content = HTMLdocument.select("h4");
                Element matchNode = null;

                for (Element node : content)
                {
                    matchNode = node;
                    Elements game = matchNode.nextElementSibling().select("table").select("tbody")
                                             .select("tr");
                    for (Element gameNode : game)
                    {
                        Elements data = gameNode.select("td");
                        Elements sTeams = data.get(3).select("a");
                        matchList.add(new Match(data.get(0).text(), data.get(1).text(),
                                                data.get(2).text(), sTeams.get(0).text(),
                                                sTeams.get(1).text(),
                                                "http://teamplaycup.se/cup/" + sTeams.get(0).attr("href"),
                                                "http://teamplaycup.se/cup/" + sTeams.get(1).attr("href")));
                    }
                }

                result.success = true;

                //get name of field
                Document html = ParserHTML.getHTMLDocument(url);
                Elements e = html.select("h2");
                Fieldname = e.text();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Utilities.Result result) {
            title.setText(Fieldname);

            adapter = new MatchAdapter(matchList, getApplicationContext());
            matchListView.setAdapter(adapter);

            //display to be configured teams on buttons
            matchListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Match xMatch = matchList.get(position);

                    registerTeam1.setText("Register players of team: \n" + xMatch.getFirstTeam());
                    registerTeam1.setEnabled(true);
                    registerTeam2.setText("Register players of team: \n" + xMatch.getSecondTeam());
                    registerTeam2.setEnabled(true);
                    selectedMatch = xMatch;
                    MatchData.getInstance().setTeamA(null);
                    MatchData.getInstance().setTeamB(null);
                    MatchData.getInstance().setSignatureTeamA(null);
                    MatchData.getInstance().setSignatureTeamB(null);
                    checkTeamConfigured();
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
            checkTeamConfigured();
    }

    private void checkTeamConfigured() {
        MatchData ins = MatchData.getInstance();
        boolean ok = ins.getTeamA() != null
                     && ins.getTeamB() != null
                     && ins.getSignatureTeamA() != null
                     && ins.getSignatureTeamB() != null;
        registerResult.setEnabled(ok);
    }
}
