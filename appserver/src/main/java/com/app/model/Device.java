package com.app.model;

import com.jfinal.plugin.activerecord.Model;

import n.fw.utils.DateUtils;
import java.util.*;

public class Device extends Model<Device> 
{
    private static final long serialVersionUID = 3554755307155224109L;

    public static final Device dao = new Device();

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String BINDCODE = "bindcode";
    public static final String BINDNAME = "bindname";
    public static final String BINDTIME = "bindtime";
    public static final String DETAIL = "detail";
    public static final String POWER = "power";
    public static final String UPDATE_TIME = "update_time";
    public static final String BINDSTATE = "bindstate";

    public Device create(String name, String bindcode, String detail) 
    {
        Device device = new Device();
        device.set(NAME, name);
        device.set(BINDCODE, bindcode);
        device.set(BINDTIME, DateUtils.getDateTime());
        device.set(DETAIL, detail);
        device.set(BINDSTATE, 0);

        if (device.save())
        {
            return device;
        }
        return null;
    }

    public Device findByName(String name)
    {
        return findFirst("SELECT * FROM device WHERE name=? LIMIE 1", name);
    }

    public List<Device> getAll(int page)
    {
        return find("SELECT * FROM device ORDER BY `id` ASC LIMIT ?,20", page * 20);
    }
}