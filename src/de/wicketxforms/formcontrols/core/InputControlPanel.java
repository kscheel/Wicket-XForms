package de.wicketxforms.formcontrols.core;

import java.util.Objects;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.ajax.form.OnChangeAjaxBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.w3c.dom.Element;

import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.wicketxforms.formcontrols.AbstractFormControl;
import de.wicketxforms.formcontrols.IStyleUpdatingFormControlComponent;
import de.wicketxforms.formcontrols.XFormsCssClass;
import de.wicketxforms.processor.ProcessorID;
import de.wicketxforms.processor.ProcessorRegistry;

/**
 * The Wicket component representing an XForms input element.
 * 
 * @see <a href="http://www.w3.org/TR/xforms11/#ui-input">8.1.2 The input Element</a>
 * @author Karl Scheel
 */
class InputControlPanel extends Panel implements IStyleUpdatingFormControlComponent {

  private static final long serialVersionUID = 1L;

  private static final String WICKET_ID_CONTAINER_ELEMENT = "inputContainer";
  private static final String WICKET_ID_LABEL_ELEMENT     = "labelElement";
  private static final String WICKET_ID_INPUT_ELEMENT     = "inputElement";
  
  private final ProcessorID xfProcessorID;
  private final String formControlID;
  
  private FormComponent<?> inputElement;
  
  private boolean isCheckBox = false;
  private boolean isReadOnly; // will be initialized through the onStateChange()-hook
  private boolean isDisabled; // see above
  
  
  InputControlPanel(String wicketID, InputControl inputControl) {

    super(wicketID);
    
    this.xfProcessorID = inputControl.getXfProcessorID();
    this.formControlID = inputControl.getFormControlID();
    
    addControlSpecificComponents(inputControl);
  }
  

  private void addControlSpecificComponents(InputControl inputControl) {
    
    // <span>-element acting as container holding the label and input field
    WebMarkupContainer inputContainer = new WebMarkupContainer(WICKET_ID_CONTAINER_ELEMENT);
    inputControl.applyStyleAttributes(inputContainer);

    inputElement = createInputElement(inputControl);
    
    inputContainer.add(inputElement);
    inputContainer.add(createLabelElement(inputControl));
    inputContainer.add(inputControl.createRedrawBehavior(inputContainer));
    
    add(inputContainer);
  }
  
  
  private Component createLabelElement(AbstractFormControl inputControl) {

    Component label = inputControl.createLabelComponent(WICKET_ID_LABEL_ELEMENT);
    
    if (label == null)
      throw new RuntimeException("The Input-Control [" + formControlID
                                 + " ]is missing a label child element!");
    
    // the "for" attribute connects <input> and <label> so the label is clickable
    label.add(new AttributeModifier("for", inputElement.getMarkupId()));
    
    return label;
  }
  
  
  private FormComponent<?> createInputElement(InputControl inputControl) {

    FormComponent<?> inputElement;
    
    String type = inputControl.getTypeBinding();
    
    switch (type) {

      case "xsd:boolean":
        isCheckBox = true;
        inputElement = createCheckBox(inputControl.getBfDataValue());
        break;

      case "xsd:string":
      default:
        inputElement = createTextField(inputControl);
        break;
    }
    
    inputElement.add(new AttributeModifier("class", XFormsCssClass.PS_VALUE));
    
    return inputElement;
  }
  
  
  private CheckBox createCheckBox(String modelValue) {
    
    IModel<Boolean> model = Model.of(Boolean.valueOf(modelValue));

    CheckBox checkBox = new CheckBox(WICKET_ID_INPUT_ELEMENT, model) {
      private static final long serialVersionUID = 1L;
      
      @Override
      protected void onComponentTag(ComponentTag tag) {
      
        tag.put("type", "checkbox");
        super.onComponentTag(tag);
      }
    };
    
    checkBox.add(createUpdateBehavior("change"));
    
    return checkBox;
  }
  
  
  private TextField<String> createTextField(AbstractFormControl inputControl) {

    String modelValue = inputControl.getBfDataValue();
    
    TextField<String> textField = new TextField<String>(WICKET_ID_INPUT_ELEMENT, Model.of(modelValue)) {
      private static final long serialVersionUID = 1L;

      @Override
      protected boolean shouldTrimInput() {
        
        return false;
      }
      
      @Override
      protected void onComponentTag(ComponentTag tag) {
      
        tag.put("type", "text");
        super.onComponentTag(tag);
      }
    };

    // incremental defines if every change of the input should immediately be processed.
    // Otherwise the new value will be processed after changing the value has finished.
    String event = isIncremental(inputControl) ? OnChangeAjaxBehavior.EVENT_NAME : "change";

    textField.add(createUpdateBehavior(event));
    textField.setConvertEmptyInputStringToNull(false);

    return textField;
  }
  
  
  // Checks wether this inputs incremental attribute is set to true
  private Boolean isIncremental(AbstractFormControl inputControl) {
    
    Element inputElement = inputControl.getFormControlNode();
    String value = inputElement.getAttribute("incremental");
    
    return (value == null) ? Boolean.FALSE : Boolean.valueOf(value);
  }
  
  
  private AjaxFormComponentUpdatingBehavior createUpdateBehavior(String event) {
    
    return new AjaxFormComponentUpdatingBehavior(event) {
      private static final long serialVersionUID = 1L;

      @Override
      protected void onUpdate(AjaxRequestTarget target) {
        
        FormComponent<?> inputElement = (FormComponent<?>) getComponent();
        System.out.println("New value for input " + formControlID + " = ["
                           + inputElement.getValue() + "]\n"); // TODO dbg log
        setFormControlValue(inputElement.getValue());
      }
    };
  }
  
  
  private void setFormControlValue(String newValue) {
    
    try {
      XFormsProcessor xfProcessor = ProcessorRegistry.getProcessor(xfProcessorID);
      
      if (xfProcessor == null)
        System.out.println("Error while updating FormControl value: XForms processor not found!"); // TODO error log
      else
        xfProcessor.setControlValue(formControlID, newValue);
      
    } catch (XFormsException e) {
      System.out.println("Error while updating FormControl value:"); // TODO error log
      e.printStackTrace();
    }
  }
  
  
  /**
   * Updates the Model of the component displaying the value of this inputControl. Does not trigger
   * a redraw of the component.
   * 
   * @param newValue
   *          The new value to set.
   * 
   * @return {@code true}, if newValue differed from the previous Value.
   */
  public boolean updateModelValue(String newValue) {

    Object oldModelObject = inputElement.getDefaultModelObject();
    
    // a checkboxes modelObject type is Boolean. The new modelObject needs to be of the same type.
    Object newModelObject = isCheckBox
                            ? Boolean.valueOf(newValue)
                            : newValue;
                            
    if (!Objects.equals(oldModelObject, newModelObject)) {
      
      inputElement.setDefaultModelObject(newModelObject);
      System.out.println("new value: " + newValue + "\n"); // TODO dbg log
      return true;
    }
    
    return false;
  }
  
  
  @Override
  public void onReadOnlyStateChange(boolean readonly) {
    
    isReadOnly = readonly;
  
    if (readonly)
      inputElement.add(new AttributeModifier("readonly", "readonly"));
    else
      inputElement.add(AttributeModifier.remove("readonly"));
    
    // Even with the readonly attribute a checkbox still allows user interaction..
    // see: http://stackoverflow.com/questions/155291/can-html-checkboxes-be-set-to-readonly
    
    // Therefore a readonlycheckbox has to be disabled to prevent user interaction
    
    // Prevent enabling a checkbox when it is properly disabled because of it's relevant state
    if (isCheckBox && !isDisabled)
      inputElement.setEnabled(!readonly);
  }
  
  
  @Override
  public void onRequiredStateChange(boolean required) {

    // using inputElement.setRequired(boolean) here will prevent the AjaxBehavior from being
    // triggered when the textfield is cleared..
    
    if (required)
      inputElement.add(new AttributeModifier("required", "required"));
    else
      inputElement.add(AttributeModifier.remove("required"));
  }
  
  
  @Override
  public void onRelevantStateChange(boolean relevant) {
    
    isDisabled = !relevant;
  
    // Prevent altering the enabled state, when a checkbox is readonly and therefore disabled
    if (!(isCheckBox && isReadOnly))
      inputElement.setEnabled(relevant);
  }
  

  @Override
  public void onValidStateChange(boolean valid) {

    // nothing to do here
  }
}
