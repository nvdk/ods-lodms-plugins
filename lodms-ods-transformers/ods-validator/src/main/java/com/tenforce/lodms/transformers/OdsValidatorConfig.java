package com.tenforce.lodms.transformers;

import com.tenforce.lodms.transformers.validator.ValidationRule;
import com.thoughtworks.xstream.XStream;
import org.apache.log4j.Logger;

import java.util.Collections;
import java.util.List;

public class OdsValidatorConfig {
    private List<ValidationRule> validationRules;
    public OdsValidatorConfig() {
        validationRules = getDefaultValidationRules();
    }

    public List<ValidationRule> getDefaultValidationRules() {
        XStream xstream = new XStream();
        try {
            return (List<ValidationRule>) xstream.fromXML(this.getClass().getResourceAsStream("default_rules.xml"));
        }
        catch (Exception e) {
            Logger logger = Logger.getLogger(this.getClass());
            logger.error(e.getMessage(),e);
            return Collections.emptyList();
        }
    }

    public List<ValidationRule> getValidationRules() {
        return validationRules;
    }

    public void setValidationRules(List<ValidationRule> validationRules) {
        this.validationRules = validationRules;
    }
}
