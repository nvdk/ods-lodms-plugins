package com.tenforce.lodms.transformers.translators.bing;

import junit.framework.Assert;
import org.testng.annotations.Test;

public class BingAuthenticatorTest {

  @Test
  public void itShouldReturnAToken() {
    BingAuthenticator auth = new BingAuthenticator("ODSTranslatorPlugin","yPPz/ZsvqQsagN7CSqNQuc5VbaBiQTJMhqYp0zMFY0s=");
    String token = auth.getToken("http://api.microsofttranslator.com");
    Assert.assertTrue(token != null && token.length() > 0);
  }
}
