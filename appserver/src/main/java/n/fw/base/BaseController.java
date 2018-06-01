package n.fw.base;

import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import n.fw.utils.Result;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by nijie on 2016/5/18.
 */
public class BaseController extends Controller {
	public int needLevel()
	{
		return 1;
	}

	public void success() {
		renderJson(new Result(StringUtils.EMPTY, new Object()));
	}

    public void success(Object object) {
		renderJson(new Result(StringUtils.EMPTY, object));
	}
	
    public void success(Long id) {
		JSONObject json = new JSONObject();
    	json.put("id", id);
    	success(json);
    }

    public void error(String message) {
        renderJson(new Result(message, new Object()));
    }
    public void error() {
        renderJson(new Result(Result.API_ERROR, new Object()));
    }
    
    public void errorInvalid(){
    	renderJson(new Result(Result.INVALID, new Object()));
    }
    
    public void errorInvalidOper(){
    	renderJson(new Result(Result.INVALID_OPERATION, new Object()));
    }
    
    public void errorForbidden(){
    	renderJson(new Result(Result.FORBIDDEN, new Object()));
    }
    
    public void errorReplace(){
    	renderJson(new Result(Result.REPLACE_OPERATION, new Object()));
    }
    
    public void tokenInvalid(){
    	renderJson(new Result(Result.SESSION_INVALID, new Object()));
    }

    public static String getRemoteIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Requested-For");
        if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Forwarded-For");
        }
        if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StrKit.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
    
    public void setUid(Long uid) {
		if (uid == 0) {
			removeSessionAttr("uid");
		}
		else {
			setSessionAttr("uid", uid);
		}
	}
    
	public Long getUid() {
		Long uid = getSessionAttr("uid");
		if (uid == null) {
			return 0L;
		}
		return uid;
	}
	
	public void setLevel(Integer level) {
		setSessionAttr("level", level);
	}
	
	public Integer getLevel() {
		Integer level = getSessionAttr("level");
		if (level == null) {
			return 0;
		}
		return level;
	}
	
	public boolean checkLevel(Integer level) {
		Integer lvl = getLevel();
		if (lvl < level) {
			return false;
		}
		
		return true;
	}
	
	public boolean checkValue(String value) {
		if (StringUtils.isBlank(value)) {
			setAttr("error", Result.INVALID);
			return false;
		}
		
		return true;
	}
	
	public boolean checkValue(Integer value) {
		if (value == 0) {
			setAttr("error", Result.INVALID);
			return false;
		}
		
		return true;
	}
	
	public boolean checkValue(Long value) {
		if (value == 0L) {
			setAttr("error", Result.INVALID);
			return false;
		}
		
		return true;
	}

	public String getRemoteClientIp(){
		HttpServletRequest request = getRequest();
		String ip = request.getHeader("x-forwarded-for"); 
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) 
		{ 
			ip = request.getHeader("Proxy-Client-IP"); 
		} 
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) 
		{ 
			ip = request.getHeader("WL-Proxy-Client-IP"); 
		} 
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) 
		{ 
			ip = request.getHeader("X-Real-IP"); 
		} 
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip))
		{ 
			ip = request.getRemoteAddr(); 
		} 
		return ip; 
	}
}
