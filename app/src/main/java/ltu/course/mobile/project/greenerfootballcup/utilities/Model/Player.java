package ltu.course.mobile.project.greenerfootballcup.utilities.Model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Player {

    private String name;
    private String age;
    private boolean selected;

    public Player(String name, String age) {
        this.name = name;
        this.age = age;
        selected = false;
    }

    public String getName() {
        return name;
    }

    public String getAge(){
        return age;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAge(String age){
        this.age = age;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Date getDateOfBirth(){
        Date now = Calendar.getInstance().getTime();
        Date d = now;
        if(age != null && age.length() == 4)
        {
            DateFormat format = new SimpleDateFormat("yyMM", Locale.getDefault());
            boolean correct = false;
            try
            {
                 d = format.parse(age);
                correct = true;
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
            if(!correct || d.after(now))
            {
                DateFormat format2 = new SimpleDateFormat("yyyy", Locale.getDefault());
                try
                {
                    d = format2.parse(age);
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
        }
        return d;
    }

}
