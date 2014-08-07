package de.wicketxforms.formcontrols.support;

import java.util.Collection;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;

import de.betterform.xml.events.BetterFormEventNames;
import de.wicketxforms.formcontrols.AbstractFormControl;
import de.wicketxforms.formcontrols.XFormsCssClass;
import de.wicketxforms.processor.ProcessorID;

/**
 * The XForms label FormControl. This is a support FormControl and only occurs as a child of
 * other FormControls.
 * 
 * @see <a href="http://www.w3.org/TR/xforms11/#ui-commonelems-label">8.2.1 The label Element</a>
 * @author Karl Scheel
 */
public class LabelControl extends AbstractFormControl {

  private final Label label;
  
  /**
   * Should only be constructed by calling {@link AbstractFormControl#createLabelComponent(String)}
   * from it's parent FormControl.
   */
  public LabelControl(String wicketID, ProcessorID processorID, Element formControlNode) {
  
    super(processorID, formControlNode);
    label = createLabelComponent(wicketID, formControlNode);
  }
  
  private Label createLabelComponent (String wicketID, Element formControlNode) {

    Label label = new Label(wicketID, getLabelText(formControlNode));
    
    applyStyleAttributes(label);
    label.add(createRedrawBehavior(label));
    
    return label;
  }

  @Override
  protected XFormsCssClass getCssClass() {
  
    return XFormsCssClass.SP_LABEL;
  }

  @Override
  public Component getWicketComponent() {
  
    return label;
  }
  
  @Override
  protected Collection<String> getEventsToListenTo() {

    Collection<String> eventTypes = super.getEventsToListenTo();
    eventTypes.add(BetterFormEventNames.STATE_CHANGED);
    
    return eventTypes;
  }
  
  @Override
  protected void onEventReceived(Event e) {

    super.onEventReceived(e);

    if (BetterFormEventNames.STATE_CHANGED.equals(e.getType())) {
      
      String newValue = getLabelText(getFormControlNode());
      label.setDefaultModelObject(newValue);
      sendRedrawMessage();
    }
  }
  
  private static String getLabelText(Node labelNode) {
    
    // Das erste Kindelement ist der Textinhalt
    return labelNode.hasChildNodes()
            ? labelNode.getFirstChild().getTextContent()
            : "";
  }
  
}
