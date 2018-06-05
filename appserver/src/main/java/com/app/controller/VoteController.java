package com.app.controller;

import com.app.model.Vote;
import com.app.model.VoteSeed;

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

        Vote vote = Vote.dao.findByUid(uid);
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