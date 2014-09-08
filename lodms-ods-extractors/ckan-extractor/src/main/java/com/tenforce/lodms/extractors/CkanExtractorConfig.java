package com.tenforce.lodms.extractors;

import org.springframework.http.HttpMethod;

import java.util.HashSet;
import java.util.Set;

public class CkanExtractorConfig {
  private String baseUri = "http://odp.tenforce.com/data/";
  private String publisher = "http://odp.tenforce.com/";
  private String license = "";
  private String description = "";
  private String title = "";
  private String subjectPrefix = "http://odp.tenforce.com/data/dataset/";
  private String predicatePrefix = "http://odp.tenforce.com/data/predicate/";
  private HttpMethod httpMethod = HttpMethod.GET;

  private String ignoredKeys = "rdf";
  private Boolean allDatasets = true;
  private Set<String> packageIds = new HashSet<String>();

  public String getSubjectPrefix() {
    return subjectPrefix;
  }

  public void setSubjectPrefix(String subjectPrefix) {
    this.subjectPrefix = subjectPrefix;
  }

  public String getPredicatePrefix() {
    return predicatePrefix;
  }

  public void setPredicatePrefix(String predicatePrefix) {
    this.predicatePrefix = predicatePrefix;
  }

  public String getBaseUri() {
    return baseUri;
  }

  public void setBaseUri(String baseUri) {
    this.baseUri = baseUri;
  }

  public Boolean getAllDatasets() {
    return allDatasets;
  }

  public void setAllDatasets(Boolean allDatasets) {
    this.allDatasets = allDatasets;
  }

  public String getIgnoredKeys() {
    return ignoredKeys;
  }

  public void setIgnoredKeys(String ignoredKeys) {
    this.ignoredKeys = ignoredKeys;
  }

  public String getPublisher() {
    return publisher;
  }

  public void setPublisher(String publisher) {
    this.publisher = publisher;
  }

  public Set<String> getPackageIds() {
    return packageIds;
  }

  public void setPackageIds(Set<String> packageIds) {
    this.packageIds = packageIds;
  }

  public String getLicense() {
    return license;
  }

  public void setLicense(String license) {
    this.license = license;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public HttpMethod getHttpMethod() {
    return httpMethod;
  }

  public void setHttpMethod(HttpMethod httpMethod) {
    this.httpMethod = httpMethod;
  }
}
