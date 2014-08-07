package de.wicketxforms.testapp;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * Hier wird die Startseite der Anwendung definiert.
 * 
 * @author Karl Scheel
 */
public class PrototypeApplication extends WebApplication {

  @Override
  public Class<? extends Page> getHomePage() {

    return HomePage.class;
  }
}
