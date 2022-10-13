package com.common.home.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

public class CustomSecurityFilter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub
		String filterParam = filterConfig.getInitParameter("CustomSecurityFilter");
		System.out.println("FilterParam: "+filterParam);
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// TODO Auto-generated method stub		
		HttpServletResponse hsResponse = (HttpServletResponse)response;
		
		
		/* OWASP ZAP : ClickJacking  */
		hsResponse.addHeader("X-Frame-Options", "DENY");
		
		/* OWASP ZAP : X-Content-Type-Options*/
		//IE 표현 오류
		hsResponse.addHeader("X-Content-Type-Options","nosniff");
		//no-cache
		hsResponse.addHeader("Cache-Control", "no-cache, no-store, max-age=0, must-revalidate");
		hsResponse.addHeader("Pragma", "no-cache");
		
		
		chain.doFilter(request, hsResponse);
	}

	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		System.out.println("Destroy Filter : CustomSecurityFilter");
	}
	
}
