package com.app.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class Gps extends Model<Gps>
{
    private static final long serialVersionUID = 3554755307155224111L;

    public static final Gps dao = new Gps();

    public static final String ID = "id";
    public static final String DID = "did";
    public static final String TIME = "time";
    public static final String LON = "lon";
    public static final String LAT = "lat";
    public static final String HIGH = "high";

    public List<Gps> getAll()
    {
        return find("SELECT * FROM gps WHERE id in (SELECT MAX(id) FROM gps GROUP by did)");
    }

    public List<Gps> findByDidToday(String did)
    {
        return find("SELECT * FROM gps WHERE did=? AND time>=CURDATE() ORDER BY id ASC", did);
    }

    public List<Gps> findByDid(String did, String date)
    {
        return find("SELECT * FROM gps WHERE did=? AND DATE(time)=? ORDER BY id ASC", did, date);
    }
}