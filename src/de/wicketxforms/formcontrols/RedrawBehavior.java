package de.wicketxforms.formcontrols;

import org.apache.wicket.Component;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

import de.wicketxforms.processor.ProcessorID;


/**
 * Behavior triggering a redraw upon the reception of a {@link RedrawPushMessage}.
 * 
 * @author Karl Scheel
 */
class RedrawBehavior extends WebSocketBehavior {
  
  private static final long serialVersionUID = 1L;
  
  // The component to redraw
  private final Component   component;
  private final String      formControlID;
  private final ProcessorID processorID;
  
  /**
   * Use {@link AbstractFormControl#createRedrawBehavior(Component)} to create an instance.
   * 
   * @param component
   * @param processorID
   * @param formControlID
   */
  RedrawBehavior(Component component, ProcessorID processorID, String formControlID) {
  
    this.component     = component;
    this.processorID   = processorID;
    this.formControlID = formControlID;
  }
  
//  @Override
//  protected void onConnect(ConnectedMessage message) {
//
//    System.out.println("====== WebSocket connection established for FormControl " + formControlID);
//  }
//
//  @Override
//  protected void onClose(ClosedMessage message) {
//
//    System.out.println("====== WebSocket connection closed for FormControl " + formControlID);
//  }
  
  @Override
  protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
  
    if (message instanceof RedrawPushMessage) {
      RedrawPushMessage msg = (RedrawPushMessage) message;
      
      // check wether the message is actually targeted at this formControl
      if ( msg.getXFormsProcessorID().equals(processorID)
        && msg.getTargetControlID().equals(formControlID)) {
        
        System.out.println("====== PushMessage via WebSocket received for FormControl " + formControlID); // TODO log
        // redraw the component
        handler.add(component);
      }
    }
  }
}
