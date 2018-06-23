package com.app.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class BasicRecord extends Model<BasicRecord> {


	/**
	 * 
	 */
	private static final long serialVersionUID = 1882409792770164690L;
	public static final BasicRecord dao = new BasicRecord();
	public static final String ID = "id";
    public static final String SHEEPID = "sheepid";
    public static final String RECORDTIME = "recordtime";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";
    public static final String DIFFW = "diffw";
    public static final String DIFFH = "diffh";
    
    public BasicRecord getLeastRecord(Long sheepid)
    {
    	return findFirst("SELECT * FROM basicrecord WHERE sheepid=? ORDER BY id DESC limit 1", sheepid);
    }

    public List<BasicRecord> getAll(Long sheepid)
    {
    	return find("SELECT * FROM basicrecord WHERE sheepid=?", sheepid);
    }
}
