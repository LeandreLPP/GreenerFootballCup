package ltu.course.mobile.project.greenerfootballcup.utilities.Model;

public class Match {
    private String number;
    private String group;
    private String time;
    private Team firstTeam;
    private Team secondTeam;
    private String groupURL;



    public  Match (String number, String group, String groupURL, String time, Team firstTeam, Team secondTeam){
        this.number = number;
        this.group = group;
        this.groupURL = groupURL;
        this.time = time;
        this.firstTeam = firstTeam;
        this.secondTeam = secondTeam;

    }

    public String getNumber() {
        return number;
    }

    public String getGroup() {
        return group;
    }

    public String getGroupURL(){return groupURL; }

    public String getTime() {
        return time;
    }

    public Team getFirstTeam() {
        return firstTeam;
    }

    public Team getSecondTeam() {
        return secondTeam;
    }
}
