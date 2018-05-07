package ltu.course.mobile.project.greenerfootballcup.utilities;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;


public class ParserHTML {
    private static final String beginningURL = "http://www.teamplaycup.se/cup/?games&home=kurirenspelen/";
    private static final String endURL = "&scope=all&field=";
    private static final String arenaArg = "&arena=";

    public static String getURLofAllMatches(Date year)
    {
        DateFormat format = new SimpleDateFormat("yy", Locale.getDefault());
        String yearStr = format.format(year);
        return beginningURL + yearStr + endURL;
    }

    public static String getURLwithField(Date year, Field field)
    {
        DateFormat format = new SimpleDateFormat("yy", Locale.getDefault());
        String yearStr = format.format(year);
        return beginningURL + yearStr + arenaArg + field.getUrlArgument() + endURL;
    }

    public static Document getHTMLDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    public static Field[] extractFields(Document htmlDoc) {
        Elements allAheaders = htmlDoc.select("a");
        Set<String> addresses = new HashSet<>();
        for (Element aHeader : allAheaders)
        {
            String address = aHeader.attr("href");
            if(address != null && address.contains("&arena="))
            {
                int beginning = address.indexOf("&arena=")+7;
                address = address.substring(beginning, address.indexOf("&", beginning));
                addresses.add(address);
            }
        }
        List<Field> list = new ArrayList<>();
        for(String s : addresses)
            list.add(new Field(s));
        return list.toArray(new Field[list.size()]);
    }

    public static Match[] extractMatches(Document htmlDoc) {
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
