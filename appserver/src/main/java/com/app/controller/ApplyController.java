package com.app.controller;

import com.app.model.Apply;

import n.fw.base.BaseController;

public class ApplyController extends BaseController
{
  public void create()
  {
	  Long uid = getUid();
      if (uid == null || uid == 0)
      {
          error("请先登录");
          return;
      }
      String contact = getPara("contact", "");
      if(contact.trim().equals(""))
      {
    	  error("联系方式不能为空");
          return;
      }
      String name = getPara("name", "");
      if(name.trim().equals(""))
      {
    	  error("联系人不能为空");
          return;
      }
      Apply apply = Apply.dao.create(uid, contact, 1,name);
      if(null == apply)
      {
    	  error("数据异常，请联系客服");
          return;
      }
      success(apply);
  }
}