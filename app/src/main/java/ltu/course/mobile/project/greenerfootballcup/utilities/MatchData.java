package ltu.course.mobile.project.greenerfootballcup.utilities;

import java.io.File;

import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Field;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Match;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Team;

public class MatchData {
    private static MatchData instance = new MatchData();
    private MatchData() {}

    private Match match;
    private Team teamA, teamB;
    private Field field;
    private File signatureTeamA, signatureTeamB;

    public static MatchData getInstance() {
        return instance;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Team getTeamA() {
        return teamA;
    }

    public void setTeamA(Team teamA) {
        this.teamA = teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public void setTeamB(Team teamB) {
        this.teamB = teamB;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public File getSignatureTeamB() {
        return signatureTeamB;
    }

    public void setSignatureTeamB(File signatureTeamB) {
        this.signatureTeamB = signatureTeamB;
    }

    public File getSignatureTeamA() {
        return signatureTeamA;
    }

    public void setSignatureTeamA(File signatureTeamA) {
        this.signatureTeamA = signatureTeamA;
    }
}
