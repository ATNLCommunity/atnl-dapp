package com.app.controller;

import com.app.model.SmsCode;
import com.app.model.User;

import org.apache.commons.lang3.StringUtils;

import n.fw.base.BaseController;
import n.fw.utils.CacheUtils;
import n.fw.utils.SmsUtils;
import net.sf.json.JSONObject;

public class CashController extends BaseController
{
    public void smscode()
	{
        success();
    }

    public void smscode1728()
    {
        Long uid = getUid();
        if (uid == 0)
        {
            errorForbidden();
            return;
        }

        User user = User.dao.findByUid(uid);
        if (user == null)
        {
            errorInvalid();
            return;
        }

		String phone = user.getStr(User.PHONE);
		if (StringUtils.isBlank(phone) || phone.length() != 11)
		{
			error("电话号码格式不对");
			return;
        }
        
        SmsCode sms = SmsCode.dao.findByPhone(phone, 2);
		if (sms != null && sms.getInt(SmsCode.COUNT) >= 5)
		{
			error("您已多次申请验证码，请联系客户");
			return;
		}

		SmsCode.dao.increase(phone, 2);

		String code = CacheUtils.rand();
		JSONObject json = new JSONObject();
		json.put("code", code);

		// SmsUtils.singleCallByTts("TTS_134317301", phone, json);
        //SmsUtils.singleCallByTts("TTS_133974695", phone, json);
        SmsUtils.sendSms("SMS_132240021", phone, json);
		CacheUtils.instance.set("sms_" + phone, code);
		success();
	}

    public void needcash()
    {
        float cash = getParaToInt("cash", 0);
        if (cash <= 0)
        {
            errorInvalid();
            return;
        }

        String code = getPara("code", "");
        if (StringUtils.isBlank(code))
        {
            error("验证码不能为空");
            return;
        }

        Long uid = getUid();
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

        String phone = user.getStr(User.PHONE);
		if (StringUtils.isBlank(phone))
		{
			error("电话号码格式不对");
			return;
		}

        //String smscode = CacheUtils.instance.get("sms_" + phone);
        String smscode = CacheUtils.instance.get("tts_" + phone);
		if (smscode == null || !StringUtils.equals(code, smscode))
		{
			error("验证码错误");
			return;
		}
		// CacheUtils.instance.set("sms_" + phone, null);
		CacheUtils.instance.set("tts_" + phone, null);
        
        float atnl = user.getFloat(User.ATNL);
        float needatnl = user.getFloat(User.NEEDATNL);

        if (cash > atnl)
        {
            error("ANTL 不足");
            return;
        }

        if (atnl < 1000)
        {
            error("可提现数量需要大于1000，才能提现");
            return;
        }

        user.set(User.NEEDATNL, needatnl + cash);
        user.set(User.ATNL, atnl - cash);
        user.update();
        success();
    }
}