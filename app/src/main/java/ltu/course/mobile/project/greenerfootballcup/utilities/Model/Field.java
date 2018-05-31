package ltu.course.mobile.project.greenerfootballcup.utilities.Model;

public class Field {

    private String displayName;
    private String fullName;
    private String urlArgument;

    public Field(String argument)
    {
        if(argument.contains("%20") && !argument.contains(" "))
        {
            urlArgument = argument;
            fullName = argument.replaceAll("%20", " ").trim();
        }
        else if (!argument.contains("%20") && argument.contains(" "))
        {
            fullName = argument;
            urlArgument = argument.replaceAll(" ", "%20").trim();
        }

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
