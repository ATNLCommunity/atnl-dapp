package com.app.model;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

public class Addr extends Model<Addr>
{
    private static final long serialVersionUID = 3554755307155224121L;

    public static final Addr dao = new Addr();

    public static final String UID = "uid";
    public static final String ADDR = "addr";
    public static final String NAME = "name";
    public static final String PHONE = "phone";

    public Addr get(Long uid)
    {
        return findFirst("SELECT * FROM addr WHERE uid=? LIMIT 1", uid);
    }

    public void edit(Long uid, String addr, String name, String phone)
    {
        Db.update("INSERT INTO addr(uid,addr,name,phone) VALUES(?,?,?,?) ON DUPLICATE KEY UPDATE addr=VALUES(addr),name=VALUES(name),phone=VALUES(phone)", uid, addr, name, phone);
    }
}