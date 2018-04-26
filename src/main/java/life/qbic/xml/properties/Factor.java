package life.qbic.xml.properties;

@Deprecated
/**
 * use Property with PropertyType Factor
 * @author Andreas Friedrich
 *
 */
public class Factor {
  
  private String label;
  private String value;
  private String unit;
  private boolean hasUnit;

  public Factor(String label, String value, String unit) {
    this.label = label;
    this.value = value;
    this.unit = unit;
    if(unit.equals(""))
      hasUnit = false;
    else
      hasUnit = true;
  }
  
  public Factor(String label, String value) {
    this(label, value, "");
  }

  public String toString() {
    return label+": "+value+unit;
  }
  
  public String getLabel() {
    return label;
  }

  public String getValue() {
    return value;
  }

  public String getUnit() {
    return unit;
  }
  
  public boolean hasUnit() {
    return hasUnit;
  }

}
