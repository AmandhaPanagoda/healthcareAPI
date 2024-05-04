package com.mycompany.healthcare.security;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.LogRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * RequestLoggingFilter logs incoming requests to the application, including the timestamp, IP address, HTTP method, and URI.
 * 
 * @author Amandha
 */
@WebFilter("/*")
public class RequestLoggingFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestLoggingFilter.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Initializes the filter.
     * @param filterConfig
     * @throws javax.servlet.ServletException
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
       
    }

    /**
     * Logs the incoming request.
     * @param request
     * @param response
     * @param chain
     * @throws java.io.IOException
     * @throws javax.servlet.ServletException
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        String timestamp = LocalDateTime.now().format(FORMATTER);
        String ip = httpRequest.getRemoteAddr();
        String method = httpRequest.getMethod();
        String uri = httpRequest.getRequestURI();

        LOGGER.info("[{}] Request from IP : {} - HTTP method {} - URI: {}", timestamp, ip, method, uri);

        chain.doFilter(request, response);
    }
    
    /**
     * Destroys the filter.
     */
    @Override
    public void destroy() {
      
    }
    
    /**
     * Determines if a log record should be logged.
     * @param record
     * @return 
     */
    public boolean isLoggable(LogRecord record) {
        return true; 
    }
}
