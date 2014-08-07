package de.wicketxforms.testapp;

import static org.joox.JOOX.$;

import org.joox.Match;


public class Address {
  
  private String city;
  private String street;
  private int    zipCode;
  
  /* -------------------------------------------------------------------------------------------- */
  
  
  public Address(String city, String street, int zipCode) {
  
    this.city    = city;
    this.street  = street;
    this.zipCode = zipCode;
  }
  
  public static Address fromXml(Match xmlData) {
    
    String city    = xmlData.attr("city");
    String street  = xmlData.attr("street");
    int zipCode    = xmlData.attr("zip", int.class);
    
    return new Address(city, street, zipCode);
  }

  public Match toXml() {
  
    return $("my:address").attr("city", getCity())
                          .attr("street", getStreet())
                          .attr("zip", Integer.toString(getZipCode()));
  }
  
  /**
   * @return the city
   */
  public String getCity() {
  
    return city;
  }
  
  /**
   * @param city
   *          the city to set
   */
  public void setCity(String city) {
  
    this.city = city;
  }
  
  /**
   * @return the street
   */
  public String getStreet() {
  
    return street;
  }
  
  /**
   * @param street
   *          the street to set
   */
  public void setStreet(String street) {
  
    this.street = street;
  }
  
  /**
   * @return the zipCode
   */
  public int getZipCode() {
  
    return zipCode;
  }
  
  /**
   * @param zipCode
   *          the zipCode to set
   */
  public void setZipCode(int zipCode) {
  
    this.zipCode = zipCode;
  }
  
}
