package com.app.controller;

import javax.servlet.http.HttpServletRequest;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class CommonInterceptor implements Interceptor {

//	@Override
	public void intercept(Invocation ai) {
		HttpServletRequest request = ai.getController().getRequest();
		String requestURI = request.getRequestURI();
		System.out.println("requestURI:" + requestURI);
		
		if (requestURI.contains("/user/login")) {
			ai.invoke();
			return;
		}

		Long uid = ai.getController().getSessionAttr("uid");
		if (uid == null) {
			return;
		}
		
		ai.invoke();
	}
}
