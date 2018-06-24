package com.app.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import n.fw.utils.DateUtils;

public class StepRecord extends Model<StepRecord> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -1493155023418047046L;
	public static final StepRecord dao = new StepRecord();
	public static final String ID = "id";
    public static final String SHEEPID = "sheepid";
    public static final String RECORDTIME = "recordtime";
    public static final String STEPS = "steps";
	
    public StepRecord getLeastRecord(int sheepid)
    {
    	return findFirst("SELECT * FROM steprecord WHERE sheepid=? ORDER BY id DESC limit 1", sheepid);
    }

    public List<StepRecord> getByDay(Long sheepid,String date)
    {
    	return find("SELECT * FROM steprecord WHERE sheepid=? and DATEDIFF(recordtime,?) = 0",sheepid,date);
    }
    
    public List<StepRecord> getByWeek(Long sheepid,String date)
    {
    	return find("SELECT * FROM steprecord WHERE sheepid=? and YEARWEEK(recordtime) = YEARWEEK(?)",sheepid,date);
    }
    
    public List<StepRecord> getByMonth(Long sheepid,String date)
    {
    	return find("SELECT * FROM steprecord WHERE sheepid=? and YEAR(recordtime) = YEAR(?) and MONTH(recordtime) = MONTH(?)",sheepid,date,date);
    }
    
    public List<Object> getByGroupMonth(Long sheepid,String date)
    {
    	Date mdate = DateUtils.parseDate(date);
    	Calendar cal = Calendar.getInstance();
        cal.setTime(mdate);
    	return Db.query("SELECT sheepid,concat(YEAR(recordtime),'-',MONTH(recordtime),'-',DAYOFMONTH(recordtime)) as ymd,concat(sheepid,'-',YEAR(recordtime),'-',MONTH(recordtime),'-',DAYOFMONTH(recordtime)) as "
    			+ "symd ,SUM(steps) as distance FROM steprecord GROUP BY symd HAVING symd=?",sheepid+"-"+cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH));
    }
}
