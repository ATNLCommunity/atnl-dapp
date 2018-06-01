package com.app.model;

import java.util.List;

import com.jfinal.plugin.activerecord.Model;

public class Step extends Model<Step>
{
    private static final long serialVersionUID = 3554755307155224110L;

    public static final Step dao = new Step();

    public static final String ID = "id";
    public static final String DID = "did";
    public static final String TDATE = "tdate";
    public static final String TTIME = "ttime";
    public static final String STEP = "step";
    public static final String ROLL = "roll";

    public List<Step> findByName(String did)
    {
        return find("SELECT tdate,MAX(step) AS step FROM step WHERE did=? GROUP BY tdate", did);
    }

    public List<Step> getAll()
    {
        return find("SELECT did,tdate,MAX(step) AS step FROM step GROUP BY did,tdate");
    }
}