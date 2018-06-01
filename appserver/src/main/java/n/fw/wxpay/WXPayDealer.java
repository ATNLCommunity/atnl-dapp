package n.fw.wxpay;

import java.util.HashMap;
import java.util.Map;

import com.github.wxpay.sdk.WXPay;
import com.github.wxpay.sdk.WXPayUtil;

public class WXPayDealer
{
    private WXPayConfigImpl mConfig;
    private WXPay mWxpay;

    public static final WXPayDealer dao = new WXPayDealer();

    public WXPayDealer() {
        try 
        {
            mConfig = WXPayConfigImpl.getInstance();
            mWxpay = new WXPay(mConfig);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public Map<String, String> doUnifiedOrder(String tradeId, String openid, Integer money) {
        Map<String, String> data = new HashMap<String, String>();
        data.put("body", "阿图纳拉 - 微信商城");
        data.put("out_trade_no", tradeId);
        data.put("device_info", "");
        data.put("fee_type", "CNY");
        data.put("total_fee", money.toString());
        data.put("spbill_create_ip", mConfig.getServerIP());
        data.put("notify_url", mConfig.getNotifyUrl());
        data.put("trade_type", "JSAPI");
        data.put("openid", openid);

        try {
            Map<String, String> r = mWxpay.unifiedOrder(data);
            System.out.println(r);
            return r;
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

    public String makeSign(String prepayId, String nonce, String time)
    {
        Map<String, String> packageParams = new HashMap<String, String>();
		packageParams.put("appId", mConfig.getAppID());
		packageParams.put("timeStamp", time);
		packageParams.put("nonceStr", nonce);
		packageParams.put("package", "prepay_id=" + prepayId);
        packageParams.put("signType", "MD5");
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