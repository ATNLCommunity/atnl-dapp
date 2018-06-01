package com.app.controller;

import com.app.model.Gps;
import com.app.model.Step;

import org.apache.commons.lang3.StringUtils;

import n.fw.base.BaseController;

public class LookController extends BaseController
{
    public void gpsAll()
    {
        success(Gps.dao.getAll());
    }

    public void gps()
    {
        String did = getPara("did");
        String date = getPara("date");

        if (StringUtils.isBlank(did))
        {
            error("设备id不能位空");
            return;
        }

        if (StringUtils.isBlank(date))
        {
            success(Gps.dao.findByDidToday(did));
        }
        else
        {
            success(Gps.dao.findByDid(did, date));
        }
    }

    public void stepAll()
    {
        success(Step.dao.getAll());
    }

    public void step()
    {
        String did = getPara("did");
        if (StringUtils.isBlank(did))
        {
            error("设备id不能位空");
            return;
        }

        success(Step.dao.findByName(did));
    }
}
