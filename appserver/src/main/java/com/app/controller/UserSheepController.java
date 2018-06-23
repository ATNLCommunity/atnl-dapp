package com.app.controller;


import com.app.model.Sheep;
import com.app.model.UserSheep;

import n.fw.base.BaseController;

public class UserSheepController extends BaseController
{
	/**
	 * 购买羊羔
	 * @param sheepid
	 */
	public void addUserSheep()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		Long uid = getUid();
        if (uid == null || uid == 0)
        {
            error("请先登录");
            return;
        }
        UserSheep us = UserSheep.dao.create(uid, sheepid, 0, 0);
        Sheep sheep = Sheep.dao.findById(sheepid);
        if (null == sheep)
        {
            error(" 购买羊羔失败");
            return;
        }
        sheep.set("state", 1).update();
        success(us);
	}
	/**
	 * 获取用户羊羔列表
	 */
	public void getUserSheeps()
	{
		Long uid = getUid();
        if (uid == null || uid == 0)
        {
            error("请先登录");
            return;
        }
        success(UserSheep.dao.getUserSheeps(uid));
	}
	
}
