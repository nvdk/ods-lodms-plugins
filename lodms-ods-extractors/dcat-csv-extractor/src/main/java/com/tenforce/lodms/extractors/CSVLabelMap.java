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
        put("description_nl", ODSVoc.DCT_DESCRIPTION);
        put("description_fr", ODSVoc.DCT_DESCRIPTION);
        put("publisher", ODSVoc.DCT_PUBLISHER);
        put("title", ODSVoc.DCT_TITLE);
//        put("homepage",ODSVoc.);
//        put("release date",);
        put("themes", ODSVoc.DCAT_THEME_TAXONOMY);
        put("update/modification date", ODSVoc.DCT_MODIFIED);
        put("record", ODSVoc.DCAT_CAT_PROP_RECORD);
//        put("rights",);
        put("spatial/geographic", ODSVoc.DCT_SPATIAL);
        put("datasetURI", ODSVoc.DCAT_DATASET);
        put("title_nl", ODSVoc.DCT_TITLE);
        put("title_fr", ODSVoc.DCT_TITLE);
        put("contact point", ODSVoc.ADMS_CONTACT_POINT);
        put("dataset distribution", ODSVoc.DCAT_DATASET_DISTRIBUTION);
        put("keyword/tag_nl", ODSVoc.DCAT_KEYWORD);
        put("keyword/tag_fr", ODSVoc.DCAT_KEYWORD);
        put("theme/category", ODSVoc.DCAT_THEME);
//        put("conforms to",);
        put("frequency", ODSVoc.DCT_ACCRUAL_PERIODICTY);
//        put("identifier",ODSVoc.);
//        put("landing page",);
//        put("other identifier",);
        put("spatial/geographical coverage", ODSVoc.DCT_SPATIAL);
//        put("temporal coverage",ODSVoc.);
//        put("version",);
//        put("version notes",);
        put("recordURI", ODSVoc.DCAT_CATALOGRECORD);
        put("primary topic", ODSVoc.FOAF_PRIMARYTOPIC);
//        put("listing date",);
        put("change type", ODSVoc.ADMS_STATUS);
//        put("distributionURI",);
        put("access URL", ODSVoc.DCAT_ACCESS_URL);
        put("description", ODSVoc.DCT_DESCRIPTION);
        put("format", ODSVoc.DCT_FORMAT);
//        put("byte size",ODSVoc.DCAT_B);
//        put("download URL",);
        put("media type", ODSVoc.DCAT_MEDIA_TYPE);
    }};
}
