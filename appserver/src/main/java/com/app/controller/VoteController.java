package com.app.controller;

import java.util.Date;

import com.app.model.User;
import com.app.model.Vote;
import com.app.model.VoteSeed;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;

import n.fw.base.BaseController;
import n.fw.utils.DateUtils;

public class VoteController extends BaseController
{
    public void doit()
    {
        Long uid = getUid();
        if (uid == null || uid == 0)
        {
            error("请先登录");
            return;
        }

        Integer idx = getParaToInt("idx", 0);
        Vote vote = Vote.dao.findByUid(uid);
        if (vote != null)
        {
            error("你已经投过票");
            return;
        }

        Date date = DateUtils.parseDate("2018-06-14");
        Date now = new Date();
        if (now.after(date))
        {
            error("投票已经结束");
            return;
        }

        User user = User.dao.findByUid(uid);
        if (user == null)
        {
            errorForbidden();
            return;
        }

        float atnl = user.getFloat(User.ATNL);
        if (atnl < 200)
        {
            error("ATNL 不够");
            return;
        }

        user.set(User.ATNL, atnl - 200);
        user.update();

        vote = new Vote();
        vote.set(Vote.UID, uid);
        vote.set(Vote.IDX, idx);
        vote.set(Vote.UPDATE_TIME, DateUtils.getDateTime());
        vote.save();

        success("投票成功");
    }

    public void get()
    {
        Long uid = getUid();
        if (uid == null || uid == 0)
        {
            error("请先登录");
            return;
        }

        Record vote = Db.findFirst("SELECT voteseed.*,vote.update_time FROM vote INNER JOIN voteseed ON vote.idx=voteseed.id WHERE vote.uid=?", uid);
        if (vote == null)
        {
            error("你还没投过票");
            return;
        }

        success(vote);
    }

    public void list()
    {
        success(VoteSeed.dao.getAll());
    }
}