package com.tenforce.lodms.extractors.models;

import com.tenforce.lodms.extractors.utils.RestTemplateFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CkanDataSetList extends JSONModel {
  private List<String> result;

  public List<String> getResult() {
    return Collections.unmodifiableList(result);
  }

  public void setResult(List<String> result) {
    this.result = result;
  }

  public static List<String> getPackageIds(String uri, HttpMethod method) {
    RestTemplate rest = RestTemplateFactory.getRestTemplate();
    Map<String, String> map = new HashMap<String, String>();
    HttpEntity<?> httpEntity = new HttpEntity<Object>(map, RestTemplateFactory.getHttpHeaders());
    try {
      // ?h is included because some ckan instances will complain no request body is present on http get if no request param is present
      // other portals disallow POST all together so this seems to be the "solution" that works on all portals (so far)
      ResponseEntity<CkanDataSetList> dataSetList = rest.exchange(uri + "action/package_list?wtf", method, httpEntity, CkanDataSetList.class);
      return dataSetList.getBody().getResult();
    } catch (HttpClientErrorException ignored) {
      return Collections.emptyList();
    } catch (HttpServerErrorException ignored) {
      return Collections.emptyList();
    }
  }

}