package com.tenforce.lodms.extractors;

import org.openrdf.model.URI;

public class CSVHeader {
    private URI uri;
    private String language = null;

    public CSVHeader(URI uri) {
        this.uri = uri;
    }

    public CSVHeader(URI uri, String language) {
        this.uri = uri;
        this.language = language;
    }

    public URI getUri() {
        return uri;
    }

    public String getLanguage() {
        return language;
    }

    public boolean hasLanguage() {
        return null != language;
    }
}
