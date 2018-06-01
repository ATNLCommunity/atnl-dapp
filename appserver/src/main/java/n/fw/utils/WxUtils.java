package n.fw.utils;

import com.app.config.Constants;

import org.apache.commons.lang3.StringUtils;

import net.sf.json.JSONObject;

public class WxUtils {
    public static final WxUtils Instance = new WxUtils();

    public static final String APPID = "wx05e8bef324d0c06b";
    public static final String APPSECRET = "fffa5ba5dff874ba9edb72bc98e4cc79";
    public static final String TOKEN = "qkl";

    private String mAccessToken = null;
    private Long mExpiredTime = 0L;

    public String getAccessToken() {
        Long now = System.currentTimeMillis() / 1000;
        if (now < mExpiredTime && StringUtils.isNotBlank(mAccessToken)) {
            return mAccessToken;
        }

        return "";
    }

    public static String wxBaseUrl(String url)
    {
        String authorize_url = "https://open.weixin.qq.com/connect/oauth2/authorize" + "?appid=" + APPID
            + "&redirect_uri={redirecturi}" + "&response_type=code" + "&scope=snsapi_base"
            + "&state=235#wechat_redirect";
        String redirectUrl = Constants.BASE_URL + "/wx/baseNotify?goto=" + url;
        redirectUrl = HttpClientUtils.urlEncode(redirectUrl);

        String toUrl = authorize_url.replace("{redirecturi}", redirectUrl);

        System.out.print(toUrl);
        return toUrl;
    }

    public static String wxUrl(String url) {
        String authorize_url = "https://open.weixin.qq.com/connect/oauth2/authorize" + "?appid={appid}"
            + "&redirect_uri={redirecturi}" + "&response_type=code" + "&scope=snsapi_userinfo"
            + "&state=235#wechat_redirect";
        String redirectUrl = Constants.BASE_URL + "/user/callback?goto=" + url;
        redirectUrl = HttpClientUtils.urlEncode(redirectUrl);

        String toUrl = authorize_url.replace("{redirecturi}", redirectUrl).replace("{appid}", APPID.trim());

        System.out.print(toUrl);

        return toUrl;
    }

    public static JSONObject getUserToken(String code) {
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token" + "?appid=" + APPID + "&secret=" + APPSECRET
                + "&code=" + code + "&grant_type=authorization_code";

        String jsonResult = null;
        try {
            jsonResult = HttpClientUtils.getGetResponse(url);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.print("usertoken:" + jsonResult);

        if (jsonResult == null)
        {
            return null;
        }

        JSONObject json = JSONObject.fromObject(jsonResult);

        return json;
    }

    public static JSONObject getUserInfo(String openid, String token)
    {
        String url = "https://api.weixin.qq.com/sns/userinfo?access_token={access_token}&openid={openid}&lang=zh_CN";

        String getInfoUrl = url.replace("{access_token}", token).replace("{openid}", openid);

        String jsonResult = null;
        try {
            jsonResult = HttpClientUtils.getGetResponse(getInfoUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.print("userinfo:" + jsonResult);
        if (jsonResult == null)
        {
            return null;
        }

        JSONObject json = JSONObject.fromObject(jsonResult);

        return json;
    }
}