package ltu.course.mobile.project.greenerfootballcup.utilities;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ParserHTML {
    private static final String beginningURL = "http://www.teamplaycup.se/cup/?games&home=kurirenspelen/";
    private static final String endURL = "&scope=all&field=";
    private static final String arenaArg = "&arena=";

    public static String getURLofAllMatches(Date year)
    {
        DateFormat format = new SimpleDateFormat("yy");
        String yearStr = format.format(year);
        return beginningURL + yearStr + endURL;
    }

    public static String getURLwithField(Date year, Field field)
    {
        DateFormat format = new SimpleDateFormat("yy");
        String yearStr = format.format(year);
        return beginningURL + yearStr + arenaArg + field.getUrlArgument() + endURL;
    }

    public static Document getHTMLDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    public static Field[] extractFields(Document htmlDoc) {
    }

    public static Match[] extractMatches(Document htmlDoc) {
        return new Match[0];
    }

}
