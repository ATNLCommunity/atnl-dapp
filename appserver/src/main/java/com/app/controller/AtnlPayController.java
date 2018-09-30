package com.app.controller;



import java.util.Date;
import java.util.HashMap;

import com.app.model.AtnlPayRecord;

import com.app.model.User;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;

import n.fw.base.BaseController;
import n.fw.utils.DateUtils;
import n.fw.utils.MD5Util;

public class AtnlPayController extends BaseController
{
  
    public void showQR()
    {
    	Long uid = getUid();
        if (uid == null || uid == 0)
        {
            error("请先登录");
            return;
        }
        User user = User.dao.findByUid(uid);
        if(null == user)
        {
        	error("用户不存在");
            return;
        }
        AtnlPayRecord apr = AtnlPayRecord.dao.getLeastRecord(uid);
        if(null == apr)
        {
        	apr = AtnlPayRecord.dao.create(uid);
        }
        else
        {
        	Date mdate = apr.getDate(AtnlPayRecord.CREATETIME);        	
        	if(new Date().getTime() - mdate.getTime() >= 180*1000)
        	{
        		apr.set(AtnlPayRecord.CREATETIME, DateUtils.getDateTime());
        		if(apr.update())
        		{
        			apr = AtnlPayRecord.dao.getLeastRecord(uid);
        		}
        	}
        }       		
        HashMap<String, Object> map = new HashMap<String,Object>();
        user.put(User.PWD, null);
        map.put("user", user);		
        String sign = MD5Util.MD5Encode(apr.getLong(AtnlPayRecord.ID)+""+DateUtils.formatDateTime(apr.getDate(AtnlPayRecord.CREATETIME))
        +apr.getLong(AtnlPayRecord.USERID)+"atunala@101", "utf-8");	
        map.put("qrstr", apr.getLong(AtnlPayRecord.ID)+"_"+sign);
        success(map);
    }
    
    /**
     * 商家扫码
     */
    public void checkQR()
    {
    	Long uid = getUid();
        if (uid == null || uid == 0)
        {
            error("请先登录");
            return;
        }
        User user = User.dao.findByUid(uid);
        if(null == user)
        {
        	error("用户不存在");
            return;
        }
        if(!(user.getInt(User.UTYPE) == 1))
    	{
    		error("非商检类型用户不能使用扫码功能");
            return;
    	}
    	String qrstr = getPara("qrstr","");
		if ("".equals(qrstr))
        {
			error("二维码不符合要求");
            return;
        }
		String[] strs = qrstr.split("_");
		if(strs.length != 2)
		{
			error("二维码不符合要求");
            return;
		}
		AtnlPayRecord apr = AtnlPayRecord.dao.findById(Long.parseLong(strs[0]));
		if(null == apr)
		{
			error("二维码不符合要求");
            return;	
		}
		String sign = MD5Util.MD5Encode(apr.getLong(AtnlPayRecord.ID)+""+DateUtils.formatDateTime(apr.getDate(AtnlPayRecord.CREATETIME))
        +apr.getLong(AtnlPayRecord.USERID)+"atunala@101", "utf-8");
    	if(!sign.equals(strs[1]))
    	{
    		error("二维码已失效");
            return;
    	}
    	if(apr.getInt(AtnlPayRecord.STATE) == 2)
    	{
    		error("订单已经完成");
            return;
    	}
    	apr.set(AtnlPayRecord.STATE,1);
    	apr.set(AtnlPayRecord.TOUSERID,uid);
    	if(!apr.update())
    	{
    		error("支付异常");
            return;	
    	}
    	success(apr);
    	
    }
    
    /**
     * 判断是否扫码完成
     */
    public void userCheck()
    {
    	String qrstr = getPara("qrstr","");
		if ("".equals(qrstr))
        {
			error("二维码不符合要求");
            return;
        }
		String[] strs = qrstr.split("_");
		if(strs.length != 2)
		{
			error("二维码不符合要求");
            return;
		}
		AtnlPayRecord apr = AtnlPayRecord.dao.findById(Long.parseLong(strs[0]));
		if(null == apr)
		{
			error("二维码不符合要求");
            return;	
		}
		String sign = MD5Util.MD5Encode(apr.getLong(AtnlPayRecord.ID)+""+DateUtils.formatDateTime(apr.getDate(AtnlPayRecord.CREATETIME))
        +apr.getLong(AtnlPayRecord.USERID)+"atunala@101", "utf-8");
    	if(!sign.equals(strs[1]))
    	{
    		error("二维码已失效");
            return;
    	}
    	if(!(apr.getInt(AtnlPayRecord.STATE) == 1))
    	{
    		error("等待商家扫码");
            return;
    	}
    	success(apr);
    }
    
    /**
     * 完成支付
     */
    public void finish()
    {
    	Long uid = getUid();
        if (uid == null || uid == 0)
        {
            error("请先登录");
            return;
        }
        User user = User.dao.findByUid(uid);
        if(null == user)
        {
        	error("用户不存在");
            return;
        }
        Long rid = getParaToLong("rid", 0l);
        if (rid == null || rid == 0)
        {
            error("支付异常");
            return;
        }
        AtnlPayRecord apr = AtnlPayRecord.dao.findById(rid);
		if(null == apr)
		{
			error("二维码不符合要求");
            return;	
		}
        float paynum = Float.parseFloat(getPara("paynum","0"));
        if(paynum <= 0)
        {
        	error("支付金额异常");
            return;
        }
        if(user.getFloat(User.ATNL) < paynum)
        {
        	error("余额不足");
            return;
        }
        try {
			this.updateAtnl(uid, apr.getLong(AtnlPayRecord.TOUSERID), paynum);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			error("支付金额异常");
            return;
		}
        apr.set(AtnlPayRecord.PAYNUM, paynum);
        apr.set(AtnlPayRecord.STATE, 2);
        apr.set(AtnlPayRecord.PAYTIME, DateUtils.getDateTime());
        apr.set(AtnlPayRecord.INFO, "阿图纳拉餐厅消费");
        if(!apr.update())
    	{
    		error("支付异常");
            return;	
    	}
    	success(apr);
        
    }
   
    @Before(Tx.class)
    private boolean updateAtnl(Long uid,Long toUid,float paynum) throws Exception
    {
    	User user = User.dao.findByUid(uid);
        if(null == user)
        {
            return false;
        }
        
        User userTo = User.dao.findByUid(toUid);
        if(null == userTo)
        {
            return false;
        }
        user.set(User.ATNL, user.getFloat(User.ATNL) - paynum);
        userTo.set(User.ATNL, userTo.getFloat(User.ATNL) + paynum);
        user.update();
        userTo.update();
        return true;
    }
    public void getByMonth()
    {
    	Long uid = getUid();
        if (uid == null || uid == 0)
        {
            error("请先登录");
            return;
        }
        int page = getParaToInt("page",0);
    	String datestr = getPara("month",DateUtils.getDate());
    	HashMap<String, Object> map = new HashMap<String,Object>();
        map.put("records", AtnlPayRecord.dao.getByMonth(uid, datestr,page));
        map.put("sum", AtnlPayRecord.dao.getSumByMonth(uid, datestr));
    	success(map);
    }
    
    public void getAll()
    {
    	Long uid = getUid();
        if (uid == null || uid == 0)
        {
            error("请先登录");
            return;
        }
    	int page = getParaToInt("page",0);
    	success(AtnlPayRecord.dao.getAll(uid,page));
    }
}