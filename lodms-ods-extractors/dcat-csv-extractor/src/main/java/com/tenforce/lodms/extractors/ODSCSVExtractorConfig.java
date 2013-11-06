package com.tenforce.lodms.extractors;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ODSCSVExtractorConfig {
    private String catalogCsv = "";
    private String recordCsv = "";
    private String datasetCsv = "";
    private String distributionCsv = "";
    private String agentCsv = "";
    private String licenseCsv = "";

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

    public String getLicenseCsv() {
        return licenseCsv;
    }

    public void setLicenseCsv(String licenseCsv) {
        this.licenseCsv = licenseCsv;
    }

    // yeay reflection
    public List<String> definedCsvs() throws IllegalAccessException {
        List<String> csvList = new ArrayList<String>();
        Field[] allFields = this.getClass().getDeclaredFields();
        for (Field field : allFields) {
            String value = (String) field.get(this);
            if (!value.isEmpty())
                csvList.add(value);
        }
        return csvList;
    }
}
