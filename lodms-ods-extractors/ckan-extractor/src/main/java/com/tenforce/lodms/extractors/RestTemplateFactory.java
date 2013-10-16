package com.tenforce.lodms.extractors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestTemplateFactory {
    private RestTemplateFactory() {
    }

    public static HttpHeaders getHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.<MediaType>asList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("User-Agent", "LODMS harvesting plugin");
        headers.setAcceptCharset(Arrays.<Charset>asList(Charset.forName("UTF-8")));
        return headers;
    }

    public static RestTemplate getRestTemplate() {
        synchronized (RestTemplateFactory.class) {
            RestTemplate rest = new RestTemplate();
            List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
            converters.add(new MappingJacksonHttpMessageConverter());
            rest.setMessageConverters(converters);
            return rest;
        }
    }
}
