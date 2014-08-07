package de.wicketxforms.processor;

import java.io.InputStream;
import java.net.URI;
import java.util.Objects;

import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.xml.sax.InputSource;

import de.betterform.xml.config.Config;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.AbstractProcessorDecorator;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsException;
import de.wicketxforms.DomUtil;
import de.wicketxforms.WicketXFormsException;
import de.wicketxforms.WxfAction;
import de.wicketxforms.XFormsComponent;


/**
 * TODO
 * 
 * @author Karl Scheel
 */
class WxfProcessor extends AbstractProcessorDecorator {
  
  private final XFormsComponent component;
  
  WxfProcessor(XFormsComponent component) {

    super();
    this.component = component;
  }
  
  @Override
  public void init() throws XFormsException {

    this.configuration = Config.getInstance();
    this.addEventListeners(); // TODO nur bei DEBUG
    this.xformsProcessor.init();
  }
  
  @Override
  public void handleEvent(Event e) {

    DomUtil.printEvent("Processor Event:", e);
  }
  
  @Override
  public boolean equals(Object obj) {
  
    if (obj instanceof XFormsProcessorImpl)
      return this.xformsProcessor == obj;

    return super.equals(obj);
  }
  
  @Override
  public int hashCode() {
    
    return Objects.hash(xformsProcessor, component);
  };

  /**
   * @return the component
   */
  XFormsComponent getComponent() {
  
    return component;
  }

  /**
   * Initializes the betterFORM XFormsProcessor.
   * <p>
   * Initilization must occur, after the processor instance is accessible through the
   * {@link ProcessorRegistry}. Otherwise {@link WxfAction}-Instances triggered by
   * {@link XFormsEventNames#MODEL_CONSTRUCT_DONE} events will fail!
   * 
   * @param xformsDocument
   *          Either an {@link InputStream}, {@link InputSource}, {@link Node} or {@link URI}
   *          instance representing the Wicket-XForms-Document.
   * @param component
   *          The {@link XFormsComponent} representing the UI described through the xformsDocument.
   * @param baseURI
   *          Used for resolving any URI used within betterFORM and the loaded XForms.
   * @throws WicketXFormsException
   *           Should an error have occured during initilization.
   * @see <a href="http://betterform.wordpress.com/short-introduction-to-the-xformsprocessor-api/">
   *      betterFORM-Dokumentation: "Short introduction to the XFormsProcessor API"</a>
   */
  void initProcessor(Object xformsDocument, XFormsComponent component, String baseURI)
      throws WicketXFormsException {

    try {
      setXFormsDocument(xformsDocument);
      setBaseURI(baseURI);
      init();
      
    } catch (XFormsException e) {
      throw new WicketXFormsException("Error during initialization of the betterForm XForms processor!", e);
    }
  }
  
  private void setXFormsDocument(Object xfDocument)
      throws WicketXFormsException, XFormsException {
  
    if (xfDocument instanceof InputStream)
      setXForms((InputStream) xfDocument);
    
    else if (xfDocument instanceof InputSource)
      setXForms((InputSource) xfDocument);
    
    else if (xfDocument instanceof Node)
      setXForms((Node) xfDocument);
    
    else if (xfDocument instanceof URI)
      setXForms((URI) xfDocument);
    
    else
      throw new WicketXFormsException("Wrong xformsDocument object type!");
  }
}