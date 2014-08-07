package de.wicketxforms.testapp;

import static org.joox.JOOX.$;

import java.util.ArrayList;
import java.util.List;

import org.joox.Match;


public class Customer {
  
  private String  name;
  private String  email;
  private boolean unmarried;
  // TODO Date birthday?
  // TODO Kreditkartennr?
  
  private Address address;
  
  /* -------------------------------------------------------------------------------------------- */
  
  public Customer(String name, String email, boolean unmarried, Address address) {
    
    setName(name);
    setEmail(email);
    setUnmarried(unmarried);
    setAddress(address);
  }
  
  public static List<Customer> getSampleCustomers() {
    
    List<Customer> customers = new ArrayList<>();
    
    Address addressA = new Address("Ahrensburg", "Arbeiterweg 1", 22926),
            addressB = new Address("Buxtehude", "Bockshornstr. 21", 21614),
            addressC = new Address("Bargteheide", "Bahnhofstraße 24", 1337);
    
    customers.add(new Customer("Andreas Arbeiter", "a.arbeiter@mailbox.org", false, addressA));
    customers.add(new Customer("Bodo Boom", "bodo@MichaelBayMovies.com", false, addressB));
    customers.add(new Customer("Carl Cheops", "carl.cheops@etvice.com", true, addressC));
    customers.add(new Customer("Daniel Düsentrieb", "daniel@duesentrieb.de", true, addressC));
    
    return customers;
  }

  public static Customer fromXML(Match xmlData) {
    
    String name       = xmlData.attr("name");
    String email      = xmlData.find("email").text();
    boolean unmarried = xmlData.attr("unmarried", boolean.class);
    Address address   = Address.fromXml(xmlData.find("address"));
    
    return new Customer(name, email, unmarried, address);
  }
  
  public Match toXml() {
  
    // ein JOOX-Match mit dem Aufbau der XML Daten eines Kunden erzeugen
    Match customer = $("my:customer", $("my:email", getEmail()), getAddress().toXml());
    
    // Dem Kunden noch seine Attribute geben
    return customer.attr("name", getName())
                   .attr("unmarried", Boolean.toString(isUnmarried()));
  }

  /* -------------------------------------------------------------------------------------------- */
  
  /**
   * @return the name
   */
  public String getName() {
  
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
  
    this.name = name;
  }

  /**
   * @return the email
   */
  public String getEmail() {
  
    return email;
  }

  /**
   * @param email the email to set
   */
  public void setEmail(String email) {
  
    this.email = email;
  }

  /**
   * @return the address
   */
  public Address getAddress() {
  
    return address;
  }

  /**
   * @param address the address to set
   */
  public void setAddress(Address address) {
  
    this.address = address;
  }

  /**
   * @return the unmarried
   */
  public boolean isUnmarried() {
  
    return unmarried;
  }

  /**
   * @param unmarried the unmarried to set
   */
  public void setUnmarried(boolean unmarried) {
  
    this.unmarried = unmarried;
  }
  
}
