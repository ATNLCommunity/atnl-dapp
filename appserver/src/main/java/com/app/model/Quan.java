package com.app.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

import n.fw.utils.DateUtils;

public class Quan extends Model<Quan>
{
    private static final long serialVersionUID = 3554755307155224115L;

    public static final Quan dao = new Quan();

    public static final String ID = "id";
    public static final String UID = "uid";
    public static final String NAME = "name";
    public static final String MONEY = "money";
    public static final String STATE = "state";
    public static final String EXPIRE_TIME = "expire_time";
    public static final String CREATE_TIME = "create_time";

    public List<Quan> getQuans()
    {
        return find("SELECT * FROM quan");
    }

    public List<Quan> findByUid(Long uid)
    {
        return find("SELECT * FROM quan WHERE uid=? AND state>0 AND expire_time>=NOW()", uid);
    }

    public Quan findByQid(Long uid, Long qid)
    {
        return findFirst("SELECT * FROM quan WHERE id=? AND uid=? AND expire_time>=NOW()", qid, uid);
    }

    public Quan create(Long uid, String name, Float money)
    {
        Quan quan = new Quan();
        quan.set(UID, uid);
        quan.set(NAME, name);
        quan.set(MONEY, money);
        quan.set(STATE, 1);
        quan.set(EXPIRE_TIME, "2100-12-31");
        quan.set(CREATE_TIME, DateUtils.getDateTime());
        if (quan.save())
        {
            return quan;
        }
        return null;
    }
}