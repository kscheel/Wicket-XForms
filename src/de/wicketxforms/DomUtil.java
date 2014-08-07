package de.wicketxforms;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;

import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.ns.NamespaceConstants;

/**
 * Hilfsmethoden zum Testen und so
 *
 * @author Karl Scheel
 */
public class DomUtil {
  
  private DomUtil() {};

  public static Node getDomNodeByIDAttribute(Document doc, String id) {

    System.out.println("## getElemByID("+id+"): " + doc.getElementById(id));
    
    /* document.getElementById() geht vermutlich wegen der Konfiguration des
     * betterform-DocumentBuilders nicht richtig.
     *
     * http://stackoverflow.com/questions/3423430/java-xml-dom-how-are-id-attributes-special
     * http://blog.amitsharma.in/2008/05/java-how-to-make-getelementbyid-work.html
     */
    // return document.getElementById(observerAttr.getTextContent());

    XPath xPath = XPathFactory.newInstance().newXPath();

    try {
      // XPath id-Funktion geht vermutlich aus dem selben Grund wie document.getElementById() nicht
      // String exprStr = "id('"+id+"')";

      // XPath Ausdruck der alle Knoten erfasst, deren ID Attribut den gesuchten Wert hat.
      // (Der Schema Typ wird dabei nicht beachtet)
      String exprStr = "//*[@id='"+id+"']";
      XPathExpression expr = xPath.compile(exprStr);

      // sicherer Cast, da XPathConstants.NODE sich auf ein org.w3c.dom.Node-Objekt bezieht.
      return (Node) expr.evaluate(doc, XPathConstants.NODE);

    } catch (XPathExpressionException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return null;
  }

  /**
   * Liste aller Events erhalten (mit reflection.. >_>)
   */
  public static Collection<String> getAllPossibleEvents() {

    Collection<String> eventTypes = new ArrayList<>();

    try {

      for (Field event : XFormsEventNames.class.getFields())
        eventTypes.add((String) event.get(null));

      for (Field event : BetterFormEventNames.class.getFields())
        eventTypes.add((String) event.get(null));

      for (Field event : DOMEventNames.class.getFields())
        eventTypes.add((String) event.get(null));

    } catch (IllegalArgumentException | IllegalAccessException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    
    return eventTypes;
  }

  public static void printEvent(String msg, Event e) {

    System.out.println("\n"+msg);

    String s = " Event " + e.getType() + " received " + "\n";
    s += " Event is cancelable :" + e.getCancelable() + "\n";
    s += " Event is bubbling event :" + e.getBubbles() + "\n";
    s += " The Target is " + ((Node) (e.getTarget())).getNodeName() + "\n";
    s += " Type: " + e.toString() + "\n";
    System.out.println(s);
  }

  public static String compactNodeToString(Node n, boolean descend) {

    return compactNodeToString(n, "", "", "", descend)+"\n";
  }

  private static String compactNodeToString(Node n, String indentToUse, String indentGoingDown,
                                            String previousIndent, boolean descend) {

    if (n != null) {

      String s = indentToUse + n.getNodeName();
      
      if (n.getNodeValue() != null)
        s += " (" + n.getNodeValue().trim() + ")";

      if (n.hasAttributes())
        s += " " + getAttributesCompact(n.getAttributes());

      // │ ├ └
      if (descend && n.hasChildNodes()) {
        
        NodeList children = n.getChildNodes();
        
        for(int i=0; i < children.getLength()-1; ++i) {
          s += "\n" + compactNodeToString(children.item(i),
                                          indentGoingDown+"├ ",
                                          indentGoingDown+"│ ",
                                          indentGoingDown,
                                          true);
        }

        s += "\n" + compactNodeToString(children.item(children.getLength()-1),
                                        indentGoingDown+"└ ",
                                        previousIndent+"│   ",
                                        indentGoingDown,
                                        true) +"\n"+previousIndent+"│";
      }
      
      return s;
    }
    return "";
  }

  public static void printToTerminal(Node n, String indent, boolean descend) {

    if (n != null) {

      System.out.println(indent + "Name: " + n.getNodeName());
      System.out.println(indent + "NS  : " + n.getNamespaceURI());
      System.out.println(indent + "txt : " + n.getTextContent());
      System.out.println(indent + "Type: " + n.getNodeType());
      System.out.println(indent + "Val : [" + n.getNodeValue() + "]");

      if (n.hasAttributes())
        printAttributesCompact(n.getAttributes(), indent, descend);

      if (descend && n.hasChildNodes())
        printChildNode(n.getChildNodes(), indent, descend);

      if (indent == "")
        System.out.println(indent + "________________________\n");
    }
  }

  private static void printChildNode(NodeList childNodes, String indent, boolean descend) {

    System.out.println();
    for (int i = 0; i < childNodes.getLength(); ++i) {
      System.out.println(indent + "-- Child: " + i + " ---------");
      printToTerminal(childNodes.item(i), indent + "  ", descend);
    }

    System.out.println(indent + "-- ^last child ------\n");
  }

  @SuppressWarnings("unused")
  private static void printAttributes(NamedNodeMap attributes, String indent, boolean descend) {

    System.out.println();
    for (int i = 0; i < attributes.getLength(); ++i) {
      System.out.println(indent + "-- Attribute: " +i+ " -----");
      printToTerminal(attributes.item(i), indent + "  ", descend);
    }

    System.out.println(indent + "-- ^last attribute --\n");
  }

  private static String getAttributesCompact(NamedNodeMap attributes) {

    String s = "[";
    for (int i = 0; i < attributes.getLength(); ++i) {
      Node n = attributes.item(i);
      s += n.getNodeName() + "=\"" + n.getNodeValue() + "\"";
      s += ", ";
    }
    s += "]";

    return s;
  }

  private static void printAttributesCompact(NamedNodeMap attributes, String indent, boolean descend) {

    System.out.println(indent + "Attributes: " + getAttributesCompact(attributes));
  }

  public static Node getChild(Node parentNode, String localName, String namespaceURI) {

    NodeList children = parentNode.getChildNodes();
    
    for (int i = 0; i < children.getLength(); ++i) {
      Node n = children.item(i);

      if (   Objects.equals(n.getLocalName(),    localName)
          && Objects.equals(n.getNamespaceURI(), namespaceURI) ) {

        return n;
      }
    }
    
    return null;
  }

  public static String getBfDataAttributeValue(Node n, String AttributeName) {

    Node data = DomUtil.getChild(n, "data", NamespaceConstants.BETTERFORM_NS);
    
    if (data == null)
      return "";
    
    Node attrNode = data.getAttributes().getNamedItemNS(NamespaceConstants.BETTERFORM_NS,
                                                        AttributeName);
    
    return (attrNode == null) ? "" : attrNode.getTextContent();
  }
  
  public static Element getLabelChild(Node parentNode) {

    NodeList children = parentNode.getChildNodes();
    
    for (int i = 0; i < children.getLength(); ++i) {
      Node n = children.item(i);

      if (   Objects.equals(n.getLocalName(),    "label")
          && Objects.equals(n.getNamespaceURI(), NamespaceConstants.XFORMS_NS)
          && n.getNodeType() == Node.ELEMENT_NODE) {

        return (Element) n;
      }
    }
    
    return null;
  }

  /**
   * 
   * @param parentNode
   * @param localName
   * @param namespaceURI
   * @return
   */
  public static String getNodeText(Node parentNode, String localName, String namespaceURI) {

    Node n = getChild(parentNode, localName, namespaceURI);
    
    if (n == null)
      return  null;

    // The first child usually holds a nodes textContent
    if (n.hasChildNodes() &&  (n.getFirstChild().getNodeType() == Node.TEXT_NODE))
      return n.getFirstChild().getNodeValue();
    else
      return n.getTextContent();
  }

  /**
   * Hilfsmethode um festzustellen, ob das erste XForms-Kind-Element ein Label ist.
   */
  public static boolean isFirstChildLabel(Node parentNode) {

    NodeList childNodes = parentNode.getChildNodes();
    
    for (int i = 0; i < childNodes.getLength(); ++i) {
      Node child = childNodes.item(i);

      // erstes Xforms-Kind finden
      if (child.getNamespaceURI() == NamespaceConstants.XFORMS_NS)
        
        return Objects.equals(child.getLocalName(), "label")
            && child.getNodeType() == Node.ELEMENT_NODE;
    }

    return false;
  }
}
