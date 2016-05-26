package com.wintous.web.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LogInterceptor implements HandlerInterceptor {

	private final Logger log=LoggerFactory.getLogger(this.getClass());
	@Override
	public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
			throws Exception {
		log.debug(arg0.getRequestURI()+":end:{}",System.currentTimeMillis());
	}

	@Override
	public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
			throws Exception {
		String path=arg0.getContextPath();
		arg0.setAttribute("ctx",path);
	}

	@Override
	public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
		log.debug(arg0.getRequestURI()+":start:{}",System.currentTimeMillis());
		getReqClientInfo(arg0);
		return true;
	}
	
	/**
	 * 获取请求信息
	 * @param req
	 */
	public void getReqClientInfo(HttpServletRequest req){
		StringBuilder sb=new StringBuilder();
		//ip:url:contenttype:params
		sb.append(req.getRemoteHost()).append(":").append(req.getRequestURI()).append(":").append(req.getHeader("User-Agent"));
		sb.append(":").append(req.getQueryString());
		log.debug(sb.toString());
	}
	

}
