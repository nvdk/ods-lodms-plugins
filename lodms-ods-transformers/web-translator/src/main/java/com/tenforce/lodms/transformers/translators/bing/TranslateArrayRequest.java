package com.tenforce.lodms.transformers.translators.bing;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

/*
<TranslateArrayRequest>
  <AppId />
  <From>language-code</From>
  <Options>
    <Category xmlns="http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2" >string-value</Category>
    <ContentType xmlns="http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2">text/plain</ContentType>
    <ReservedFlags xmlns="http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2" />
    <State xmlns="http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2" >int-value</State>
    <Uri xmlns="http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2" >string-value</Uri>
    <User xmlns="http://schemas.datacontract.org/2004/07/Microsoft.MT.Web.Service.V2" >string-value</User>
  </Options>
  <Texts>
    <string xmlns="http://schemas.microsoft.com/2003/10/Serialization/Arrays">string-value</string>
    <string xmlns="http://schemas.microsoft.com/2003/10/Serialization/Arrays">string-value</string>
    </Texts>
  <To>language-code</To>
</TranslateArrayRequest>

 */
@XmlRootElement(name="TranslateArrayRequest")
public class TranslateArrayRequest {
  private String from;
  private String to = "en";
  private String appId ="";

  private List<String> texts;

  @XmlElement(name="From")
  public String getFrom() {
    return from;
  }

  public void setFrom(String from) {
    this.from = from;
  }

  @XmlElement(name="To")
  public String getTo() {
    return to;
  }

  public void setTo(String to) {
    this.to = to;
  }

  @XmlElementWrapper(name = "Texts")
  @XmlElement(name="string", namespace = "http://schemas.microsoft.com/2003/10/Serialization/Arrays")
  public List<String> getTexts() {
    return texts;
  }

  public void setTexts(List<String> texts) {
    this.texts = texts;
  }

  public void addText(String text) {
    if (texts == null)
      texts = new ArrayList<String>();
    texts.add(text);
  }

  @XmlElement(name="AppId")
  public String getAppId() {
    return appId;
  }

  public void setAppId(String appId) {
    this.appId = appId;
  }
}
