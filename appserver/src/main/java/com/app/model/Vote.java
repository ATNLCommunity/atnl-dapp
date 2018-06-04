package com.app.model;

import com.jfinal.plugin.activerecord.Model;

public class Vote extends Model<Vote>
{
    private static final long serialVersionUID = 3554755307155224124L;

    public static final Vote dao = new Vote();

    public static final String UID = "uid";
    public static final String IDX = "idx";
    public static final String UPDATE_TIME = "update_time";

    public Vote findByUid(Long uid)
    {
        return findFirst("SELECT * FROM vote WHERE uid=?", uid);
    }
}