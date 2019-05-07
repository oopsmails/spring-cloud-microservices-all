package com.oopsmails.spring.cloud.microservices.zuulproxy.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SEND_RESPONSE_FILTER_ORDER;

@Component
@Slf4j
public class LoggerPostFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return SEND_RESPONSE_FILTER_ORDER;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        switch (ctx.getResponseStatusCode()){
            case 500:
                log.error("Response Status {}, Path: {}",
                        ctx.getResponseStatusCode(),
                        ctx.getRequest().getRequestURI());
                break;
            case 302:
                log.warn("Response Status {}, Path: {}",
                        ctx.getResponseStatusCode(),
                        ctx.getRequest().getRequestURI());
                break;
            case 200:
            default:
                log.debug("Response Status {}", ctx.getResponseStatusCode());

        }
        return null;
    }

}
