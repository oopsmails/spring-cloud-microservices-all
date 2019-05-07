package com.oopsmails.spring.cloud.microservices.zuulproxy.filter;

import com.google.common.io.CharStreams;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.POST_TYPE;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.SEND_RESPONSE_FILTER_ORDER;

@Component
@Slf4j
public class ResourceLinksBaseUrlPostFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return POST_TYPE;
    }

    @Override
    public int filterOrder() {
        return SEND_RESPONSE_FILTER_ORDER-1;
    }

    @Override
    public boolean shouldFilter() {
        return RequestContext.getCurrentContext().getRequest().getRequestURI().contains("configuration/oopsmails");
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        try (InputStream responseDataStream = ctx.getResponseDataStream()) {
            InputStream inputStream = responseDataStream;
            if(ctx.getResponseGZipped()){
                inputStream = new GZIPInputStream(inputStream);
            }
            final String responseData = CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
            ctx.setResponseBody(responseData.replace("\"resourceLink\":\"\"", "\"resourceLink\":\"/api/resourceLink/replacement\""));
            ctx.setResponseGZipped(false);
            ctx.setResponseDataStream(new ByteArrayInputStream(responseData.getBytes()));
        } catch (IOException e) {
            log.error("Failed to get response of configuration call", e);
        }
        return null;
    }

}
