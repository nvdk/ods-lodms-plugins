package com.tenforce.lodms.transformers;

import com.tenforce.lodms.ODSVoc;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.ui.Select;
import org.openrdf.model.URI;

import java.util.ArrayList;
import java.util.List;

public class PredicateSelector extends Select {
    private static final List<MappedPredicate> predicates = new ArrayList<MappedPredicate>() {
        {
            add(new MappedPredicate(ODSVoc.DCAT_DATASET, ODSVoc.DCAT_THEME, "Dataset Theme"));
            add(new MappedPredicate(ODSVoc.DCAT_DATASET, ODSVoc.DCT_ACCRUAL_PERIODICTY, "Dataset Frequency"));
            add(new MappedPredicate(ODSVoc.DCAT_DATASET, ODSVoc.DCT_LANGUAGE, "Dataset Language"));
            add(new MappedPredicate(ODSVoc.DCAT_DATASET, ODSVoc.DCT_PUBLISHER, "Dataset Publisher"));
            add(new MappedPredicate(ODSVoc.DCAT_DATASET, ODSVoc.DCT_SPATIAL, "Dataset Spatial"));
            add(new MappedPredicate(MappedPredicate.DISTRIBUTION, ODSVoc.DCAT_MEDIA_TYPE, "Distribution Media Type"));
            add(new MappedPredicate(MappedPredicate.DISTRIBUTION, ODSVoc.DCT_FORMAT, "Distribution Format"));
            add(new MappedPredicate(MappedPredicate.DISTRIBUTION, ODSVoc.ADMS_STATUS, "Distribution Status"));
            add(new MappedPredicate(MappedPredicate.AGENT, ODSVoc.DCT_TYPE, "Agent Type"));
            add(new MappedPredicate(MappedPredicate.LICENSE, ODSVoc.DCT_TYPE, "License Type"));
        }
    };

    public PredicateSelector(String caption) {
        super(caption);
        configurePSelect();
    }

    public void selectPredicate(URI value) {
      for (MappedPredicate m : predicates) {
        if(m.getDcatProp().equals(value)) {
          this.setValue(m);
        }
      }
    }

    private void configurePSelect() {
        setDescription("Select the property for which you want to create a value mapping.");
        for (MappedPredicate m : predicates) {
            addItem(m);
            setItemCaption(m, m.getDcatDesc());
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
