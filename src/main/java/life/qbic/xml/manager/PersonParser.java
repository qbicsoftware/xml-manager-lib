package life.qbic.xml.manager;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import life.qbic.xml.persons.Persons;
import life.qbic.xml.persons.Qperson;



public class PersonParser {

  public List<Qperson> getPersonsFromXML(String xml) throws JAXBException {
    Persons props = parseXMLString(xml).getValue();
    if (props.getQperson() != null)
      return props.getQperson();
    else
      return new ArrayList<Qperson>();
  }

  public JAXBElement<Persons> createXMLFromPersons(List<Qperson> persons) throws JAXBException {
    return addPersons(getEmptyXML(), persons);
  }

  private JAXBElement<Persons> addPersons(JAXBElement<Persons> root, List<Qperson> persons) {
    root.getValue().getQperson().addAll(persons);
    return root;
  }

  public JAXBElement<life.qbic.xml.persons.Persons> parseXMLString(String xml) throws JAXBException {
    JAXBContext jc = JAXBContext.newInstance("life.qbic.xml.persons");
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    JAXBElement<Persons> root =
        unmarshaller.unmarshal(new StreamSource(new StringReader(xml)), Persons.class);
    return root;
  }

  public JAXBElement<life.qbic.xml.persons.Persons> getEmptyXML() throws JAXBException {
    JAXBContext jc = JAXBContext.newInstance("life.qbic.xml.persons");
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    JAXBElement<Persons> root =
        unmarshaller.unmarshal(new StreamSource(new StringReader(
            "<?life.qbic.xml.properties version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<life.qbic.xml.persons>"
                + "</life.qbic.xml.persons>")), Persons.class);
    return root;
  }

  public String toString(JAXBElement<life.qbic.xml.persons.Persons> root) throws JAXBException {
    JAXBContext jc = JAXBContext.newInstance("life.qbic.xml.persons");
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    StringWriter sw = new StringWriter();
    marshaller.marshal(root, sw);
    return sw.toString();
  }

}
