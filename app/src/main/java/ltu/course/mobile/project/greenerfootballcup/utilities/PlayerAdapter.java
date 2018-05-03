package ltu.course.mobile.project.greenerfootballcup.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ltu.course.mobile.project.greenerfootballcup.R;

public class PlayerAdapter extends BaseAdapter {
    Context context;
    List<Player> players;
    LayoutInflater inflter;

    public PlayerAdapter(Context applicationContext, List<Player> players) {
        this.context = context;
        this.players = players;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    static class ViewHolder {
        protected CheckBox checkbox;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = inflter.inflate(R.layout.player_list_view, null);

            final ViewHolder viewHolder = new ViewHolder();

            TextView name = (TextView) convertView.findViewById(R.id.player_name);
            TextView age = (TextView) convertView.findViewById(R.id.player_age);
            viewHolder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);

            name.setText(players.get(position).getName());
            age.setText(players.get(position).getAge());

            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Player player = (Player) viewHolder.checkbox.getTag();
                    player.setSelected(buttonView.isChecked());
                }
            });
            convertView.setTag(viewHolder);
            viewHolder.checkbox.setTag(players.get(position));
        }else{
            ((ViewHolder)convertView.getTag()).checkbox.setTag(players.get(position));
        }

        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.checkbox.setChecked(players.get(position).isSelected());
        return convertView;
    }
}
