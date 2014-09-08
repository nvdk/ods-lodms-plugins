package com.tenforce.lodms.extractors;

import at.punkt.lodms.spi.extract.ExtractException;
import com.tenforce.lodms.ODSVoc;
import com.tenforce.lodms.extractors.models.Catalog;
import com.tenforce.lodms.extractors.models.CkanDataSetList;
import com.tenforce.lodms.extractors.utils.MapToRdfConverter;
import org.apache.log4j.Logger;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.springframework.http.HttpMethod;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class CkanHarvester {
  private static final ValueFactory valueFactory = ValueFactoryImpl.getInstance();
  protected Logger logger = Logger.getLogger(CkanHarvester.class);

  private String subjectPrefix;
  private String predicatePrefix;
  private String apiUri;
  private String baseUri;
  private List<String> ignoredKeys;
  private RDFHandler handler;
  private boolean enableProvenance = true;
  private String catalogOwner = "";
  private List<String> warnings = new ArrayList<String>();
  private String catalogTitle;
  private String catalogDescription;
  private String license;
  private HttpMethod httpMethod;

  public CkanHarvester(String baseUri, String subjectPrefix, String predicatePrefix, RDFHandler handler, HttpMethod httpMethod) {
    this.subjectPrefix = subjectPrefix;
    this.predicatePrefix = predicatePrefix;
    this.handler = handler;
    this.baseUri = baseUri;
    this.httpMethod = httpMethod;
    apiUri = baseUri.endsWith("/") ? baseUri + "api/3/" : baseUri + "/api/3/";
  }

  public void setIgnoredKeys(List<String> ignoredKeys) {
    this.ignoredKeys = ignoredKeys;
  }

  private void addCatalogProvenance() throws RDFHandlerException, DatatypeConfigurationException {
    URI source = valueFactory.createURI(baseUri);
    handler.handleStatement(new StatementImpl(source, ODSVoc.ODS_HARVEST_DATE, valueFactory.createLiteral(getXMLNow())));
    handler.handleStatement(new StatementImpl(source, ODSVoc.RDFTYPE, ODSVoc.DCAT_CATALOG));

    if (!catalogOwner.isEmpty())
      handler.handleStatement(new StatementImpl(source, ODSVoc.DCT_PUBLISHER, valueFactory.createLiteral(catalogOwner)));
    if (catalogDescription != null && !catalogDescription.isEmpty())
      handler.handleStatement(new StatementImpl(source, ODSVoc.DCT_DESCRIPTION, valueFactory.createLiteral(catalogDescription)));
    if (catalogTitle != null && !catalogTitle.isEmpty())
      handler.handleStatement(new StatementImpl(valueFactory.createURI(baseUri), ODSVoc.DCT_TITLE, valueFactory.createLiteral(catalogTitle)));
    if (license != null && !license.isEmpty())
      handler.handleStatement(new StatementImpl(valueFactory.createURI(baseUri), ODSVoc.DCT_LICENSE, valueFactory.createLiteral(license)));
  }

  public void harvest() throws RDFHandlerException, ExtractException, DatatypeConfigurationException {
    List<String> datasetIds = CkanDataSetList.getPackageIds(apiUri, httpMethod);
    if (datasetIds.isEmpty())
      throw new ExtractException("no datasets found in packageList: " + apiUri);

    harvest(datasetIds);
  }

  public void harvest(List<String> datasetIds) throws RDFHandlerException, ExtractException, DatatypeConfigurationException {
    if (datasetIds.isEmpty()) {
      throw new ExtractException("no datasets specified");
    }
    if (enableProvenance)
      addCatalogProvenance();

    MapToRdfConverter converter = new MapToRdfConverter(predicatePrefix, ignoredKeys, handler);
    ExecutorService executorService = Executors.newFixedThreadPool(5);
    CountDownLatch barrier = new CountDownLatch(datasetIds.size());
    Catalog catalog = new Catalog(baseUri, subjectPrefix);

    try {
      for (String datasetId : datasetIds) {
        executorService.execute(new DataSetHarvester(catalog, converter, handler, apiUri, datasetId, barrier, warnings, httpMethod));
      }
      executorService.shutdown();
      barrier.await();
    } catch (Exception e) {
      executorService.shutdownNow();
      throw new ExtractException(e.getMessage(), e);
    }

  }

  private static XMLGregorianCalendar getXMLNow() throws DatatypeConfigurationException {
    GregorianCalendar gregorianCalendar = new GregorianCalendar();
    DatatypeFactory datatypeFactory;
    datatypeFactory = DatatypeFactory.newInstance();
    return datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
  }

  public void setEnableProvenance(boolean enableProvenance) {
    this.enableProvenance = enableProvenance;
  }

  public void setCatalogOwner(String catalogOwner) {
    this.catalogOwner = catalogOwner;
  }

  public void setWarnings(List<String> warnings) {
    this.warnings = warnings;
  }

  public void setCatalogTitle(String catalogTitle) {
    this.catalogTitle = catalogTitle;
  }

  public void setCatalogDescription(String catalogDescription) {
    this.catalogDescription = catalogDescription;
  }

  public void setLicense(String license) {
    this.license = license;
  }
}
