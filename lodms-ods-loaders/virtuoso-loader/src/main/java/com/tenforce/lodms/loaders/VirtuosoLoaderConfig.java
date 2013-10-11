package com.tenforce.lodms.loaders;

public class VirtuosoLoaderConfig {
    public static final String GRAPHSOURCE_CUSTOM = "custom";
    public static final String GRAPHSOURCE_CKANURI = "ckan uri";
    public static final String GRAPHSOURCE_ODSURI = "harmonized uri";
    private String host = "localhost";
    private String port = "1111";
    private String userName = "dba";
    private String password = "dba";
    private boolean versioned = false;
    private String graphSource = GRAPHSOURCE_CUSTOM;
    private String graph = "";

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

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
