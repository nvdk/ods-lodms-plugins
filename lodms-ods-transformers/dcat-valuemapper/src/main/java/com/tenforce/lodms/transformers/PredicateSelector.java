package com.tenforce.lodms.transformers;

import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.Select;

import java.util.ArrayList;
import java.util.List;

public class PredicateSelector extends Select {
    private static final String DCAT = "http://www.w3.org/ns/dcat#";
    private static final String DC_TERMS = "http://purl.org/dc/terms/";
    private static final List<MappedPredicate> predicates = new ArrayList<MappedPredicate>() {
        {
            add(new MappedPredicate(MappedPredicate.DATASET,DCAT+"theme", "Dataset Theme"));
            add(new MappedPredicate(MappedPredicate.DATASET,DC_TERMS+"accrualPeriodicity", "Dataset Frequency"));
            add(new MappedPredicate(MappedPredicate.DATASET,DC_TERMS+"language", "Dataset Language"));
            add(new MappedPredicate(MappedPredicate.DATASET,DC_TERMS+"publisher", "Dataset Publisher"));
            add(new MappedPredicate(MappedPredicate.DATASET,DC_TERMS+"spatial", "Dataset Spatial"));
            add(new MappedPredicate(MappedPredicate.DISTRIBUTION,DCAT+"mediaType", "Distribution Media Type"));
            add(new MappedPredicate(MappedPredicate.DISTRIBUTION,DCAT+"format", "Distribution Format"));
            add(new MappedPredicate(MappedPredicate.DISTRIBUTION,"http://www.w3.org/ns/adms#status", "Distribution Status"));
            add(new MappedPredicate(MappedPredicate.AGENT,DC_TERMS + "type", "Agent Type"));
            add(new MappedPredicate(MappedPredicate.LICENSE,DC_TERMS + "type", "License Type"));
        }
    };
    public PredicateSelector(String caption) {
        super(caption);
        configurePSelect();
    }

    private void configurePSelect() {
        setDescription("Select the property for which you want to create a value mapping.");
        for (MappedPredicate m: predicates) {
            addItem(m);
            setItemCaption(m,m.getDcatDesc());
            if ("Dataset Theme".equals(m.getDcatDesc())) {
                setValue(m);
            }
        }
        setImmediate(true);
        addValidator(new AbstractStringValidator(null) {
            @Override
            protected boolean isValidString(String value) {
                return (null != value && !value.isEmpty());
            }
        });
    }
}
