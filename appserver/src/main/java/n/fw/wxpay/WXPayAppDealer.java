package n.fw.wxpay;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Logger;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;
import com.jfinal.log.Log4jLog;

import n.fw.utils.MD5Util;
import net.sf.json.JSONObject;

public class WXPayAppDealer
{
    private WXPayAppConfigImpl mConfig;
    private WXPay mWxpay;

    public static final WXPayAppDealer dao = new WXPayAppDealer();

    public WXPayAppDealer() {
        try 
        {
            mConfig = WXPayAppConfigImpl.getInstance();
            mWxpay = new WXPay(mConfig);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 微信支付app
     * @param 
     * @return
     */
    public String doAppUnifiedOrder(String tradeId, Integer money,String clientIp) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "阿图纳拉 - app商城");
        data.put("out_trade_no", tradeId);
        data.put("device_info", "");
        data.put("fee_type", "CNY");
        data.put("total_fee", money.toString());
        data.put("spbill_create_ip", clientIp);
        data.put("notify_url", mConfig.getNotifyUrl());
        data.put("trade_type", "APP");

        try {
            Map<String, String> r = mWxpay.unifiedOrder(data);
            JSONObject json = new JSONObject();
            if("SUCCESS".equals(r.get("return_code")))
            {
            	//String nonce = WXPayUtil.generateNonceStr();
            	json.put("noncestr", r.get("nonce_str"));
                json.put("package", "Sign=WXPay");
                int time = (int) (System.currentTimeMillis()/1000);
                json.put("timestamp",time);
                json.put("appid", r.get("appid"));
                json.put("partnerid", r.get("mch_id"));
                json.put("prepayid", r.get("prepay_id"));
                String sign = makeSign(r.get("prepay_id"),r.get("nonce_str"),time+"");
                json.put("sign", sign);
                return json.toString();
            }
            else
            {
            	return r.get("return_code");
            }
            //System.out.println(r);
            //json = JSONObject.fromObject(r); 
            //return json.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public Map<String, String> doOrderQuery(String tradeId) {
        System.out.println("查询订单");
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("out_trade_no", tradeId);

        try {
            Map<String, String> r = mWxpay.orderQuery(data);
            System.out.println(r);
            return r;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String createSign(String characterEncoding,SortedMap<String,String> parameters){
        StringBuffer sb = new StringBuffer();
        Set es = parameters.entrySet();
        Iterator it = es.iterator();
        while(it.hasNext()) {
            Map.Entry entry = (Map.Entry)it.next();
            String k = (String)entry.getKey();
            Object v = entry.getValue();
            if(null != v && !"".equals(v)
                    && !"sign".equals(k) && !"key".equals(k)) {
                sb.append(k + "=" + v + "&");
            }
        }
        sb.append("key=" + mConfig.getKey());//最后加密时添加商户密钥，由于key值放在最后，所以不用添加到SortMap里面去，单独处理，编码方式采用UTF-8
        String sign = MD5Util.MD5Encode(sb.toString(), characterEncoding).toUpperCase();
        return sign;
    }

    public String makeSign(String prepayId, String nonce,String time)
    {
    	
    	String signstr= "appid=wxbc9ce0692eff95a2&noncestr="+nonce+"&package=Sign=WXPay&partnerid=1508784811&prepayid="+prepayId+"&timestamp="+time+"&key=atunalabkl1860924atunalaatunalaa";
    	Logger.getLogger("").error("signstr:"+signstr);
    	
    	String sign = MD5Util.MD5Encode(signstr, "UTF-8").toUpperCase();
        return sign;
    	/**SortedMap<String, String> signParams = new TreeMap<String, String>();
        signParams.put("appid", mConfig.getAppID());
        signParams.put("partnerid", mConfig.getMchID());
        signParams.put("prepayid", prepayId);
        signParams.put("timestamp", time);
        signParams.put("noncestr", nonce);
        signParams.put("package", "Sign=WXPay");
        try
        {
            String packageSign = this.createSign("UTF-8",signParams);
            return packageSign;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }**/
    }
}