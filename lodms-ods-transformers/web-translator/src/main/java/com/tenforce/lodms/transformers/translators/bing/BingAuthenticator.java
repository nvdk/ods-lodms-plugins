package com.tenforce.lodms.transformers.translators.bing;

import com.tenforce.lodms.transformers.utils.RestFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

public class BingAuthenticator {
  private String clientId;
  private String clientSecret;
  private static final String AUTH_URL = "https://datamarket.accesscontrol.windows.net/v2/OAuth2-13/";
  private Map<String, String> tokens = new HashMap<String, String>();

  public BingAuthenticator(String clientId, String secret) {
    this.clientId = clientId;
    clientSecret = secret;
  }

  public String getToken(String scope) {
    if (!tokens.containsKey(scope))
      tokens.put(scope, requestToken(scope));
    return tokens.get(scope);
  }

  public void invalidateToken(String scope) {
    tokens.remove(scope);
  }

  private String requestToken(String scope) {
    RestTemplate restTemplate = RestFactory.getRest();
    MultiValueMap<String, String> requestBody = new LinkedMultiValueMap<String, String>();
    requestBody.add("grant_type", "client_credentials");
    requestBody.add("scope", scope);
    requestBody.add("client_id", clientId);
    requestBody.add("client_secret", clientSecret);
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(requestBody, headers);
    ResponseEntity<BingAuthResponse> model = restTemplate.exchange(AUTH_URL, HttpMethod.POST, request, BingAuthResponse.class);
    return model.getBody().getAccess_token();
  }


}
