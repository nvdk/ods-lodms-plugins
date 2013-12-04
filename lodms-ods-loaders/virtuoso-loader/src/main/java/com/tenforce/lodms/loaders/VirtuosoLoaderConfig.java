package com.tenforce.lodms.loaders;

public class VirtuosoLoaderConfig {
    public static final String GRAPHSOURCE_CUSTOM = "custom";
    public static final String GRAPHSOURCE_CKANURI = "ckan uri";
    public static final String GRAPHSOURCE_ODSURI = "harmonized uri";
    private boolean versioned = false;
    private String graphSource = GRAPHSOURCE_CUSTOM;
    private String graph = "";

    public boolean isVersioned() {
        return versioned;
    }

    public void setVersioned(boolean versioned) {
        this.versioned = versioned;
    }

    public String getGraph() {
        return graph;
    }

    public void setGraph(String graph) {
        this.graph = graph;
    }

    public String getGraphSource() {
        return graphSource;
    }

    public void setGraphSource(String graphSource) {
        this.graphSource = graphSource;
    }
}
