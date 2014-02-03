package com.tenforce.lodms.transformers.translators.bing;

/*
<ArrayOfTranslateArrayResponse xmlns="http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2" xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
  <TranslateArrayResponse>
    <From>language-code</From>
    <OriginalTextSentenceLengths xmlns:a="http://schemas.microsoft.com/2003/10/Serialization/Arrays">
      <a:int>int-value</a:int>
    </OriginalTextSentenceLengths>
    <State/>
    <TranslatedText>string-value</TranslatedText>
    <TranslatedTextSentenceLengths xmlns:a="http://schemas.microsoft.com/2003/10/Serialization/Arrays">
      <a:int>int-value</a:int>
    </TranslatedTextSentenceLengths>
  </TranslateArrayResponse>
 </ArrayOfTranslateArrayResponse>
 */

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;


@XmlRootElement(name = "ArrayOfTranslateArrayResponse", namespace = "http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2")
public class ArrayOfTranslateArrayResponse {

  private List<TranslateResponse> translateResponses;

  @XmlElement(name = "TranslateArrayResponse", namespace = "http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2")
  public List<TranslateResponse> getTranslateResponses() {
    return translateResponses;
  }

  public void setTranslateResponses(List<TranslateResponse> translateResponses) {
    this.translateResponses = translateResponses;
  }
}
