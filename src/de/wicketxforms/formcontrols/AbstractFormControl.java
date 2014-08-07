package de.wicketxforms.formcontrols;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.wicket.Application;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.IWebSocketConnection;
import org.apache.wicket.protocol.ws.api.registry.IKey;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.apache.wicket.protocol.ws.api.registry.PageIdKey;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;

import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.XFormsProcessor;
import de.wicketxforms.DomUtil;
import de.wicketxforms.WicketXFormsException;
import de.wicketxforms.formcontrols.container.GroupControl;
import de.wicketxforms.formcontrols.container.RepeatControl;
import de.wicketxforms.formcontrols.core.InputControl;
import de.wicketxforms.formcontrols.core.OutputControl;
import de.wicketxforms.formcontrols.core.SubmitControl;
import de.wicketxforms.formcontrols.core.TriggerControl;
import de.wicketxforms.formcontrols.support.LabelControl;
import de.wicketxforms.processor.ProcessorID;
import de.wicketxforms.processor.ProcessorRegistry;

/**
 * Base class for any XForms FormControl.
 *
 * @see <a href="http://www.w3.org/TR/xforms11/#controls">8 Core Form Controls</a>
 * @see <a href="http://www.w3.org/TR/xforms11/#ui">9 Container Form Controls</a>
 * @author Karl Scheel
 */
public abstract class AbstractFormControl implements EventListener {

  /** Wicket-XForms namespace */
  protected static final String WICKETXFORMS_NAMESPACE = "http://www.example.org/Wicket-XForms";
  
  /**
   * XForms appearance attribute.
   *
   * @see <a href="http://www.w3.org/TR/xforms11/#attrs-ui-common">The appearance Attribute</a>
   */
  private final String appearance;
  
  /** identifies this FormControl and it's DOM element */
  private final String formControlID;
  
  /** identifies this FormControls XFormsProcessor */
  private final ProcessorID processorID;
  
  /** This FormControls DOM node as an {@link Element} instance */
  private final Element formControlNode;
  
  
  /* -- Construction ---------------------------------------------------------------------------- */
  
  /**
   * Hidden constructor. Use {@link #createFormControlComponent(String, ProcessorID, Element)}
   * to create an instance.
   * 
   * @param processorID
   *          The ID of this FormControls XForms processor.
   * @param formControlNode
   *          The DOM node representing this FormControl.
   */
  protected AbstractFormControl(final ProcessorID processorID, final Element formControlNode) {

    this.processorID     = processorID;
    this.appearance      = formControlNode.getAttribute("appearance");
    this.formControlID   = formControlNode.getAttribute("id");
    this.formControlNode = formControlNode;
    
    // register for events to listen to on this formControl DOM node
    for(String eventType: getEventsToListenTo()) {

      ((EventTarget) this.formControlNode).addEventListener(eventType, this, false);
    }
  }

  /**
   * Creates a Wicket component representing the given XForms FormControl element.
   * 
   * @param wicketID
   *          The components {@code wicket:id}.
   * @param processorID
   *          The ID of this FormControls XForms processor.
   * @param formControlElement
   *          The DOM node representing this FormControl as an {@link Element} instance.
   * @return A new Wicket component representing the given {@code formControlElement} or
   *         {@code null} in case the {@code formControlElement} does not describe a supported
   *         XForms FormControl.
   */
  public static Component createFormControlComponent(String wicketID, ProcessorID processorID,
                                                     Element formControlElement) {

    String controlName = formControlElement.getLocalName();
    AbstractFormControl formControl;
    
    switch (controlName) {

      case "input":
        formControl = new InputControl(wicketID, processorID, formControlElement);
        break;

      case "output":
        formControl = new OutputControl(wicketID, processorID, formControlElement);
        break;
        
      case "trigger":
        formControl = new TriggerControl(wicketID, processorID, formControlElement);
        break;

      case "submit":
        formControl = new SubmitControl(wicketID, processorID, formControlElement);
        break;
        
      case "group":
        formControl = new GroupControl(wicketID, processorID, formControlElement);
        break;

      case "repeat":
        formControl = new RepeatControl(wicketID, processorID, formControlElement);
        break;

      default:
        return null;
    }

    return formControl.getWicketComponent();
  }

  /**
   * Same as {@link #createFormControlComponent(String, ProcessorID, Element)} but prints
   * this FormControls DOM node as additional debug info.
   * 
   * @param wicketID
   *          The components {@code wicket:id}.
   * @param processorID
   *          The ID of this FormControls XForms processor.
   * @param formControlElement
   *          The DOM node representing this FormControl as an {@link Element} instance.
   * @return A new Wicket component representing the given {@code formControlElement} or
   *         {@code null} in case the {@code formControlElement} does not describe a supported
   *         XForms FormControl.
   */
  public static Component createAndPrintFormControlComponent(String wicketID,
                                                             ProcessorID processorID,
                                                             Element formControlElement) {

    Component component = createFormControlComponent(wicketID, processorID, formControlElement);

    if (component != null)
      DomUtil.printToTerminal(formControlElement, "", true);

    return component;
  }

  /* -- Methods to be overriden by subclasses --------------------------------------------------- */

  // TODO getLog()
  
  /**
   * Hook method being called when this FormControl is the target of an event it registered to.
   * Subclasses must implement a {@code super.onEventReceived(e)} call.
   * 
   * @param e
   *          The received event
   * 
   * @see #getEventsToListenTo()
   */
  protected void onEventReceived(Event e) {

    // stop listening to events on Processor shutdown
    if (XFormsEventNames.MODEL_DESTRUCT.equals(e.getType()))
      removeEventListener();
  }
  
  /**
   * Defines on which events this FormControl will listen. Subclasses must add to the already
   * defined events to listen to by retrieving the current collection with a {@code super}-call.
   * 
   * @return The collection of events this FormControl will listen to.
   * @see #onEventReceived(Event)
   * @see DomUtil#getAllPossibleEvents()
   */
  protected Collection<String> getEventsToListenTo() {
    
    
    Collection<String> eventTypes = new ArrayList<String>();
    eventTypes.add(XFormsEventNames.MODEL_DESTRUCT);
    
    return eventTypes;
  }
  
  /**
   * Defines which {@link XFormsCssClass} represents this FormControl. This will be used to add
   * the appropriate CSS class to the HTML-Markup of this FormControl.
   * 
   * @return The {@link XFormsCssClass} representing this FormControl.
   */
  abstract protected XFormsCssClass getCssClass();
  
  /**
   * Retrieves the instance of the Wicket component used to display this FormControl.
   * 
   * @return This FormControls Wicket component.
   */
  abstract public Component getWicketComponent();
  
  /* -------------------------------------------------------------------------------------------- */
  
  private void removeEventListener() {

    for(String eventType: getEventsToListenTo())
      ((EventTarget) this.formControlNode).removeEventListener(eventType, this, false);
  }
  
  /**
   * Applies the attributes "id", "class" and "style" used for CSS styling on the given
   * {@code component}. These attributes will be passed through form the original XForms xml
   * document and the proper {@link XFormsCssClass XFormsCssClasses} will be added as well.
   * 
   * @param component
   *          Wicket component the style attributes should be applied to.
   * 
   * @return The given compoent for further chaining.
   * @see #getCssClass()
   */
  public Component applyStyleAttributes(Component component) {
    
    // set ID
    component.setMarkupId(getFormControlID());
    
    // apply the CSS class values
    modifyClassAttribute(component);
    
    // pass through the style attributes value
    if (formControlNode.hasAttributeNS(WICKETXFORMS_NAMESPACE, "style")) {
      
      String styleAttributeValue = formControlNode.getAttributeNS(WICKETXFORMS_NAMESPACE, "style");
      component.add(new AttributeModifier("style", styleAttributeValue));
    }
    
    return component;
  }
  
  /**
   * Adds the proper {@link XFormsCssClass} to the given Component. Overriden by
   * {@link AbstractStyleUpdatingFormControl} to add additional CSS classes representing the
   * FormControls current state. <br>
   * Is used by {@link #applyStyleAttributes(Component)} and does usually not need to be called
   * directly.
   * 
   * @param component
   */
  protected void modifyClassAttribute(Component component) {
    
    String classValue = getCssClass().toString();
    
    if (formControlNode.hasAttributeNS(WICKETXFORMS_NAMESPACE, "class"))
      classValue += " " + formControlNode.getAttributeNS(WICKETXFORMS_NAMESPACE, "class");
    
    component.add(new AttributeModifier("class", classValue));
  }

  /**
   * Creates a Wicket component representing the XForms label child of this FormControl.
   * 
   * @param wicketID
   *          The labels {@code wicket:id}.
   * @return A new Wicket label or null, if this FormControls DOM node does not have a label child
   *         element.
   */
  public Component createLabelComponent(String wicketID) {
    
    Element labelNode = DomUtil.getLabelChild(formControlNode);
    
    if (labelNode == null)
      return null;
    
    LabelControl label = new LabelControl(wicketID, processorID, labelNode);
    return label.getWicketComponent();
  }

  /**
   * Constructs a Behavior triggering a redraw upon the reception of a {@link RedrawPushMessage}
   * for this FormControl.<br>
   * RedrawPushMessages are sent by {@link #sendRedrawMessage()}.
   * 
   * @param component
   *          The Wicket component to redraw.
   * 
   * @return A {@link RedrawBehavior} for this FormControl.
   * 
   * @see #sendRedrawMessage()
   */
  public RedrawBehavior createRedrawBehavior(Component component) {

    return new RedrawBehavior(component, getXfProcessorID(), getFormControlID());
  }
  
  /**
   * Triggers a redraw of this FormControls Wicket components that have a {@link RedrawBehavior}
   * attached.
   * 
   * @see #createRedrawBehavior(Component)
   */
  protected void sendRedrawMessage() {
    
    IWebSocketConnection connection = getWebSocketConnection();

    if (connection != null && connection.isOpen()) {

      // TODO dbg log
      System.out.println("====== Sending redraw message to " + formControlID);
      connection.sendMessage(new RedrawPushMessage(processorID, formControlID));
    } else {
      // TODO error log
      System.out.println("====== Redraw message could not be sent...");
      System.out.println("====== WebSocket connection: " + connection);
    }
  }
  
  /**
   * @return The {@link IWebSocketConnection} for this FormControls Wicket component.
   */
  private IWebSocketConnection getWebSocketConnection() {

    Component                    component          = getWicketComponent();
    Application                  application        = component.getApplication();
    WebSocketSettings            settings           = WebSocketSettings.Holder.get(application);
    IWebSocketConnectionRegistry connectionRegistry = settings.getConnectionRegistry();

    String sessionID   = component.getSession().getId();
    IKey connectionKey = new PageIdKey(component.getPage().getPageId());
    
    return connectionRegistry.getConnection(application, sessionID, connectionKey);
  }

  /**
   * @return the appearance
   */
  public final String getAppearance() {

    return appearance;
  }

  /**
   * @return the formControlID
   */
  public final String getFormControlID() {

    return formControlID;
  }

  /**
   * @return the processorID
   */
  public final ProcessorID getXfProcessorID() {

    return processorID;
  }
  
  /**
   * @return This FormControls DOM-Node as an {@link Element}-Instance.
   */
  public final Element getFormControlNode() {
    
    return formControlNode;
  }
  
  /**
   * Get's the schema type this FormControl is bound to.<br>
   * E.g. {@code xsd:string} or {@code xsd:boolean}. Returns the empty string if no type binding
   * is present.
   * 
   * @return This FormControls schema type.
   */
  public final String getTypeBinding() {
    
    return DomUtil.getBfDataAttributeValue(formControlNode, "type");
  }
  
  /**
   * Receives the content of the bf:data child of this FormControls DOM node. Which is the value of
   * the model item an input or output control is bound to.
   * 
   * @return The value of the model item this FormControl is bound to.
   */
  public final String getBfDataValue() {

    return DomUtil.getNodeText(formControlNode, "data", NamespaceConstants.BETTERFORM_NS);
  }

  /**
   * @return This FormControls XForms processor.
   * @throws WicketXFormsException
   *           Thrown if this FormControls XForms Processor can not be found.
   */
  protected final XFormsProcessor getXfProcessor() throws WicketXFormsException {

    XFormsProcessor xfProcessor = ProcessorRegistry.getProcessor(processorID);

    if (xfProcessor == null)
      throw new WicketXFormsException("XformsProcessor for FormControl "
                                      + this.getClass().getSimpleName()
                                      + " [" + formControlID + "] not found!");

    return xfProcessor;
  }

  /**
   * Override {@link #onEventReceived(Event)} to process events.
   */
  @Override
  public final void handleEvent(Event e) {

    // TODO if dbg > log
    DomUtil.printEvent(this.getClass().getSimpleName() + " [" + formControlID + "] received Event:", e);

    // Check wether this FormControl is actually the events target
    if (e.getTarget().equals(formControlNode))
      onEventReceived(e);
  }
}
