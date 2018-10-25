package com.app.controller;

import java.math.BigDecimal;
import java.util.List;

import com.app.model.BankInfo;
import com.app.model.Order;
import com.app.model.Withdrawal;

import n.fw.base.BaseController;
import net.sf.json.JSONArray;

public class WithdrawalController extends BaseController {

	/**
	 * 查看可申请提现列表
	 */
	public void list() {
		Long uid = getUid();
		if (uid == null || uid == 0) {
			error("请先登录");
			return;
		}
		Withdrawal wd = Withdrawal.dao.getLeastRecord(uid);
		String month = "197001";
		if(null != wd)
		{
			month = wd.getStr(Withdrawal.MONTH);
		}
		List<Object> pre_wds = Order.dao.getAmountByMonth(uid,month);
		for(Object o:pre_wds)
		{
			JSONArray json = JSONArray.fromObject(o);
			Float salemoney = Float.parseFloat(json.get(0).toString());
			BigDecimal m1 = new BigDecimal(json.get(0).toString());
			Float money = 0f;
			m1.multiply(new BigDecimal("20")).divide(new BigDecimal("100")).floatValue();
			if(salemoney <= 1000)
			{
				money = m1.multiply(new BigDecimal("20")).divide(new BigDecimal("100")).floatValue();
			}
			else if(salemoney <= 5000 && salemoney > 1000)
			{				
				m1 = m1.subtract(new BigDecimal("1000"));
				money = m1.multiply(new BigDecimal("30")).divide(new BigDecimal("100")).add(new BigDecimal("200")).floatValue();
			}
			else
			{
				m1 = m1.subtract(new BigDecimal("5000"));
				money = m1.multiply(new BigDecimal("40")).divide(new BigDecimal("100")).add(new BigDecimal("1400")).floatValue();
			}
			Withdrawal.dao.create(uid, json.getString(1), salemoney, money);
		}
		
		success(Withdrawal.dao.getAll(uid,0));
	}

	public void send() {
		Long uid = getUid();
		if (uid == null || uid == 0) {
			error("请先登录");
			return;
		}
		Long wid = getParaToLong("wid", 0l);
		if (wid == 0l) {
			error("提现月份不能为空");
			return;
		}
		Withdrawal wd = Withdrawal.dao.findById(wid);
		if (null == wd) {
			error("提现申请月份不存在");
			return;
		}
		if(wd.getInt(Withdrawal.STATE) == 1)
		{
			error("提现申请已提交，请等待");
			return;
		}
		Long bid = getParaToLong("bid", 0l);
		if (bid == 0l) {
			error("提现银行信息不能为空");
			return;
		}
		BankInfo bi = BankInfo.dao.findById(bid);
		if (null == bi) {
			error("提现银行信息不能为空");
			return;
		}
		wd.set(Withdrawal.BANKID, bid);
		wd.set(Withdrawal.STATE, 1);
		if(!wd.update())
		{
			error("数据异常，请联系客服");
			return;
		}
		success(wd);
	}

	public void myRecords() {
		Long uid = getUid();
		if (uid == null || uid == 0) {
			error("请先登录");
			return;
		}
		success(Withdrawal.dao.getAll(uid,1));
	}
	
	/**
	 * 增加提现银行信息
	 */
	public void addBank()
	{
		Long uid = getUid();
		if (uid == null || uid == 0) {
			error("请先登录");
			return;
		}
		String bankname = getPara("bankname","");
		if(bankname.trim().equals(""))
		{
			error("请输出正确的银行名称");
			return;
		}
		String bankno = getPara("bankno","");
		if(bankno.trim().equals(""))
		{
			error("请输出正确的银行卡号");
			return;
		}
		String oname = getPara("oname","");
		if(oname.trim().equals(""))
		{
			error("请输出正确的持卡人姓名");
			return;
		}
		BankInfo bankinfo = BankInfo.dao.create(uid, bankname, bankno, oname);
		if(null == bankinfo)
		{
			error("数据异常，请联系客服");
			return;
		}
		success(bankinfo);
	}
	
	public void banks()
	{
		Long uid = getUid();
		if (uid == null || uid == 0) {
			error("请先登录");
			return;
		}

		success(BankInfo.dao.getList(uid));
	}
	
}