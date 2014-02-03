package com.tenforce.lodms.transformers.translators.bing;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/*
<ArrayOfTranslateArrayResponse
    xmlns="http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2"
    xmlns:i="http://www.w3.org/2001/XMLSchema-instance">
    <TranslateArrayResponse>
        <From>nl</From>
        <OriginalTextSentenceLengths
            xmlns:a="http://schemas.microsoft.com/2003/10/Serialization/Arrays">
            <a:int>19</a:int>
        </OriginalTextSentenceLengths>
        <TranslatedText>an example title</TranslatedText>
        <TranslatedTextSentenceLengths
            xmlns:a="http://schemas.microsoft.com/2003/10/Serialization/Arrays">
            <a:int>16</a:int>
        </TranslatedTextSentenceLengths>
    </TranslateArrayResponse>
    <TranslateArrayResponse>
        <From>nl</From>
        <OriginalTextSentenceLengths
            xmlns:a="http://schemas.microsoft.com/2003/10/Serialization/Arrays">
            <a:int>16</a:int>
        </OriginalTextSentenceLengths>
        <TranslatedText>another title</TranslatedText>
        <TranslatedTextSentenceLengths
            xmlns:a="http://schemas.microsoft.com/2003/10/Serialization/Arrays">
            <a:int>13</a:int>
        </TranslatedTextSentenceLengths>
    </TranslateArrayResponse>
</ArrayOfTranslateArrayResponse>
 */
@XmlRootElement(name = "TranslateArrayResponse", namespace = "http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2")
public class TranslateResponse {

  private String translatedText;
  private String from;

  @XmlElement(name = "TranslatedText", namespace = "http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2")
  public String getTranslatedText() {
    return translatedText;
  }

  @XmlElement(name = "From", namespace = "http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2")
  public String getFrom() {
    return from;
  }

  public void setTranslatedText(String translatedText) {
    this.translatedText = translatedText;
  }


  public void setFrom(String from) {
    this.from = from;
  }
}
