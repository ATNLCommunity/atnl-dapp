package com.app.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

import n.fw.utils.DateUtils;

public class Withdrawal extends Model<Withdrawal> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7979270375025535022L;

	public static final Withdrawal dao = new Withdrawal();
	
	public static final String ID = "id";
	public static final String MONTH = "month";
    public static final String UID = "uid";
    public static final String SALEMONEY = "salemoney";
    public static final String MONEY = "money";
    public static final String APPLYTIME = "applytime";
    public static final String STATE = "state";
    public static final String CHECKID = "checkid";
    public static final String CHECKTIME = "checktime";
    public static final String BANKID = "bankid";
	
    public Withdrawal create(Long userid,String month,float salemoney,float money)
    {
    	Withdrawal wd = new Withdrawal();
        wd.set(STATE, 0);
        wd.set(APPLYTIME, DateUtils.getDateTime());
        wd.set(UID, userid);
        wd.set(MONTH, month);
        wd.set(SALEMONEY, salemoney);
        wd.set(MONEY, money);

        if (wd.save())
        {
            return wd;
        }
        return null;
    }
    public Withdrawal getLeastRecord(Long uid)
    {
    	return findFirst("select * from withdrawal where uid=? order By id desc limit 1", uid);
    }
      
    public Withdrawal get(Long uid,String month)
    {
    	return findFirst("select * from withdrawal where uid=? and month = ?", uid,month);
    }
    
    public List<Withdrawal> getAll(Long uid,int state)
    {
    	return find("select * from withdrawal where uid=? and state >=? ",uid,state);
    }
}
