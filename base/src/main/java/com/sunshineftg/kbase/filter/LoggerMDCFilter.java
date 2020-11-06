package com.sunshineftg.kbase.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.UUID;

@WebFilter(urlPatterns = "/*",filterName = "LoggerMDCFilter")
@Configuration
@Slf4j
public class LoggerMDCFilter  implements Filter {
    @Override
    public void init(FilterConfig filterConfig) {
        log.info("LoggerMDCFilter start");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        String reqId = request.getParameter("reqId");
        if (StringUtils.isEmpty(reqId)) {
            reqId = UUID.randomUUID().toString();
        }
        ThreadContext.put("req.id", reqId);
        // ThreadContext.put("req.requestURIWithQueryString", ((HttpServletRequest) request).getRequestURI() + (((HttpServletRequest) request).getQueryString() == null ? "" : "?" + ((HttpServletRequest) request).getQueryString()));
        chain.doFilter(request, response);
    }

}
