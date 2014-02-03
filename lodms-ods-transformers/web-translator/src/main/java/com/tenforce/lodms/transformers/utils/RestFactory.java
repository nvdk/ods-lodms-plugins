package com.tenforce.lodms.transformers.utils;

import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class RestFactory {
  public static RestTemplate getRest() {
    RestTemplate rest = new RestTemplate();
    List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
    converters.add(new Jaxb2RootElementHttpMessageConverter());
    converters.add(new FormHttpMessageConverter());
    converters.add(new MappingJacksonHttpMessageConverter());
    rest.setMessageConverters(converters);
    return rest;
  }
}

