package de.wicketxforms.formcontrols;

/**
 * May be implemented by all formControl Wicket components that represent an
 * {@link AbstractStyleUpdatingFormControl}. Offers callback hooks to notify the component
 * when one of it's property states changes.
 * 
 * @see AbstractStyleUpdatingFormControl
 * @author Karl Scheel
 */
public interface IStyleUpdatingFormControlComponent {
  
  /**
   * Callback hook that get's called, when the "valid" state changes.
   * 
   * @param valid
   *          The new valid state.
   */
  public void onValidStateChange(boolean valid);
  
  /**
   * Callback hook that get's called, when the "readonly" state changes.
   * 
   * @param readonly
   *          The new readonly state.
   */
  public void onReadOnlyStateChange(boolean readonly);
  
  /**
   * Callback hook that get's called, when the "required" state changes.
   * 
   * @param required
   *          The new required state.
   */
  public void onRequiredStateChange(boolean required);
  
  /**
   * Callback hook that get's called, when the "relevant" state changes.
   * This state either maps to the "xf-enabled" or "xf-disabled" CSS class.
   * 
   * @param required
   *          The new relevant state.
   */
  public void onRelevantStateChange(boolean relevant);
  
}
