package com.app.model;

import java.util.List;


import com.jfinal.plugin.activerecord.Model;

public class Sheep extends Model<Sheep>
{
    private static final long serialVersionUID = 3554755307155224116L;

    public static final Sheep dao = new Sheep();

    public static final String ID = "id";
    public static final String STATE = "state";
    public static final String SID = "sid";
    public static final String UPDATE_TIME = "update_time";
    public static final String PRICE = "price";
    public static final String BIRTHDAY = "birthday";
    public static final String WEIGHT = "weight";
    public static final String HEIGHT = "height";
    public static final String PREKILLTIME = "prekilltime";
    public static final String DID = "did";
    
    public List<Sheep> getSheep(Integer count)
    {
        return find("SELECT * FROM sheep WHERE state=0 LIMIT ?", count);
    }
    
    public List<Sheep> list(int page)
    {
    	return find("SELECT * FROM sheep WHERE state=0 LIMIT ?,20", page*20);
    }
    
    public Sheep findById(Long id)
    {
    	return findFirst("SELECT * FROM sheep WHERE id=?",id);
    }
}