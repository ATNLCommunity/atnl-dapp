package n.fw.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.QuerySendDetailsResponse;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsRequest;
import com.aliyuncs.dysmsapi.model.v20170525.SendSmsResponse;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsRequest;
import com.aliyuncs.dyvmsapi.model.v20170525.SingleCallByTtsResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;

/**
 * 工程依赖了2个jar包(存放在工程的libs目录下)
 * 1:aliyun-java-sdk-core.jar
 * 2:aliyun-java-sdk-dysmsapi.jar
 */
public class SmsUtils
{

    //产品名称:云通信短信API产品,开发者无需替换
    static final String product = "Dysmsapi";
    //产品域名,开发者无需替换
    static final String domain = "dysmsapi.aliyuncs.com";

    // TODO 此处需要替换成开发者自己的AK(在阿里云访问控制台寻找)
    static final String accessKeyId = "LTAIAQktDB4WyFqr";
    static final String accessKeySecret = "aKQH11C6X860fhY2YjWLKge4yo4lzX";

    public static SendSmsResponse sendSms(String tmp, String phone, Object content) {

        //可自助调整超时时间
        try {
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");
    
            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);
    
            //组装请求对象-具体描述见控制台-文档部分内容
            SendSmsRequest request = new SendSmsRequest();
            //必填:待发送手机号
            request.setPhoneNumbers(phone);
            //必填:短信签名-可在短信控制台中找到
            //request.setSignName("云通信");
            request.setSignName("阿图纳拉牧业");
            //必填:短信模板-可在短信控制台中找到
            request.setTemplateCode(tmp);
            //可选:模板中的变量替换JSON串,如模板内容为"亲爱的${name},您的验证码为${code}"时,此处的值为
            request.setTemplateParam(content.toString());
    
            //选填-上行短信扩展码(无特殊需求用户请忽略此字段)
            //request.setSmsUpExtendCode("90997");
    
            //可选:outId为提供给业务方扩展字段,最终在短信回执消息中将此值带回给调用者
            //request.setOutId("yourOutId");
    
            //hint 此处可能会抛出异常，注意catch
            SendSmsResponse sendSmsResponse = acsClient.getAcsResponse(request);

            /*
            JSONObject json = new JSONObject();
            json.put("content", content);
            json.put("ret", sendSmsResponse);
            System.out.println(json.toString());
            */
            return sendSmsResponse;   
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }

    public static SingleCallByTtsResponse singleCallByTts(String tmp, String phone, Object content)
    {
        try
        {
            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", "Dyvmsapi", "dyvmsapi.aliyuncs.com");
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象-具体描述见控制台-文档部分内容
            SingleCallByTtsRequest request = new SingleCallByTtsRequest();
            //必填-被叫显号,可在语音控制台中找到所购买的显号
            String[] sendPhones = {"04756550449", "04756550450", "04756550190"};
            int rand = new Random().nextInt(sendPhones.length);
            if (rand >= sendPhones.length)
            {
                rand = 0;
            }
            request.setCalledShowNumber(sendPhones[rand]);

            //必填-被叫号码
            request.setCalledNumber(phone);
            //必填-Tts模板ID 	TTS_133974695
            request.setTtsCode(tmp);
            //可选-当模板中存在变量时需要设置此值
            request.setTtsParam(content.toString());
            // 可选-音量 取值范围 0--200
            request.setVolume(180);
            // 可选-播放次数
            request.setPlayTimes(3);
            //hint 此处可能会抛出异常，注意catch
            SingleCallByTtsResponse singleCallByTtsResponse = acsClient.getAcsResponse(request);

            //System.out.println(singleCallByTtsResponse.toString());

            return singleCallByTtsResponse;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static QuerySendDetailsResponse querySendDetails(String bizId) {
        try {
            //可自助调整超时时间
            System.setProperty("sun.net.client.defaultConnectTimeout", "10000");
            System.setProperty("sun.net.client.defaultReadTimeout", "10000");

            //初始化acsClient,暂不支持region化
            IClientProfile profile = DefaultProfile.getProfile("cn-hangzhou", accessKeyId, accessKeySecret);
            DefaultProfile.addEndpoint("cn-hangzhou", "cn-hangzhou", product, domain);
            IAcsClient acsClient = new DefaultAcsClient(profile);

            //组装请求对象
            QuerySendDetailsRequest request = new QuerySendDetailsRequest();
            //必填-号码
            request.setPhoneNumber("15000000000");
            //可选-流水号
            request.setBizId(bizId);
            //必填-发送日期 支持30天内记录查询，格式yyyyMMdd
            SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd");
            request.setSendDate(ft.format(new Date()));
            //必填-页大小
            request.setPageSize(10L);
            //必填-当前页码从1开始计数
            request.setCurrentPage(1L);

            //hint 此处可能会抛出异常，注意catch
            QuerySendDetailsResponse querySendDetailsResponse = acsClient.getAcsResponse(request);

            return querySendDetailsResponse;
        } catch (Exception e) {
            //TODO: handle exception
            e.printStackTrace();
            return null;
        }
    }
}
