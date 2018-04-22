package ltu.course.mobile.project.greenerfootballcup.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Field {

    private String displayName;
    private String fullName;
    private String urlArgument;

    public Field(String fullName, String urlArgument)
    {
        this.fullName = fullName;
        this.urlArgument = urlArgument;

        Pattern nameFind = Pattern.compile("(\\w+)-manna");
        Matcher m = nameFind.matcher(fullName);
        this.displayName = m.group(0).trim();
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getUrlArgument() {
        return urlArgument;
    }
}
