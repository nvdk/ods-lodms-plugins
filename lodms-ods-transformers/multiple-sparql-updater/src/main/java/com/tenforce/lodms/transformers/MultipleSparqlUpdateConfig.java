package com.tenforce.lodms.transformers;

import java.util.ArrayList;

public class MultipleSparqlUpdateConfig {
    private ArrayList<String> queries = new ArrayList<String>();

    public ArrayList<String> getQueries() {
        return queries;
    }

    public void setQueries(ArrayList<String> queries) {
        this.queries = queries;
    }
}
