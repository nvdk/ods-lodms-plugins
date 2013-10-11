package com.tenforce.lodms.transformers;

import java.util.Collections;
import java.util.List;

public class ODSValueMapperConfig {
    private MappedPredicate mappedPredicate;
    private List<Mapping> mappings = Collections.emptyList();

    public List<Mapping> getMappings() {
        return Collections.unmodifiableList(mappings);
    }

    public void setMappings(List<Mapping> mappings) {
        this.mappings = mappings;
    }

    public MappedPredicate getMappedPredicate() {
        return mappedPredicate;
    }

    public void setMappedPredicate(MappedPredicate mappedPredicate) {
        this.mappedPredicate = mappedPredicate;
    }
}
