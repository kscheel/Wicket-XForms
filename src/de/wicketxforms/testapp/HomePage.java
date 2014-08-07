package de.wicketxforms.testapp;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.Model;
import org.joox.Match;

import de.wicketxforms.WicketXFormsException;
import de.wicketxforms.XFormsComponent;

/**
 * TODO
 * 
 * @author Karl Scheel
 */
public class HomePage extends WebPage {

  private static final long serialVersionUID = 1L;
  
  // private static final String BASE_URI = "file://home/karl/Documents/thesis/wxf_workspace/Wicket-XForms/src/resources/";
  public static final String BASE_URI;
  
  // Haelt einen neu angelegten Kunden fuer die Demos createCustomer und listCustomers
  private static Customer newCustomer;
  
  static
  {
//      Logger bfLogger = Logger.getLogger(XFormsProcessorImpl.class);
//      bfLogger = (Logger) bfLogger.getParent();
//
//      bfLogger.setLevel(Level.ALL);
//      bfLogger.addAppender(new ConsoleAppender(
//                 new PatternLayout("%c\n%m\n\n")));

    // Base URI bestimmen
    URL someFile = WebPage.class.getResource("/resources/Wicket-XForms.xsd");
    
    String basePath = "";
    
    try {
      
      basePath = someFile.toURI().getPath();
      basePath = basePath.replace("Wicket-XForms.xsd", "");
      basePath = "file:/" + basePath;
      System.out.println("Base URI: "+ basePath);
      
    } catch (URISyntaxException e) {
      System.out.println(e);
    }

    BASE_URI = basePath;
  }

  private class LinkListView extends ListView<AbstractLink> {
    private static final long serialVersionUID = 1L;

    public LinkListView(String id, List<? extends AbstractLink> list) {
      super(id, list);
    }

    @Override
    protected void populateItem(ListItem<AbstractLink> item) {
      item.add(item.getModelObject());
    }

  }
  
  /* -------------------------------------------------------------------------------------------- */

  public HomePage() {

    // Fuer die Beispielformulare soll eine Liste mit Links generiert werden.
    
    Map<String, String> formulare = new LinkedHashMap<>();
    formulare.put("simpler Addierer 1",     "/resources/wxf_calculator.xml");
    formulare.put("simpler Addierer 2",     "/resources/wxf_calculator2.xml");
    formulare.put("Backend-Taschenrechner", "/resources/wxf_backend_calculator.xml");
    //formulare.put("input-Variationen",      "/resources/wxf_input_variationen.xml");
    formulare.put("group-Test",             "/resources/wxf_group_test.xml");
    formulare.put("repeat-Test",            "/resources/wxf_repeat_test.xml");
    //formulare.put("Typen-Test",             "/resources/wxf_type_validation_test.xml");
    formulare.put("Demo: Repeats",          "/resources/wxf_demoRepeat.xml");
    formulare.put("Demo: Core Controls",    "/resources/wxf_demoCoreControls.xml");
    formulare.put("Demo: Kunden anlegen",   "/resources/wxf_demoCreateCustomer.xml");
    formulare.put("Demo: Kunden auflisten",   "/resources/wxf_demoCustomerList.xml");

    add(new LinkListView("repeatedLink", createLinkList(formulare)));
    
    // Die erste XForms Komponente direkt anzeigen
    try {
      add(new XFormsComponent("xfComponent",
                              this.getClass().getResourceAsStream("/resources/wxf_calculator.xml"),
                              BASE_URI));
    } catch (WicketXFormsException e) {
      e.printStackTrace();
    }
  }

  /**
   * 
   * @param formulare
   * @return
   */
  private List<AbstractLink> createLinkList(Map<String, String> formulare) {

    List<AbstractLink> links = new ArrayList<>(formulare.size());

    for (Map.Entry<String, String> entry : formulare.entrySet()) {
      String key = entry.getKey();
      final String value = entry.getValue();

      links.add(new Link<String>("link") {

        private static final long serialVersionUID = 1L;

        @Override
        public void onClick() {
          
          replaceXFormsComponent(value);
        }
      }.setBody(Model.of(key)));
    }

    return links;
  }
  
  /**
   * 
   * @param xfFile
   */
  private void replaceXFormsComponent(String xfFile) {
      
    InputStream xfFileStream = this.getClass().getResourceAsStream(xfFile);
    Component xfComponent;
    
    String cssFilename = xfFile.replace(".xml", ".css");
    String cssContent = null;
    
    // Existiert für die XML-Datei eine eigene CSS Datei?
    if (this.getClass().getResource(cssFilename) != null) {

      InputStream cssFile = this.getClass().getResourceAsStream(cssFilename);
      cssContent = convertStreamToString(cssFile);
      
      try {
        cssFile.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    try {
      
      switch (xfFile) {
        
        case "/resources/wxf_demoCreateCustomer.xml":
          xfComponent = createDemoNewCustomer(cssContent, xfFileStream);
          break;
          
          case "/resources/wxf_demoCustomerList.xml":
            xfComponent = createDemoCustomerList(cssContent, xfFileStream);
            break;
        
        case "/resources/wxf_backend_calculator.xml":
          xfComponent = createBackendCalculator(cssContent, xfFileStream);
          break;
        
        default:
            if (cssContent == null)
              xfComponent = new XFormsComponent("xfComponent", xfFileStream, BASE_URI);
            else
              xfComponent = createXfComponentWithCustomCSS(cssContent, xfFileStream);
          break;
      }
      replace(xfComponent);

    } catch (WicketXFormsException e) {
      e.printStackTrace();
    }
  }


  /**
   * 
   * @param cssContent
   * @param xfFileStream
   * @return
   * @throws WicketXFormsException
   */
  private Component createDemoCustomerList(final String cssContent, InputStream xfFileStream) throws WicketXFormsException {
  
    return new XFormsComponent("xfComponent", xfFileStream, BASE_URI) {

      private static final long serialVersionUID = 1L;
      
      @Override
      protected void onAction(String name, Match instance) {
      
        System.out.println(">> customAction " + name);

        if ("initCustomerData".equals(name))
          initInstance(instance);
      }

      /**
       * 
       * @param instance
       */
      private void initInstance(Match instance) {
        
        // vorhandene Dummy-Daten entfernen
        instance.empty();
        
        // pro Kunde ein XML-Element anhaengen
        for (Customer customer: Customer.getSampleCustomers())
          instance.append(customer.toXml());
        
        if (newCustomer != null)
          instance.append(newCustomer.toXml());
      }
    };
  }


  /**
   * 
   * @param cssContent
   * @param xfFileStream
   * @return
   * @throws WicketXFormsException
   */
  private Component createDemoNewCustomer(final String cssContent, InputStream xfFileStream) throws WicketXFormsException {
  
    return new XFormsComponent("xfComponent", xfFileStream, BASE_URI) {

      private static final long serialVersionUID = 1L;
      
      @Override
      protected void onSubmit(Match instance) {
        
        System.out.println(">> createCustomer submit");
        newCustomer = Customer.fromXML(instance.find("customer"));
      }

      @Override
      public void renderHead(org.apache.wicket.markup.head.IHeaderResponse response) {
        
        CssHeaderItem cssItem = CssHeaderItem.forCSS(cssContent, null);

        response.render(cssItem);
      }
    };
  }

  /**
   * 
   * @param cssContent
   * @param xfFileStream
   * @return
   * @throws WicketXFormsException
   */
  protected static Component createBackendCalculator(final String cssContent, InputStream xfFileStream) throws WicketXFormsException {
  
    return new XFormsComponent("xfComponent", xfFileStream, BASE_URI) {

      private static final long serialVersionUID = 1L;
      
      @Override
      protected void onAction(String name, Match instance) {

        System.out.println(">> customAction " + name);
        
        Float z1 = instance.attr("zahl1", Float.class);
        Float z2 = instance.attr("zahl2", Float.class);
        
        if (z1 != null && z2 != null) {

          Float res = calculate(name, z1, z2);
          instance.find("ergebnis").text(res.toString());
          System.out.println(">> Ergebnis: " + res.toString());
        }
      }
      
      // Berechnung durchfuehren
      private Float calculate(String actionName, Float z1, Float z2) {
        
        switch (actionName) {
          
          case "add":
            return z1 + z2;
            
          case "subtract":
            return z1 - z2;
            
          case "multiply":
            return z1 * z2;
            
          case "divide":
            return z1 / z2;
            
          default:
            return z1;
        }
      }

      @Override
      public void renderHead(org.apache.wicket.markup.head.IHeaderResponse response) {
        
        CssHeaderItem cssItem = CssHeaderItem.forCSS(cssContent, null);

        response.render(cssItem);
      }
    };
  }

  /**
   * 
   * @param cssContent
   * @param xfFileStream
   * @return
   * @throws WicketXFormsException
   */
  private Component createXfComponentWithCustomCSS(final String cssContent, InputStream xfFileStream) throws WicketXFormsException {
  
    return new XFormsComponent("xfComponent", xfFileStream, BASE_URI) {

      private static final long serialVersionUID = 1L;

      @Override
      public void renderHead(org.apache.wicket.markup.head.IHeaderResponse response) {
        
        CssHeaderItem cssItem = CssHeaderItem.forCSS(cssContent, null);

        response.render(cssItem);
      };
    };
  }

  static String convertStreamToString(java.io.InputStream is) {
  
    @SuppressWarnings("resource")
    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
    return s.hasNext() ? s.next() : "";
  }
}
