package com.tenforce.lodms.transformers;

import at.punkt.lodms.base.TransformerBase;
import at.punkt.lodms.integration.ConfigBeanProvider;
import at.punkt.lodms.integration.ConfigurationException;
import at.punkt.lodms.spi.transform.TransformContext;
import at.punkt.lodms.spi.transform.TransformException;
import com.tenforce.lodms.ODSVoc;
import com.vaadin.Application;
import com.vaadin.terminal.ClassResource;
import com.vaadin.terminal.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.Update;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.List;

public class ODSDCatGenerator extends TransformerBase<ODSDcatGeneratorConfig> implements ConfigBeanProvider<ODSDcatGeneratorConfig> {
    private static final String odsUrl = "http://data.opendatasupport.eu/";
    private static final ValueFactory valueFactory = ValueFactoryImpl.getInstance();

    private String catalogIdentifier = "";

    @Override
    public void transform(Repository repository, URI graph, TransformContext context) throws TransformException {
        String catalogUrl = odsUrl + "id/catalog/" + catalogIdentifier + '/';
        context.getCustomData().put("dcatTransformerGraph", catalogUrl);
        Collection<String> warnings = context.getWarnings();
        URI catalogUri = valueFactory.createURI(catalogUrl);
        Value rawGraph = getRawGraph(repository, graph);
        if (rawGraph == null) {
            warnings.add("no catalog found");
            throw new TransformException("no catalog found in raw data");
        }

        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                connection.add(valueFactory.createStatement(catalogUri, ODSVoc.ODS_RAW_CATALOG, rawGraph), graph);
                connection.add(valueFactory.createStatement(catalogUri, ODSVoc.RDFTYPE, ODSVoc.DCAT_CATALOG), graph);
                copyCatalogAttributes(graph, catalogUri, rawGraph, connection);
                extractDatasetInfo(graph, catalogUri, rawGraph, connection);
            } catch (RepositoryException e) {
                warnings.add(e.getMessage());
                throw new TransformException(e.getMessage(), e);
            } catch (MalformedQueryException e) {
                warnings.add(e.getMessage());
                throw new TransformException(e.getMessage(), e);
            } catch (UpdateExecutionException e) {
                warnings.add(e.getMessage());
                throw new TransformException(e.getMessage(), e);
            } finally {
                connection.close();
            }
        } catch (Exception e) {
            throw new TransformException(e.getMessage(), e);
        }
    }

    private void extractDatasetInfo(URI graph, org.openrdf.model.Resource catalogUri, Value rawGraph, RepositoryConnection connection) throws RepositoryException, DatatypeConfigurationException {
        RepositoryResult<Statement> statements = connection.getStatements(null, ODSVoc.DCAT_CAT_PROP_DATASET, null, false, graph);
        Collection<Statement> statementList = statements.asList();
        for (Statement s : statementList) {
            Value rawDatasetUrl = s.getObject();
            String rawDatasetId = getRawDatasetId(rawDatasetUrl);
            URI catalogRecordUri = valueFactory.createURI(catalogUri.toString() + "record/" + rawDatasetId);
            URI harmonizedDatasetUri = valueFactory.createURI(catalogUri.toString() + "dataset/" + rawDatasetId);
            connection.add(valueFactory.createStatement(catalogUri, ODSVoc.DCAT_CAT_PROP_RECORD, catalogRecordUri), graph);
            connection.add(valueFactory.createStatement(catalogRecordUri, ODSVoc.FOAF_PRIMARYTOPIC, harmonizedDatasetUri), graph);
            connection.add(valueFactory.createStatement(catalogRecordUri, ODSVoc.RDFTYPE, ODSVoc.DCAT_CATALOGRECORD), graph);
            connection.add(valueFactory.createStatement(catalogRecordUri, ODSVoc.ODS_RAW_DATASET, rawDatasetUrl), graph);
            connection.add(valueFactory.createStatement(catalogRecordUri, ODSVoc.DCT_MODIFIED, valueFactory.createLiteral(getXMLNow())));
            connection.add(valueFactory.createStatement(harmonizedDatasetUri, ODSVoc.RDFTYPE, ODSVoc.DCAT_DATASET), graph);
            connection.add(valueFactory.createStatement(catalogUri, ODSVoc.DCAT_CAT_PROP_DATASET, harmonizedDatasetUri), graph);

        }
        statements.close();
    }

    private String getRawDatasetId(Value rawDatasetUrl) {
        return getRawDatasetId(rawDatasetUrl.stringValue());
    }

    private String getRawDatasetId(String dataset) {
        if (dataset.endsWith("/")) {
            System.out.println(dataset);
            return getRawDatasetId(dataset.substring(0,dataset.length() - 1));
        }

        int datasetOffset = dataset.lastIndexOf("dataset/") ;
        if (datasetOffset >= 0) {
            return dataset.substring(datasetOffset + 8);
        }
        return dataset.substring(dataset.lastIndexOf("/") + 1);
    }

    private static XMLGregorianCalendar getXMLNow() throws DatatypeConfigurationException {
        GregorianCalendar gregorianCalendar = new GregorianCalendar();
        DatatypeFactory datatypeFactory = null;
        datatypeFactory = DatatypeFactory.newInstance();
        return datatypeFactory.newXMLGregorianCalendar(gregorianCalendar);
    }

    private void copyCatalogAttributes(Value graph, Value catalogUri, Value rawGraph, RepositoryConnection connection) throws RepositoryException, MalformedQueryException, UpdateExecutionException {
        String query = "insert into ?graph { ?catalogUri ?p  ?t. }\n" +
                "                    where { graph ?graph {\n" +
                "                        ?rawCatalogUri ?p ?t." +
                "}}";
        Update u = connection.prepareUpdate(QueryLanguage.SPARQL, query);
        u.setBinding("catalogUri", catalogUri);
        u.setBinding("graph", graph);
        u.setBinding("rawCatalogUri", rawGraph);
        u.setBinding("p", ODSVoc.DCT_PUBLISHER);
        u.execute();
        u.setBinding("p", ODSVoc.DCT_DESCRIPTION);
        u.execute();
        u.setBinding("p", ODSVoc.DCT_TITLE);
        u.execute();
    }

    private Value getRawGraph(Repository repository, URI graph) throws TransformException {
        RepositoryResult<Statement> s = null;
        try {
            RepositoryConnection connection = repository.getConnection();
            try {
                List<Statement> catalogStatement = connection.getStatements(null, ODSVoc.RDFTYPE, ODSVoc.DCAT_CATALOG, false, graph).asList();
                if (catalogStatement.isEmpty())
                    return null;
                return catalogStatement.get(0).getSubject();
            } catch (RepositoryException e) {
                connection.close();
                return null;
            }
        } catch (RepositoryException e) {
            throw new TransformException(e.getMessage(), e);
        }
    }

    @Override
    public String getName() {
        return "ODS DCAT Application Profile Harmonizer";
    }

    @Override
    public String getDescription() {
        return "Add this plugin to a DCAT harmonization pipeline to create an initial DCAT structure for each dataset in the pipeline.";
    }

    @Override
    public Resource getIcon(Application application) {
        return new ClassResource("/com/tenforce/lodms/transform/ods.png", application);
    }

    @Override
    public String asString() {
        return getName();
    }

    @Override
    public ODSDcatGeneratorConfig newDefaultConfig() {
        return new ODSDcatGeneratorConfig();
    }

    @SuppressWarnings("ParameterHidesMemberVariable")
    @Override
    protected void configureInternal(ODSDcatGeneratorConfig config) throws ConfigurationException {
        catalogIdentifier = config.getCatalogIdentifier();
    }

}
