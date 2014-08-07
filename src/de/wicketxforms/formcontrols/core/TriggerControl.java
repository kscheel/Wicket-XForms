package de.wicketxforms.formcontrols.core;

import org.apache.wicket.Component;
import org.w3c.dom.Element;

import de.wicketxforms.formcontrols.AbstractStyleUpdatingFormControl;
import de.wicketxforms.formcontrols.XFormsCssClass;
import de.wicketxforms.processor.ProcessorID;

/**
 * The XForms trigger FormControl.
 *
 * @see <a href="http://www.w3.org/TR/xforms11/#ui-trigger">8.1.8 The trigger Element</a>
 * @author Karl Scheel
 */
public class TriggerControl extends AbstractStyleUpdatingFormControl {
  
  private final TriggerControlPanel triggerPanel;

  public TriggerControl(String wicketID, ProcessorID processorID, Element formControlNode) {

    super(processorID, formControlNode);
    
    triggerPanel = new TriggerControlPanel(wicketID, this);
    callOnStateChangeHooks();
  }

  @Override
  protected XFormsCssClass getCssClass() {
  
    return XFormsCssClass.FC_TRIGGER;
  }

  @Override
  public Component getWicketComponent() {
  
    return triggerPanel;
  }

}
