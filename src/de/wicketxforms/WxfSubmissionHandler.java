package de.wicketxforms;

import java.util.HashMap;
import java.util.Map;

import org.joox.JOOX;
import org.w3c.dom.Node;

import de.betterform.connector.AbstractConnector;
import de.betterform.connector.SubmissionHandler;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import de.wicketxforms.processor.ProcessorRegistry;

/**
 * Represents the custom Wicket-XForms submission element. This class is advertised to betterFORM
 * in the betterFORM configuration file.
 * <p>
 * It get's invoked by using the {@code wxf}-URI scheme in the resource attribute of a submission
 * element. <br>
 * E.g. "{@code <xf:submission resource="wxf://wicket-xforms/" method="wxf" replace="none" id="s00"/>}"
 * 
 * @author Karl Scheel
 * @see betterform-config.xml
 */
public class WxfSubmissionHandler extends AbstractConnector implements SubmissionHandler {
  
  @Override
  public Map<?,?> submit(Submission submission, Node instance) throws XFormsException {
    
    XFormsProcessor xfProcessor = submission.getContainerObject().getProcessor();
    XFormsComponent xfComponent = ProcessorRegistry.getComponent(xfProcessor);
    
    if (xfComponent == null) {
      Exception e = new WicketXFormsException("Unable to process submission!"
                                              + "No Wicket component found for the XForms document "
                                              + "containing this submission element: "
                                              + DOMUtil.getCanonicalPath(submission.getElement()));
      throw new XFormsException(e);
    }
    
    // Call the Wicket components hook method
    xfComponent.onSubmit(JOOX.$(instance));
    
    return new HashMap<>();
  }
  
}
