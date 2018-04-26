package life.qbic.xml.manager;


import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;

import life.qbic.xml.loci.GeneLocus;
import life.qbic.xml.loci.Loci;
import life.qbic.xml.loci.Locus;

public class LociParser {

  public List<GeneLocus> getLociFromXML(String xml) throws JAXBException {
    Loci loci = parseXMLString(xml).getValue();
    List<GeneLocus> res = new ArrayList<GeneLocus>();
    if (loci.getLocus() != null) {
      for (Locus l : loci.getLocus())
        res.add(new GeneLocus(l.getName(), l.getAllele()));
    }
    return res;
  }

  public JAXBElement<Loci> createXMLFromLoci(List<GeneLocus> loci) throws JAXBException {
    return addLoci(getEmptyXML(), loci);
  }

  private JAXBElement<Loci> addLoci(JAXBElement<Loci> root, List<GeneLocus> gLoci) {
    List<Locus> lociRoot = root.getValue().getLocus();
    for (GeneLocus l : gLoci) {
      Locus locus = new Locus();
      locus.setName(l.getLabel());
      for (String allele : l.getAlleles())
        locus.getAllele().add(allele);
      lociRoot.add(locus);
    }
    return root;
  }

  public JAXBElement<life.qbic.xml.loci.Loci> parseXMLString(String xml) throws JAXBException {
    JAXBContext jc = JAXBContext.newInstance("life.qbic.xml.loci");
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    JAXBElement<Loci> root =
        unmarshaller.unmarshal(new StreamSource(new StringReader(xml)), Loci.class);
    return root;
  }

  public JAXBElement<life.qbic.xml.loci.Loci> getEmptyXML() throws JAXBException {
    JAXBContext jc = JAXBContext.newInstance("life.qbic.xml.loci");
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    JAXBElement<Loci> root =
        unmarshaller.unmarshal(new StreamSource(
            new StringReader("<?life.qbic.xml.loci version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
                + "<life.qbic.xml.loci>" + "</life.qbic.xml.loci>")), Loci.class);
    return root;
  }

  public static void main(String[] args) throws JAXBException, SAXException, IOException {
    String xml =
        "<?life.qbic.xml.loci version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?> <life.qbic.xml.loci> <locus name=\"a\"><allele>wt</allele><allele>wt</allele></locus><locus name=\"b\"><allele>wt</allele><allele>wt</allele></locus></life.qbic.xml.loci>";
    LociParser p = new LociParser();
    XMLValidator x = new XMLValidator();
    x.validate(xml, new File("schemas/life.qbic.xml.loci.xsd"));
    JAXBContext jc = JAXBContext.newInstance("life.qbic.xml.loci");
    Unmarshaller unmarshaller = jc.createUnmarshaller();
    File file = new File("examples/loci_ex.xml");
    JAXBElement<Loci> root = unmarshaller.unmarshal(new StreamSource(file), Loci.class);
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    marshaller.marshal(root, System.out);
    p.addLoci(
        root,
        new ArrayList<GeneLocus>(Arrays.asList(new GeneLocus("sunday driver",
            new ArrayList<String>(Arrays.asList("wt", "wt"))))));
    marshaller.marshal(root, System.out);
  }

  public String toString(JAXBElement<life.qbic.xml.loci.Loci> root) throws JAXBException {
    JAXBContext jc = JAXBContext.newInstance("life.qbic.xml.loci");
    Marshaller marshaller = jc.createMarshaller();
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
    StringWriter sw = new StringWriter();
    marshaller.marshal(root, sw);
    return sw.toString();
  }

}
