package com.tenforce.lodms.extractors;

import org.apache.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DataSetHarvester implements Runnable {
    private RestTemplate rest = RestTemplateFactory.getRestTemplate();
    private String apiUri;
    private String dataSetId;
    private MapToRdfConverter converter;
    private String subjectPrefix;
    private List<String> warnings;
    private CountDownLatch barrier;
    private Logger logger = Logger.getLogger(DataSetHarvester.class);

    public DataSetHarvester(CountDownLatch barrier, MapToRdfConverter converter, String apiUri, String subjectPrefix, String dataSetId, List<String> warnings) {
        this.apiUri = apiUri;
        this.dataSetId = dataSetId;
        this.converter = converter;
        this.subjectPrefix = subjectPrefix;
        this.warnings = warnings;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        try {
            HashMap map = new HashMap<String, String>();
            map.put("id", dataSetId);
            HttpEntity<?> httpEntity = new HttpEntity<Object>(map, RestTemplateFactory.getHttpHeaders());
            CkanDataSet dataSet = rest.postForObject(apiUri + "action/package_show", httpEntity, CkanDataSet.class);
            converter.convert(dataSet.getResult(), subjectPrefix + dataSetId);
        } catch (Exception e) {
            warnings.add("Failed to retrieve dataset " + dataSetId + ": " + e.getMessage());
            warnings.add("Failed to retrieve dataset " + dataSetId + ": " + e.getMessage());
        } finally {
            barrier.countDown();
        }
    }
}
