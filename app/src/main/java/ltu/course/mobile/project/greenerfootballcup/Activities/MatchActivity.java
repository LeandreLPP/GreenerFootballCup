package ltu.course.mobile.project.greenerfootballcup.Activities;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

import ltu.course.mobile.project.greenerfootballcup.R;
import ltu.course.mobile.project.greenerfootballcup.utilities.Adapter.MatchAdapter;
import ltu.course.mobile.project.greenerfootballcup.utilities.CustomView.LoadingView;
import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;
import ltu.course.mobile.project.greenerfootballcup.utilities.MatchData;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Field;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Match;
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
    private String fieldArg;
    private String fieldName;
    private Match selectedMatch;
    private LoadingView loadingView;
    private Handler handlerActivity;

    public static final String TEAM_URL = "FIELD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        title = findViewById(R.id.title);
        fieldArg = getIntent().getStringExtra(FieldActivity.FIELD_ARGUMENT_ID);

        matchList = new ArrayList<>();
        matchListView = findViewById(R.id.matches);
        View header = getLayoutInflater().inflate(R.layout.match_list_header, null);
        matchListView.addHeaderView(header);
        matchListView.setVisibility(View.INVISIBLE);
        handlerActivity = new Handler();
        selectedMatch = null;

        registerTeam1 = findViewById(R.id.team1);
        registerTeam2 = findViewById(R.id.team2);
        registerResult = findViewById(R.id.register);

        loadingView = findViewById(R.id.loadingMatchView);
        loadingView.setMaxProgress(3);

        final fillList fillMatchList = new fillList();
        fillMatchList.execute();

        registerTeam1.setOnClickListener(v -> {
            if (selectedMatch != null)
            {
                Intent intent = new Intent(getApplicationContext(), TeamActivity.class);
                intent.putExtra(TEAM_URL, selectedMatch.getFirstTeamURL());
                intent.putExtra(CODE_TEAM, CODE_TEAM_A);
                startActivityForResult(intent, CODE_TEAM_A);
            }
        });
        registerTeam1.setEnabled(false);

        registerTeam2.setOnClickListener(v -> {
            if (selectedMatch != null)
            {
                Intent intent = new Intent(getApplicationContext(), TeamActivity.class);
                //passing the whole URL of the selected team
                intent.putExtra(TEAM_URL, selectedMatch.getSecondTeamURL());
                intent.putExtra(CODE_TEAM, CODE_TEAM_B);
                startActivityForResult(intent, CODE_TEAM_B);
            }
        });
        registerTeam2.setEnabled(false);

        registerResult.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
            MatchData.getInstance().setField(new Field(fieldArg));
            MatchData.getInstance().setMatch(selectedMatch);

            startActivity(intent);
        });
        registerResult.setEnabled(false);
    }

    private class fillList extends AsyncTask<String, Integer, Utilities.Result> {

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
        protected Utilities.Result doInBackground(String... strings) {
            Utilities.Result result = new Utilities.Result();
            result.success = false;
            try
            {
                DateFormat format = new SimpleDateFormat("yy", Locale.getDefault());
                String yearStr = format.format(LoginDatas.getInstance().getYear());
                String url = ("http://www.teamplaycup.se/cup/?games&home=kurirenspelen/" + yearStr +
                              "&scope=all&arena=" + fieldArg + "&field=");
                publishProgress(0);
                handlerActivity.post(() -> Utilities.checkInternetConnection(MatchActivity.this));
                publishProgress(1);
                Document htmlDocument = ParserHTML.getHTMLDocument(url);
                publishProgress(2);
                Elements content = htmlDocument.select("h4");

                for (Element matchNode : content)
                {
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
                publishProgress(3);

                result.success = true;

                //get name of field
                Document html = ParserHTML.getHTMLDocument(url);
                Elements e = html.select("h2");
                fieldName = e.text();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Utilities.Result result) {
            title.setText(fieldName);


            if (result.success) {
                title.setText(fieldName);
                MatchAdapter adapter = new MatchAdapter(matchList, getApplicationContext());
                matchListView.setAdapter(adapter);
                //display to be configured teams on buttons
                matchListView.setOnItemClickListener((parent, view, position, id) -> {
                    if(position <= 0) return;
                    Match xMatch = matchList.get(position - 1);

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
                });

                matchListView.setVisibility(View.VISIBLE);
                loadingView.setVisibility(View.INVISIBLE);
            }
            else {
                loadingView.displayError(result.errorMessage);
                loadingView.setOnClickListener((c) -> (new fillList()).execute());
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            loadingView.displayError(getString(R.string.loading_failed));
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

        boolean teamAConfigured = ins.getTeamA() != null && ins.getSignatureTeamA() != null;
        boolean teamBConfigured = ins.getTeamB() != null && ins.getSignatureTeamB() != null;

        Drawable drawableOn;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP)
            drawableOn = getDrawable(R.drawable.success);
        else
            drawableOn = getResources().getDrawable(R.drawable.success);
        drawableOn.setBounds(new Rect(0,0,50,50));
        registerTeam1.setCompoundDrawables(null, null,
                                                              teamAConfigured ? drawableOn : null,
                                                              null);
        registerTeam2.setCompoundDrawables(null, null,
                                                              teamBConfigured ? drawableOn : null,
                                                              null);

        boolean ok = teamAConfigured && teamBConfigured;
        registerResult.setEnabled(ok);
    }
}
