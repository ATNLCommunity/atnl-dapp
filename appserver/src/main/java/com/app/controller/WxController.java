package com.app.controller;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;

import com.app.config.Constants;
import com.app.model.Order;
import com.app.model.Product;
import com.app.model.Sheep;
import com.app.model.User;
import com.github.wxpay.sdk.WXPayUtil;

import org.apache.commons.lang3.StringUtils;

import n.fw.base.BaseController;
import n.fw.utils.DateUtils;
import n.fw.utils.WxUtils;
import n.fw.wxpay.WXPayConfigImpl;
import n.fw.wxpay.WXPayDealer;
import net.sf.json.JSONObject;

public class WxController extends BaseController
{
    public void payNotify()
    {
        try {
            BufferedReader reader = getRequest().getReader();
            String line = "";
            StringBuffer inputStr = new StringBuffer();
            while ((line = reader.readLine()) != null)
            {
                inputStr.append(line);
            }

            String xmlString = inputStr.toString();
            System.out.println(xmlString);
            reader.close();

            System.out.println("----接收到的数据如下：---" + xmlString);
            Map<String, String> map = WXPayUtil.xmlToMap(xmlString);

            if (!WXPayUtil.isSignatureValid(map, WXPayConfigImpl.getInstance().getKey()))
            {
                System.out.println("error key");
                String returnResutStr ="<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[error]]></return_msg></xml>";
                renderText(returnResutStr);
                return;
            }

            String result_code = map.get("result_code");
            String out_trade_no = map.get("out_trade_no");

            System.out.println("result = " + result_code + " trade_no = " + out_trade_no);

            if (StringUtils.equals(result_code, "SUCCESS"))
            {
                String[] strs = out_trade_no.split("_");
                if (strs.length == 2) 
                {
                    Order order = Order.dao.findByOid(Long.parseLong(strs[1]));
                    if (order != null)
                    {
                        String total_fee = map.get("total_fee");
                        //String transaction_id = map.get("transaction_id");
                        StringBuffer extra = new StringBuffer();
                        Long pid = order.getLong(Order.PID);
                        if (pid == 1L)
                        {
                            List<Sheep> sheeps = Sheep.dao.getSheep(order.getInt(Order.COUNT));
                            for (Sheep sheep : sheeps) {
                                if (extra.length() > 0) {
                                    extra.append('|');
                                }
                                extra.append(sheep.getStr(Sheep.SID));
                                sheep.set(Sheep.STATE, 1);
                                sheep.update();
                            }
                        }
                        Float fee = Float.parseFloat(total_fee);
                        order.set(Order.PAYMENT, fee / 100);
                        order.set(Order.EXTRA, extra.toString());
                        order.set(Order.STATE, 2);
                        order.set(Order.PAYSTATE, 1);
                        order.set(Order.PAY_TIME, DateUtils.getDateTime());
                        order.update();

                        System.out.println("product pid = " + pid);

                        Product product = Product.dao.findProduct(pid);
                        if (product != null)
                        {
                            Integer productCount = product.getInt(Product.COUNT);
                            System.out.println("product count = " + productCount);
                            if (productCount > 0) {
                                System.out.println("product count new = " + (productCount - order.getInt(Order.COUNT)));
                                product.set(Product.COUNT, productCount - order.getInt(Order.COUNT));
                                product.update();
                            }

                            Float gift = product.getFloat(Product.GIFT);
                            Float lp = product.getFloat(Product.LP);
                            if (gift > 0 || lp > 0)
                            {
                                User user = User.dao.findByUid(order.getLong(Order.UID));
                                if (user != null)
                                {
                                    user.set(User.ATNL, user.getFloat(User.ATNL) + gift);
                                    user.set(User.LP, user.getFloat(User.LP) + lp);
                                    user.update();
                                }
                            }
                        }

                        String returnResutStr = "<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>";
                        System.out.println(returnResutStr);
                        renderText(returnResutStr);

                        return;
                    }
                }
            }

            String returnResutStr = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[error]]></return_msg></xml>";
            System.out.println(returnResutStr);
            renderText(returnResutStr);
            return;
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
            String returnResutStr = "<xml><return_code><![CDATA[FAIL]]></return_code><return_msg><![CDATA[error]]></return_msg></xml>";
            System.out.println(returnResutStr);
            renderText(returnResutStr);
        }
    }

    public void baseNotify()
    {
        String url = getPara("goto", "");
		String code = getPara("code");

		JSONObject json = WxUtils.getUserToken(code);

		String openid = json.getString("openid");
        setSessionAttr("openid", openid);

        redirect(url + "&openid=" + openid);
    }

    public void pay()
    {
        Long uid = getUid();
        
        Long oid = getParaToLong("oid", 0L);
        if (oid == 0L)
        {
            error("订单id不能为空");
            return;
        }

        Order order = Order.dao.findByOid(oid);
        if (order == null)
        {
            error("订单id不能为空");
            return;
        }

        Product product = Product.dao.findProduct(order.getLong(Order.PID));
        if (product == null)
        {
            error("商品已经下架");
            return;
        }

        Integer productCount = product.getInt(Product.COUNT);
        if (productCount >= 0 && order.getInt(Order.COUNT) > productCount)
        {
            error("库存不足");
            return;
        }

        String openid = getPara("openid", "");
        if (StringUtils.isBlank(openid))
        {
            openid = getSessionAttr("openid");
            if (StringUtils.isBlank(openid))
            {
                String url = WxUtils.wxBaseUrl(Constants.BASE_URL + "/wx/pay?oid=" + oid);
                redirect(url);
                return;
            }
        }

        System.out.println(order.getFloat(Order.REALPRICE));
        Integer fee = (int)(order.getFloat(Order.REALPRICE) * 100);
        if (fee <= 0)
        {
            error("不需要支付");
            return;
        }
        System.out.println("fee = " + fee.toString() + "  >> " + String.valueOf(fee));
        Map<String, String> ret = WXPayDealer.dao.doUnifiedOrder("oid" + uid + "_" + oid, openid, fee);
        String time =  System.currentTimeMillis() / 1000 + "";
        String prepay_id = ret.get("prepay_id");
        String nonce = WXPayUtil.generateNonceStr();
        String sign = WXPayDealer.dao.makeSign(prepay_id, nonce, time);
        
        String url = "http://wx.atunala.com/wx/pay.html?appid=" + WxUtils.APPID + "&time=" + time + "&nonce=" + nonce + "&prepay_id=" + prepay_id + "&sign=" + sign + "&oid=" + oid;
        System.out.println(url);
        redirect(url);
    }

    public void state()
    {
        String tradeId = getPara("oid", "");
        Map<String, String> ret = WXPayDealer.dao.doOrderQuery(tradeId);
        success(ret);
    }
}