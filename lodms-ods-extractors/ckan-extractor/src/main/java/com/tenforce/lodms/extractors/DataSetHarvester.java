package com.tenforce.lodms.extractors;

import com.tenforce.lodms.extractors.models.Catalog;
import com.tenforce.lodms.extractors.models.CkanDataSet;
import com.tenforce.lodms.extractors.utils.MapToRdfConverter;
import com.tenforce.lodms.extractors.utils.RestTemplateFactory;
import org.apache.log4j.Logger;
import org.openrdf.model.Statement;
import org.openrdf.rio.RDFHandler;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class DataSetHarvester implements Runnable {
  private RestTemplate rest = RestTemplateFactory.getRestTemplate();
  private String apiUri;
  private String dataSetId;
  private MapToRdfConverter converter;
  private Catalog catalog;
  private RDFHandler handler;
  private List<String> warnings;
  private CountDownLatch barrier;
  private Logger logger = Logger.getLogger(DataSetHarvester.class);

  public DataSetHarvester(Catalog catalog, MapToRdfConverter converter, RDFHandler handler, String apiUri, String dataSetId, CountDownLatch barrier, List<String> warnings) {
    this.apiUri = apiUri + "action/package_show?id={datasetId}";
    this.dataSetId = dataSetId;
    this.converter = converter;
    this.catalog = catalog;
    this.handler = handler;
    this.warnings = warnings;
    this.barrier = barrier;
  }

  @Override
  public void run() {
    try {
      HttpEntity<?> httpEntity = new HttpEntity<Object>(RestTemplateFactory.getHttpHeaders());
      ResponseEntity<CkanDataSet> dataSetResponseEntity = rest.exchange(apiUri, HttpMethod.GET, httpEntity, CkanDataSet.class, dataSetId);
      CkanDataSet dataSet = dataSetResponseEntity.getBody();
      if (dataSet.success) {
        converter.convert(dataSet.getResult(), catalog.generateDatasetUri(dataSetId).stringValue());
        for (Statement s : catalog.datasetProvenance(dataSetId)) {
          handler.handleStatement(s);
        }
      }
    } catch (Exception e) {
      warnings.add("Failed to retrieve dataset " + dataSetId + ": " + e.getMessage());
      logger.error(e.getMessage());
      logger.error(e.getStackTrace());
    } finally {
      barrier.countDown();
    }
  }
}
