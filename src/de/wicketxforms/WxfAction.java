package de.wicketxforms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joox.JOOX;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.wicketxforms.processor.ProcessorRegistry;

/**
 * Represents the custom Wicket-XForms action element. This class is advertised to betterFORM
 * in the betterFORM configuration file.
 * 
 * @author Karl Scheel
 * @see betterform-config.xml
 */
public class WxfAction extends de.betterform.xml.xforms.action.AbstractAction {

  private static final Log LOGGER = LogFactory.getLog(WxfAction.class);
  
  // additional attributes of an Wicket-Xforms action element
  private static final String ATTRIBUTE_NAME     = "name";
  private static final String ATTRIBUTE_MODEL    = "model";
  private static final String ATTRIBUTE_INSTANCE = "instance";
  
  /** model instance to be passed to the XFormsComponents onAction() callback hook */
  private final Document instance;
  private final String name;
  
  
  /* -- construction ---------------------------------------------------------------------------- */
  
  /**
   * The constructor as expected by betterFORM.
   */
  public WxfAction(Element element, Model model) {
  
    super(element, model);
    
    this.name     = determineActionName(element);
    this.instance = determineModelInstance(element);
  }
  

  private String determineActionName(Element element) {
    
    if (element.hasAttribute(ATTRIBUTE_NAME))
      return element.getAttribute(ATTRIBUTE_NAME);
    
    getLogger().warn("Wicket-XForms action " + DOMUtil.getCanonicalPath(element)
                     + " is missing it's name attribute.");
    return null;
  }
  

  private Document determineModelInstance(Element element) {
    
    Model model = determineModel(element);
  
    String instanceID = element.hasAttribute(ATTRIBUTE_INSTANCE)
                         ? element.getAttribute(ATTRIBUTE_INSTANCE) // use defined instance
                         : "";                                      // use default instance
    
    return model.getInstanceDocument(instanceID);
  }
  
  
  private Model determineModel(Element element) {
    
    if (!element.hasAttribute(ATTRIBUTE_MODEL))
      return getModel();
      
    String modelID = element.getAttribute(ATTRIBUTE_MODEL);
        
    try {
      
      return getEnclosingXFormsContainer().getContainerObject().getModel(modelID);
      
    } catch (XFormsException e) {
      getLogger().error("Could not resolve model attribute for Wicket-XForms action "
                        + DOMUtil.getCanonicalPath(element) + " with name=\""
                        + name + "\"!", e);
      return getModel();
    }
  }
  
  
  /* -------------------------------------------------------------------------------------------- */
  
  
  @Override
  protected Log getLogger() {
  
    return LOGGER;
  }
  

  @Override
  public void perform() throws XFormsException {

    XFormsProcessor xfProcessor = getContainerObject().getProcessor();
    XFormsComponent xfComponent = ProcessorRegistry.getComponent(xfProcessor);
    
    if (xfComponent == null) {
      Exception e = new WicketXFormsException("Unable to invoke onAction() for Wicket-XForms action "
                                              + DOMUtil.getCanonicalPath(element) + " with name=\""
                                              + name + "\"! The actions XFormsComponent was not found.");
      throw new XFormsException(e);
    }

    // Call the Wicket components hook method
    xfComponent.onAction(name, JOOX.$(instance));
  }
}
