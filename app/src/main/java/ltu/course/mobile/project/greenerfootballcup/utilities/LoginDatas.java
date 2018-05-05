package ltu.course.mobile.project.greenerfootballcup.utilities;

import android.graphics.Bitmap;

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
    private Bitmap signature;

    private LoginDatas() {
        initialized = false;
    }

    public void Initialize(Date year, String adminCode, String emailAddress)
    {
        this.year = year;
        this.adminCode = adminCode;
        this.emailAddress = emailAddress;
        signature = null;
        initialized = true;
    }

    public boolean isInitialized() {
        if(year != null && adminCode != null && emailAddress != null)
            initialized = true;
        else
            initialized = false;
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

    public void setYear(Date year){ this.year = year;}

    public void setAdminCode(String adminCode){ this.adminCode = adminCode;}


    public void setEmailAddress(String emailAddress){ this.emailAddress = emailAddress;}

    public void setSignature(Bitmap bitmap){
        signature = bitmap;
    }

    public Bitmap getSignature() {
        return signature;
    }
}
