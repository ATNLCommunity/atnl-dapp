package n.fw.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import com.jfinal.core.JFinal;

/**
 * @author Tom
 */
public class HttpClientUtils {
	private static final int connectionTimeout = 60000;// 1分钟
	private static final int soTimeout = 60000;

	public static String getGetResponse(String url) {
		String response = "";
		GetMethod getMethod = new GetMethod(url);
		try {
			HttpClient client = new HttpClient();
			HttpConnectionManagerParams managerParams = client.getHttpConnectionManager().getParams();
			managerParams.setConnectionTimeout(connectionTimeout);
			managerParams.setSoTimeout(soTimeout);
			int status = client.executeMethod(getMethod);
			if (status == HttpStatus.SC_OK) {
				byte[] rbytes = getMethod.getResponseBody();
				response = new String(rbytes, "UTF-8");
			} else {
				//LogUtils.log4Error("getGetResponse error,statusCode=" + status + "url=" + url);
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			getMethod.releaseConnection();
		}
		return response;
	}
	
	public static String getGetResponse(String url, String encoding) {
		String response = "";
		GetMethod getMethod = new GetMethod(url);
		try {
			HttpClient client = new HttpClient();
			HttpConnectionManagerParams managerParams = client.getHttpConnectionManager().getParams();
			managerParams.setConnectionTimeout(connectionTimeout);
			managerParams.setSoTimeout(soTimeout);
			int status = client.executeMethod(getMethod);
			if (status == HttpStatus.SC_OK) {
				byte[] rbytes = getMethod.getResponseBody();
				response = new String(rbytes, encoding);
			} else {
				//LogUtils.log4Error("getGetResponse error,statusCode=" + status + "url=" + url);
			}
		} catch (HttpException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			getMethod.releaseConnection();
		}
		return response;
	}
	
	public static String getGetResponse(String url, Map<String, String> headers) {
		String html = "";
		HttpClient httpClient = new HttpClient();
		HttpConnectionManagerParams managerParams = httpClient.getHttpConnectionManager().getParams();
		managerParams.setConnectionTimeout(connectionTimeout);
		managerParams.setSoTimeout(soTimeout);
		GetMethod getMethod = new GetMethod(url);
		getMethod.setRequestHeader("accept", "*/*");
		getMethod.setRequestHeader("connection", "Keep-Alive");
		getMethod.setRequestHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
		getMethod.setRequestHeader("Accept-Language", "zh-cn,zh;q=0.5");
		if (headers != null && !headers.isEmpty())
		{
			for (String key : headers.keySet())
			{
				getMethod.setRequestHeader(key, headers.get(key).toString());
			}
		}
		// 执行postMethod
		try {
			int statusCode = httpClient.executeMethod(getMethod);
			// HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发
			// 301或者302
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				// 从头中取出转向的地址
				Header locationHeader = getMethod.getResponseHeader("location");
				String location = null;
				if (locationHeader != null) {
					location = locationHeader.getValue();
					System.out.println("The page was redirected to:" + location);
				} else {
					System.err.println("Location field value is null.");
				}
				return html;
			} else if(statusCode == HttpStatus.SC_OK) {
				BufferedReader in = null;
				try {
					in = new BufferedReader(new InputStreamReader(getMethod.getResponseBodyAsStream(), getMethod.getResponseCharSet()));
					StringBuffer sb = new StringBuffer();
					int chari;
					while ((chari = in.read()) != -1) {
						sb.append((char) chari);
					}
					html = sb.toString();
				} catch (RuntimeException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (in != null) {
						in.close();
					}
				}
			}else{
				System.err.println("Httpclient Request Bad, is Code : " + statusCode + ", Uri : " + url);
				return html;
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			getMethod.releaseConnection();
		}
		return html;
	}

	public static String getPostResponse(String url, Part[] parts) {
		HttpClient httpClient = new HttpClient();
		PostMethod postMethod = new UTF8PostMethod(url);
		String response = null;
		try {
			HttpConnectionManagerParams managerParams = httpClient.getHttpConnectionManager().getParams();
			managerParams.setConnectionTimeout(connectionTimeout);
			managerParams.setSoTimeout(soTimeout);
			postMethod.setRequestEntity(new MultipartRequestEntity(parts, postMethod.getParams()));
			httpClient.executeMethod(postMethod);
			if (postMethod.getStatusCode() == HttpStatus.SC_OK) {
				response = postMethod.getResponseBodyAsString();
			} else {
//				LogUtils.log4Error("getPostResponse error,statusCode=" + postMethod.getStatusCode() + "url=" + url + ";parts=" + ObjectUtils.toString(parts));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
//			LogUtils.log4Error("getPostResponse error,statusCode=" + postMethod.getStatusCode() + "url=" + url + ";parts=" + ObjectUtils.toString(parts), e);
		} catch (HttpException e) {
			e.printStackTrace();
//			LogUtils.log4Error("getPostResponse error,statusCode=" + postMethod.getStatusCode() + "url=" + url + ";parts=" + ObjectUtils.toString(parts), e);
		} catch (IOException e) {
			e.printStackTrace();
//			LogUtils.log4Error("getPostResponse error,statusCode=" + postMethod.getStatusCode() + "url=" + url + ";parts=" + ObjectUtils.toString(parts), e);
		} catch (Throwable e) {
			e.printStackTrace();
//			LogUtils.log4Error("getPostResponse error,statusCode=" + postMethod.getStatusCode() + "url=" + url + ";parts=" + ObjectUtils.toString(parts), e);
		} finally {
			postMethod.releaseConnection();
		}
		return response;
	}

	public static String getPostResponse(String url, Map<String, String> params, Map<String, Object> headers) {
		String html = "";
		HttpClient httpClient = new HttpClient();
		HttpConnectionManagerParams managerParams = httpClient.getHttpConnectionManager().getParams();
		managerParams.setConnectionTimeout(connectionTimeout);
		managerParams.setSoTimeout(soTimeout);
		PostMethod postMethod = new UTF8PostMethod(url);
		postMethod.setRequestHeader("accept", "*/*");
		postMethod.setRequestHeader("connection", "Keep-Alive");
		postMethod.setRequestHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
		postMethod.setRequestHeader("Accept-Language", "zh-cn,zh;q=0.5");
		if (headers != null && !headers.isEmpty())
		{
			for (String key : headers.keySet())
			{
				postMethod.setRequestHeader(key, headers.get(key).toString());
			}
		}
		// postMethod.setRequestHeader("Accept-Encoding", "gzip,deflate");
		// postMethod.setRequestHeader("Content-Type", "text/html;charset=utf-8");
		// 填入各个表单域的值
		Iterator<String> keys = params.keySet().iterator();
		List<NameValuePair> ps = new ArrayList<NameValuePair>();
		while (keys.hasNext()) {
			String key = keys.next();
			ps.add(new NameValuePair(key, params.get(key)));
		}
		NameValuePair[] data = ps.toArray(new NameValuePair[] {});
		// 将表单的值放入postMethod中
		postMethod.setRequestBody(data);
		// 执行postMethod
		try {
			int statusCode = httpClient.executeMethod(postMethod);
			// HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发
			// 301或者302
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				// 从头中取出转向的地址
				Header locationHeader = postMethod.getResponseHeader("location");
				String location = null;
				if (locationHeader != null) {
					location = locationHeader.getValue();
					System.out.println("The page was redirected to:" + location);
				} else {
					System.err.println("Location field value is null.");
				}
				return html;
			} else if(statusCode == HttpStatus.SC_OK) {
				BufferedReader in = null;
				try {
					in = new BufferedReader(new InputStreamReader(postMethod.getResponseBodyAsStream(), postMethod.getResponseCharSet()));
					StringBuffer sb = new StringBuffer();
					int chari;
					while ((chari = in.read()) != -1) {
						sb.append((char) chari);
					}
					html = sb.toString();
				} catch (RuntimeException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (in != null) {
						in.close();
					}
				}
			}else{
				System.err.println("Httpclient Request Bad, is Code : " + statusCode + ", Uri : " + url);
				return html;
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			postMethod.releaseConnection();
		}
		return html;
	}
	
	@SuppressWarnings("deprecation")
	public static String getPostResponse(String url, String params, Map<String, Object> headers) {
		String html = "";
		HttpClient httpClient = new HttpClient();
		HttpConnectionManagerParams managerParams = httpClient.getHttpConnectionManager().getParams();
		managerParams.setConnectionTimeout(connectionTimeout);
		managerParams.setSoTimeout(soTimeout);
		PostMethod postMethod = new UTF8PostMethod(url);
		postMethod.setRequestHeader("accept", "*/*");
		postMethod.setRequestHeader("connection", "Keep-Alive");
		postMethod.setRequestHeader("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
		postMethod.setRequestHeader("Accept-Language", "zh-cn,zh;q=0.5");
		if (headers != null && !headers.isEmpty())
		{
			for (String key : headers.keySet())
			{
				postMethod.setRequestHeader(key, headers.get(key).toString());
			}
		}
		// postMethod.setRequestHeader("Accept-Encoding", "gzip,deflate");
		// postMethod.setRequestHeader("Content-Type", "text/html;charset=utf-8");
		// 将表单的值放入postMethod中
		postMethod.setRequestBody(params);
		// 执行postMethod
		try {
			int statusCode = httpClient.executeMethod(postMethod);
			// HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发
			// 301或者302
			if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
				// 从头中取出转向的地址
				Header locationHeader = postMethod.getResponseHeader("location");
				String location = null;
				if (locationHeader != null) {
					location = locationHeader.getValue();
					System.out.println("The page was redirected to:" + location);
				} else {
					System.err.println("Location field value is null.");
				}
				return html;
			} else if(statusCode == HttpStatus.SC_OK){
				BufferedReader in = null;
				try {
					in = new BufferedReader(new InputStreamReader(postMethod.getResponseBodyAsStream(), postMethod.getResponseCharSet()));
					StringBuffer sb = new StringBuffer();
					int chari;
					while ((chari = in.read()) != -1) {
						sb.append((char) chari);
					}
					html = sb.toString();
				} catch (RuntimeException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					if (in != null) {
						in.close();
					}
				}
			}else if(statusCode == HttpStatus.SC_UNAUTHORIZED){
				System.err.println("Httpclient Request Bad, is Code : " + statusCode + ", Uri : " + url);
				return html = "401";
			}else{
				System.err.println("Httpclient Request Bad, is Code : " + statusCode + ", Uri : " + url);
				return html;
			}
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			postMethod.releaseConnection();
		}
		return html;
	}

	public static class UTF8PostMethod extends PostMethod {
		public UTF8PostMethod(String url) {
			super(url);
		}

		@Override
		public String getRequestCharSet() {
			return "UTF-8";
		}
	}

	public static String urlDecode(String string) {
		try {
			return URLDecoder.decode(string, JFinal.me().getConstants().getEncoding());
		} catch (UnsupportedEncodingException e) {
			//log.error("urlDecode is error", e);
		}
		return string;
	}

	public static String urlEncode(String string) {
		try {
			return URLEncoder.encode(string, JFinal.me().getConstants().getEncoding());
		} catch (UnsupportedEncodingException e) {
			//log.error("urlEncode is error", e);
		}
		return string;
	}
}
