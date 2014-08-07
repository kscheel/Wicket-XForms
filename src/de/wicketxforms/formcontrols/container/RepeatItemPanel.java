package de.wicketxforms.formcontrols.container;

import org.apache.wicket.ajax.AjaxEventBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebMarkupContainer;

import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.wicketxforms.formcontrols.AbstractFormControl;
import de.wicketxforms.processor.ProcessorID;
import de.wicketxforms.processor.ProcessorRegistry;

/**
 * The Wicket component representing a child of a {@link RepeatControl}.
 * A repeat-item is essentially the same as an xforms group control without label and
 * appearance="minimal", therfore this class extends {@link GroupControlPanel}. Additionally it
 * handles updating the repeat-controls current index.
 * 
 * @see RepeatItem
 * @author Karl Scheel
 */
class RepeatItemPanel extends GroupControlPanel {

  private static final long serialVersionUID = 1L;

  private final ProcessorID xfProcessorID;
  private final String repeatControlID; // identifies the parent repeat
  private final int position;           // this repeat items position in the parent repeat
  
  private WebMarkupContainer groupComponent;

  
  RepeatItemPanel(String wicketID, RepeatItem repeatItem,
                  String repeatControlID, int position) {
  
    super(wicketID, repeatItem);
    
    this.xfProcessorID = repeatItem.getXfProcessorID();
    this.repeatControlID = repeatControlID;
    this.position = position;
  }
  
  @Override
  protected void addControlSpecificComponents(AbstractFormControl groupControl) {
  
    groupComponent = createGroup(groupControl, false);
    groupComponent.add(new AjaxEventBehavior("click") {

      private static final long serialVersionUID = 1L;

      @Override
      protected void onEvent(AjaxRequestTarget target) {
      
        setRepeatIndex();
      }
    });
    
    add(groupComponent);
  }

  private void setRepeatIndex() {

    XFormsProcessor xfProcessor = ProcessorRegistry.getProcessor(xfProcessorID);
    
    if (xfProcessor != null)
      try {

        // Set the repeat items position as the repeat index
        xfProcessor.setRepeatIndex(repeatControlID, position);

      } catch (XFormsException e) {
        System.out.println("Error while setting repeat index to " + position
                           + "for repeat [" + repeatControlID + "]:"); // TODO error log
        e.printStackTrace();
      }
    else
      System.out.println("Cannot set repeat index " + position + ". The repeat element ["
                         + repeatControlID + "]. The FormControl can't find it's XFormsProcessor!");
  }
  
  WebMarkupContainer getGroupComponent() {
    
    return groupComponent;
  }
  
}
