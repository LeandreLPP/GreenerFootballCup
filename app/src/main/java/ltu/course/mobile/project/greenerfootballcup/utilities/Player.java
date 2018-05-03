package ltu.course.mobile.project.greenerfootballcup.utilities;

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

}
