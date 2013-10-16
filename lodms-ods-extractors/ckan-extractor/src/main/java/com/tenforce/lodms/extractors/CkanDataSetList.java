package com.tenforce.lodms.extractors;

import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CkanDataSetList extends JSONModel {
    private List<String> result;

    public List<String> getResult() {
        return Collections.unmodifiableList(result);
    }

    public void setResult(List<String> result) {
        this.result = result;
    }

    public static List<String> getPackageIds(String uri) {
        RestTemplate rest = RestTemplateFactory.getRestTemplate();
        Map<String, String> map = new HashMap<String, String>();
        HttpEntity<?> httpEntity = new HttpEntity<Object>(map, RestTemplateFactory.getHttpHeaders());
        try {
            CkanDataSetList dataSetList = rest.postForObject(uri + "action/package_list", httpEntity, CkanDataSetList.class);
            return dataSetList.getResult();
        } catch (HttpClientErrorException ignored) {
            return Collections.emptyList();
        } catch (HttpServerErrorException ignored) {
            return Collections.emptyList();
        }
    }

}