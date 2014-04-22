package com.tenforce.lodms.extractors.models;

import com.tenforce.lodms.ODSVoc;
import org.openrdf.model.Literal;
import org.openrdf.model.Model;
import org.openrdf.model.URI;
import org.openrdf.model.impl.LinkedHashModel;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.model.impl.ValueFactoryImpl;
import org.openrdf.model.vocabulary.DCTERMS;
import org.openrdf.model.vocabulary.RDF;

import java.util.Date;

public class Catalog {
  private URI catalogUri;
  private String datasetPrefix;

  public Catalog(String catalogUri, String datasetPrefix) {
    this.catalogUri = new URIImpl(catalogUri);
    this.datasetPrefix = datasetPrefix;
  }

  public URI generateDatasetUri(String datasetId) {
    return new URIImpl(concatWithSlash(datasetPrefix, datasetId));
  }

  public URI generateRecordUri(String datasetId) {
    return buildURI(catalogUri.stringValue(), "record", datasetId);
  }

  public Model datasetProvenance(String datasetId) {
    Model statements = new LinkedHashModel();
    URI dataset = generateDatasetUri(datasetId);
    URI record = generateRecordUri(datasetId);
    Literal now = ValueFactoryImpl.getInstance().createLiteral(new Date());
    statements.add(record, DCTERMS.MODIFIED, now);
    statements.add(record, DCTERMS.ISSUED, now);
    statements.add(record, RDF.TYPE, ODSVoc.DCAT_CATALOGRECORD);
    statements.add(record, ODSVoc.FOAF_PRIMARYTOPIC, dataset);
    statements.add(catalogUri, ODSVoc.DCAT_CAT_PROP_DATASET, dataset);
    statements.add(catalogUri, ODSVoc.DCAT_CAT_PROP_RECORD, record);
    statements.add(dataset, RDF.TYPE, ODSVoc.DCAT_DATASET);
    return statements;
  }

  private URI buildURI(String base, String sub, String id) {
    return new URIImpl(concatWithSlash(concatWithSlash(base, sub), id));
  }

  private String concatWithSlash(String pref, String end) {
    if (pref.endsWith("/"))
      return pref + end;
    else
      return pref + '/' + end;
  }

}
