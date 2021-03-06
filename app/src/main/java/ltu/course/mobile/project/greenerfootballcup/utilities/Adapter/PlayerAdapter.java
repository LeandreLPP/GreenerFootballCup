package ltu.course.mobile.project.greenerfootballcup.utilities.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import ltu.course.mobile.project.greenerfootballcup.R;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Player;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Team;

public class PlayerAdapter extends BaseAdapter {
    Context context;
    Player[] players;
    LayoutInflater inflter;
    Team team;

    public PlayerAdapter(Context applicationContext, Player[] players, Team team) {
        this.context = applicationContext;
        this.players = players;
        this.team = team;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return players.length;
    }

    @Override
    public Object getItem(int i) {
        return players[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder {
        protected CheckBox checkbox;
        protected TextView name;
        protected TextView age;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;

        if(convertView == null) {
            convertView = inflter.inflate(R.layout.player_list_view, null);
            viewHolder = new ViewHolder();
            viewHolder.name = (TextView) convertView.findViewById(R.id.player_name);
            viewHolder.age = (TextView) convertView.findViewById(R.id.player_age);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);

            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Player player = (Player) viewHolder.checkbox.getTag();
                    if(player.isSelected() != isChecked){
                        player.setSelected(buttonView.isChecked());
                        if(isChecked)
                            team.addPlayer(player);
                        else
                            team.removePlayer(player);
                    }

                }
            });

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Player currentPlayer = (Player)getItem(position);

        viewHolder.name.setText(currentPlayer.getName());
        viewHolder.age.setText(currentPlayer.getBirthDateString());
        viewHolder.checkbox.setTag(currentPlayer);

        viewHolder.checkbox.setChecked(currentPlayer.isSelected());
        return convertView;

    }
}
