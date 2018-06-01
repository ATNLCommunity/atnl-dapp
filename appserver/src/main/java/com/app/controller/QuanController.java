package com.app.controller;

import com.app.model.Quan;

import n.fw.base.BaseController;

public class QuanController extends BaseController
{
    public void list()
    {
        success(Quan.dao.getQuans());
    }

    public void mine()
    {
        Long uid = getUid();
        if (uid == null || uid == 0)
        {
            error("请先登录");
            return;
        }

        success(Quan.dao.findByUid(uid));
    }

    public void get()
    {
        Long uid = getUid();
        if (uid == null || uid == 0)
        {
            error("请先登录");
            return;
        }

        Long qid = getParaToLong("qid", 0l);
        if (qid == 0)
        {
            error("优惠券id不能唯恐");
            return;
        }
        success(Quan.dao.findByQid(uid, qid));
    }
}