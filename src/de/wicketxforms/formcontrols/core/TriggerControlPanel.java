package de.wicketxforms.formcontrols.core;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.wicketxforms.formcontrols.AbstractFormControl;
import de.wicketxforms.formcontrols.IStyleUpdatingFormControlComponent;
import de.wicketxforms.processor.ProcessorID;
import de.wicketxforms.processor.ProcessorRegistry;

/**
 * The Wicket component representing an XForms trigger element. Also used by the
 * {@link SubmitControl} as the trigger and submit are visually the same.
 *
 * @see TriggerControl
 * @see SubmitControl
 * @see <a href="http://www.w3.org/TR/xforms11/#ui-trigger">8.1.8 The trigger Element</a>
 * @see <a href="http://www.w3.org/TR/xforms11/#ui-submit">8.1.9 The submit Element</a>
 * 
 * @author Karl Scheel
 */
class TriggerControlPanel extends Panel implements IStyleUpdatingFormControlComponent {

  private static final long serialVersionUID = 1L;
  
  private final ProcessorID xfProcessorID;
  private final String formControlID;
  
  private AbstractLink trigger;

  TriggerControlPanel(String wicketID, AbstractFormControl formControl) {

    super(wicketID);
    
    this.xfProcessorID = formControl.getXfProcessorID();
    this.formControlID = formControl.getFormControlID();
    
    addControlSpecificComponents(formControl);
  }

  protected void addControlSpecificComponents(AbstractFormControl formControl) {

    String fragmentMarkupId = ("minimal".equals(formControl.getAppearance()))
                               ? "triggerFragment_minimal"
                               : "triggerFragment_full";
    
    Fragment fragment = new Fragment("fragmentContainer", fragmentMarkupId, this);
    
    // Ein Wicket-Link funktioniert auch mit einem HTML <button> Tag
    trigger = createLink();
    formControl.applyStyleAttributes(trigger);
    
    Component label = formControl.createLabelComponent("label");
    if (label == null)
      throw new RuntimeException("The Trigger-Control [" + formControlID
                                 + " ] is missing a label child element!");
    trigger.add(label);
    
    trigger.add(formControl.createRedrawBehavior(trigger));
    
    fragment.add(trigger);
    add(fragment);
  }

  private AbstractLink createLink() {

    return new AjaxLink<String>("trigger") {
      private static final long serialVersionUID = 1L;

      @Override
      public void onClick(AjaxRequestTarget target) {
        executeTriggerAction();
      }
    };
  }

  private void executeTriggerAction() {

    XFormsProcessor xfProcessor = ProcessorRegistry.getProcessor(xfProcessorID);
    
    if (xfProcessor != null)
      try {

        // Notify the DOM document of the trigger/submit element activation
        xfProcessor.dispatch(formControlID, DOMEventNames.ACTIVATE);

      } catch (XFormsException e) {
        System.out.println("Error while executing Trigger [" + formControlID + "]:"); // TODO error log
        e.printStackTrace();
      }
    else
      System.out.println("Cannot process Trigger [" + formControlID
                         + "] activation. The FormControl can't find it's XFormsProcessor!");
  }
  
  @Override
  public void onRelevantStateChange(boolean relevant) {
    
    trigger.setEnabled(relevant);
  }

  @Override
  public void onValidStateChange(boolean valid) {
  
    // nothing to do here
  }

  @Override
  public void onReadOnlyStateChange(boolean readonly) {

    // nothing to do here
  }

  @Override
  public void onRequiredStateChange(boolean required) {

    // nothing to do here
  }

}
