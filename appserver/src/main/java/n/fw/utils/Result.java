package n.fw.utils;


/**
 * Created by Jerry on 15/4/4.
 */

public class Result {
	
	static public String FAIL = "操作失败";
	static public String INVALID = "无效的参数";
	static public String SYSTEM_ERROR = "服务异常";
	static public String ACCOUNT_ERROR = "账号或密码错误";
	static public String EXISTS_ACCOUNT = "账号已存在";
	static public String INVALID_OPERATION = "无效的操作";
	static public String REPLACE_OPERATION = "重复操作";
	static public String SESSION_INVALID = "登录失效";
	static public String TOKEN_INVALID = "无效的Token";
	static public String FORBIDDEN = "非法的操作";
	static public String API_ERROR = "服务繁忙";
	
    private String err;
    private Object data;

    public Result() {
        this.err = "";
        this.data = new Object();
    }

    public Result(String err, Object data) {
        this.err = err;
        this.data = data;
    }

    public String getErr() {
        return err;
    }

    public void setErr(String err) {
        this.err = err;
    }

    public Object getData() {
    	if (data == null)
		{
			data = new Object();
		}
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
