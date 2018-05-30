package ltu.course.mobile.project.greenerfootballcup.utilities.Model;

import android.content.Intent;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
            DateFormat format = new SimpleDateFormat("yy");
            Date birthDate = null;
            try
            {
                birthDate = format.parse(player.getYear()+"");
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            String yearStr =  new SimpleDateFormat("yyyy").format(LoginDatas.getInstance().getYear());
            int currentYear = Integer.parseInt(yearStr);
            int birthYear =  Integer.parseInt(new SimpleDateFormat("yyyy").format(birthDate));
            int age = currentYear - birthYear;
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
