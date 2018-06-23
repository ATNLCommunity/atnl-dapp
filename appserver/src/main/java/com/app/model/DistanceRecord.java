package com.app.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

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
}
