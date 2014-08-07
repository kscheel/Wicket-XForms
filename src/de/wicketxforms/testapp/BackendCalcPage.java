package de.wicketxforms.testapp;

import java.io.IOException;
import java.io.InputStream;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import de.wicketxforms.WicketXFormsException;


public class BackendCalcPage extends WebPage {

  private static final long serialVersionUID = 1L;
  
  
  public BackendCalcPage(final PageParameters parameters) {
    
    super(parameters);
   
    
    InputStream xformsDocument = this.getClass().getResourceAsStream("/resources/wxf_backend_calculator.xml");
    
    try {
      
      add(HomePage.createBackendCalculator(getCssContent(), xformsDocument));
      
    } catch (WicketXFormsException e) {
      add(new Label("xfComponent", "Fehler beim Erzeugen der Komponente"));
    }
  }
  
  private String getCssContent() {
    
      InputStream cssFile = this.getClass().getResourceAsStream("/resources/wxf_backend_calculator.css");
      String cssContent = HomePage.convertStreamToString(cssFile);
      
      try {
        cssFile.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
      
      return cssContent;
  }
}
