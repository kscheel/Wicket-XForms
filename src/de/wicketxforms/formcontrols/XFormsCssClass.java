package de.wicketxforms.formcontrols;

/**
 * Enumeration of the CSS classes that can be used for styling the HTML markup generated
 * by Wicket-XForms
 * 
 * @author Karl Scheel
 */
public enum XFormsCssClass {
  
  // FormControl state property classes
  IS_VALID_TRUE ("xf-valid"),
  IS_VALID_FALSE("xf-invalid"),
  
  IS_REQUIRED_TRUE ("xf-required"),
  IS_REQUIRED_FALSE("xf-optional"),
  
  IS_READ_ONLY_TRUE ("xf-read-only"),
  IS_READ_ONLY_FALSE("xf-read-write"),
  
  IS_RELEVANT_TRUE ("xf-enabled"),
  IS_RELEVANT_FALSE("xf-disabled"),
  
  // FormControl CSS classes
  FC_INPUT("xf-input"),
  FC_OUTPUT("xf-output"),
  FC_TRIGGER("xf-trigger"),
  FC_SUBMIT("xf-submit"),
  FC_GROUP("xf-group"),
  FC_REPEAT("xf-repeat"),
  
  // Support Elements CSS classes
  SP_LABEL("xf-label"),
  
  // XForms Pseudo Elements CSS classes
  PS_VALUE("xf-value"),
  PS_REPEAT_ITEM("xf-repeat-item"),
  PS_REPEAT_INDEX("xf-repeat-index");

  /* -------------------------------------------------------------------------------------------- */
  
  private final String classValue;
  
  private XFormsCssClass(String classValue) {
    
    this.classValue = classValue;
  }
  
  @Override
  public String toString() {
    
    return classValue;
  }
}
