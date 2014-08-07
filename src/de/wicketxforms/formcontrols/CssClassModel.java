package de.wicketxforms.formcontrols;

import static de.wicketxforms.formcontrols.XFormsCssClass.IS_READ_ONLY_FALSE;
import static de.wicketxforms.formcontrols.XFormsCssClass.IS_READ_ONLY_TRUE;
import static de.wicketxforms.formcontrols.XFormsCssClass.IS_RELEVANT_FALSE;
import static de.wicketxforms.formcontrols.XFormsCssClass.IS_RELEVANT_TRUE;
import static de.wicketxforms.formcontrols.XFormsCssClass.IS_REQUIRED_FALSE;
import static de.wicketxforms.formcontrols.XFormsCssClass.IS_REQUIRED_TRUE;
import static de.wicketxforms.formcontrols.XFormsCssClass.IS_VALID_FALSE;
import static de.wicketxforms.formcontrols.XFormsCssClass.IS_VALID_TRUE;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.joox.JOOX;
import org.joox.Match;
import org.w3c.dom.Element;

import de.betterform.xml.ns.NamespaceConstants;
import de.wicketxforms.DomUtil;


/**
 * Wicket-Model, das dynamisch CSS-Klassen je nach Zustand dieses FormControls liefert.
 * TODO funktioniert das überhaupt noch, sollte es mal deserialisiert werden? oO
 * 
 * @author Karl Scheel
 */
class CssClassModel extends AbstractReadOnlyModel<String> {
  
  private static final long serialVersionUID = 1L;

  // state properties common to all form controls
  boolean isValid;
  boolean isRequired;
  boolean isReadOnly;
  boolean isRelevant; // > enabled / disabled
  
  
  CssClassModel(Element formControl) {
  
    initStateProperties(formControl);
  }
  
  private void initStateProperties(Element formControl) {
    
    Match bfData = JOOX.$(DomUtil.getChild(formControl, "data", NamespaceConstants.BETTERFORM_NS));
    
    Boolean isValidAttr = bfData.attr("valid", Boolean.class);
    isValid = !Boolean.FALSE.equals( isValidAttr ); // true ist default,
                                                    // falls das Attribut nicht vorhanden ist
    
    isRequired = bfData.attr("required", boolean.class); // false ist default
    isReadOnly = bfData.attr("readonly", boolean.class); // false ist default
    
    Boolean isRelevantAttr = bfData.attr("enabled", Boolean.class);
    isRelevant = !Boolean.FALSE.equals( isRelevantAttr ); // true ist default
  }

  @Override
  public String getObject() {
    
    String cssClassValue =  isValid    ? IS_VALID_TRUE.toString()     : IS_VALID_FALSE.toString();
    cssClassValue += " " + (isRequired ? IS_REQUIRED_TRUE.toString()  : IS_REQUIRED_FALSE.toString());
    cssClassValue += " " + (isReadOnly ? IS_READ_ONLY_TRUE.toString() : IS_READ_ONLY_FALSE.toString());
    cssClassValue += " " + (isRelevant ? IS_RELEVANT_TRUE.toString()  : IS_RELEVANT_FALSE.toString());
    
    return cssClassValue;
  }
}
