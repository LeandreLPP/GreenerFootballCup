package ltu.course.mobile.project.greenerfootballcup.utilities.Model;

import java.util.ArrayList;
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
            if(player.getYear() < LoginDatas.getInstance().getAgeThreshold())
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
