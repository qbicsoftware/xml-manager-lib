package life.qbic.xml.properties;
//
// This file was xml_new by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/life.qbic.xml.properties/jaxb">http://java.sun.com/life.qbic.xml.properties/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.11.14 at 02:49:30 PM CET 
//


import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}qcategorical" maxOccurs="unbounded"/>
 *         &lt;element ref="{}qcontinous" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "qcategorical",
    "qcontinous"
})
@XmlRootElement(name = "qfactors")
public class Qfactors {

    protected List<Qcategorical> qcategorical;
    protected List<Qcontinous> qcontinous;

    /**
     * Gets the value of the qcategorical property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qcategorical property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQcategorical().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Qcategorical }
     * 
     * 
     */
    public List<Qcategorical> getQcategorical() {
        if (qcategorical == null) {
            qcategorical = new ArrayList<Qcategorical>();
        }
        return this.qcategorical;
    }

    /**
     * Gets the value of the qcontinous property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the qcontinous property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQcontinous().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Qcontinous }
     * 
     * 
     */
    public List<Qcontinous> getQcontinous() {
        if (qcontinous == null) {
            qcontinous = new ArrayList<Qcontinous>();
        }
        return this.qcontinous;
    }

}
