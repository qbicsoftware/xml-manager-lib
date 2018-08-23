package life.qbic.xml.manager;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

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
import life.qbic.xml.study.TechnologyType;

public class NewXMLParser {

	private final Logger logger = LogManager.getLogger(NewXMLParser.class);
	final private XMLValidator validator = new XMLValidator();

	public boolean validate(String xml) {
		File xsd = new File(getClass().getResource("qproperties.xsd").getFile());
		try {
			return validator.validate(xml, xsd);
		} catch (SAXException | IOException e) {
			return false;
		}
	}

	public List<TechnologyType> mapToTechnologyTypes(Map<String, List<String>> techTypes) {
		ObjectFactory factory = new ObjectFactory();
		List<TechnologyType> res = new ArrayList<TechnologyType>();
		for (String technology : techTypes.keySet()) {
			TechnologyType tt = factory.createTechnologyType();
			tt.setName(technology);
			for (String id : techTypes.get(technology)) {
				tt.getEntityId().add(id);
			}
			res.add(tt);
		}
		return res;
	}

	public JAXBElement<Qexperiment> createNewDesign(List<TechnologyType> techTypes,
			Map<String, Map<Pair<String, String>, List<String>>> expDesign, Map<String, List<Property>> otherProps)
			throws JAXBException {
		ObjectFactory factory = new ObjectFactory();
		JAXBElement<Qexperiment> res = getEmptyXML();
		Qexperiment root = res.getValue();

		root.getTechnologyType().addAll(techTypes);
		//		List<TechnologyType> techs = root.getTechnologyType();
		//		for (String tech : newTechTypes.keySet()) {
		//			TechnologyType tt = factory.createTechnologyType();
		//			tt.setName(tech);
		//			tt.getEntityId().addAll(newTechTypes.get(tech));
		//			techs.add(tt);
		//		}

		life.qbic.xml.study.Qfactors factors = new Qfactors();
		root.setQfactors(factors);
		List<life.qbic.xml.study.Qcategorical> cats = factors.getQcategorical();
		List<Qcontinuous> conts = factors.getQcontinuous();
		List<life.qbic.xml.study.Qproperty> props = root.getQproperty();
		for (String id : otherProps.keySet()) {
			for (Property p : otherProps.get(id)) {
				life.qbic.xml.study.Qproperty qp = factory.createQproperty();
				qp.setEntityId(id);
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
						catLvl.setValue(valunit.getLeft());
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
						contLvl.setValue(valunit.getLeft());
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

	public Set<String> getFactorLabels(JAXBElement<Qexperiment> expDesign) {
		Set<String> res = new HashSet<String>();
		Qfactors factors = expDesign.getValue().getQfactors();
		if (factors != null) {
			for (Qcategorical cat : factors.getQcategorical())
				res.add(cat.getLabel());
			for (Qcontinuous cont : factors.getQcontinuous())
				res.add(cont.getLabel());
		}
		return res;
	}

	public Map<Pair<String, String>, Property> getFactorsForLabelsAndSamples(JAXBElement<Qexperiment> expDesign) {
		Map<Pair<String, String>, Property> res = new HashMap<Pair<String, String>, Property>();
		Qfactors factors = expDesign.getValue().getQfactors();
		if (factors != null) {
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
		}
		return res;
	}

	public JAXBElement<Qexperiment> removeReferencesToMissingIDs(JAXBElement<Qexperiment> expDesign,
			Set<String> existingIDs) throws JAXBException {
		List<TechnologyType> techTypes = getSamplesForTechTypes(expDesign);
		List<TechnologyType> newTechTypes = new ArrayList<TechnologyType>();
		for (TechnologyType t : techTypes) {
			TechnologyType newT = new TechnologyType();
			for (String code : t.getEntityId()) {
				if (existingIDs.contains(code)) {
					newT.getEntityId().add(code);
				}
			}
			newTechTypes.add(newT);
		}

		Map<String, List<Property>> props = getPropertiesForSampleCode(expDesign);
		Map<String, List<Property>> newProps = new HashMap<String, List<Property>>();
		for (String id : props.keySet()) {
			if (existingIDs.contains(id)) {
				newProps.put(id, props.get(id));
			} else {
				logger.info("removing property for sample " + id
						+ " from the design, since sample with this id does not exist anymore.");
			}
		}

		Map<Pair<String, String>, Property> factors = getFactorsForLabelsAndSamples(expDesign);
		Map<String, Map<Pair<String, String>, List<String>>> newFactors = new HashMap<String, Map<Pair<String, String>, List<String>>>();

		for (Pair<String, String> labelAndCode : factors.keySet()) {
			String label = labelAndCode.getLeft();
			String id = labelAndCode.getRight();
			Property prop = factors.get(labelAndCode);
			if (existingIDs.contains(id)) {
				String unit = null;
				if (prop.hasUnit()) {
					unit = prop.getUnit().toString();
				}
				Pair<String, String> newProp = new ImmutablePair<String, String>(prop.getValue(), unit);
				Map<Pair<String, String>, List<String>> level = new HashMap<Pair<String, String>, List<String>>();
				if (newFactors.containsKey(label)) {
					level = newFactors.get(label);

					if (level.containsKey(newProp)) {
						level.get(newProp).add(id);
					} else {
						level.put(newProp, new ArrayList<String>(Arrays.asList(id)));
					}
				} else {
					level.put(newProp, new ArrayList<String>(Arrays.asList(id)));
					newFactors.put(label, level);
				}
			}
		}
		return createNewDesign(newTechTypes, newFactors, newProps);
	}

	public JAXBElement<Qexperiment> mergeDesigns(JAXBElement<Qexperiment> existing, List<TechnologyType> techTypes,
			Map<String, Map<Pair<String, String>, List<String>>> expDesign, Map<String, List<Property>> otherProps) {
		ObjectFactory factory = new ObjectFactory();
		Qexperiment root = existing.getValue();

//		root.getTechnologyType().addAll(techTypes);
				List<TechnologyType> techs = root.getTechnologyType();
				for (String tech : newTechTypes.keySet()) {
					TechnologyType tt = factory.createTechnologyType();
					tt.setName(tech);
					tt.getEntityId().addAll(newTechTypes.get(tech));
					techs.add(tt);
				}

		life.qbic.xml.study.Qfactors factors = new Qfactors();
		root.setQfactors(factors);
		List<life.qbic.xml.study.Qcategorical> cats = factors.getQcategorical();
		List<Qcontinuous> conts = factors.getQcontinuous();
		List<life.qbic.xml.study.Qproperty> props = root.getQproperty();
		for (String id : otherProps.keySet()) {
			for (Property p : otherProps.get(id)) {
				life.qbic.xml.study.Qproperty qp = factory.createQproperty();
				qp.setEntityId(id);
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
						catLvl.setValue(valunit.getLeft());
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
						contLvl.setValue(valunit.getLeft());
						contLvl.getEntityId().addAll(ids);
						cont.getQcontlevel().add(contLvl);
					}
					conts.add(cont);
				}
			}
		}
		return existing;
	}

	private List<TechnologyType> getSamplesForTechTypes(JAXBElement<Qexperiment> expDesign) {
		List<TechnologyType> types = expDesign.getValue().getTechnologyType();
		return types;
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
