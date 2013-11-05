package com.tenforce.lodms.extractors;

public class ODSCSVExtractorConfig {
    private String catalogCsv = "";
    private String recordCsv = "";
    private String datasetCsv = "";
    private String distributionCsv = "";
    private String agentCsv = "";

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

    public String getAgentCsv() {
        return agentCsv;
    }

    public void setAgentCsv(String agentCsv) {
        this.agentCsv = agentCsv;
    }
}
