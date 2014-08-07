package de.wicketxforms.formcontrols.core;

import org.apache.wicket.Component;
import org.w3c.dom.Element;

import de.wicketxforms.formcontrols.AbstractStyleUpdatingFormControl;
import de.wicketxforms.formcontrols.XFormsCssClass;
import de.wicketxforms.processor.ProcessorID;

/**
 * The XForms submit FormControl.
 *
 * @see <a href="http://www.w3.org/TR/xforms11/#ui-submit">8.1.9 The submit Element</a>
 * @author Karl Scheel
 */
public class SubmitControl extends AbstractStyleUpdatingFormControl {
  
  private final TriggerControlPanel submitPanel;

  public SubmitControl(String wicketID, ProcessorID processorID, Element formControlNode) {

    super(processorID, formControlNode);
    
    submitPanel = new TriggerControlPanel(wicketID, this);
    callOnStateChangeHooks();
  }

  @Override
  protected XFormsCssClass getCssClass() {
  
    return XFormsCssClass.FC_SUBMIT;
  }

  @Override
  public Component getWicketComponent() {
  
    return submitPanel;
  }

}
