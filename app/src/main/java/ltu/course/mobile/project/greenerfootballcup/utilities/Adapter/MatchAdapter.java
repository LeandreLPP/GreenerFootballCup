package ltu.course.mobile.project.greenerfootballcup.utilities.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.View;



import java.util.ArrayList;

import ltu.course.mobile.project.greenerfootballcup.R;
import ltu.course.mobile.project.greenerfootballcup.utilities.Model.Match;


public class MatchAdapter extends ArrayAdapter<Match> implements View.OnClickListener{
    Context mContext;
    private ArrayList<Match> dataSet;


    public  MatchAdapter (ArrayList<Match> dataSet, Context mContext){
        super(mContext, R.layout.match_list, dataSet);
        this.mContext = mContext;
        this.dataSet = dataSet;
    }

    private static class ViewHolder{
        protected TextView mNr;
        protected TextView mGroup;
        protected TextView mTime;
        protected TextView mTeam1;
        protected TextView mTeam2;
    }

    @Override
    public void onClick (View v){
        int position = (Integer) v.getTag();
        Object object = getItem(position);
        Match mMatch = (Match) object;

    }



    @Override
    public View getView (int position, View convertView, ViewGroup parent){
        Match mMatch = getItem(position);
        ViewHolder viewHolder;
        final View result;


        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.match_list, parent, false);
            viewHolder = new ViewHolder();


           viewHolder.mNr = (TextView) convertView.findViewById(R.id.match_nr);
           viewHolder.mGroup = (TextView) convertView.findViewById(R.id.match_group);
           viewHolder.mTime = (TextView) convertView.findViewById(R.id.match_time);
           viewHolder.mTeam1 = (TextView) convertView.findViewById(R.id.match_team1);
           viewHolder.mTeam2 = (TextView) convertView.findViewById(R.id.match_team2);

           result = convertView;
           convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }


        viewHolder.mNr.setText(mMatch.getNumber());
        viewHolder.mGroup.setText(mMatch.getGroup());
        viewHolder.mTime.setText(mMatch.getTime());
        viewHolder.mTeam1.setText(mMatch.getFirstTeam());
        viewHolder.mTeam2.setText(mMatch.getSecondTeam());

        return convertView;
    }
}
