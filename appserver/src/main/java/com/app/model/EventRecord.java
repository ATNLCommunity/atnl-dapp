package com.app.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class EventRecord extends Model<EventRecord> {

	private static final long serialVersionUID = 2184272854750919080L;
	public static final EventRecord dao = new EventRecord();
	public static final String ID = "id";
    public static final String SHEEPID = "sheepid";
    public static final String RECORDTIME = "recordtime";
    public static final String EVENT = "event";
    
    public EventRecord getLeastRecord(Long sheepid)
    {
    	return findFirst("SELECT * FROM eventrecord WHERE sheepid=? ORDER BY id DESC limit 1", sheepid);
    }

    public List<EventRecord> getAll(Long sheepid)
    {
    	return find("SELECT * FROM eventrecord WHERE sheepid=?", sheepid);
    }
}
