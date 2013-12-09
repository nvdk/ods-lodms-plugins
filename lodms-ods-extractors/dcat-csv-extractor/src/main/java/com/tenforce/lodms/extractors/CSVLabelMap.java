package com.tenforce.lodms.extractors;

import com.tenforce.lodms.ODSVoc;
import org.openrdf.model.URI;

import java.util.HashMap;
import java.util.Map;

public interface CSVLabelMap {
    static final Map<String, URI> HEADERS = new HashMap<String, URI>() {{
        put("language", ODSVoc.DCT_LANGUAGE);
        put("dataset", ODSVoc.DCAT_CAT_PROP_DATASET);
        put("licence", ODSVoc.DCT_LICENSE);
        put("status", ODSVoc.ADMS_STATUS);
        put("catalogueURI", ODSVoc.DCAT_CATALOG);
        put("description", ODSVoc.DCT_DESCRIPTION);
        put("publisher", ODSVoc.DCT_PUBLISHER);
        put("title", ODSVoc.DCT_TITLE);
        put("homepage", ODSVoc.FOAF_HOMEPAGE);
        put("release date", ODSVoc.DCT_ISSUED);
        put("themes", ODSVoc.DCAT_THEME_TAXONOMY);
        put("update/modification date", ODSVoc.DCT_MODIFIED);
        put("record", ODSVoc.DCAT_CAT_PROP_RECORD);
        put("rights", ODSVoc.DCT_RIGHTS);
        put("spatial/geographic", ODSVoc.DCT_SPATIAL);
        put("datasetURI", ODSVoc.DCAT_DATASET);
        put("title", ODSVoc.DCT_TITLE);
        put("contact point", ODSVoc.ADMS_CONTACT_POINT);
        put("dataset distribution", ODSVoc.DCAT_DATASET_DISTRIBUTION);
        put("keyword/tag", ODSVoc.DCAT_KEYWORD);
        put("theme/category", ODSVoc.DCAT_THEME);
        put("conforms to", ODSVoc.DCT_CONFORMS_TO);
        put("frequency", ODSVoc.DCT_ACCRUAL_PERIODICTY);
        put("identifier", ODSVoc.DCT_IDENTIFIER);
        put("landing page", ODSVoc.DCAT_LANDING_PAGE);
        put("other identifier", ODSVoc.ADMS_IDENTIFIER);
        put("spatial/geographical coverage", ODSVoc.DCT_SPATIAL);
        put("temporal coverage", ODSVoc.DCT_TEMPORAL);
        put("version", ODSVoc.ADMS_VERSION);
        put("version notes", ODSVoc.ADMS_VERSION_NOTES);
        put("recordURI", ODSVoc.DCAT_CATALOGRECORD);
        put("primary topic", ODSVoc.FOAF_PRIMARYTOPIC);
        put("listing date", ODSVoc.DCT_ISSUED);
        put("change type", ODSVoc.ADMS_STATUS);
        put("distributionURI", ODSVoc.DCAT_DISTRIBUTION);
        put("access URL", ODSVoc.DCAT_ACCESS_URL);
        put("description", ODSVoc.DCT_DESCRIPTION);
        put("format", ODSVoc.DCT_FORMAT);
        put("byte size", ODSVoc.DCAT_BYTE_SIZE);
        put("download URL", ODSVoc.DCAT_DOWNLOAD_URL);
        put("media type", ODSVoc.DCAT_MEDIA_TYPE);
        put("agentURI", ODSVoc.FOAF_AGENT);
    }};
}
