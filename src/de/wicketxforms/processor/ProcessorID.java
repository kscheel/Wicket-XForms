package de.wicketxforms.processor;

import java.io.Serializable;
import java.util.Random;
import java.util.Set;


/**
 * TODO
 * 
 * @author Karl Scheel
 */
public class ProcessorID implements Serializable {
  
  private static final long serialVersionUID = 1L;
  
  private static Random random = new Random();
  
  final long id;
  
  private ProcessorID(long id) {
  
    this.id = id;
  }
  
  /**
   * 
   * @param usedIDs
   * @return
   * @throws RuntimeException
   *           When no unique ProcessorID could be created because all possible values are already
   *           taken. But an {@link OutOfMemoryError} should occur first.
   */
  static ProcessorID createUniqueId(Set<ProcessorID> usedIDs) {

    // Try generating a random ID
    for (int i = 0; i < 2; ++i)
    {
      for (int j = 0; j < 3; ++j)
      {
        ProcessorID newId = new ProcessorID(random.nextLong());
        
        if (!usedIDs.contains(newId))
          return newId;
      }
      // Reinitialize the number generator
      random = new Random();
    }
    
    // TODO Warn log
    System.out.println("Attempts to generate a random ProcessorID failed. Now trying to find an unused ID");
    
    // Search for an unused ID
    for (long idValue = Long.MIN_VALUE; idValue < Long.MAX_VALUE; ++idValue)
    {
      ProcessorID newId = new ProcessorID(idValue);
      
      if (!usedIDs.contains(newId))
        return newId;
    }
    
    throw new RuntimeException("No unique ProcessorID could be created!");
  }
  
  @Override
  public boolean equals(Object obj) {
  
    if (this == obj)
      return true;
    
    if ( !(obj instanceof ProcessorID))
      return false;
    
    return id == ((ProcessorID) obj).id;
  }
  
  @Override
  public int hashCode() {
  
    return new Long(id).hashCode();
  }
  
  @Override
  public String toString() {
  
    return super.toString() + " (Long value = " + id +")";
  }
}