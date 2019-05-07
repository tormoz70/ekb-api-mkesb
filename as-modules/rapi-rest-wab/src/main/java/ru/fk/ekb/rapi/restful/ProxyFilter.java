package ru.fk.ekb.rapi.restful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.bio4j.ng.service.types.WarSecurityFilterBase;

import javax.servlet.*;
import java.io.IOException;

public class ProxyFilter extends WarSecurityFilterBase implements Filter {
    private static final Logger LOG = LoggerFactory.getLogger(ProxyFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        super.doFilter(request, response, chain);
    }

    @Override
    public void destroy() {
        super.destroy();
    }

}
