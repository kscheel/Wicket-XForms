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
 * The XForms output FormControl.
 * 
 * @see <a href="http://www.w3.org/TR/xforms11/#ui-output">8.1.5 The output Element</a>
 * @author Karl Scheel
 */
public class OutputControl extends AbstractStyleUpdatingFormControl {

  private final OutputControlPanel outputPanel;
  
  
  public OutputControl(String wicketID, ProcessorID processorID, Element formControlNode) {
  
    super(processorID, formControlNode);
    outputPanel = new OutputControlPanel(wicketID, this);
  }

  @Override
  protected XFormsCssClass getCssClass() {
  
    return XFormsCssClass.FC_OUTPUT;
  }

  @Override
  public Component getWicketComponent() {
  
    return outputPanel;
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
      
      outputPanel.updateModelValue(getBfDataValue());
      // Redraw has to occur always because the state properties like "valid" might have changed
      sendRedrawMessage();
    }
  }
}
