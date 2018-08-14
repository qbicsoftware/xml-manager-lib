package life.qbic.xml.manager;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import life.qbic.xml.properties.Property;
import life.qbic.xml.properties.PropertyType;
import life.qbic.xml.properties.Unit;
import life.qbic.xml.study.ObjectFactory;
import life.qbic.xml.study.Qcategorical;
import life.qbic.xml.study.Qcatlevel;
import life.qbic.xml.study.Qcontinuous;
import life.qbic.xml.study.Qcontlevel;
import life.qbic.xml.study.Qexperiment;
import life.qbic.xml.study.Qfactors;
import life.qbic.xml.study.Qproperty;

public class NewXMLParser {

	public JAXBElement<Qexperiment> createNewDesign(List<String> omicsTypes,
			Map<String, Map<Pair<String, String>, List<String>>> expDesign, Map<String, List<Property>> otherProps)
			throws JAXBException {
		ObjectFactory factory = new ObjectFactory();
		JAXBElement<Qexperiment> res = getEmptyXML();
		Qexperiment root = res.getValue();
		root.getOmicsType().addAll(omicsTypes);
		life.qbic.xml.study.Qfactors factors = new Qfactors();
		root.setQfactors(factors);
		List<life.qbic.xml.study.Qcategorical> cats = factors.getQcategorical();
		List<Qcontinuous> conts = factors.getQcontinuous();
		List<life.qbic.xml.study.Qproperty> props = root.getQproperty();
		for (String code : otherProps.keySet()) {
			for (Property p : otherProps.get(code)) {
				life.qbic.xml.study.Qproperty qp = factory.createQproperty();
				qp.setEntityId(code);
				qp.setLabel(p.getLabel());
				qp.setValue(p.getValue());
				if (p.hasUnit())
					qp.setUnit(p.getUnit().getValue());
				props.add(qp);
			}
		}
		for (String label : expDesign.keySet()) {
			Map<Pair<String, String>, List<String>> levels = expDesign.get(label);
			Iterator<Pair<String, String>> it = levels.keySet().iterator();
			if (it.hasNext()) {
				String firstUnit = it.next().getRight();
				if (firstUnit == null || firstUnit.isEmpty()) {
					life.qbic.xml.study.Qcategorical cat = factory.createQcategorical();
					cat.setLabel(label);
					for (Pair<String, String> valunit : levels.keySet()) {
						List<String> ids = levels.get(valunit);
						life.qbic.xml.study.Qcatlevel catLvl = factory.createQcatlevel();
						catLvl.setValue(valunit.getValue());
						catLvl.getEntityId().addAll(ids);
						cat.getQcatlevel().add(catLvl);
					}
					cats.add(cat);
				} else {
					life.qbic.xml.study.Qcontinuous cont = factory.createQcontinuous();
					cont.setLabel(label);
					cont.setUnit(firstUnit);
					for (Pair<String, String> valunit : levels.keySet()) {
						List<String> ids = levels.get(valunit);
						life.qbic.xml.study.Qcontlevel contLvl = factory.createQcontlevel();
						contLvl.setValue(valunit.getValue());
						contLvl.getEntityId().addAll(ids);
						cont.getQcontlevel().add(contLvl);
					}
					conts.add(cont);
				}
			}
		}
		return res;
	}

	public JAXBElement<life.qbic.xml.study.Qexperiment> parseXMLString(String xml) throws JAXBException {
		if (xml == null || xml.isEmpty())
			return null;
		JAXBContext jc = JAXBContext.newInstance("life.qbic.xml.study");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		JAXBElement<Qexperiment> root = unmarshaller.unmarshal(new StreamSource(new StringReader(xml)),
				Qexperiment.class);
		return root;
	}

	public JAXBElement<life.qbic.xml.study.Qexperiment> getEmptyXML() throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("life.qbic.xml.study");
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		JAXBElement<Qexperiment> root = unmarshaller.unmarshal(new StreamSource(
				new StringReader("<?life.qbic.xml.study version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
						+ "<qexperiment>" + "</qexperiment>")),
				Qexperiment.class);
		return root;
	}

	public String toString(JAXBElement<life.qbic.xml.study.Qexperiment> root) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance("life.qbic.xml.study");
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
		StringWriter sw = new StringWriter();
		marshaller.marshal(root, sw);
		return sw.toString();
	}

	public List<String> getFactorLabels(JAXBElement<Qexperiment> expDesign) {
		List<String> res = new ArrayList<String>();
		Qfactors factors = expDesign.getValue().getQfactors();
		for (Qcategorical cat : factors.getQcategorical())
			res.add(cat.getLabel());
		for (Qcontinuous cont : factors.getQcontinuous())
			res.add(cont.getLabel());
		return res;
	}

	public Map<Pair<String, String>, Property> getFactorsForLabelsAndSamples(JAXBElement<Qexperiment> expDesign) {
		Map<Pair<String, String>, Property> res = new HashMap<Pair<String, String>, Property>();
		Qfactors factors = expDesign.getValue().getQfactors();
		for (Qcategorical cat : factors.getQcategorical()) {
			String lab = cat.getLabel();
			for (Qcatlevel level : cat.getQcatlevel()) {
				String val = level.getValue();
				Property p = new Property(lab, val, PropertyType.Factor);
				for (String sampleCode : level.getEntityId()) {
					Pair<String, String> labelCode = new ImmutablePair<String, String>(lab, sampleCode);
					res.put(labelCode, p);
				}
			}
		}
		for (Qcontinuous cont : factors.getQcontinuous()) {
			String lab = cont.getLabel();
			String unit = cont.getUnit();
			for (Qcontlevel level : cont.getQcontlevel()) {
				String val = level.getValue();
				Property p = new Property(lab, val, Unit.fromString(unit), PropertyType.Factor);
				for (String sampleCode : level.getEntityId()) {
					Pair<String, String> labelCode = new ImmutablePair<String, String>(lab, sampleCode);
					res.put(labelCode, p);
				}
			}
		}
		return res;
	}

	public Map<String, List<Property>> getPropertiesForSampleCode(JAXBElement<Qexperiment> expDesign) {
		List<Qproperty> props = expDesign.getValue().getQproperty();
		Map<String, List<Property>> res = new HashMap<String, List<Property>>();
		PropertyType type = PropertyType.Property;
		for (Qproperty p : props) {
			String code = p.getEntityId();
			Property property;
			if (p.getUnit() == null)
				property = new Property(p.getLabel(), p.getValue(), type);
			else
				property = new Property(p.getLabel(), p.getValue(), Unit.fromString(p.getUnit()), type);
			if (res.containsKey(code)) {
				res.get(code).add(property);
			} else {
				res.put(code, new ArrayList<Property>(Arrays.asList(property)));
			}
		}
		return res;
	}

}
