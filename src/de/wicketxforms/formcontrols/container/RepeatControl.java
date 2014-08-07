package de.wicketxforms.formcontrols.container;

import java.util.Collection;

import org.apache.wicket.Component;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;

import de.betterform.xml.events.BetterFormEventNames;
import de.wicketxforms.formcontrols.AbstractFormControl;
import de.wicketxforms.formcontrols.XFormsCssClass;
import de.wicketxforms.processor.ProcessorID;


/**
 * The XForms repeat FormControl.
 * 
 * @see <a href="http://www.w3.org/TR/xforms/#ui-repeat">9.3.1 The repeat Element</a>
 * @author Karl Scheel
 */
public class RepeatControl extends AbstractFormControl {
  
  private final RepeatControlPanel repeatPanel;
  
  public RepeatControl(String wicketID, ProcessorID processorID, Element formControlNode) {
  
    super(processorID, formControlNode);
    repeatPanel = new RepeatControlPanel(wicketID, this);
  }

  @Override
  protected XFormsCssClass getCssClass() {
  
    return XFormsCssClass.FC_REPEAT;
  }

  @Override
  public Component getWicketComponent() {
  
    return repeatPanel;
  }
  
  @Override
  protected Collection<String> getEventsToListenTo() {

    Collection<String> eventTypes = super.getEventsToListenTo();
    
    eventTypes.add(BetterFormEventNames.ITEM_INSERTED);
    eventTypes.add(BetterFormEventNames.ITEM_DELETED);
    eventTypes.add(BetterFormEventNames.INDEX_CHANGED);
    
    return eventTypes;
  }
  
  @Override
  protected void onEventReceived(Event e) {

    super.onEventReceived(e);
    
    // FIXME unnecessary redraw when INDEX_CHANGED occurs after INSERTED or DELETED
    String event = e.getType();
    if (   BetterFormEventNames.INDEX_CHANGED.equals(event)
        || BetterFormEventNames.ITEM_INSERTED.equals(event)
        || BetterFormEventNames.ITEM_DELETED.equals(event))
    {
      sendRedrawMessage();
    }
  }
}
