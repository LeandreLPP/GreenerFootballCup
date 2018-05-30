package ltu.course.mobile.project.greenerfootballcup.utilities.Model;

import android.os.Build;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.TemporalUnit;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ltu.course.mobile.project.greenerfootballcup.utilities.LoginDatas;

public class Team {

    private List<Player> players;

    public Team(){
        players = new ArrayList<>();
    }

    public List<Player> getPlayers(){return players;}

    public void addPlayer(Player player){
        players.add(player);
    }

    public void removePlayer(Player player){
        players.remove(player);
    }

    public int getNumberOlderPlayers(){
        int i = 0;
        for (Player player : players) {
            Date birth = player.getDateOfBirth();
            Date currentYear = LoginDatas.getInstance().getYear();

            Calendar birthCal = Calendar.getInstance();
            birthCal.setTime(birth);
            Calendar currentCal = Calendar.getInstance();
            currentCal.setTime(currentYear);

            int age = currentCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
            if(age > LoginDatas.getInstance().getAgeThreshold())
                i++;
        }
        return i;
    }

    public boolean maxOlderPlayerOvershoot(){
        if(getNumberOlderPlayers() > LoginDatas.getInstance().getMaxOveragedPlayer())
            return true;
        return false;
    }

    public boolean maxPlayerOvershoot(){
        if(players.size() > LoginDatas.getInstance().getMaxPlayer())
            return true;
        return false;
    }

    public int getNumberPlayer(){
        return players.size();
    }

}
