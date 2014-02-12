package com.tenforce.lodms.transformers.translators.bing;

import com.tenforce.lodms.transformers.translators.TranslationApi;
import junit.framework.Assert;
import org.openrdf.model.Model;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.Rio;
import org.testng.annotations.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

public class BingTranslatorTest {

//  @Test
//  public void shouldMarshalRequest() throws Exception {
//    TranslateArrayRequest requestBody = new TranslateArrayRequest();
//    requestBody.setTexts(Arrays.asList("test titel", "andere titel"));
//    JAXBContext jaxbContext = JAXBContext.newInstance(TranslateArrayRequest.class);
//    Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
//    jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//    jaxbMarshaller.marshal(requestBody, System.out);
//  }

  @Test
  public void shouldUnMarshalResponse() throws Exception {
    JAXBContext jaxbContext = JAXBContext.newInstance(ArrayOfTranslateArrayResponse.class);
    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
    ArrayOfTranslateArrayResponse response = (ArrayOfTranslateArrayResponse) jaxbUnmarshaller.unmarshal(this.getClass().getResource("sample_response.xml"));
    Assert.assertNotNull("translate responses should not be null", response.getTranslateResponses());
    Assert.assertEquals("should have found two translations", 2, response.getTranslateResponses().size());
    Assert.assertEquals("first translation should contain the correct translated text", "an example title", response.getTranslateResponses().get(0).getTranslatedText());
    Assert.assertEquals("first translation should contain the correct from language", "nl", response.getTranslateResponses().get(0).getFrom());
  }


  @Test
  public void testTranslateStatements() throws Exception {
    TranslationApi translatorApi = new BingTranslator();
    translatorApi.setClientId("CLIENTID");
    translatorApi.setClientSecret("CLIENTSECRET");
    Model statements = Rio.parse(this.getClass().getResourceAsStream("statements.ttl"), "", RDFFormat.TURTLE);
//    translatorApi.translateStatements(statements);
  }
}
