package com.app.model;


import java.util.List;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Model;

public class AtnlAddRecord extends Model<AtnlAddRecord> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3213065356024501131L;

	public static final AtnlAddRecord dao = new AtnlAddRecord();
	public static final String ID = "id";
    public static final String SHEEPID = "sheepid";
    public static final String RECORDTIME = "recordtime";
    public static final String ATNLADD = "atnladd";
	
    public AtnlAddRecord getLeastRecord(Long sheepid)
    {
    	return findFirst("SELECT * FROM atnladdrecord WHERE sheepid=? ORDER BY id DESC limit 1", sheepid);
    }

    public Double getAtnlSum(Long sheepid)
    {
    	return Db.queryDouble("SELECT sum(atnladd) FROM atnladdrecord WHERE sheepid=?", sheepid);
    }
    
    public List<AtnlAddRecord> getAll(Long sheepid)
    {
    	return find("SELECT * FROM atnladdrecord WHERE sheepid=?", sheepid);
    }
}
