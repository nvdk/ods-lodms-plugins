package com.tenforce.lodms.extractors;

import org.openrdf.model.Statement;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

public class DataSetHarvester implements Callable<List<Statement>> {
    private RestTemplate rest;
    private String apiUri;
    private String dataSetId;
    private MapToRdfConverter converter;

    public DataSetHarvester(MapToRdfConverter converter, String apiUri, String dataSetId) {
        this.apiUri = apiUri;
        this.dataSetId = dataSetId;
        this.converter = converter;
        rest = RestTemplateFactory.getRestTemplate();
    }

    @Override
    public List<Statement> call() throws HttpClientErrorException, ResourceAccessException {
        HashMap map = new HashMap<String, String>();
        map.put("id", dataSetId);
        HttpEntity<?> httpEntity = new HttpEntity<Object>(map, RestTemplateFactory.getHttpHeaders());

        try {
            CkanDataSet dataSet = rest.postForObject(apiUri + "action/package_show", httpEntity, CkanDataSet.class);
            converter.setMap(dataSet.getResult());
            return converter.convert();
        } catch (HttpClientErrorException e) {
            return Collections.emptyList();
        }
    }
}
