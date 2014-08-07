package de.wicketxforms.formcontrols.container;

import org.apache.wicket.Component;
import org.w3c.dom.Element;

import de.wicketxforms.formcontrols.AbstractFormControl;
import de.wicketxforms.formcontrols.XFormsCssClass;
import de.wicketxforms.processor.ProcessorID;

/**
 * This represents a {@link RepeatControl}s child.
 * 
 * @author Karl Scheel
 */
class RepeatItem extends AbstractFormControl {
  
  private final RepeatItemPanel repeatItemPanel;
  
  protected RepeatItem(String wicketID, ProcessorID processorID, Element formControlNode,
                       String repeatControlID, int position) {
  
    super(processorID, formControlNode);
    repeatItemPanel = new RepeatItemPanel(wicketID, this, repeatControlID, position);
  }

  @Override
  protected XFormsCssClass getCssClass() {

    return XFormsCssClass.PS_REPEAT_ITEM;
  }
  
  @Override
  public Component getWicketComponent() {
  
    return repeatItemPanel;
  }
  
  RepeatItemPanel getRepeatItemPanel() {
  
    return repeatItemPanel;
  }
}
