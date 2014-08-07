package de.wicketxforms.processor;

import java.io.InputStream;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import de.betterform.xml.config.Config;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.wicketxforms.WicketXFormsException;
import de.wicketxforms.XFormsComponent;

/**
 * TODO
 *
 * @author Karl Scheel
 */
public class ProcessorRegistry {

  /** Pfad zur Konfigurationsdatei fuer den betterFORM-processor. */
  private static final String BETTERFORM_CONFIG_FILE = "/resources/betterform-config.xml";

  // betterForm-Konfiguration initialisieren
  static {
    InputStream configStream = ProcessorRegistry.class.getResourceAsStream(BETTERFORM_CONFIG_FILE);

    try {
      Config.getInstance(configStream);
      System.out.println("~~ betterFORM configuration initalized");

    } catch (XFormsConfigException e) {

      // TODO Log-Ausgabe implementieren
      e.printStackTrace();
      System.exit(1);
    }
  }
  
  // TODO maybe use ConcurrentHashMap for thread safety?
  private static final Map<ProcessorID, WxfProcessor> instances = new HashMap<>();
  
  /* -------------------------------------------------------------------------------------------- */


  // no instance allowed
  private ProcessorRegistry() {}
  
  public static ProcessorID createProcessor(InputStream xformsDocument,
                                                  XFormsComponent component, String baseURI)throws WicketXFormsException {

    return createProcessor((Object) xformsDocument, component, baseURI);
  }
  
  public static ProcessorID createProcessor(InputSource xformsDocument,
                                                  XFormsComponent component, String baseURI) throws WicketXFormsException {

    return createProcessor((Object) xformsDocument, component, baseURI);
  }
  
  public static ProcessorID createProcessor(Node xformsDocument,
                                                  XFormsComponent component, String baseURI) throws WicketXFormsException {

    return createProcessor((Object) xformsDocument, component, baseURI);
  }
  
  public static ProcessorID createProcessor(URI xformsDocument,
                                                  XFormsComponent component, String baseURI) throws WicketXFormsException {

    return createProcessor((Object) xformsDocument, component, baseURI);
  }
  
  private static ProcessorID createProcessor(Object xformsDocument, XFormsComponent component,
                                                   String baseURI) throws WicketXFormsException {

    WxfProcessor xfProcessor = new WxfProcessor(component);
    ProcessorID instanceID   = ProcessorID.createUniqueId(instances.keySet());
    
    instances.put(instanceID, xfProcessor);
    xfProcessor.initProcessor(xformsDocument, component, baseURI);

    return instanceID;
  }

  /**
   * TODO
   *
   * @param id
   * @throws WicketXFormsException
   */
  public static void shutdownProcessor(ProcessorID id) throws WicketXFormsException {

    XFormsProcessor instance = instances.remove(id);

    if (instance != null) {
      try {
        instance.shutdown();
        
      } catch (XFormsException e) {
        throw new WicketXFormsException(e);
      }
    }
    else
      // TODO warn log
      System.out.println("Could not shutdown Processor with ID " + id + ". Instance not found.");
  }

  /**
   * TODO
   *
   * @param id
   * @return Der XFormsProcessor oder {@code null} falls unter der {@code id} kein Kontext
   */
  public static XFormsProcessor getProcessor(ProcessorID id) {

    return instances.get(id);
  }
  
  /**
   * Retrieves the {@link XFormsComponent} a specific processor instance is associated with.
   * 
   * @param xfProcessor
   * @return The {@link XFormsComponent} or {@code null} if the given xfProcessor is not known to
   *         the XFormsProcessorRegistry.
   */
  public static XFormsComponent getComponent(XFormsProcessor xfProcessor) {
    
    for (WxfProcessor xfp: instances.values()) {
      
      if (xfp.equals(xfProcessor))
        return xfp.getComponent();
    }

    return null;
  }
  
}
