package de.wicketxforms.formcontrols.core;

import java.util.Objects;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import de.wicketxforms.DomUtil;
import de.wicketxforms.formcontrols.XFormsCssClass;

/**
 * The Wicket component representing an XForms output element.
 * 
 * @see <a href="http://www.w3.org/TR/xforms11/#ui-output">8.1.5 The output Element</a>
 * @author Karl Scheel
 */
class OutputControlPanel extends Panel {

  private static final long serialVersionUID = 1L;
  
  private Component outputElement;


  OutputControlPanel(String wicketID, OutputControl outputControl) {

    super(wicketID);
    addControlSpecificComponents(outputControl);
  }

  protected void addControlSpecificComponents(OutputControl outputControl) {

    boolean hasLabel   = DomUtil.isFirstChildLabel(outputControl.getFormControlNode());
    String typeBinding = outputControl.getTypeBinding();
    String fragmentMarkupId;
    
    // choose fragment depending on the controls type binding
    if ("xsd:boolean".equals(typeBinding))
      fragmentMarkupId = hasLabel ? "outputFragment_withLabel_boolean"
                                  : "outputFragment_noLabel_boolean";
    else
      fragmentMarkupId = hasLabel ? "outputFragment_withLabel_string"
                                  : "outputFragment_noLabel_string";
    
    Fragment outputContainer = new Fragment("output", fragmentMarkupId, this);
    outputControl.applyStyleAttributes(outputContainer);
    
    if (hasLabel) {

      Component label = outputControl.createLabelComponent("label");
      outputContainer.add(label);
    }
    
    outputElement = createOutputElement(outputControl.getBfDataValue(), typeBinding);
    outputContainer.add(outputElement);
    outputContainer.add(outputControl.createRedrawBehavior(outputContainer));
    
    add(outputContainer);
  }
  
  private Component createOutputElement(String modelValue, String typeBinding) {

    Component outputElement;
    
    if ("xsd:boolean".equals(typeBinding)) {
      
      IModel<Boolean> valueModel = Model.of(Boolean.valueOf(modelValue));
      outputElement = new CheckBox("outputValue", valueModel);
      outputElement.setEnabled(false);
      
    } else
      outputElement = new Label("outputValue", Model.of(modelValue));
    
    outputElement.add(new AttributeModifier("class", XFormsCssClass.PS_VALUE));
    
    return outputElement;
  }
  
  /**
   * Updates the Model of the component displaying the value of this inputControl. Does not trigger
   * a redraw of the component.
   * A Redraw will be triggered if this method returns true.
   * 
   * @param newValue
   *          The new value to set.
   * 
   * @return {@code true}, if newValue differed from the previous Value.
   */
  public boolean updateModelValue(String newValue) {

    Object oldModelObject = outputElement.getDefaultModelObject();
    
    // Bei einer Checkbox muss der ModelObject-Typ Boolean sein
    Object newModelObject = (oldModelObject instanceof Boolean)
                            ? Boolean.valueOf(newValue)
                            : newValue;
                            
    if (!Objects.equals(oldModelObject, newModelObject)) {
      
      outputElement.setDefaultModelObject(newModelObject);
      System.out.println("new value: [" + newValue + "]\n"); // TODO dbg log
      return true;
    }
    
    return false;
  }
}
