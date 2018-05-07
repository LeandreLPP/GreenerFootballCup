package ltu.course.mobile.project.greenerfootballcup.utilities;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ParserHTML {

    public static Document getHTMLDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    public static Field[] extractFields(Document htmlDoc) throws WrongDocumentException {
        return new Field[0];
    }

    public static Match[] extractMatches(Document htmlDoc) throws WrongDocumentException {
        return new Match[0];
    }

    public class WrongDocumentException extends Exception {
    }

    public static Player[] extractPlayers(Document html) throws WrongDocumentException{
        Player[] players;

        //Get all hearders
        Elements allH3headers = html.select("h3");
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

        players = new Player[tablePlayers.size()];
        //Parse name and age
        for (int i = 0 ; i < tablePlayers.size();i++) {
            Elements data = tablePlayers.get(i).select("td");
            players[i] = new Player(data.get(0).text(), data.get(1).text());
        }

        return players;

    }
}
