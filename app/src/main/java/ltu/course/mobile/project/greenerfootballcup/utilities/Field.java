package ltu.course.mobile.project.greenerfootballcup.utilities;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Field {

    private String displayName;
    private String fullName;
    private String urlArgument;

    public Field(String urlArgument)
    {
        this.urlArgument = urlArgument;
        fullName = urlArgument.replaceAll("%20", " ").trim();

        if(fullName.contains("-manna"))
            displayName = fullName.substring(0,fullName.indexOf("-manna")).trim();
        else
            displayName = fullName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUrlArgument() {
        return urlArgument;
    }
}
