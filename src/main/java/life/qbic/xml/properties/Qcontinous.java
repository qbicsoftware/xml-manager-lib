package life.qbic.xml.properties;
//
// This file was xml_new by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation,
// v2.2.4-2
// See <a href="http://java.sun.com/life.qbic.xml.properties/jaxb">http://java.sun.com/life.qbic.xml.properties/jaxb</a>
// Any modifications to this file will be lost upon recompilation of the source schema.
// Generated on: 2014.11.14 at 02:49:30 PM CET
//


import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="label" use="required" type="{}variable_name_format" />
 *       &lt;attribute name="unit" use="required" type="{}unit_types" />
 *       &lt;attribute name="value" use="required" type="{http://www.w3.org/2001/XMLSchema}decimal" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "qcontinous")
public class Qcontinous {

  @XmlAttribute(name = "label", required = true)
  protected String label;
  @XmlAttribute(name = "unit", required = true)
  protected Unit unit;
  @XmlAttribute(name = "value", required = true)
  protected BigDecimal value;

  /**
   * Gets the value of the label property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public String getLabel() {
    return label;
  }

  /**
   * Sets the value of the label property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setLabel(String value) {
    this.label = value;
  }

  /**
   * Gets the value of the unit property.
   * 
   * @return possible object is {@link String }
   * 
   */
  public Unit getUnit() {
    return unit;
  }

  /**
   * Sets the value of the unit property.
   * 
   * @param value allowed object is {@link String }
   * 
   */
  public void setUnit(Unit unit) {
    this.unit = unit;
  }

  /**
   * Gets the value of the value property.
   * 
   * @return possible object is {@link BigDecimal }
   * 
   */
  public BigDecimal getValue() {
    return value;
  }

  /**
   * Sets the value of the value property.
   * 
   * @param value allowed object is {@link BigDecimal }
   * 
   */
  public void setValue(BigDecimal value) {
    this.value = value;
  }

}
