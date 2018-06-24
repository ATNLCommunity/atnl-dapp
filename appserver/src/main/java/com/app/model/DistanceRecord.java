package com.app.model;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

import n.fw.utils.DateUtils;

public class DistanceRecord extends Model<DistanceRecord> {



	private static final long serialVersionUID = -2629290688060592885L;
	public static final DistanceRecord dao = new DistanceRecord();
	public static final String ID = "id";
    public static final String SHEEPID = "sheepid";
    public static final String RECORDTIME = "recordtime";
    public static final String DISTANCE = "distance";
	
    public DistanceRecord getLeastRecord(Long sheepid)
    {
    	return findFirst("SELECT * FROM distancerecord WHERE sheepid=? ORDER BY id DESC limit 1", sheepid);
    }

    public List<DistanceRecord> getByDay(Long sheepid,String date)
    {
    	return find("SELECT * FROM distancerecord WHERE sheepid=? and DATEDIFF(recordtime,?) = 0",sheepid,date);
    }
    
    public List<DistanceRecord> getByWeek(Long sheepid,String date)
    {
    	return find("SELECT * FROM distancerecord WHERE sheepid=? and YEARWEEK(recordtime) = YEARWEEK(?)",sheepid,date);
    }
    
    public List<DistanceRecord> getByMonth(Long sheepid,String date)
    {
    	return find("SELECT * FROM distancerecord WHERE sheepid=? and YEAR(recordtime) = YEAR(?) and MONTH(recordtime) = MONTH(?)",sheepid,date,date);
    }
    
    public List<Object> getByGroupMonth(Long sheepid,String date)
    {
    	Date mdate = DateUtils.parseDate(date);
    	Calendar cal = Calendar.getInstance();
        cal.setTime(mdate);
    	return Db.query("SELECT sheepid,concat(YEAR(recordtime),'-',MONTH(recordtime),'-',DAYOFMONTH(recordtime)) as ymd"
    			+ ",concat(sheepid,'-',YEAR(recordtime),'-',MONTH(recordtime),'-',DAYOFMONTH(recordtime)) as "
    			+ "symd ,SUM(distance) as distance FROM distancerecord GROUP BY symd HAVING symd=?",sheepid+"-"+cal.get(Calendar.YEAR)+"-"+(cal.get(Calendar.MONTH)+1)+"-"+cal.get(Calendar.DAY_OF_MONTH));
    }
    
    public List<Object> getTop20()
    {
    	return Db.query("SELECT a.sheepid,SUM(distance) as distance,b.uid,c.name,d.sid FROM distancerecord a LEFT JOIN "
    			+ "usersheep b ON a.sheepid = b.sheepid LEFT JOIN `user` c ON b.uid = c.id LEFT JOIN sheep d "
    			+ "ON a.sheepid= d.id GROUP BY a.sheepid ORDER BY distance DESC LIMIT 20");
    }
}
