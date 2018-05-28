package ltu.course.mobile.project.greenerfootballcup.utilities.Model;

public class Match {
    private String number;
    private String group;
    private String time;
    private String firstTeam;
    private String secondTeam;
    private String firstTeamURL;
    private String secondTeamURL;



    public  Match (String number, String group, String time, String firstTeam, String secondTeam, String firstTeamURL, String secondTeamURL){
        this.number = number;
        this.group = group;
        this.time = time;
        this.firstTeam = firstTeam;
        this.firstTeamURL =firstTeamURL;
        this.secondTeam = secondTeam;
        this.secondTeamURL =secondTeamURL;
    }

    public String getNumber() {
        return number;
    }

    public String getGroup() {
        return group;
    }

    public String getTime() {
        return time;
    }

    public String getFirstTeam() {
        return firstTeam;
    }

    public String getSecondTeam() {
        return secondTeam;
    }

    public  String getFirstTeamURL(){return firstTeamURL;}

    public  String getSecondTeamURL(){return secondTeamURL;}
}
