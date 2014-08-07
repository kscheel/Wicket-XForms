package de.wicketxforms.formcontrols.container;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.markup.repeater.AbstractRepeater;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.XFormsProcessor;
import de.wicketxforms.DomUtil;
import de.wicketxforms.formcontrols.XFormsCssClass;
import de.wicketxforms.processor.ProcessorID;
import de.wicketxforms.processor.ProcessorRegistry;


/**
 * The Wicket component representing an XForms repeat element.
 * 
 * @see <a href="http://www.w3.org/TR/xforms/#ui-repeat">9.3.1 The repeat Element</a>
 * @author Karl Scheel
 */
class RepeatControlPanel extends Panel {
  
  private static final long serialVersionUID = 1L;
  
  private static final String REPEATED_COMPONENT_ID = "repeatedItem";
  
  private final ProcessorID xfProcessorID;
  private final String formControlID;
  
  /** Model loading the current list of the repeat items wicket components. */
  private IModel<List<Component>> componentListModel = new LoadableDetachableModel<List<Component>>() {
    
    private static final long serialVersionUID = 1L;

    @Override
    protected List<Component> load() {

      List<Component> componentList = new ArrayList<>();
      XFormsProcessor xfProcessor   = ProcessorRegistry.getProcessor(xfProcessorID);
      
      if (xfProcessor == null)
        throw new RuntimeException("RepeatControl [" + formControlID
                                   + "] can't find it's XForms processor!");
      
      // Retrieve this Repeats current children
      Element formControl = xfProcessor.lookup(formControlID).getElement();
      Integer repeatIndex = Integer.valueOf(DomUtil.getBfDataAttributeValue(formControl, "index"));
      NodeList children   = formControl.getChildNodes();
      
      // add each childs Wicket compononent to the result list
      for (int i = 0; i < children.getLength(); ++i) {
        
        Node n = children.item(i);
        
        // The repeat items just appear as xforms group elements
        if ("group".equals(n.getLocalName())
            && NamespaceConstants.XFORMS_NS.equals(n.getNamespaceURI())) {

          RepeatItem repeatItem = new RepeatItem(REPEATED_COMPONENT_ID, xfProcessorID, (Element) n,
                                                 formControlID, i+1);
          RepeatItemPanel panel = repeatItem.getRepeatItemPanel();
          
          // prepend the repeat-index CSS class if this repeat item is the currently selected one
          if (Integer.valueOf(i).equals(repeatIndex-1)) {
            
            Behavior modifier = AttributeModifier.prepend("class", XFormsCssClass.PS_REPEAT_INDEX);
            panel.getGroupComponent().add(modifier);
          }

          componentList.add(panel);
        }
      }

      return componentList;
    }
  };

  /* -------------------------------------------------------------------------------------------- */


  RepeatControlPanel(String wicketID, RepeatControl repeatControl) {
  
    super(wicketID);
    
    this.xfProcessorID = repeatControl.getXfProcessorID();
    this.formControlID = repeatControl.getFormControlID();
    
    addControlSpecificComponents(repeatControl);
  }
  
  protected void addControlSpecificComponents(RepeatControl repeatControl) {

    // TODO number attribut beachten
    //      > scrollbalken oder so?
    
    WebMarkupContainer repeatContainer = new WebMarkupContainer("repeatContainer");
    repeatControl.applyStyleAttributes(repeatContainer);
    
    repeatContainer.add(createRepeater());
    repeatContainer.add(repeatControl.createRedrawBehavior(repeatContainer));
    
    add(repeatContainer);
  }

  private AbstractRepeater createRepeater() {

    ListView<Component> repeater = new ListView<Component>("repeater", componentListModel) {

      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Component> item) {
      
        item.add(item.getModelObject());
      }};
      
    // The current list of children has to be created every time the repeat FormControl is rendered
    repeater.setReuseItems(false);
    
    return repeater;
  }
}
