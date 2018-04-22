package ltu.course.mobile.project.greenerfootballcup.utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

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
}
