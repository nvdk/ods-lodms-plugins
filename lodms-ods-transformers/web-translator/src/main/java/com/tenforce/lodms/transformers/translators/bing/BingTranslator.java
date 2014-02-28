package com.tenforce.lodms.transformers.translators.bing;

import com.tenforce.lodms.transformers.translators.TranslatedStatement;
import com.tenforce.lodms.transformers.translators.TranslationApi;
import com.tenforce.lodms.transformers.translators.TranslationException;
import com.tenforce.lodms.transformers.utils.RestFactory;
import org.apache.log4j.Logger;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class BingTranslator implements TranslationApi {
  private String clientId;
  private String clientSecret;
  private BingAuthenticator authenticator;
  private static final String API_URL = "http://api.microsofttranslator.com";
  private static final String TRANSLATE_ARRAY = API_URL + "/v2/Http.svc/TranslateArray";
  // JSON http://api.microsofttranslator.com/V2/Ajax.svc/TranslateArray?
  private static final int MAX_TEXT_SIZE = 5000;
  private Logger logger = Logger.getLogger(this.getClass());
  private static final int DEFAULT_RETRIES = 3;
  private List<String> warnings = new ArrayList<String>();


  /**
   * Set the client id to be used to get a access_token from microsoft. Do this before calling translateStatements!
   *
   * @param id
   */
  @Override
  public void setClientId(String id) {
    clientId = id;
  }

  /**
   * Set the client secret to be used to get a access_token from microsoft.  Do this before calling translateStatements!
   *
   * @param secret
   */
  @Override
  public void setClientSecret(String secret) {
    clientSecret = secret;
  }

  @Override
  public List<String> getWarnings() {
    return warnings;
  }

  /**
   * Iteratively collects translations for the provided set of statements, [MAX_ARRAY_SIZE] elements at a time.
   * Make sure to set clientId & clientSecret before calling this.
   *
   * @param statements a collection of statements
   * @return translatedStatements a collection of translatedStatements
   */
  @Override
  public Collection<TranslatedStatement> translateStatements(Collection<Statement> statements) throws TranslationException {
    if (clientId == null || clientSecret == null)
      throw new IllegalStateException("clientId and clientSecret are required to translate statements on bing");

    authenticator = new BingAuthenticator(clientId, clientSecret);

    List<TranslatedStatement> translatedStatements = new ArrayList<TranslatedStatement>(statements.size());
    Iterator<Statement> iter = statements.iterator();
    List<Statement> toBeTranslated = new ArrayList<Statement>(100);
    int i = 0;
    String currentLang = null;
    while (iter.hasNext()) {
      Statement s = iter.next();
      Literal object = (Literal) s.getObject();
      String newLang = object.getLanguage();
      i = i + object.stringValue().length();
      toBeTranslated.add(s);
      if (i >= MAX_TEXT_SIZE || !iter.hasNext() || currentLang != newLang) {
        translatedStatements.addAll(getTranslationsList(toBeTranslated, DEFAULT_RETRIES));
        i = 0;
        toBeTranslated = new ArrayList<Statement>(100);
      }
      currentLang = newLang;
      i++;
    }
    return translatedStatements;
  }

  /**
   * Queries the bing translation api for a translation
   * Note that bing only supports one input language at a time
   *
   * @param statements
   * @return
   */
  private List<TranslatedStatement> getTranslationsList(List<Statement> statements, int retries) throws TranslationException {
    try {
      RestTemplate restTemplate = RestFactory.getRest();
      restTemplate.setErrorHandler(new BingResponseHandler());
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_XML);
      headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
      headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
      headers.set("Authorization", "Bearer " + authenticator.getToken(API_URL));
      TranslateArrayRequest requestBody = new TranslateArrayRequest();
      requestBody.setTexts(extractValues(statements));
      HttpEntity<TranslateArrayRequest> request = new HttpEntity<TranslateArrayRequest>(requestBody, headers);
      ArrayOfTranslateArrayResponse responseArray = restTemplate.postForObject(TRANSLATE_ARRAY, request, ArrayOfTranslateArrayResponse.class);
      return createTranslatedStatements(statements, responseArray.getTranslateResponses(), requestBody.getTo());
    } catch (HttpStatusCodeException e) {
      if (isZeroBalance(e))
        throw new TranslationException("bing account is out of balance");
      if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST) && retries > 0) {
        warnings.add(e.getStatusText());
        logger.error(e.getStatusCode() + ": " + e.getStatusText());
        logger.error(e.getResponseBodyAsString());
        authenticator.invalidateToken(API_URL);
        return getTranslationsList(statements, --retries);
      }
      return Collections.emptyList();
    }
  }

  private boolean isZeroBalance(HttpStatusCodeException e) {
    return e.getStatusCode().equals(HttpStatus.BAD_REQUEST) && e.getResponseBodyAsString().contains("zero balance");
  }


  private List<TranslatedStatement> createTranslatedStatements(List<Statement> originalStatements, List<TranslateResponse> translations, String translatedTo) {
    if (originalStatements.size() != translations.size())
      throw new IllegalArgumentException("size of originalStatements and translations does not match. can't merge");
    int i = 0;
    List<TranslatedStatement> translatedStatementList = new ArrayList<TranslatedStatement>(originalStatements.size());
    for (Statement s : originalStatements) {
      TranslatedStatement translatedStatement = new TranslatedStatement();
      translatedStatement.setOriginalStatement(s);
      TranslateResponse response = translations.get(i++);
      Statement newStatement = new StatementImpl(s.getSubject(), s.getPredicate(), new LiteralImpl(response.getTranslatedText(), translatedTo));
      translatedStatement.setTranslatedStatement(newStatement);
      translatedStatementList.add(translatedStatement);
    }
    return translatedStatementList;
  }

  private List<String> extractValues(List<Statement> statements) {
    List<String> strings = new ArrayList<String>(statements.size());
    for (Statement s : statements) {
      if (s.getObject() instanceof Literal) {
        strings.add(s.getObject().stringValue());
      }
    }
    return strings;
  }

  private class BingResponseHandler extends DefaultResponseErrorHandler {
    @Override
    public void handleError(ClientHttpResponse response) throws IOException {

      super.handleError(response);
    }
  }
}
