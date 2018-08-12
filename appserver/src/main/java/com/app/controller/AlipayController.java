package com.app.controller;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.alipay.api.internal.util.AlipaySignature;
import com.app.model.Order;
import com.app.model.Product;
import com.app.model.Sheep;
import com.app.model.User;
import com.app.model.UserSheep;

import org.apache.commons.lang3.StringUtils;

import n.fw.alipay.AlipayConfig;
import n.fw.alipay.AlipayDealer;
import n.fw.base.BaseController;
import n.fw.utils.DateUtils;

public class AlipayController extends BaseController {
    public void payNotify() {
        Map<String, String> params = new HashMap<String, String>();
        Map requestParams = getParaMap();
        for (Iterator iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i] : valueStr + values[i] + ",";
            }
            // 乱码解决，这段代码在出现乱码时使用。如果mysign和sign不相等也可以使用这段代码转化
            // valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        // 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以上仅供参考)//
        // 计算得出通知验证结果
        // boolean AlipaySignature.rsaCheckV1(Map<String, String> params, String
        // publicKey, String charset, String sign_type)
        boolean verify_result = false;
        String out_trade_no = null;
        String trade_status = null;
        Float amount = 0F;
        try {
            // 获取支付宝的通知返回参数，可参考技术文档中页面跳转同步通知参数列表(以下仅供参考)//
            // 商户订单号
            out_trade_no = new String(getRequest().getParameter("out_trade_no").getBytes("ISO-8859-1"), "UTF-8");
            // 支付宝交易号
            // String trade_no = new
            // String(request.getParameter("trade_no").getBytes("ISO-8859-1"), "UTF-8");
            // 交易状态
            trade_status = new String(getRequest().getParameter("trade_status").getBytes("ISO-8859-1"), "UTF-8");
            verify_result = AlipaySignature.rsaCheckV1(params, AlipayConfig.ALIPAY_PUBLIC_KEY, AlipayConfig.CHARSET, AlipayConfig.SIGNTYPE);

            System.out.println("verify " + verify_result);
            amount = Float
                    .parseFloat(new String(getRequest().getParameter("total_amount").getBytes("ISO-8859-1"), "UTF-8"));
        } catch (Exception e) {
            renderText("");
            return;
        }

        HttpServletResponse response = getResponse();
        response.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
        try {
            if (verify_result)
             {// 验证成功
                System.out.println("verify success");
                if (StringUtils.equals(trade_status, "TRADE_SUCCESS"))
                {
                    String[] strs = out_trade_no.split("_");
                    if (strs.length == 3) {
                    	if(Integer.parseInt(strs[2]) >0)
                    	{
                    		UserSheep us = UserSheep.dao.findByOid(Long.parseLong(strs[1]));
                    		if(us != null)
                    		{
                    			us.set(UserSheep.PAYSTATUS, 1);
                                us.set(UserSheep.PAYTIME, DateUtils.getDateTime());
                                us.update();
                                response.getWriter().write("success");// 直接将完整的表单html输出到页面
                                response.getWriter().flush();
                                response.getWriter().close();
                                renderText("");
                                return;
                            }
                        }
                    	else
                    	{
                    		Order order = Order.dao.findByOid(Long.parseLong(strs[1]));
                            if (order != null) {
                                StringBuffer extra = new StringBuffer();
                                Long pid = order.getLong(Order.PID);
                                if (pid == 1)
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
                                order.set(Order.PAYMENT, amount);
                                order.set(Order.EXTRA, extra.toString());
                                order.set(Order.STATE, 2);
                                order.set(Order.PAYSTATE, 1);
                                order.set(Order.PAY_TIME, DateUtils.getDateTime());
                                order.update();
        
                                Product product = Product.dao.findProduct(pid);
                                if (product != null) {
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
                                            user.set(User.ATNL, user.getFloat(User.ATNL) + gift*order.getInt(Order.COUNT));
                                            user.set(User.LP, user.getFloat(User.LP) + lp*order.getInt(Order.COUNT));
                                            user.update();
                                        }
                                    }
                                }
        
                                response.getWriter().write("success");// 直接将完整的表单html输出到页面
                                response.getWriter().flush();
                                response.getWriter().close();
                                renderText("");
                                return;
                            }
                        }
                    }
                    	}
                    	
                        
                response.getWriter().write("fail");// 直接将完整的表单html输出到页面
                response.getWriter().flush();
                response.getWriter().close();
            } else {// 验证失败
                System.out.println("verify failed");

                response.getWriter().write("fail");// 直接将完整的表单html输出到页面
                response.getWriter().flush();
                response.getWriter().close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            renderText("");
            return;
        }
        renderText("");
    }

    public void returnurl() {
        // 获取支付宝GET过来反馈信息
        redirect("http://wx.atunala.com/reg/paysucc.html");
    }

    public void pay() {
        Long uid = getUid();
        
        Long oid = getParaToLong("oid", 0L);
        if (oid == 0L) {
            error("订单id不能为空");
            return;
        }
        Float fee = 0f;
        Integer otype = getParaToInt("otype", 0);
        if(otype > 0)
        {
        	UserSheep  us = UserSheep.dao.findByOid(oid);
        	fee = us.getFloat("paymoney");	
        }
        else
        {
	        Order order = Order.dao.findByOid(oid);
	        if (order == null) {
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
	        fee = order.getFloat(Order.REALPRICE);
	        if (fee <= 0)
	        {
	            error("不需要支付");
	            return;
	        }
        }
        String ret = AlipayDealer.pay("oid" + uid + "_" + oid+"_"+otype, "order", fee.toString(), "");
        // System.out.println("pay - " + DateUtils.getDateTime() + " pay id=" + oid + "
        // content : " + ret);

        try {
            HttpServletResponse response = getResponse();
            response.setContentType("text/html;charset=" + AlipayConfig.CHARSET);
            response.getWriter().write(ret);// 直接将完整的表单html输出到页面
            response.getWriter().flush();
            response.getWriter().close();
            renderText("");
        } catch (Exception e) {
            e.printStackTrace();
            renderText("");
        }
    }
    
    public void payByApp() {
        Long uid = getUid();
        
        Long oid = getParaToLong("oid", 0L);
        if (oid == 0L) {
            error("订单id不能为空");
            return;
        }
        Float fee = 0f;
        Integer otype = getParaToInt("otype", 0);
        if(otype > 0)
        {
        	UserSheep  us = UserSheep.dao.findByOid(oid);
        	fee = us.getFloat("paymoney");	
        }
        else
        {
	        Order order = Order.dao.findByOid(oid);
	        if (order == null) {
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
	        fee = order.getFloat(Order.REALPRICE);
	        if (fee <= 0)
	        {
	            error("不需要支付");
	            return;
	        }
        }
        String ret = AlipayDealer.appPay("oid" + uid + "_" + oid+"_"+otype, "order", fee.toString(), "");
        // System.out.println("pay - " + DateUtils.getDateTime() + " pay id=" + oid + "
        // content : " + ret);

        try {
            HttpServletResponse response = getResponse();
            response.getWriter().write(ret);// 直接将完整的表单html输出到页面
            response.getWriter().flush();
            response.getWriter().close();
            renderText("");
        } catch (Exception e) {
            e.printStackTrace();
            renderText("");
        }
        
    }
}