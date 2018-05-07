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

}
