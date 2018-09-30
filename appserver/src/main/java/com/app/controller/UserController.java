package com.app.controller;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import com.app.config.Constants;
import com.app.model.Addr;
import com.app.model.Invite;
import com.app.model.PreSell;
import com.app.model.Quan;
import com.app.model.SmsCode;
import com.app.model.User;
import com.github.cage.Cage;
import com.github.cage.GCage;

import org.apache.commons.lang3.StringUtils;

import n.fw.base.BaseController;
import n.fw.utils.CacheUtils;
import n.fw.utils.DateUtils;
import n.fw.utils.MD5Util;
import n.fw.utils.SmsUtils;
import net.sf.json.JSONObject;

public class UserController extends BaseController {
	public void index() {
		error("test test test <br > test test");
	}

	public void invite()
	{
		success(Invite.dao.get());
	}

	public void login() {
		String phone = getPara("phone", "");
		String pwd = getPara("pwd", "");

		if (StringUtils.isBlank(phone) || StringUtils.isBlank(pwd)) {
			errorInvalid();
			return;
		}

		User user = User.dao.findByPhone(phone);
		if (user == null) {
			error("用户不存在");
			return;
		}

		if (!StringUtils.equals(pwd, user.getStr(User.PWD))) {
			error("密码错误");
			return;
		}

		if (user.getInt(User.STATE) >= 100)
		{
			error("用户被限制");
			return;
		}

		setSessionAttr("uid", user.get(User.ID));
		user.set(User.LOGINTOKEN, getSession().getId());
		user.update();
		user.put(User.PWD, null);
		success(user);
	}
	
	public void tokenLogin()
	{
		String token = getPara("token", "");
		if (StringUtils.isBlank(token)) {
			errorInvalid();
			return;
		}
		User user = User.dao.findByToken(token);
		if (user == null) {
			error("用户不存在");
			return;
		}
		setSessionAttr("uid", user.get(User.ID));
		user.set(User.LOGINTOKEN, getSession().getId());
		user.update();
		user.put(User.PWD, null);
		success(user);
	}

	public void smscode()
	{
		success();
	}

	public void smscode1728()
	{
		String phone = getPara("phone", "");
		if (StringUtils.isBlank(phone) || phone.length() != 11)
		{
			error("电话号码格式不对");
			return;
		}

		SmsCode sms = SmsCode.dao.findByPhone(phone, 1);
		if (sms != null && sms.getInt(SmsCode.COUNT) >= 5)
		{
			error("您已多次申请验证码，请联系客户");
			return;
		}

		SmsCode.dao.increase(phone, 1);

		String code = CacheUtils.rand();
		JSONObject json = new JSONObject();
		//json.put("product", "阿图纳拉牧业APP");
		json.put("code", code);

		SmsUtils.sendSms("SMS_132240021", phone, json);
		CacheUtils.instance.set("sms_" + phone, code);
		success();
	}

	public void smscode6624()
	{
		String phone = getPara("phone", "");
		String name = getPara("name", "");
		if (StringUtils.isBlank(phone) || phone.length() != 11 || StringUtils.isBlank(name))
		{
			error("电话号码格式不对");
			return;
		}

		JSONObject json = new JSONObject();
		//json.put("product", "阿图纳拉牧业APP");
		json.put("name", name);
		json.put("time", DateUtils.getDateTime());

		SmsUtils.sendSms("SMS_141596624", phone, json);
		success();
	}

	public void ttscode()
	{
		String phone = getPara("phone", "");
		if (StringUtils.isBlank(phone) || phone.length() != 11)
		{
			error("电话号码格式不对");
			return;
		}

		User user = User.dao.findByPhone(phone);
		if (user != null) {
			error("用户已存在");
			return;
		}

		SmsCode sms = SmsCode.dao.findByPhone(phone, 0);
		if (sms != null && sms.getInt(SmsCode.COUNT) >= 5)
		{
			error("您已多次申请验证码，请联系客户");
			return;
		}


		SmsCode.dao.increase(phone, 0);

		String code = CacheUtils.rand();
		JSONObject json = new JSONObject();
		//json.put("product", "阿图纳拉牧业APP");
		json.put("code", code);

		SmsUtils.sendSms("SMS_132240021", phone, json);

		//SmsUtils.sendSms("SMS_132240021", phone, json);
		//SmsUtils.singleCallByTts("TTS_134155538", phone, json);
		CacheUtils.instance.set("tts_" + phone, code);
		success();
	}

	public void register()
	{
		String phone = getPara("phone", "");
		String pwd = getPara("pwd", "");
		String code = getPara("code", "");
		String mail = getPara("mail", "");
		String tokenAddr = getPara("tokenaddr", "");
		Long inviteId = getParaToLong("inviteid", 0L);

		if (StringUtils.isBlank(phone) || StringUtils.isBlank(pwd) || StringUtils.isBlank(code)) {
			errorInvalid();
			return;
		}

		String smscode = CacheUtils.instance.get("tts_" + phone);
		if (smscode == null || !StringUtils.equals(code, smscode))
		{
			error("验证码错误");
			return;
		}
		CacheUtils.instance.set("tts_" + phone, null);

		Invite invite = Invite.dao.get();
		Integer count = invite.getInt(Invite.COUNT);
		if (count <= 0)
		{
			error("注册人数已经达到上限");
			return;
		}

		Float gift = invite.getFloat(Invite.REG);
		Float inviteGift = invite.getFloat(Invite.INVITE);

		User user = User.dao.findByPhone(phone);
		if (user != null) {
			error("账号存在");
			return;
		}
		else 
		{
			if (inviteId != 0L)
			{
				User inviter = User.dao.findByUid(inviteId);
				if (inviter == null) 
				{
					user = User.dao.create(phone, pwd, mail, tokenAddr, 0L, gift, getRemoteClientIp());
				}
				else
				{
					user = User.dao.create(phone, pwd, mail, tokenAddr, inviteId, gift, getRemoteClientIp());
					
					inviter.set(User.LOCKATNL, inviter.getFloat(User.LOCKATNL) + inviteGift);
					inviter.set(User.M31, inviter.getFloat(User.M31) + 1);
					inviter.update();
					//inviter.set(User.LOCKATNL, inviter.getFloat(User.LOCKATNL) + inviteGift);
					inviter.update();
					// reward(inviteId, inviteGift.intValue(), 1, 1, 6);
				}
			}
			else
			{
				user = User.dao.create(phone, pwd, mail, tokenAddr, 0L, gift, getRemoteClientIp());
			}

			PreSell presell = PreSell.dao.getPreSell(phone);
			if (presell != null)
			{
				if (presell.getInt(PreSell.STATE) == 0)
				{
					PreSell.dao.update(phone, 1);
					Quan.dao.create(user.getLong(User.ID), "88元抵288元预购码", 288F);
				}
			}

			invite.set(Invite.COUNT, count - 1);
			invite.update();
		}

		setSessionAttr("uid", user.get(User.ID));
		user.put(User.PWD, null);
		success(user);
	}

	public void forget()
	{
		String phone = getPara("phone", "");
		String pwd = getPara("pwd", "");
		String code = getPara("code", "");

		if (StringUtils.isBlank(phone) || StringUtils.isBlank(pwd) || StringUtils.isBlank(code)) {
			errorInvalid();
			return;
		}

		String smscode = CacheUtils.instance.get("sms_" + phone);
		if (smscode == null || !StringUtils.equals(code, smscode))
		{
			error("验证码错误");
			return;
		}

		CacheUtils.instance.set("sms_" + phone, null);

		User user = User.dao.findByPhone(phone);
		if (user == null) {
			error("用户不存在");
			return;
		}

		user.set(User.PWD, pwd);
		user.set(User.UPDATE_TIME, DateUtils.getDateTime());
		user.update();

		setSessionAttr("uid", user.get(User.ID));
		user.put(User.PWD, null);
		success(user);
	}

	public void tokenaddr()
	{
		String tokenAddr = getPara("tokenaddr", "");
		if (StringUtils.isBlank(tokenAddr))
		{
			error("token addr 不能为空");
			return;
		}
		Long uid = getSessionAttr("uid");
		if (uid == null || uid == 0) {
			errorInvalidOper();
			return;
		}

		User user = User.dao.findByUid(uid);
		if (user == null) {
			removeSessionAttr("uid");
			errorInvalid();
			return;
		}

		user.set(User.TOKENADDR, tokenAddr);
		user.update();

		user.put(User.PWD, null);
		success(user);
	}

	public void info() {
		Long uid = getSessionAttr("uid");
		if (uid == null || uid == 0) {
			errorInvalidOper();
			return;
		}

		User user = User.dao.findByUid(uid);
		if (user == null) {
			removeSessionAttr("uid");
			errorInvalid();
			return;
		}

		user.put(User.PWD, null);
		success(user);
	}
	
	public void logout() {
		removeSessionAttr("uid");
		success("ok");
	}

	public void unlock()
	{
		Long uid = getUid();
		if (uid == 0)
		{
			errorInvalidOper();
			return;
		}

		Integer atnl = getParaToInt("atnl", 0);
		if (atnl <= 0)
		{
			errorInvalid();
			return;
		}

		User user = User.dao.findByUid(uid);
		if (user == null)
		{
			errorInvalid();
			return;
		}

		Float userLp = user.getFloat(User.LP);
		Float lockAtnl = user.getFloat(User.LOCKATNL);
		if (userLp < atnl || lockAtnl < atnl)
		{
			error("拥有的令牌或者锁定币不足");
			return;
		}

		user.set(User.LP, userLp - atnl);
		user.set(User.LOCKATNL, lockAtnl - atnl);
		user.set(User.ATNL, user.getFloat(User.ATNL) + atnl);
		user.update();

		success(user);
	}

	public void invited()
	{
		Long uid = getSessionAttr("uid");
		if (uid == null || uid == 0) {
			errorInvalidOper();
			return;
		}

		List<User> arr = User.dao.findInvites(uid);
		success(arr);
	}

	public void addr()
	{
		Long uid = getSessionAttr("uid");
		if (uid == null || uid == 0) {
			errorInvalidOper();
			return;
		}

		success(Addr.dao.get(uid));
	}

	public void setaddr()
	{
		Long uid = getSessionAttr("uid");
		if (uid == null || uid == 0) {
			errorInvalidOper();
			return;
		}

		String name = getPara("name", "");
		String phone = getPara("phone", "");
		String addr = getPara("addr", "");

		Addr.dao.edit(uid, addr, name, phone);
		success();
	}

	private void reward(Long uid, Integer atnl, Integer idx, Integer start, Integer depth)
    {
        if (uid == 0 || atnl == 0)
        {
            return;
        }

        if (idx > depth)
        {
            return;
        }

        User user = User.dao.findByUid(uid);
        if (user == null)
        {
            return;
        }

        if (idx >= start)
        {
			switch (idx)
			{
				case 1:
				user.set(User.M1, user.getLong(User.M1) + 1);
				break;
				case 2:
				user.set(User.M2, user.getLong(User.M2) + 1);
				break;
				case 3:
				user.set(User.M3, user.getLong(User.M3) + 1);
				break;
				case 4:
				user.set(User.M4, user.getLong(User.M4) + 1);
				break;
				case 5:
				user.set(User.M5, user.getLong(User.M5) + 1);
				break;
				case 6:
				user.set(User.M6, user.getLong(User.M6) + 1);
				break;
				default:
				break;
			}
            user.set(User.LOCKATNL, user.getFloat(User.LOCKATNL) + atnl);
            user.update();
        }

        Long inviteid = user.getLong(User.INVITEID);
        if (inviteid != 0)
        {
            reward(inviteid, atnl / 2, idx + 1, start, depth);
        }
	}
	
	public void codeimg() {
		String phone = getPara("phone", "");
		if (StringUtils.isBlank(phone) || phone.length() != 11)
		{
			error("电话号码格式不对");
			return;
		}
		
		Cage cage = new GCage();
		String code = CacheUtils.rand();
		CacheUtils.instance.set("tts_" + phone, code);
		String fileName =  CacheUtils.rand() + "_" + System.currentTimeMillis();
		try {
			OutputStream os = new FileOutputStream(Constants.UPLOAD_DIR + fileName + ".jpg", false);
			cage.draw(code, os);
			os.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		} 
		success(fileName + ".jpg");
	}
}
