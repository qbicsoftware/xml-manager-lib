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

public class StudyXMLParser {

	private final Logger logger = LogManager.getLogger(StudyXMLParser.class);
	final private XMLValidator validator = new XMLValidator();
	final public static ObjectFactory factory = new ObjectFactory();

	public boolean validate(String xml) {
		File xsd = new File(getClass().getResource("qproperties.xsd").getFile());
		try {
			return validator.validate(xml, xsd);
		} catch (SAXException | IOException e) {
			return false;
		}
	}

	public List<TechnologyType> mapToTechnologyTypes(Map<String, Set<String>> techTypes) {
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
			Map<String, Map<Pair<String, String>, List<String>>> expDesign, Map<String, List<Qproperty>> otherProps)
			throws JAXBException {
		JAXBElement<Qexperiment> res = getEmptyXML();
		mergeDesigns(res, techTypes, expDesign, otherProps);

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
			for (Qcategorical f : factors.getQcategorical()) {
				Qcategorical cat = (Qcategorical) f;
				res.add(cat.getLabel());
			}
			for (Qcontinuous f : factors.getQcontinuous()) {
				Qcontinuous cont = (Qcontinuous) f;
				res.add(cont.getLabel());
			}
		}
		return res;
	}

	public Map<Pair<String, String>, Property> getFactorsForLabelsAndSamples(JAXBElement<Qexperiment> expDesign) {
		Map<Pair<String, String>, Property> res = new HashMap<Pair<String, String>, Property>();
		Qfactors factors = expDesign.getValue().getQfactors();
		if (factors != null) {
			for (Qcategorical f : factors.getQcategorical()) {
				Qcategorical cat = (Qcategorical) f;
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
			for (Qcontinuous f : factors.getQcontinuous()) {
				Qcontinuous cont = (Qcontinuous) f;
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
			if (!newT.getEntityId().isEmpty()) {
				newT.setName(t.getName());
				newTechTypes.add(newT);
			}
		}

		Map<String, List<Property>> props = getPropertiesForSampleCode(expDesign);
		Map<String, List<Qproperty>> newProps = new HashMap<String, List<Qproperty>>();
		for (String id : props.keySet()) {
			if (existingIDs.contains(id)) {
				List<Qproperty> propsForThisID = new ArrayList<Qproperty>();
				for (Property p : props.get(id)) {
					Qproperty newProp = factory.createQproperty();
					newProp.setLabel(p.getLabel());
					newProp.setValue(p.getValue());
					if (p.hasUnit())
						newProp.setUnit(p.getUnit().getValue());
					newProp.setEntityId(id);
					propsForThisID.add(newProp);
				}
				newProps.put(id, propsForThisID);
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
					unit = prop.getUnit().getValue();
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

	private JAXBElement<Qexperiment> addTechTypesToDesign(JAXBElement<Qexperiment> experiment,
			List<TechnologyType> techTypes) {
		Qexperiment root = experiment.getValue();
		Map<String, Set<String>> idsForType = new HashMap<String, Set<String>>();
		for (TechnologyType t : root.getTechnologyType()) {
			idsForType.put(t.getName(), t.getEntityId());
		}
		List<TechnologyType> existing = root.getTechnologyType();
		for (TechnologyType t : techTypes) {
			if (idsForType.containsKey(t.getName())) {
				idsForType.get(t.getName()).addAll(t.getEntityId());
			} else {
				existing.add(t);
			}
		}
		return experiment;
	}

	private JAXBElement<Qexperiment> addPropertiesToDesign(JAXBElement<Qexperiment> existing,
			Map<String, List<Qproperty>> newProps) {
		Map<Pair<String, String>, Qproperty> labelAndID = new HashMap<Pair<String, String>, Qproperty>();
		List<Qproperty> existingProps = existing.getValue().getQproperty();
		for (Qproperty prop : existingProps) {
			labelAndID.put(new ImmutablePair<String, String>(prop.getLabel(), prop.getEntityId()), prop);
		}
		for (String id : newProps.keySet()) {
			for (Qproperty p : newProps.get(id)) {
				Pair<String, String> labelID = new ImmutablePair<String, String>(p.getLabel(), id);
				// for every id and property level add if it doesn't exist, update otherwise
				if (!labelAndID.containsKey(labelID)) {
					existingProps.add(p);
				} else {
					Qproperty old = labelAndID.get(labelID);
					old.setValue(p.getValue());
					old.setUnit(p.getUnit());
				}
			}
		}
		return existing;
	}

	private JAXBElement<Qexperiment> addFactorsToDesign(JAXBElement<Qexperiment> existing,
			Map<String, Map<Pair<String, String>, List<String>>> newDesign) {
		Qfactors factors = existing.getValue().getQfactors();
		if (factors == null) {
			existing.getValue().setQfactors(factory.createQfactors());
			factors = existing.getValue().getQfactors();
		}
		for (String label : newDesign.keySet()) {
			Map<Pair<String, String>, List<String>> levels = newDesign.get(label);
			Iterator<Pair<String, String>> it = levels.keySet().iterator();
			if (it.hasNext()) {
				String unit = it.next().getRight();
				// no unit: categorical factor
				if (unit == null || unit.isEmpty()) {
					Qcategorical oldCat = factors.getCatFactorOrNull(label);
					// no factor, add everything
					if (oldCat == null) {
						factors.createNewFactor(label, levels);
					}
					// factor exists, add missing information
					else {
						oldCat.update(levels);
					}
				}
				// unit: continuous factor
				else {
					Qcontinuous oldCont = factors.getContFactorOrNull(label);
					// no factor, add everything
					if (oldCont == null) {
						factors.createNewFactor(label, unit, levels);
					}
					// factor exists, add missing information
					else {
						oldCont.update(levels);
					}
				}
			}
		}
		return existing;
	}

	public JAXBElement<Qexperiment> mergeDesigns(JAXBElement<Qexperiment> existing, List<TechnologyType> techTypes,
			Map<String, Map<Pair<String, String>, List<String>>> expDesign, Map<String, List<Qproperty>> otherProps) {

		addTechTypesToDesign(existing, techTypes);
		addPropertiesToDesign(existing, otherProps);
		addFactorsToDesign(existing, expDesign);

		return existing;
	}

	public List<TechnologyType> getSamplesForTechTypes(JAXBElement<Qexperiment> expDesign) {
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

	public List<Property> getFactorsAndPropertiesForSampleCode(JAXBElement<Qexperiment> expDesign, String code) {
		List<Property> res = new ArrayList<>();
		List<Property> props = getPropertiesForSampleCode(expDesign).get(code);
		if (props != null) {
			res.addAll(props);
		}
		Map<Pair<String, String>, Property> factorsForLabelsAndSamples = getFactorsForLabelsAndSamples(expDesign);
		for (String label : getFactorLabels(expDesign)) {
			Pair<String, String> key = new ImmutablePair<>(label, code);
			Property f = factorsForLabelsAndSamples.get(key);
			if (f != null) {
				res.add(f);
			}
		}
		return res;
	}

}
