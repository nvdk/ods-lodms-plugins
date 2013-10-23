package com.tenforce.lodms;

import org.openrdf.model.URI;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.ValueFactoryImpl;

public interface ODSVoc {
    ValueFactory valueFactory = ValueFactoryImpl.getInstance();
    URI RDFTYPE = valueFactory.createURI("http://www.w3.org/1999/02/22-rdf-syntax-ns#type");
    String DCAT = "http://www.w3.org/ns/dcat";
    URI DCAT_CATALOG = valueFactory.createURI(DCAT + "#Catalog");
    URI DCAT_CAT_PROP_DATASET = valueFactory.createURI(DCAT + "#dataset");
    URI DCAT_CAT_PROP_RECORD = valueFactory.createURI(DCAT + "#record");
    URI DCAT_CATALOGRECORD = valueFactory.createURI(DCAT + "#CatalogRecord");
    URI DCAT_DATASET = valueFactory.createURI(DCAT + "#Dataset");
    URI DCAT_MEDIA_TYPE = valueFactory.createURI(DCAT + "mediaType");
    URI DCAT_THEME = valueFactory.createURI(DCAT + "#theme");
    URI FOAF_PRIMARYTOPIC = valueFactory.createURI("http://xmlns.com/foaf/0.1/primaryTopic");
    String DC_TERMS = "http://purl.org/dc/terms/";
    URI DCT_PUBLISHER = valueFactory.createURI(DC_TERMS + "publisher");
    URI DCT_MODIFIED = valueFactory.createURI(DC_TERMS + "modified");
    URI DCT_DESCRIPTION = valueFactory.createURI(DC_TERMS + "description");
    URI DCT_TITLE = valueFactory.createURI(DC_TERMS + "title");
    URI DCT_LICENSE = valueFactory.createURI(DC_TERMS + "license");
    URI DCT_FORMAT = valueFactory.createURI(DC_TERMS + "format");
    URI DCT_SPATIAL = valueFactory.createURI(DC_TERMS + "spatial");
    URI DCT_LANGUAGE = valueFactory.createURI(DC_TERMS + "language");
    URI DCT_ACCRUAL_PERIODICTY = valueFactory.createURI(DC_TERMS + "accrualPeriodicity");
    URI DCT_TYPE = valueFactory.createURI(DC_TERMS + "type");
    String ADMS_NS = "http://www.w3.org/ns/adms#";
    URI ADMS_STATUS = valueFactory.createURI(ADMS_NS + "status");
    URI ADMS_CONTACT_POINT = valueFactory.createURI(ADMS_NS + "contactPoint");
    URI ODS_NS = valueFactory.createURI("http://data.opendatasupport.eu/ontology/harmonisation.owl#");
    URI ODS_RAW_CATALOG = valueFactory.createURI(ODS_NS + "raw_catalog");
    URI ODS_RAW_DATASET = valueFactory.createURI(ODS_NS + "raw_dataset");
    URI ODS_HARVEST_DATE = valueFactory.createURI(ODS_NS + "harvest_date");


}
