package ltu.course.mobile.project.greenerfootballcup.utilities.Model;

import java.util.ArrayList;
import java.util.List;

public class Team {



    public static final int maxPlayer = 11;
    public static final int maxOveragedPlayer = 2;
    public static final int ageThreshold = 05;

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
            if(player.getYear() < ageThreshold)
                i++;
        }
        return i;
    }

    public boolean maxOlderPlayerOvershoot(){
        if(getNumberOlderPlayers() > maxOveragedPlayer)
            return true;
        return false;
    }

    public boolean maxPlayerOvershoot(){
        if(players.size() > maxPlayer)
            return true;
        return false;
    }

    public int getNumberPlayer(){
        return players.size();
    }

}
