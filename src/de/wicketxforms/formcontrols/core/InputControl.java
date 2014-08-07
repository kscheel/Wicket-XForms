package de.wicketxforms.formcontrols.core;

import java.util.Collection;

import org.apache.wicket.Component;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;

import de.betterform.xml.events.BetterFormEventNames;
import de.wicketxforms.formcontrols.AbstractStyleUpdatingFormControl;
import de.wicketxforms.formcontrols.XFormsCssClass;
import de.wicketxforms.processor.ProcessorID;

/**
 * The XForms input FormControl.
 * 
 * @see <a href="http://www.w3.org/TR/xforms11/#ui-input">8.1.2 The input Element</a>
 * @author Karl Scheel
 */
public class InputControl extends AbstractStyleUpdatingFormControl {
  
  private final InputControlPanel inputPanel;

  
  public InputControl(String wicketID, ProcessorID processorID, Element formControlNode) {

    super(processorID, formControlNode);
    
    inputPanel = new InputControlPanel(wicketID, this);
    callOnStateChangeHooks();
  }
  
  @Override
  protected XFormsCssClass getCssClass() {
  
    return XFormsCssClass.FC_INPUT;
  }
  
  @Override
  public Component getWicketComponent() {
  
    return inputPanel;
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
        
      inputPanel.updateModelValue(getBfDataValue());
      sendRedrawMessage();
    }
  }
}
