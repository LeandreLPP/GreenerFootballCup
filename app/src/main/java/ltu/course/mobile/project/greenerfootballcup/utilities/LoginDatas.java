package ltu.course.mobile.project.greenerfootballcup.utilities;

public class LoginDatas {
    private static final LoginDatas ourInstance = new LoginDatas();

    public static LoginDatas getInstance() {
        return ourInstance;
    }

    private boolean initialized;

    private String year;
    private String adminCode;
    private String emailAddress;

    private LoginDatas() {
        initialized = false;
    }

    public void Initialize(String year, String adminCode, String emailAddress)
    {
        this.year = year;
        this.adminCode = adminCode;
        this.emailAddress = emailAddress;
        initialized = true;
    }

    public boolean isInitialized() {
        if(year != null && adminCode != null && emailAddress != null)
            initialized = true;
        else
            initialized = false;
        return initialized;
    }

    public String getYear() {
        return year;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setYear(String year){ this.year = year;}

    public void setAdminCode(String adminCode){ this.adminCode = adminCode;}


    public void setEmailAddress(String emailAddress){ this.emailAddress = emailAddress;}



}
