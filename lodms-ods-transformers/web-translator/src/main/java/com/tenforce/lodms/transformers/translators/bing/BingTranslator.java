package com.tenforce.lodms.transformers.translators.bing;

import com.tenforce.lodms.transformers.translators.TranslatedStatement;
import com.tenforce.lodms.transformers.translators.TranslationApi;
import com.tenforce.lodms.transformers.utils.RestFactory;
import org.openrdf.model.Literal;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.StatementImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class BingTranslator implements TranslationApi {
  private String clientId;
  private String clientSecret;
  private BingAuthenticator authenticator;
  private static final String API_URL = "http://api.microsofttranslator.com";
  private static final String TRANSLATE_ARRAY = API_URL + "/v2/Http.svc/TranslateArray";
  // JSON http://api.microsofttranslator.com/V2/Ajax.svc/TranslateArray?
  private static final int MAX_ARRAY_SIZE = 1000;


  /**
   * Set the client id to be used to get a access_token from microsoft. Do this before calling translateStatements!
   * @param id
   */
  @Override
  public void setClientId(String id) {
    clientId = id;
  }

  /**
   * Set the client secret to be used to get a access_token from microsoft.  Do this before calling translateStatements!
   * @param secret
   */
  @Override
  public void setClientSecret(String secret) {
    clientSecret = secret;
  }

  /**
   * Iteratively collects translations for the provided set of statements, [MAX_ARRAY_SIZE] elements at a time.
   * Make sure to set clientId & clientSecret before calling this.
   * @param statements a collection of statements
   * @return translatedStatements a collection of translatedStatements
   */
  @Override
  public Collection<TranslatedStatement> translateStatements(Collection<Statement> statements) {
    if (clientId == null || clientSecret == null)
      throw new IllegalStateException("clientId and clientSecret are required to translate statements on bing");

    if (authenticator == null)
      authenticator = new BingAuthenticator(clientId,clientSecret);
    String token = authenticator.getToken(API_URL);

    List<TranslatedStatement> translatedStatements = new ArrayList<TranslatedStatement>(statements.size());
    Iterator<Statement> iter = statements.iterator();
    List<Statement> toBeTranslated = new ArrayList<Statement>(MAX_ARRAY_SIZE);
    int i = 1;
    while (iter.hasNext()) {
      toBeTranslated.add(iter.next());
      if (i % MAX_ARRAY_SIZE == 0 || ! iter.hasNext() )   {
        translatedStatements.addAll(getTranslationsList(toBeTranslated, token));
        i = 1;
        toBeTranslated = new ArrayList<Statement>(MAX_ARRAY_SIZE);
      }
      i++;
    }
    return translatedStatements;
  }

  /**
   * Queries the bing translation api for a translation
   * @param statements
   * @param token
   * @return
   */
  private List<TranslatedStatement> getTranslationsList(List<Statement> statements,String token) {
    RestTemplate restTemplate = RestFactory.getRest();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_XML);
    headers.setAccept(Arrays.asList(MediaType.APPLICATION_XML));
    headers.setAcceptCharset(Arrays.asList(Charset.forName("UTF-8")));
    headers.set("Authorization","Bearer " + token);
    TranslateArrayRequest requestBody = new TranslateArrayRequest();
    requestBody.setTexts(extractValues(statements));
    HttpEntity<TranslateArrayRequest> request = new HttpEntity<TranslateArrayRequest>(requestBody, headers);
    ArrayOfTranslateArrayResponse responseArray = restTemplate.postForObject(TRANSLATE_ARRAY,request,ArrayOfTranslateArrayResponse.class);
    return createTranslatedStatements(statements, responseArray.getTranslateResponses(),requestBody.getTo());
  }

  private List<TranslatedStatement> createTranslatedStatements(List<Statement> originalStatements, List<TranslateResponse> translations,String translatedTo) {
    if (originalStatements.size() != translations.size())
      throw new IllegalArgumentException("size of originalStatements and translations does not match. can't merge");
    int i = 0;
    List<TranslatedStatement> translatedStatementList = new ArrayList<TranslatedStatement>(originalStatements.size());
    for (Statement s: originalStatements) {
      TranslatedStatement translatedStatement = new TranslatedStatement();
      translatedStatement.setOriginalStatement(s);
      TranslateResponse response = translations.get(i++);
      Statement newStatement = new StatementImpl(s.getSubject(),s.getPredicate(),new LiteralImpl(response.getTranslatedText(),translatedTo));
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
}
