package com.app.controller;

import com.app.model.Notice;


import n.fw.base.BaseController;

public class NoticeController extends BaseController
{
    /**
     * 分页获取公告信息
     */
	public void getNotice()
	{
		int page = getParaToInt("page",0);
		if(page <= 0)
		{
			page = 0;
		}
		else
		{
			page = page -1;
		}
		success(Notice.dao.list(page));
	}
	
	/**
	 * 显示单条详情
	 */
	public void getNoticeContent()
	{
		Long nid = getParaToLong("nid", 0l);
		
		success(Notice.dao.getByNid(nid));
	}
}
