package com.app.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

public class SmsCode extends Model<SmsCode>
{
    private static final long serialVersionUID = 3554755307155224222L;

    public static final SmsCode dao = new SmsCode();
    
    public final static String PHONE = "phone";
    public final static String COUNT = "count";
    public final static String TYPE = "type";

    public SmsCode findByPhone(String phone, int type)
    {
        return findFirst("SELECT * FROM smscode WHERE phone=? AND type=? LIMIT 1", phone, type);
    }

    public void increase(String phone, int type)
    {
        Db.update("INSERT INTO smscode(phone,type, count) VALUES(?,?,1) ON DUPLICATE KEY UPDATE count=count+1", phone, type);
    }
}