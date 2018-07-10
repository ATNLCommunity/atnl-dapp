package com.app.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

public class SmsCode extends Model<SmsCode>
{
    private static final long serialVersionUID = 3554755307155224222L;

    public static final SmsCode dao = new SmsCode();
    
    public final static String PHONE = "phone";
    public final static String COUNT = "count";

    public SmsCode findByPhone(String phone)
    {
        return findFirst("SELECT * FROM smscode WHERE phone=? LIMIT 1", phone);
    }

    public void increase(String phone)
    {
        Db.update("INSERT INTO smscode(phone,count) VALUES(?,1) ON DUPLICATE KEY UPDATE count=count+1", phone);
    }
}