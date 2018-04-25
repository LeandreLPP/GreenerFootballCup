package ltu.course.mobile.project.greenerfootballcup.utilities;

import java.util.Date;

public class Match {
    private short number;
    private String group;
    private Date time;
    private Team firstTeam;
    private Team secondTeam;

    public short getNumber() {
        return number;
    }

    public String getGroup() {
        return group;
    }

    public Date getTime() {
        return time;
    }

    public Team getFirstTeam() {
        return firstTeam;
    }

    public Team getSecondTeam() {
        return secondTeam;
    }
}
