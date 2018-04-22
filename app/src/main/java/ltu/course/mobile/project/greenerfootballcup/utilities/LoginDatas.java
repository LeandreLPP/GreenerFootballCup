package ltu.course.mobile.project.greenerfootballcup.utilities;

public class LoginDatas {
    private static final LoginDatas ourInstance = new LoginDatas();

    static LoginDatas getInstance() {
        return ourInstance;
    }

    private boolean initialized;

    private short year;
    private String adminCode;
    private String emailAddress;

    private LoginDatas() {
        initialized = false;
    }

    public void Initialize(short year, String adminCode, String emailAddress)
    {
        this.year = year;
        this.adminCode = adminCode;
        this.emailAddress = emailAddress;
        initialized = true;
    }

    public boolean isInitialized() {
        return initialized;
    }

    public short getYear() {
        return year;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
}
