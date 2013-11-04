package com.tenforce.lodms.extractors;

public class ODSCSVExtractorConfig {
    private String baseUri = "http://data.opendatasupport.eu/id/catalog/";
    private String catalogCsv = "file:///home/nielsv/Documents/catalogue.csv";
    private String recordCsv = "file:///home/nielsv/Documents/records.csv";
    private String datasetCsv = "file:///home/nielsv/Documents/datatasets.csv";
    private String distributionCsv = "file:///home/nielsv/Documents/distributions.csv";

    public String getCatalogCsv() {
        return catalogCsv;
    }

    public void setCatalogCsv(String catalogCSV) {
        this.catalogCsv = catalogCSV;
    }

    public String getDistributionCsv() {
        return distributionCsv;
    }

    public void setDistributionCsv(String distributionCsv) {
        this.distributionCsv = distributionCsv;
    }

    public String getBaseUri() {
        return baseUri;
    }

    public void setBaseUri(String baseUri) {
        this.baseUri = baseUri;
    }

    public String getRecordCsv() {
        return recordCsv;
    }

    public void setRecordCsv(String recordCsv) {
        this.recordCsv = recordCsv;
    }

    public String getDatasetCsv() {
        return datasetCsv;
    }

    public void setDatasetCsv(String datasetCsv) {
        this.datasetCsv = datasetCsv;
    }
}
