package de.wicketxforms.testapp;

import java.io.InputStream;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.string.StringValue;

import de.wicketxforms.WicketXFormsException;
import de.wicketxforms.XFormsComponent;


public class TestPage extends WebPage {

  private static final long serialVersionUID = 1L;
  
  
  public TestPage(final PageParameters parameters) {
    
    super(parameters);
    
    StringValue docName = parameters.get("xfdoc");
    InputStream xformsDocument = this.getClass().getResourceAsStream("/resources/" + docName + ".xml");
    
    if (xformsDocument == null)
      xformsDocument = this.getClass().getResourceAsStream("/resources/wxf_calculator.xml");
    
    try {
      
      add(new XFormsComponent("xfComponent", xformsDocument, HomePage.BASE_URI));
      
    } catch (WicketXFormsException e) {
      add(new Label("xfComponent", "Fehler beim Erzeugen der Komponente"));
    }
  }
}
