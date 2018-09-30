package com.app.model;


import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import n.fw.utils.DateUtils;

public class AtnlPayRecord extends Model<AtnlPayRecord> {

	private static final long serialVersionUID = 5451297724213974415L;
	public static final AtnlPayRecord dao = new AtnlPayRecord();
	
	public static final String ID = "id";
    public static final String PAYNUM = "paynum";
    public static final String PAYTIME = "paytime";
    public static final String TOUSERID = "touserid";
    public static final String INFO = "info";
    public static final String CREATETIME = "createtime";
    public static final String STATE = "state";
    public static final String USERID = "userid";
	
    public AtnlPayRecord create(Long userid)
    {
    	AtnlPayRecord apr = new AtnlPayRecord();
        apr.set(STATE, 0);
        apr.set(CREATETIME, DateUtils.getDateTime());
        apr.set(USERID, userid);

        if (apr.save())
        {
            return apr;
        }
        return null;
    }
      
    public AtnlPayRecord getLeastRecord(Long userid)
    {
    	return findFirst("SELECT * FROM atnlpayrecord WHERE userid=? and state = 0 ORDER BY id DESC limit 1", userid);
    }

    public List<AtnlPayRecord> getAll(Long userid,int page)
    {
    	return find("SELECT * FROM atnlpayrecord WHERE userid=? and state = 2 order by id desc LIMIT ?,20",userid,page * 20);
    }
    
    public List<AtnlPayRecord> getByMonth(Long userid,String datestr, int page)
    {
    	return find("SELECT * FROM atnlpayrecord WHERE userid=? and state = 2 and YEAR(paytime) = YEAR(?) and MONTH(paytime) = MONTH(?) order by id desc LIMIT ?,20",userid,datestr,datestr,page * 20);
    }
    
    public float getSumByMonth(Long userid,String datestr)
    {
    	Number sum = Db.queryNumber("SELECT sum(paynum) FROM atnlpayrecord WHERE userid=? and state = 2 and YEAR(paytime) = YEAR(?) and MONTH(paytime) = MONTH(?)",userid,datestr,datestr);
    	if(null == sum)
    	{
    		return 0;	
    	}
    	
    	return sum.floatValue();//Db.queryNumber("SELECT sum(paynum) FROM atnlpayrecord WHERE userid=? and state = 2 and YEAR(paytime) = YEAR(?) and MONTH(paytime) = MONTH(?)",userid,datestr,datestr).floatValue();
    }

}
