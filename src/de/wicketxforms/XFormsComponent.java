package de.wicketxforms;

import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.wicket.Component;
import org.apache.wicket.markup.Markup;
import org.apache.wicket.markup.MarkupFactory;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.request.resource.PackageResourceReference;
import org.joox.Match;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.TreeWalker;
import org.xml.sax.InputSource;

import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.wicketxforms.formcontrols.AbstractFormControl;
import de.wicketxforms.processor.ProcessorID;
import de.wicketxforms.processor.ProcessorRegistry;

/**
 * The XForms Component.
 * 
 * <p>
 * 
 * Represents the user interface described by a document with XForms markup.
 * Provides the hook methods {@link #onSubmit(Match)} and {@link #onAction(String, Match)} which
 * can be overridden by subclasses.
 * When this component isn't needed anymore call {@link #shutdown()} to free up resources.
 * The component must not be deserialized afterwards.
 * 
 * <p>
 * The hook methods {@link #onSubmit(Match)} and {@link #onAction(String, Match)} provide access
 * to the XForms instance data through <a href="http://code.google.com/p/joox/"> jOOX</a> which
 * provides a simple interface to the {@link org.w3c.dom.Document} class.
 * <p>
 * This simple example would get the text content of the first {@code book} element:<br>
 * {@code instance.find("book").text()}
 * <p>
 * See the <a href="http://code.google.com/p/joox/"> project homepage</a> or Wicket-XForms demo
 * application.
 * 
 * @author Karl Scheel
 */
public class XFormsComponent extends Panel {

  private static final long serialVersionUID  = 1L;

  // wicket:ids used by the repeater
  private static final String REPEATER_ID           = "repeatedComponents";
  private static final String REPEATED_COMPONENT_ID = "formControlComponent";

  /** NodeFilter checking for the XForms namespace */
  private static final class XFormsNsFilter implements NodeFilter {

    @Override
    public short acceptNode(Node n) {

      return Objects.equals(n.getNamespaceURI(), NamespaceConstants.XFORMS_NS)
          ? NodeFilter.FILTER_ACCEPT
          : NodeFilter.FILTER_SKIP;
    }
  }

  /** identifies the XForms processor holding this components XForms document */
  private final ProcessorID processorID;
  

  /* -- Constructors ---------------------------------------------------------------------------- */
  
  // TODO Exception hier abfangen und Komponente generieren, die den Fehler beschreibt?
  
  /**
   * Creates the XFormsComponent from a document with XForms markup.
   * 
   * @param id
   *          The Wicket id. See {@link Component#Component(String)}
   * @param xformsDocument
   *          The XForms document as an {@link InputStream} instance.
   * @param baseURI
   *          The base URI used to resolve any URI within an XForms document.
   * @throws WicketXFormsException
   *           Thrown if the component creation failed.
   */
  public XFormsComponent(String id, InputStream xformsDocument, String baseURI) throws WicketXFormsException {

    super(id);
    this.processorID = ProcessorRegistry.createProcessor(xformsDocument, this, baseURI);
    add(createForm());
  }
  
  /**
   * Creates the XFormsComponent from a document with XForms markup.
   * 
   * @param id
   *          The Wicket id. See {@link Component#Component(String)}
   * @param xformsDocument
   *          The XForms document as an {@link InputSource} instance.
   * @param baseURI
   *          The base URI used to resolve any URI within an XForms document.
   * @throws WicketXFormsException
   *           Thrown if the component creation failed.
   */
  public XFormsComponent(String id, InputSource xformsDocument, String baseURI) throws WicketXFormsException {

    super(id);
    this.processorID = ProcessorRegistry.createProcessor(xformsDocument, this, baseURI);
    add(createForm());
  }
  
  /**
   * Creates the XFormsComponent from a document with XForms markup.
   * 
   * @param id
   *          The Wicket id. See {@link Component#Component(String)}
   * @param xformsDocument
   *          The XForms document as a {@link Node} instance.
   * @param baseURI
   *          The base URI used to resolve any URI within an XForms document.
   * @throws WicketXFormsException
   *           Thrown if the component creation failed.
   */
  public XFormsComponent(String id, Node xformsDocument, String baseURI) throws WicketXFormsException {

    super(id);
    this.processorID = ProcessorRegistry.createProcessor(xformsDocument, this, baseURI);
    add(createForm());
  }
  
  /**
   * Creates the XFormsComponent from a document with XForms markup.
   * 
   * @param id
   *          The Wicket id. See {@link Component#Component(String)}
   * @param xformsDocument
   *          A {@link URI} pointing to the XForms document.
   * @param baseURI
   *          The base URI used to resolve any URI within an XForms document.
   * @throws WicketXFormsException
   *           Thrown if the component creation failed.
   */
  public XFormsComponent(String id, URI xformsDocument, String baseURI) throws WicketXFormsException {

    super(id);
    this.processorID = ProcessorRegistry.createProcessor(xformsDocument, this, baseURI);
    add(createForm());
  }

  /* -------------------------------------------------------------------------------------------- */
  
  @Override
  public Markup getAssociatedMarkup() {
  
    // ensure that subclasses will always use the XFormsComponent.html markup file
    return MarkupFactory.get().getMarkup(this, XFormsComponent.class, false);
  }
  
  // Override this to provide custom CSS styling
  @Override
  public void renderHead(IHeaderResponse response) {
    
    // add default Wicket-XForms CSS styles
    PackageResourceReference cssFile = new PackageResourceReference(XFormsComponent.class,
                                                                    "defaultStyle.css");
    CssHeaderItem cssItem = CssHeaderItem.forReference(cssFile);

    response.render(cssItem);
  }

  
  private final Form<?> createForm() throws WicketXFormsException {

    Form<?> form = new Form<Object>("baseForm");
    
    List<Component> formControlPanels = generateFormControlComponents();
    ListView<Component> repeater      = new ListView<Component>(REPEATER_ID, formControlPanels) {

      private static final long serialVersionUID = 1L;

      @Override
      protected void populateItem(ListItem<Component> item) {

        item.add(item.getModelObject());
      }
    };

    repeater.setReuseItems(true);
    form.add(repeater);

    return form;
  }


  private List<Component> generateFormControlComponents() throws WicketXFormsException {

    List<Component> fcComponents = new ArrayList<>();

    Document          document  = getXfDocument();
    DocumentTraversal traversal = (DocumentTraversal) document;
    NodeFilter        filter    = new XFormsNsFilter();

    // Ueber XForms ELemente des Dom-Baums iterieren
    TreeWalker walker = traversal.createTreeWalker(document, NodeFilter.SHOW_ELEMENT, filter, false);

    // sichere Casts durch NodeFilter.SHOW_ELEMENT
    for (Element e = (Element) walker.firstChild(); e != null; e = (Element) walker.nextSibling()) {
      
      Component c = AbstractFormControl.createFormControlComponent(REPEATED_COMPONENT_ID,
                                                                   processorID, e);
      if (c != null)
        fcComponents.add(c);
    }

    return fcComponents;
  }


  private Document getXfDocument() throws WicketXFormsException {

    XFormsProcessor xfProcessor = ProcessorRegistry.getProcessor(processorID);

    if (xfProcessor == null)
      throw new WicketXFormsException("XformsProcessor not found!");

    try {
      return xfProcessor.getXForms();
      
    } catch (XFormsException e) {
      throw new WicketXFormsException(e);
    }
  }

  /* -------------------------------------------------------------------------------------------- */
  
  
  /**
   * Hook method getting called after a submission event occured. The submission elements resource
   * attribute has to reference a "wxf" URI, e.g. <br>
   * {@code <submission resource="wxf://wicket-xforms/" method="wxf" replace="none" id="s00"/>}
   * <p>
   * Provides access to the xml instance data through the jOOX library for convenience. If you would
   * rather work with a {@link org.w3c.dom.Document} object call {@code instance.document()}.
   * 
   * @param instance
   *          The XForms instance data as specified by the {@code submission} element.
   * 
   */
  protected void onSubmit(Match instance) {
    
  }
  
  /**
   * This hook method gets called, after a Wicket-XForms Action has been triggered.
   * <p>
   * TODO Bsp
   * <p>
   * Provides access to the xml instance data through the jOOX library for convenience. If you would
   * rather work with a {@link org.w3c.dom.Document} object call {@code instance.document()}.
   * 
   * @param name
   *          Value of the actions {@code name} attribute.
   * @param instance
   *          The XForms instance data as defined by the actions {@code model} and {@code instance}
   *          attribute.
   */
  protected void onAction(String name, Match instance) {

  }
  
  /**
   * Has to be called when the XFormsComponent isn't needed any more to free up resources.
   * Afterwards the Component must not be deserialized again.
   * 
   * @throws WicketXFormsException Thrown if an error occured during shutdown.
   */
  public final void shutdown() throws WicketXFormsException {

    ProcessorRegistry.shutdownProcessor(processorID);
  }
}
