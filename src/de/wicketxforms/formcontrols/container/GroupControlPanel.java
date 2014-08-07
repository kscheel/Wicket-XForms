package de.wicketxforms.formcontrols.container;

import java.util.Objects;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.betterform.xml.ns.NamespaceConstants;
import de.wicketxforms.DomUtil;
import de.wicketxforms.formcontrols.AbstractFormControl;


/**
 * The Wicket component representing an XForms group element.
 * 
 * @see GroupControl
 * @see <a href="http://www.w3.org/TR/xforms11/#ui-group">9.1.1 The group Element</a>
 * @author Karl Scheel
 */
class GroupControlPanel extends Panel {

  private static final long serialVersionUID = 1L;
  
  private WebMarkupContainer groupComponent;

  GroupControlPanel(String wicketID, AbstractFormControl groupControl) {

    super(wicketID);
    addControlSpecificComponents(groupControl);
  }

  protected void addControlSpecificComponents(AbstractFormControl groupControl) {

    boolean hasLabel        = DomUtil.isFirstChildLabel(groupControl.getFormControlNode());
    String fragmentMarkupId = assembleFragmentMarkupId(groupControl.getAppearance(), hasLabel);
    Fragment fragment       = new Fragment("fragmentContainer", fragmentMarkupId, this);
    
    groupComponent = createGroup(groupControl, hasLabel);
    fragment.add(groupComponent);
    add(fragment);
  }
  
  protected WebMarkupContainer createGroup(AbstractFormControl groupControl, boolean hasLabel) {
    
    WebMarkupContainer group = new WebMarkupContainer("group");
    groupControl.applyStyleAttributes(group);

    if (hasLabel) {
      
      Component label = groupControl.createLabelComponent("groupLabel");
      group.add(label);
    }

    group.add(createRepeater(groupControl));
    group.add(groupControl.createRedrawBehavior(group));
    
    return group;
  }
  
  private String assembleFragmentMarkupId(String appearance, boolean hasLabel) {
    
    String markupId = "groupFragment";
    
    markupId += hasLabel ? "_withLabel" : "_noLabel";
    
    switch (appearance) {
        
      case "full":
        markupId += "_full";
        break;
        
      case "minimal":
      default:
        markupId += "_minimal";
        break;
    }
    
    return markupId;
  }

  private AbstractRepeater createRepeater(AbstractFormControl groupControl) {

    NodeList childNodes = groupControl.getFormControlNode().getChildNodes();
    RepeatingView repeater = new RepeatingView("repeater");

    for (int i = 0; i < childNodes.getLength(); ++i) {
      Node n = childNodes.item(i);

      if (n.getLocalName() != null
          && Objects.equals(n.getNamespaceURI(), NamespaceConstants.XFORMS_NS) ) {

        Component fc = AbstractFormControl.createFormControlComponent(repeater.newChildId(),
                                                                      groupControl.getXfProcessorID(),
                                                                      (Element) n);
        if (fc != null)
          repeater.add(fc);
      }
    }

    return repeater;
  }
}
