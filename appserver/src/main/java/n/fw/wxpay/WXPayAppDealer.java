package n.fw.wxpay;

import java.util.HashMap;
import java.util.Map;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;

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
    public String doAppUnifiedOrder(String tradeId, Integer money) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "阿图纳拉 - app商城");
        data.put("out_trade_no", tradeId);
        data.put("device_info", "");
        data.put("fee_type", "CNY");
        data.put("total_fee", money.toString());
        data.put("spbill_create_ip", mConfig.getServerIP());
        data.put("notify_url", mConfig.getNotifyUrl());
        data.put("trade_type", "APP");

        try {
            Map<String, String> r = mWxpay.unifiedOrder(data);
            JSONObject json = new JSONObject();
            if("SUCCESS".equals(r.get("return_code")))
            {
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

    public String makeSign(String prepayId, String nonce,String time)
    {
        Map<String, String> packageParams = new HashMap<String, String>();
		packageParams.put("appid", mConfig.getAppID());
		packageParams.put("partnerid", mConfig.getMchID());
		packageParams.put("prepayid", prepayId);
		packageParams.put("timestamp", time);
		packageParams.put("noncestr", nonce);
		packageParams.put("package", "Sign=WXPay");
        try
        {
            String packageSign = WXPayUtil.generateSignature(packageParams, mConfig.getKey());
            return packageSign;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }
}