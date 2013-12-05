package com.tenforce.lodms.transformers.validator;

public class ValidationRule {
    public static final String SEVERITY_WARN = "warning";
    public static final String SEVERITY_ERROR = "error";
    private String sparqlQuery = "PREFIX dcterms: <http://purl.org/dc/terms/>\n" +
            "prefix dcat:<http://www.w3.org/ns/dcat#> \n" +
            "SELECT \n" +
            "?s ?p ?o \n" +
            "WHERE {\n" +
            "?s a dcat:Dataset.\n" +
            "OPTIONAL {?s dcterms:description ?desc}.\n" +
            "BIND(rdf:type AS ?p).\n" +
            "BIND(dcat:Dataset AS ?o).\n" +
            "}";
    private String description = "sample query: every dataset needs a description";
    private String message = "no description defined for this dataset";
    private String severity = SEVERITY_WARN;


    public ValidationRule() {

    }

    public String getSparqlQuery() {
        return sparqlQuery;
    }

    public void setSparqlQuery(String query) {
        this.sparqlQuery = query;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }
}
