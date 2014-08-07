package de.wicketxforms;


/**
 * Basic exception to throw if something somewhere goes terribly wrong.
 * 
 * @author Karl Scheel
 */
public class WicketXFormsException extends Exception {

  private static final long serialVersionUID = 1L;

  /**
   * @see Exception#Exception()
   */
  public WicketXFormsException() {

    super();
  }

  /**
   * @see Exception#Exception(String)
   */
  public WicketXFormsException(String message) {

    super(message);
  }


  /**
   * @see Exception#Exception(String, Throwable)
   */
  public WicketXFormsException(String message, Throwable cause) {

    super(message, cause);
  }


  /**
   * @see Exception#Exception(Throwable)
   */
  public WicketXFormsException(Throwable cause) {

    super(cause);
  }

}
