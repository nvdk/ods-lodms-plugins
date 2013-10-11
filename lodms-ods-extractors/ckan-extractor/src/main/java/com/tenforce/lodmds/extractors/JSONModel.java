package com.tenforce.lodmds.extractors;

import org.apache.log4j.Logger;
import org.codehaus.jackson.annotate.JsonAnySetter;

public class JSONModel {
    protected Logger logger = Logger.getLogger(JSONModel.class);

    public boolean success;
    public String help;

    @JsonAnySetter
    public void handleUnknown(String key, Object value) {
        logger.info("key " + key + " is ignored for " + this.getClass().getName());
    }
}
