package com.app.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class GpsRecord extends Model<GpsRecord> {


	/**
	 * 
	 */
	private static final long serialVersionUID = -8680324982362439081L;

	public static final GpsRecord dao = new GpsRecord();

	public static final String ID = "id";
    public static final String SHEEPID = "sheepid";
    public static final String RECORDTIME = "recordtime";
    public static final String LON = "lon";
    public static final String LAT = "lat";
    public static final String HIGH = "high";

    public List<GpsRecord> getAll()
    {
        return find("SELECT * FROM gpsrecord WHERE id in (SELECT MAX(id) FROM gpsrecord GROUP by sheepid)");
    }

    public GpsRecord getLeastRecord(Long sheepid)
    {
    	return findFirst("SELECT * FROM gpsrecord WHERE sheepid=? ORDER BY id DESC limit 1", sheepid);
    }
    public List<GpsRecord> getByDay(Long sheepid,String date)
    {
    	return find("SELECT * FROM gpsrecord WHERE sheepid=? and DATEDIFF(recordtime,?) = 0",sheepid,date);
    }
    
    public List<GpsRecord> getByWeek(Long sheepid,String date)
    {
    	return find("SELECT * FROM gpsrecord WHERE sheepid=? and YEARWEEK(recordtime) = YEARWEEK(?)",sheepid,date);
    }
    
    public List<GpsRecord> getByMonth(Long sheepid,String date)
    {
    	return find("SELECT * FROM gpsrecord WHERE sheepid=? and YEAR(recordtime) = YEAR(?) and MONTH(recordtime) = MONTH(?)",sheepid,date,date);
    }

}
