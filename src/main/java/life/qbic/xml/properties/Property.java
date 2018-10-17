package life.qbic.xml.properties;

public class Property {

  private String label;
  private String value;
  private Unit unit;
  private boolean hasUnit;
  private PropertyType type;

  public Property(String label, String value, Unit unit, PropertyType type) {
    this(label, value, type);
    this.unit = unit;
    hasUnit = true;
  }

  public Property(String label, String value, PropertyType type) {
    this.label = label;
    this.value = value;
    this.type = type;
  }

  public String toString() {
    String res = type + " " + label + ": " + value;
    if (hasUnit)
      res += " " + unit;
    return res;
  }

  public PropertyType getType() {
    return type;
  }

  public String getLabel() {
    return label;
  }

  public String getValue() {
    return value;
  }

  public Unit getUnit() {
    return unit;
  }

  public boolean hasUnit() {
    return hasUnit;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (hasUnit ? 1231 : 1237);
    result = prime * result + ((label == null) ? 0 : label.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    result = prime * result + ((unit == null) ? 0 : unit.hashCode());
    result = prime * result + ((value == null) ? 0 : value.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    Property other = (Property) obj;
    if (hasUnit != other.hasUnit)
      return false;
    if (label == null) {
      if (other.label != null)
        return false;
    } else if (!label.equals(other.label))
      return false;
    if (type != other.type)
      return false;
    if (unit == null) {
      if (other.unit != null)
        return false;
    } else if (!unit.equals(other.unit))
      return false;
    if (value == null) {
      if (other.value != null)
        return false;
    } else if (!value.equals(other.value))
      return false;
    return true;
  }

public void setValue(String val) {
	this.value = val;
}

}
