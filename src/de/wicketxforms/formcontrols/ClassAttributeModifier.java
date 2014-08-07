package de.wicketxforms.formcontrols;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.model.IModel;

/**
 * Setzt das {@code class}-Attribut von HTML-Elementen. Dessen Wert kann sich dabei aus einem
 * statischen und einem dynamischen Teil zusammensetzten. Der dynamische Teil ist z.B. gedacht um
 * eine Klasse fuer den Validitaetsstatus anzugeben. Die Dynamik muss dabei im uebergebenen Model
 * implementiert sein.
 * 
 * @author Karl Scheel
 */
class ClassAttributeModifier extends AttributeModifier {
  
  private static final long serialVersionUID = 1L;
  private final String staticValue;

  /**
   * 
   * @param staticValues
   * @param dynamicValues
   */
  ClassAttributeModifier(String staticValue, IModel<String> dynamicValue) {
  
    super("class", dynamicValue);
    this.staticValue = staticValue;
  }
  
  /**
   * 
   */
  @Override
  protected String newValue(String currentValue, String replacementValue) {
  
    // replacementValue enthaelt die durch das Model bestimmten dynamischen Klassen
    
    if (staticValue != null && !staticValue.isEmpty())
      return replacementValue == null
              ? staticValue
              : staticValue + " " + replacementValue;
    else
      return replacementValue;
  }
}