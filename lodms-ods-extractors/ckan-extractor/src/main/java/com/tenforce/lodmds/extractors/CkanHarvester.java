package com.tenforce.lodmds.extractors;

import at.punkt.lodms.spi.extract.ExtractException;
import com.tenforce.lodms.ODSVoc;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.springframework.web.client.RestTemplate;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class CkanHarvester {
    private static final ValueFactory valueFactory = ValueFactoryImpl.getInstance();

    private String subjectPrefix;
    private String predicatePrefix;
    private String apiUri;
    private String baseUri;
    private RestTemplate rest;
    private List<String> ignoredKeys;
    private RDFHandler handler;
    private boolean enableProvenance = true;
    private String catalogOwner = "";
    private List<String> warnings = new ArrayList<String>();
    private String catalogTitle;
    private String catalogDescription;
    private String license;

    public CkanHarvester(String baseUri, String subjectPrefix, String predicatePrefix, RDFHandler handler) {
        this.subjectPrefix = subjectPrefix;
        this.predicatePrefix = predicatePrefix;
        this.handler = handler;
        this.baseUri = baseUri;
        apiUri = baseUri.endsWith("/") ? baseUri + "api/3/" : baseUri + "/api/3/";
        rest = RestTemplateFactory.getRestTemplate();
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

    private void addDataSetProvenance(String datasetId) throws RDFHandlerException, DatatypeConfigurationException {
        URI source = valueFactory.createURI(baseUri);
        URI dataset = valueFactory.createURI(subjectPrefix + datasetId);
        handler.handleStatement(new StatementImpl(source, ODSVoc.DCAT_CAT_PROP_DATASET, valueFactory.createURI(subjectPrefix + datasetId)));
        handler.handleStatement(new StatementImpl(dataset, ODSVoc.RDFTYPE, ODSVoc.DCAT_DATASET));
        handler.handleStatement(new StatementImpl(dataset, ODSVoc.ODS_HARVEST_DATE, valueFactory.createLiteral(getXMLNow())));
    }

    public void harvest() throws RDFHandlerException, ExtractException, DatatypeConfigurationException {
        List<String> datasetIds = CkanDataSetList.getPackageIds(apiUri);
        if (datasetIds.isEmpty())
            throw  new ExtractException("no datasets found in packageList: " + apiUri);

        harvest(datasetIds);
    }

    public void harvest(List<String> datasetIds) throws RDFHandlerException, ExtractException, DatatypeConfigurationException {
        if (datasetIds.isEmpty()) {
            throw  new ExtractException("no datasets specified");
        }
        if (enableProvenance)
            addCatalogProvenance();

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        Collection<Future<List<Statement>>> futures = new ArrayList<Future<List<Statement>>>();

        for (String datasetId : datasetIds) {
            addDataSetProvenance(datasetId);
            MapToRdfConverter converter = new MapToRdfConverter(subjectPrefix + datasetId, predicatePrefix, ignoredKeys);
            converter.setSubjectPrefix(subjectPrefix + datasetId);
            DataSetHarvester harvester = new DataSetHarvester(converter, apiUri, datasetId);
            futures.add(executorService.submit(harvester));
        }
        for (Future<List<Statement>> future : futures) {
            try {
                List<Statement> statements = future.get();
                for (Statement statement : statements) {
                    handler.handleStatement(statement);
                }
            } catch (ExecutionException e) {
                warnings.add(e.getMessage());
            } catch (Exception e) {
                executorService.shutdownNow();
                throw new ExtractException(e.getMessage(), e);
            }
        }
        executorService.shutdown();
    }

    private static XMLGregorianCalendar getXMLNow() throws DatatypeConfigurationException {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        DatatypeFactory datatypeFactory = null;
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
