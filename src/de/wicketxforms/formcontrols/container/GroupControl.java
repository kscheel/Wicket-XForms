package de.wicketxforms.formcontrols.container;

import java.util.Collection;

import org.apache.wicket.Component;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;

import de.betterform.xml.events.BetterFormEventNames;
import de.wicketxforms.formcontrols.AbstractStyleUpdatingFormControl;
import de.wicketxforms.formcontrols.XFormsCssClass;
import de.wicketxforms.processor.ProcessorID;


/**
 * The XForms group FormControl.
 * 
 * @see <a href="http://www.w3.org/TR/xforms11/#ui-group">9.1.1 The group Element</a>
 * @author Karl Scheel
 */
public class GroupControl extends AbstractStyleUpdatingFormControl {

  private final GroupControlPanel groupControlPanel;
  
  public GroupControl(String wicketID, ProcessorID processorID, Element formControlNode) {

    super(processorID, formControlNode);
    groupControlPanel = new GroupControlPanel(wicketID, this);
  }

  @Override
  protected XFormsCssClass getCssClass() {
    
    return XFormsCssClass.FC_GROUP;
  }

  @Override
  public Component getWicketComponent() {
  
    return groupControlPanel;
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

    if (BetterFormEventNames.STATE_CHANGED.equals(e.getType()))
      sendRedrawMessage();
  }
}
