package life.qbic.xml.manager;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import life.qbic.xml.properties.Property;
import life.qbic.xml.properties.PropertyType;
import life.qbic.xml.properties.Qcategorical;
import life.qbic.xml.properties.Qcontinous;
import life.qbic.xml.properties.Qfactors;
import life.qbic.xml.properties.Qproperties;
import life.qbic.xml.properties.Qproperty;
import life.qbic.xml.properties.Unit;

@Deprecated
public class XMLParser {

  public Map<String, String> getMapOfProperties(JAXBElement<life.qbic.xml.properties.Qproperties> root) {
    Map<String, String> map = new HashMap<String, String>();
    if (root.getValue().getQfactors() != null) {
      Qfactors factors = root.getValue().getQfactors();
      for (Qcategorical cat : factors.getQcategorical()) {
        map.put(cat.getLabel(), cat.getValue());
      }
      for (Qcontinous cont : factors.getQcontinous()) {
        map.put(cont.getLabel(), cont.getValue() + " " + cont.getUnit());
      }
    }
    if (root.getValue().getQproperty() != null) {
      List<Qproperty> props = root.getValue().getQproperty();
      for (Qproperty prop : props) {
        String unit = "";
        if (prop.getUnit() != null)
          unit = " " + prop.getUnit();
        map.put(prop.getLabel(), prop.getValue() + unit);
      }
    }
    return map;
  }

  public List<Property> getAllProperties(JAXBElement<life.qbic.xml.properties.Qproperties> root) {
    List<Property> res = getProperties(root);
    res.addAll(getExpFactors(root));
    return res;
  }

  public List<Property> getProperties(JAXBElement<life.qbic.xml.properties.Qproperties> root) {
    List<Property> res = new ArrayList<Property>();
    if (root.getValue().getQproperty() != null) {
      List<Qproperty> props = root.getValue().getQproperty();
      for (Qproperty prop : props) {
        String label = prop.getLabel();
        String val = prop.getValue();
        Unit unit = prop.getUnit();
        if (unit == null)
          res.add(new Property(label, val, PropertyType.Property));
        else
          res.add(new Property(label, val, unit, PropertyType.Property));
      }
    }
    return res;
  }

//only gets experimental factor objects from the parsed xml object
  public List<Property> getExpFactors(JAXBElement<life.qbic.xml.properties.Qproperties> root) {
    List<Property> res = new ArrayList<Property>();
    if (root.getValue().getQfactors() != null) {
      Qfactors factors = root.getValue().getQfactors();
      for (Qcategorical cat : factors.getQcategorical()) {
        res.add(new Property(cat.getLabel(), cat.getValue(), PropertyType.Factor));
      }
      for (Qcontinous cont : factors.getQcontinous()) {
        res.add(new Property(cont.getLabel(), cont.getValue().toString(), cont.getUnit(),
            PropertyType.Factor));
      }
    }
    return res;
  }

  public String addPropertiesToXMLString(String xml, List<Property> props) throws JAXBException {
    return toString(addProperties(parseXMLString(xml), props));
  }

  public JAXBElement<life.qbic.xml.properties.Qproperties> addProperties(JAXBElement<life.qbic.xml.properties.Qproperties> root,
      List<Property> props) {
    // root of life.qbic.xml.properties that are not experimental factors
    List<Qproperty> oldProps = root.getValue().getQproperty();
    // create factors root if it doesn't exist
    if (root.getValue().getQfactors() == null)
      root.getValue().setQfactors(new Qfactors());
    Qfactors factorRoot = root.getValue().getQfactors();
    // list of categorical experimental factors
    List<Qcategorical> cats = factorRoot.getQcategorical();
    // list of continuous experimental factors
    List<Qcontinous> cont = factorRoot.getQcontinous();

    for (Property p : props) {
      String label = p.getLabel();
      String val = p.getValue().replaceAll(",", "");// TODO ?

      switch (p.getType()) {

        case Property:
          Qproperty qprop = new Qproperty();
          qprop.setLabel(label);
          qprop.setValue(val);
          if (p.hasUnit())
            qprop.setUnit(p.getUnit());
          oldProps.add(qprop);
          break;

        case Factor:
          if (p.hasUnit()) {
            Qcontinous q = new Qcontinous();
            BigDecimal value = new BigDecimal(val);
            q.setLabel(label);
            q.setValue(value);
            q.setUnit(p.getUnit());
            cont.add(q);
          } else {
            Qcategorical q = new Qcategorical();
            q.setLabel(label);
            q.setValue(val);
            cats.add(q);
          }
          break;

        default:
          break;
      }
    }
    return root;
  }

  /**
   * returns experimental factors as well as other life.qbic.xml.properties
   * 
   * @param xml
   * @return
   * @throws JAXBException
   */
  public List<Property> getAllPropertiesFromXML(String xml) throws JAXBException {
    List<Property> res = getExpFactorsFromXML(xml);
    res.addAll(getPropertiesFromXML(xml));
    return res;
  }

  /**
   * only returns experimental factors (no other life.qbic.xml.properties)
   * 
   * @param xml
   * @return
   * @throws JAXBException
   */
  public List<Property> getExpFactorsFromXML(String xml) throws JAXBException {
    List<Property> res = new ArrayList<Property>();
    Qproperties props = parseXMLString(xml).getValue();
    if (props != null) {
      Qfactors fact = props.getQfactors();
      if (fact != null) {
        for (Qcategorical cat : fact.getQcategorical())
          res.add(new Property(cat.getLabel(), cat.getValue(), PropertyType.Factor));
        for (Qcontinous cont : fact.getQcontinous())
          res.add(new Property(cont.getLabel(), cont.getValue().toString(), cont.getUnit(),
              PropertyType.Factor));
      }
    }
    return res;
  }

  /**
   * only returns life.qbic.xml.properties (as opposed to experimental factors)
   * 
   * @param xml
   * @return
   * @throws JAXBException
   */
  public List<Property> getPropertiesFromXML(String xml) throws JAXBException {
    List<Property> res = new ArrayList<Property>();
    Qproperties props = parseXMLString(xml).getValue();
    if (props != null) {
      List<Qproperty> pLis = props.getQproperty();
      if (pLis != null) {
        for (Qproperty prop : pLis) {
          String label = prop.getLabel();
          String val = prop.getValue();
          Unit unit = prop.getUnit();
          if (unit == null)
            res.add(new Property(label, val, PropertyType.Property));
          else
            res.add(new Property(label, val, unit, PropertyType.Property));
        }
      }
    }
    return res;
  }

  public JAXBElement<Qproperties> createXMLFromProperties(List<Property> props)
      throws JAXBException {
    return addProperties(getEmptyXML(), props);
  }

  public JAXBElement<life.qbic.xml.properties.Qproperties> parseXMLString(String xml) throws JAXBException {
    if (xml == null || xml.isEmpty())
      return getEmptyXML();
    JAXBContext jc = JAXBContext.newInstance("life.qbic.xml.properties");
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    JAXBElement<Qproperties> root =
        unmarshaller.unmarshal(new StreamSource(new StringReader(xml)), Qproperties.class);
    return root;
  }

  public static void main(String[] args) throws JAXBException, SAXException, IOException {
    XMLParser p = new XMLParser();
  }

  public JAXBElement<life.qbic.xml.properties.Qproperties> getEmptyXML() throws JAXBException {
    JAXBContext jc = JAXBContext.newInstance("life.qbic.xml.properties");
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    JAXBElement<Qproperties> root = unmarshaller.unmarshal(new StreamSource(
        new StringReader("<?life.qbic.xml.properties version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            + "<qproperties>" + "</qproperties>")),
        Qproperties.class);
    return root;
  }

  public String toString(JAXBElement<life.qbic.xml.properties.Qproperties> root) throws JAXBException {
    JAXBContext jc = JAXBContext.newInstance("life.qbic.xml.properties");
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    StringWriter sw = new StringWriter();
    marshaller.marshal(root, sw);
    return sw.toString();
  }

}
