package n.fw.alipay;

import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.domain.AlipayTradeAppPayModel;
import com.alipay.api.domain.AlipayTradeWapPayModel;
import com.alipay.api.request.AlipayOpenPublicTemplateMessageIndustryModifyRequest;
import com.alipay.api.request.AlipayTradeAppPayRequest;
import com.alipay.api.request.AlipayTradeWapPayRequest;
import com.alipay.api.response.AlipayTradeAppPayResponse;

public class AlipayDealer
{
    public static String pay(String trade_no, String subject, String money, String body)
    {
        if (body == null)
        {
            body = "buy product";
        }
        // 超时时间 可空
       String timeout_express="2m";
        // 销售产品码 必填
        String product_code="QUICK_WAP_WAY";
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签     
        //调用RSA签名方式
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, AlipayConfig.APPID, AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
        AlipayTradeWapPayRequest alipay_request=new AlipayTradeWapPayRequest();
        
        // 封装请求支付信息
        AlipayTradeWapPayModel model=new AlipayTradeWapPayModel();
        model.setOutTradeNo(trade_no);
        model.setSubject(subject);
        model.setTotalAmount(money);
        model.setBody(body);
        model.setTimeoutExpress(timeout_express);
        model.setProductCode(product_code);
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(AlipayConfig.notify_url);
        // 设置同步地址
        alipay_request.setReturnUrl(AlipayConfig.return_url);   
        
        // form表单生产
        String form = "";
        try {
            // 调用SDK生成表单
            form = client.pageExecute(alipay_request).getBody();
            
            return form;
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return "";
    }
    
    
    public static String appPay(String trade_no, String subject, String money, String body)
    {
        if (body == null)
        {
            body = "buy product";
        }
        // 超时时间 可空
       String timeout_express="2m";
        // 销售产品码 必填
        String product_code="QUICK_MSECURITY_WAY";
        /**********************/
        // SDK 公共请求类，包含公共请求参数，以及封装了签名与验签，开发者无需关注签名与验签     
        //调用RSA签名方式
        AlipayClient client = new DefaultAlipayClient(AlipayConfig.URL, AlipayConfig.APPID, AlipayConfig.RSA_PRIVATE_KEY, AlipayConfig.FORMAT, AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY,AlipayConfig.SIGNTYPE);
       //实例化具体API对应的request类,类名称和接口名称对应,当前调用接口名称：alipay.open.public.template.message.industry.modify 
        AlipayTradeAppPayRequest alipay_request = new AlipayTradeAppPayRequest();
        
        // 封装请求支付信息
        AlipayTradeAppPayModel model=new AlipayTradeAppPayModel();
        model.setOutTradeNo(trade_no);
        model.setSubject(subject);
        model.setTotalAmount(money);
        model.setBody(body);
        model.setTimeoutExpress(timeout_express);
        model.setProductCode(product_code);
        alipay_request.setBizModel(model);
        // 设置异步通知地址
        alipay_request.setNotifyUrl(AlipayConfig.notify_url);
        // 设置同步地址
        alipay_request.setReturnUrl(AlipayConfig.return_url);   
        
        // form表单生产
        String form = "";
        try {
            // 调用SDK生成表单
            //form = client.pageExecute(alipay_request).getBody();
            form = client.sdkExecute(alipay_request).getBody();
            return form;
        } catch (AlipayApiException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } 
        return "";
    }
    
}