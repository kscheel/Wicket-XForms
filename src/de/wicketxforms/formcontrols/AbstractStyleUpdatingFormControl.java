package de.wicketxforms.formcontrols;

import java.util.Collection;

import org.apache.wicket.Component;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;

import de.betterform.xml.events.XFormsEventNames;
import de.wicketxforms.processor.ProcessorID;

/**
 * Adds additional functionality for FormControls listening to events that may effect the styling
 * of a FormControl through CSS classes.
 * (e.g. the {@code xforms-valid} and {@code xforms-invalid} events.) <br>
 * When handling those events the CSS classes of the component with applied style attributes will be
 * updated accordingly. See also {@link #applyStyleAttributes(Component)}.
 * <p>
 * If {@link #getWicketComponent()} retrieves a class implementing the
 * {@link IStyleUpdatingFormControlComponent}-Interface, it's hook methods will be called when
 * appropriate. See also {@link #callOnStateChangeHooks()}.
 * 
 * @author Karl Scheel
 */
public abstract class AbstractStyleUpdatingFormControl extends AbstractFormControl {
  
  /**
   * Wicket-Model, das dynamisch CSS-Klassen je nach Status dieses FormControls liefert.
   */
  private final CssClassModel cssClassValueModel;
  
  /* -------------------------------------------------------------------------------------------- */

  /**
   * @see AbstractFormControl#AbstractFormControl(ProcessorID, Element)
   */
  protected AbstractStyleUpdatingFormControl(final ProcessorID processorID,
                                             final Element formControlNode) {
  
    super(processorID, formControlNode);
    
    cssClassValueModel = new CssClassModel(getFormControlNode());
  }
  
  /* -------------------------------------------------------------------------------------------- */
  
  /**
   * Executes the "onStateChange" hook methods of this controls
   * {@link IStyleUpdatingFormControlComponent}. Subclasses can use this to initialize their Wicket
   * component after creating it.
   * 
   * @see IStyleUpdatingFormControlComponent
   * @see #getWicketComponent()
   */
  protected void callOnStateChangeHooks() {
  
    if (getWicketComponent() instanceof IStyleUpdatingFormControlComponent) {
      
      IStyleUpdatingFormControlComponent component = (IStyleUpdatingFormControlComponent) getWicketComponent();
      
      component.onReadOnlyStateChange(cssClassValueModel.isReadOnly);
      component.onRelevantStateChange(cssClassValueModel.isRelevant);
      component.onRequiredStateChange(cssClassValueModel.isRequired);
      component.onValidStateChange(cssClassValueModel.isValid);
    }
  }
  
  // adds additional CSS classes describing this FormControls state.
  @Override
  protected void modifyClassAttribute(Component component) {
    
    String staticClassValue = getCssClass().toString();
    Element formControl = getFormControlNode();
    
    if (formControl.hasAttributeNS(WICKETXFORMS_NAMESPACE, "class"))
       staticClassValue += " " + formControl.getAttributeNS(WICKETXFORMS_NAMESPACE, "class");
    
    component.add(new ClassAttributeModifier(staticClassValue, cssClassValueModel));
  }

  @Override
  protected Collection<String> getEventsToListenTo() {

    Collection<String> eventTypes = super.getEventsToListenTo();
    
    eventTypes.add(XFormsEventNames.VALID);
    eventTypes.add(XFormsEventNames.INVALID);
    eventTypes.add(XFormsEventNames.REQUIRED);
    eventTypes.add(XFormsEventNames.OPTIONAL);
    eventTypes.add(XFormsEventNames.READONLY);
    eventTypes.add(XFormsEventNames.READWRITE);
    eventTypes.add(XFormsEventNames.ENABLED);
    eventTypes.add(XFormsEventNames.DISABLED);
    
    return eventTypes;
  }
  
  @Override
  protected void onEventReceived(Event e) {

    super.onEventReceived(e);

    switch (e.getType()) {

      case XFormsEventNames.VALID:
        changeValidState(true);
        break;

      case XFormsEventNames.INVALID:
        changeValidState(false);
        break;
      
      case XFormsEventNames.REQUIRED:
        changeRequiredState(true);
        break;
        
      case XFormsEventNames.OPTIONAL:
        changeRequiredState(false);
        break;
        
      case XFormsEventNames.READONLY:
        changeReadOnlyState(true);
        break;
        
      case XFormsEventNames.READWRITE:
        changeReadOnlyState(false);
        break;
        
      case XFormsEventNames.ENABLED:
        changeRelevantState(true);
        break;
        
      case XFormsEventNames.DISABLED:
        changeRelevantState(false);
        break;
    }
  }
  
  private void changeValidState(boolean newValue) {
    
    if (cssClassValueModel.isValid != newValue) {
      
      cssClassValueModel.isValid = newValue;
      
      if (getWicketComponent() instanceof IStyleUpdatingFormControlComponent)
        ((IStyleUpdatingFormControlComponent) getWicketComponent()).onValidStateChange(newValue);
    }
  }
  
  private void changeReadOnlyState(boolean newValue) {
    
    if (cssClassValueModel.isReadOnly != newValue) {
      
      cssClassValueModel.isReadOnly = newValue;
      
      if (getWicketComponent() instanceof IStyleUpdatingFormControlComponent)
        ((IStyleUpdatingFormControlComponent) getWicketComponent()).onReadOnlyStateChange(newValue);
    }
  }
  
  private void changeRequiredState(boolean newValue) {
    
    if (cssClassValueModel.isRequired != newValue) {
      
      cssClassValueModel.isRequired = newValue;
      
      if (getWicketComponent() instanceof IStyleUpdatingFormControlComponent)
        ((IStyleUpdatingFormControlComponent) getWicketComponent()).onRequiredStateChange(newValue);
    }
  }
  
  private void changeRelevantState(boolean newValue) {
    
    if (cssClassValueModel.isRelevant != newValue) {
      
      cssClassValueModel.isRelevant = newValue;
      
      if (getWicketComponent() instanceof IStyleUpdatingFormControlComponent)
        ((IStyleUpdatingFormControlComponent) getWicketComponent()).onRelevantStateChange(newValue);
    }
  }
  
}
