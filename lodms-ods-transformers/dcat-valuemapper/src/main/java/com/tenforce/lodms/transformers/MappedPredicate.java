package com.tenforce.lodms.transformers;

import org.openrdf.model.URI;
import org.openrdf.model.impl.URIImpl;

public class MappedPredicate {
    public static final URI DISTRIBUTION = new URIImpl("http://www.w3.org/ns/dcat#Distribution");
    public static final URI AGENT = new URIImpl("http://xmlns.com/foaf/0.1/Agent");
    public static final URI LICENSE = new URIImpl("http://purl.org/dc/terms/LicenseDocument");

    private URI dcatClass;
    private URI dcatProp;
    private String dcatDesc;

    public MappedPredicate() {

    }

    public MappedPredicate(URI dcatClass, String dcatProp, String dcatDesc) {
        this.dcatClass = dcatClass;
        this.dcatProp = new URIImpl(dcatProp);
        this.dcatDesc = dcatDesc;
    }

    public MappedPredicate(URI dcatClass, URI dcatProp, String dcatDesc) {
        this.dcatClass = dcatClass;
        this.dcatProp = dcatProp;
        this.dcatDesc = dcatDesc;
    }

    public URI getDcatClass() {
        return dcatClass;
    }

    public void setDcatClass(URI dcatClass) {
        this.dcatClass = dcatClass;
    }

    public URI getDcatProp() {
        return dcatProp;
    }

    public void setDcatProp(URI dcatProp) {
        this.dcatProp = dcatProp;
    }

    public String getDcatDesc() {
        return dcatDesc;
    }

    public void setDcatDesc(String dcatDesc) {
        this.dcatDesc = dcatDesc;
    }
}
