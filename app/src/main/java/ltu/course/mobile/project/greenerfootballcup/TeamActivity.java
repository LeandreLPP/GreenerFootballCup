package ltu.course.mobile.project.greenerfootballcup;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ltu.course.mobile.project.greenerfootballcup.utilities.ParserHTML;
import ltu.course.mobile.project.greenerfootballcup.utilities.Player;
import ltu.course.mobile.project.greenerfootballcup.utilities.PlayerAdapter;

public class TeamActivity extends AppCompatActivity {

    private String url = "http://teamplaycup.se/cup/?team&home=kurirenspelen/17&scope=A-2&name=Notvikens%20IK" ;
    private ListView playerList;
    private List<Player> players;
    private Document document;
    private PlayerAdapter playerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team);

        players = new ArrayList<>();
        playerList = (ListView)findViewById(R.id.players);

        GetPlayers();
    }

    public void GetPlayers(){
        final ParsePlayers parsePlayers = new ParsePlayers();
        parsePlayers.execute();
    }

    private class ParsePlayers extends AsyncTask<String, Long, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            try {
                document = ParserHTML.getHTMLDocument(url);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Get all hearders
            Elements allH3headers = document.select("h3");
            Element playerHeader = null;
            //Find the right header
            for (Element header : allH3headers) {
                if (header.text().equals("Spelare")) {
                    playerHeader = header;
                    break;
                }
            }

            //Get all the players
            Elements tablePlayers = playerHeader.nextElementSibling().select("table").select("tbody").select("tr");

            //Parse name and age
            for (Element player : tablePlayers) {
                Elements data = player.select("td");
                players.add(new Player(data.get(0).text(),data.get(1).text()));
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            playerAdapter = new PlayerAdapter(getApplicationContext(), players);
            playerList.setAdapter(playerAdapter);
        }
    }
}
