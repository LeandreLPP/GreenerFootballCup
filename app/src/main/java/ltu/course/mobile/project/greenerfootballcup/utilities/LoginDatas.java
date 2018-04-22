package ltu.course.mobile.project.greenerfootballcup.utilities;

import java.util.Date;

public class LoginDatas {
    private static final LoginDatas ourInstance = new LoginDatas();

    public static LoginDatas getInstance() {
        return ourInstance;
    }

    private boolean initialized;

    private Date year;
    private String adminCode;
    private String emailAddress;

    private LoginDatas() {
        initialized = false;
    }

    public void Initialize(Date year, String adminCode, String emailAddress)
    {
        this.year = year;
        this.adminCode = adminCode;
        this.emailAddress = emailAddress;
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public Date getYear() {
        return year;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
