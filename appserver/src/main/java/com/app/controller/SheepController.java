package com.app.controller;


import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.spongycastle.util.encoders.Base64;

import com.app.model.AtnlAddRecord;
import com.app.model.BasicRecord;
import com.app.model.DistanceRecord;
import com.app.model.EventRecord;
import com.app.model.GpsRecord;
import com.app.model.Sheep;
import com.app.model.StepRecord;
import com.app.model.User;
import com.app.model.UserSheep;

import io.nebulas.account.AccountManager;
import io.nebulas.client.NebulasClient;
import io.nebulas.client.api.request.GetAccountStateRequest;
import io.nebulas.client.api.request.SendRawTransactionRequest;
import io.nebulas.client.api.response.AccountState;
import io.nebulas.client.api.response.RawTransaction;
import io.nebulas.client.api.response.Response;
import io.nebulas.client.impl.HttpNebulasClient;
import io.nebulas.core.Address;
import io.nebulas.core.Transaction;
import io.nebulas.core.TransactionBinaryPayload;
import n.fw.base.BaseController;
import n.fw.utils.DateUtils;
import n.fw.utils.FileUtils;
import net.sf.json.JSONObject;


public class SheepController extends BaseController
{	

	/**
	 * 获取未售出药膏列表
	 * @param page
	 */
	public void getSheeps()
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
        Double sumatnl = 0d;
        float aarf = 0f;
        if(null != aar)
        {
        	sumatnl = aar.getAtnlSum(sheepid);
        	if(aar.getAtnlDay(sheepid) == null && aar.getAtnlNumDay(sheepid)  < 3)
        	{
        		aarf = this.addAAR(sheepid, 0,sumatnl,aar);
        	}
        }
        else
        {
        	aarf = this.addAAR(sheepid, 1,sumatnl,null);
        }
        if(aarf > 0)
        {
        	User user = User.dao.findByUid(uid);
            if (user != null)
            {
                user.set(User.ATNL, user.getFloat(User.ATNL) + aarf);
                user.update();
            }
        }
        aar = AtnlAddRecord.dao.getLeastRecord(sheepid);
        if(null != aar)
        {
        	sumatnl = aar.getAtnlSum(sheepid);
        }
        map.put("aar", aar);
        map.put("sumatnl", sumatnl);
        map.put("dr", dr);
        map.put("gr", gr);
        map.put("er", er);  
        map.put("sr", sr);
        
        success(map);
	}
	
	private float addAAR(Long sheepid,int type,Double sumatnl,AtnlAddRecord aar)
	{			
		if(1 == type)
		{
			int days = DateUtils.differentDaysByMillisecond(new Date(), Sheep.dao.findById(sheepid).getDate(Sheep.PREKILLTIME));			
			BigDecimal b = new  BigDecimal((float)(new Random().nextInt(1500/days) + 1));  
			float f1 = b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue()+500;  
			AtnlAddRecord.dao.create(sheepid, f1);
			return f1;
		}
		else
		{
			int usnum = UserSheep.dao.getUserSheepNum();
			int addnum = AtnlAddRecord.dao.getSheepNum();
			if(addnum*2 <= usnum && new Random().nextInt(2) == 1)
			{
				int days = DateUtils.differentDaysByMillisecond(aar.getDate(AtnlAddRecord.RECORDTIME),Sheep.dao.findById(sheepid).getDate(Sheep.PREKILLTIME));
				int sa = (int) Math.ceil(sumatnl);
				BigDecimal b = new  BigDecimal((float)(new Random().nextInt((1500-sa)/days) + 1));  
				float f1 = b.setScale(2,BigDecimal.ROUND_HALF_UP).floatValue(); 
				AtnlAddRecord.dao.create(sheepid, f1);
				return f1;
			}
			else
			{
				AtnlAddRecord.dao.create(sheepid, 0l);
			}
			
		}
		return 0f;
		
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
	
	/**
     * 区块链数据上传
     */
    public void dataUpload()
    {
    	Long sheepid = getParaToLong("sheepid", 0l);
		if (sheepid <= 0)
        {
            error("该羊羔已经不存在了");
            return;
        }
		Sheep sheep = Sheep.dao.findById(sheepid);
		if(null == sheep)
		{
			error("该羊羔已经不存在了");
            return;
		}
		//success(sheep);
    	AccountManager manager;
		try {
			manager = new AccountManager();
		
	    	byte[] passphrase = "atnl123".getBytes();
	        // binary tx
	        int chainID = 1001; //1 mainet,1001 testnet, 100 default private
	        Address from;
	        Address to;
	        byte[] walletFile = "{\"version\":4,\"id\":\"56e8fc70-f676-4c8d-91a2-b19e49a6dc2b\",\"address\":\"n1bHcQ4UCeQb4JbsDDhKtHwwcXLKCCCWXDk\",\"crypto\":{\"ciphertext\":\"f12c2a7b41b40b63189f247e8b0b63932d2867f06eb1e758561ce597b0b2d2ae\",\"cipherparams\":{\"iv\":\"7a00c94de9adc98064baf78a4d451e2b\"},\"cipher\":\"aes-128-ctr\",\"kdf\":\"scrypt\",\"kdfparams\":{\"dklen\":32,\"salt\":\"7f48eebf5d571c924ff0a2170a3125baefd023c1ba352f9bc4f66b11324e1182\",\"n\":4096,\"r\":8,\"p\":1},\"mac\":\"f5db617f67d74835b82508b7e6cacfa557f2409b012b4fae2f567ca7fad360c9\",\"machash\":\"sha3256\"}}".getBytes();
	        from = manager.load(walletFile, passphrase);
	        if("".equals(sheep.getStr(Sheep.ADDRESS)) || null == sheep.getStr(Sheep.ADDRESS))
	    	{
	        	to = manager.newAccount(passphrase);
	        	walletFile = manager.export(from,passphrase);
	        	FileUtils.writeBytesToFile("/data/keystore/sheep/"+sheepid+".json", walletFile);
	    		sheep.set(Sheep.KPATH, "/data/keystore/sheep/"+sheepid+".json");
	    		sheep.set(Sheep.ADDRESS, to.string());
	    		sheep.update();
	    	}
	        else
	        {
	        	//walletFile = FileUtils.changeFileToByte(sheep.getStr(Sheep.KPATH));
	        	//from = manager.load(walletFile, passphrase);

	        	to = Address.ParseFromString(sheep.getStr(Sheep.ADDRESS).toString());
	        	
	        }
	        BigInteger value = new BigInteger("0");
	        NebulasClient nebulasClient = HttpNebulasClient.create("https://testnet.nebulas.io");
	        Response<AccountState> accountResponse = nebulasClient.getAccountState(new GetAccountStateRequest("n1bHcQ4UCeQb4JbsDDhKtHwwcXLKCCCWXDk"));        
	        long nonce = accountResponse.getResult().getNonce() + 1;
	        Transaction.PayloadType payloadType = Transaction.PayloadType.BINARY;
	        
	        Map<String, String> smallMap = new HashMap<String, String>(); 
	        smallMap.put("出生日期：", sheep.getDate(Sheep.BIRTHDAY)+"");
	        smallMap.put("源羊编号：", sheep.getStr(Sheep.SID));
	        BasicRecord br = BasicRecord.dao.getLeastRecord(sheepid);
	        String height = "";
	        String weight = ""; 
	        String recordtime = "";
	        if(null != br)
	        {
	        	height = br.getFloat(BasicRecord.HEIGHT)+"";
	        	weight = br.getFloat(BasicRecord.WEIGHT)+"";
	        	recordtime = br.getDate(BasicRecord.RECORDTIME).toString();
	        }
	        Long sumSteps = StepRecord.dao.getSum(sheepid);
	        smallMap.put("1:",height);
	        smallMap.put("2:", weight); 
	        smallMap.put("3:",recordtime);
	        smallMap.put("4:",DateUtils.getDateTime());
	        smallMap.put("5", sumSteps+"");
	        String s = "1:"+height+"|"
	        		+"2:"+weight+"|"
	        		+"3:"+recordtime+"|"
	        		+"4:"+DateUtils.getDateTime()//+"|"
	        		+"当前总活动量:"+sumSteps+"";
	        JSONObject jsonObject = JSONObject.fromObject(smallMap);
	        //Logger.getLogger("").error("json:"+jsonObject.toString());
	        byte[] payload = new TransactionBinaryPayload(s.getBytes()).toBytes();
	        //payload = new TransactionCallPayload("function", "get").toBytes();
	        BigInteger gasPrice = new BigInteger("1000000"); // 0 < gasPrice < 10^12
	        BigInteger gasLimit = new BigInteger("20000"); // 20000 < gasPrice < 50*10^9
	        Transaction tx = new Transaction(chainID, from, to, value, nonce, payloadType, payload, gasPrice, gasLimit);
	
	        manager.signTransaction(tx, passphrase);
	        byte[] rawData = tx.toProto();
	        String rawTransaction = Base64.toBase64String(rawData);
	
	        Response<RawTransaction> response = nebulasClient.sendRawTransaction(new SendRawTransactionRequest().setData(rawTransaction));
	        success(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
}
