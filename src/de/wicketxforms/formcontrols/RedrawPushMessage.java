package de.wicketxforms.formcontrols;

import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

import de.wicketxforms.processor.ProcessorID;


/**
 * Push message triggering a redraw on the targeted Wicket component with an attached
 * {@link RedrawBehavior}.
 * 
 * @author Karl Scheel
 * @see {@link AbstractFormControlPanel#createRedrawBehavior(org.apache.wicket.Component)
 * @see {@link AbstractFormControlPanel#sendRedrawMessage()
 */
class RedrawPushMessage implements IWebSocketPushMessage {
  
  private final ProcessorID xfProcessorID;
  private final String      formControlID;
  
  /**
   * @param xfProcessorID
   *          ID of the XForms Processor managing the FormControl at which this message is targeted.
   * @param formControlID
   *          ID of the FormControl at which this message is targeted.
   */
  RedrawPushMessage(ProcessorID xfProcessorID, String formControlID) {
    
    this.xfProcessorID = xfProcessorID;
    this.formControlID = formControlID;
  }
  
  ProcessorID getXFormsProcessorID() {
  
    return xfProcessorID;
  }
  
  String getTargetControlID() {
    return formControlID;
  }
}
