package com.app.controller;


import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.app.model.AtnlAddRecord;
import com.app.model.BasicRecord;
import com.app.model.DistanceRecord;
import com.app.model.EventRecord;
import com.app.model.GpsRecord;
import com.app.model.Sheep;
import com.app.model.StepRecord;

import n.fw.base.BaseController;
import n.fw.utils.DateUtils;

public class SheepController extends BaseController
{	

	/**
	 * 获取未售出药膏列表
	 * @param page
	 */
	public void getSheeps()
	{
		int page = getParaToInt("page");
        success(new Sheep().list(page));
	}
	
	/**
	 * 获取羊羔相关信息
	 */
	public void getSheepInfo()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		Long uid = getUid();
        if (uid == null || uid == 0)
        {
            error("请先登录");
            return;
        }
        Sheep sheep = Sheep.dao.findById(sheepid);
        BasicRecord br = BasicRecord.dao.getLeastRecord(sheepid);
        AtnlAddRecord aar = AtnlAddRecord.dao.getLeastRecord(sheepid);
        DistanceRecord dr = DistanceRecord.dao.getLeastRecord(sheepid);
        GpsRecord gr = GpsRecord.dao.getLeastRecord(sheepid);
        EventRecord er = EventRecord.dao.getLeastRecord(sheepid);
        StepRecord sr = StepRecord.dao.getLeastRecord(sheepid);
        HashMap<String, Object> map = new HashMap<String,Object>();
        map.put("info", sheep);
        map.put("br", br);
        map.put("aar", aar);
        if(null != aar)
        {
        	map.put("sumatnl", aar.getAtnlSum(sheepid));
        }
        else
        {
        	map.put("sumatnl", 0);
        	AtnlAddRecord.dao.create(sheepid, (float)Math.random()*100);
        }
        map.put("dr", dr);
        map.put("gr", gr);
        map.put("er", er);  
        map.put("sr", sr);
        
        success(map);
	}
	
	/**
	 * 获取羊羔基本信息
	 */
	public void getBasicRecords()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		success(BasicRecord.dao.getAll(sheepid));
	}
	
	/**
	 * 获取羊羔事件信息
	 */
	public void getEventRecords()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		success(EventRecord.dao.getAll(sheepid));
	}
	
	/**
	 * 获取羊羔atnl记录
	 */
	public void getAtnlAddRecords()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		success(AtnlAddRecord.dao.getAll(sheepid));
	}
	
	/**
	 *按天获取距离信息 
	 */
	public void getDistanceByDay()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		String date = getPara("date");
		success(DistanceRecord.dao.getByDay(sheepid, date));
	}
	
	/**
	 *按天获取计步信息 
	 */
	public void getStepsByDay()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		String date = getPara("date");
		success(StepRecord.dao.getByDay(sheepid, date));
	}
	
	/**
	 *按天获取gps信息 
	 */
	public void getGpsByDay()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		String date = getPara("date");
		success(GpsRecord.dao.getByDay(sheepid, date));
	}
	/**
	 * 
	 */
	public void getGpsAll()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		success(GpsRecord.dao.getAll(sheepid));
	}
	
	/**
	 * 按周呈现距离信息
	 * 
	 */
	public void getDistanceByWeek()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		String date = getPara("date");
		List<DistanceRecord> list = new DistanceRecord().getByWeek(sheepid, date);
		float mon = 0;
		float tue = 0;
		float wed = 0;
		float thu = 0;
		float fri = 0;		
		float sat = 0;
		float sun = 0;
		
		for(DistanceRecord dr: list)
		{
			switch(DateUtils.getWeekNumber(dr.getDate("recordtime")))
			{
				case 1:
					sun += dr.getFloat("distance");
					break;
				case 2:
					mon += dr.getFloat("distance");
					break;
				case 3:
					tue += dr.getFloat("distance");
					break;
				case 4:
					wed += dr.getFloat("distance");
					break;
				case 5:
					thu += dr.getFloat("distance");
					break;
				case 6:
					fri += dr.getFloat("distance");
					break;
				case 7:
					sat += dr.getFloat("distance");
					break;
				default:
					break;
			}		
		}
		HashMap<String, Object> map = new HashMap<String,Object>();
        map.put("sun", sun);
        map.put("mon", mon);
        map.put("tue", tue);
        map.put("wed", wed);
        map.put("thu", thu);
        map.put("fri", fri);
        map.put("sat", sat);
        Date startDate = DateUtils.getFirstDayOfWeek(DateUtils.parseDate(date));
		Date endDate =DateUtils.getLastDayOfWeek(DateUtils.parseDate(date));
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        String start = (cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(endDate);
        String end = (cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DAY_OF_MONTH);
        map.put("s2e",start+"-"+end);
        success(map);
	}
	
	public void getDistanceByWeekAll()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		String date = getPara("date");
		success(DistanceRecord.dao.getByWeek(sheepid, date));
	}
	/**
	 * 按周呈现计步信息
	 * 
	 */
	public void getStepByWeek()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		String date = getPara("date");
		List<StepRecord> list = new StepRecord().getByWeek(sheepid, date);
		float mon = 0;
		float tue = 0;
		float wed = 0;
		float thu = 0;
		float fri = 0;		
		float sat = 0;
		float sun = 0;
		for(StepRecord dr: list)
		{
			switch(DateUtils.getWeekNumber(dr.getDate("recordtime")))
			{
				case 1:
					sun += dr.getInt("steps");
					break;
				case 2:
					mon += dr.getInt("steps");
					break;
				case 3:
					tue += dr.getInt("steps");
					break;
				case 4:
					wed += dr.getInt("steps");
					break;
				case 5:
					thu += dr.getInt("steps");
					break;
				case 6:
					fri += dr.getInt("steps");
					break;
				case 7:
					sat += dr.getInt("steps");
					break;
				default:
					break;
			}		
		}
		HashMap<String, Object> map = new HashMap<String,Object>();
        map.put("sun", sun);
        map.put("mon", mon);
        map.put("tue", tue);
        map.put("wed", wed);
        map.put("thu", thu);
        map.put("fri", fri);
        map.put("sat", sat);
        Date startDate = DateUtils.getFirstDayOfWeek(DateUtils.parseDate(date));
		Date endDate =DateUtils.getLastDayOfWeek(DateUtils.parseDate(date));
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        String start = (cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(endDate);
        String end = (cal.get(Calendar.MONTH)+1)+"/"+cal.get(Calendar.DAY_OF_MONTH);
        map.put("s2e",start+"-"+end);
        success(map);
	}
	
	public void getStepByWeekAll()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		String date = getPara("date");
		success(StepRecord.dao.getByWeek(sheepid, date));
	}
	/**
	 * 按月呈现距离信息
	 * 
	 */
	public void getDistanceByGroupMonth()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		String date = getPara("date");
		success(DistanceRecord.dao.getByGroupMonth(sheepid, date));
	}
	
	
	public void getDistanceByMonthAll()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		String date = getPara("date");
		success(DistanceRecord.dao.getByMonth(sheepid, date));
	}
	/**
	 * 按月呈现距离信息
	 * 
	 */
	public void getStepByGroupMonth()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		String date = getPara("date");
		success(StepRecord.dao.getByGroupMonth(sheepid, date));
	}
	
	public void getStepByMonthAll()
	{
		Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		String date = getPara("date");
		success(StepRecord.dao.getByMonth(sheepid, date));
	}
	/**
	 * 财富榜
	 */
	public void getWealthTop()
	{
		success(AtnlAddRecord.dao.getTop20());
	}
	
	/**
	 * 运动榜
	 */
	public void getSportsTop()
	{
		success(DistanceRecord.dao.getTop20());
	}
}
