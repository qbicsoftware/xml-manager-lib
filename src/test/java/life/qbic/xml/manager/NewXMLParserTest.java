package life.qbic.xml.manager;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import life.qbic.xml.properties.Property;
import life.qbic.xml.properties.PropertyType;
import life.qbic.xml.properties.Unit;
import life.qbic.xml.study.Qexperiment;
import life.qbic.xml.study.Qproperty;
import life.qbic.xml.study.TechnologyType;

public class NewXMLParserTest {

	private static final Logger logger = LogManager.getLogger(NewXMLParserTest.class);

	private NewXMLParser parser;
	private JAXBElement<Qexperiment> empty;
	private JAXBElement<Qexperiment> fullDesign;
	private JAXBElement<Qexperiment> noProps;
	private JAXBElement<Qexperiment> noTypes;
	private JAXBElement<Qexperiment> noPropsNoFactors;
	private JAXBElement<Qexperiment> noFactors;

	private Map<String, Map<Pair<String, String>, List<String>>> expDesign;
	private Map<String, List<String>> techTypes;
	private Map<String, List<Property>> otherProps;

	@Before
	public void setUp() throws JAXBException {
		parser = new NewXMLParser();
		empty = parser.getEmptyXML();

		techTypes = new HashMap<String,List<String>>();
		List<String> t1 = new ArrayList<String>(Arrays.asList("1", "2", "3"));
		List<String> t2 = new ArrayList<String>(Arrays.asList("1", "4", "5"));
		List<String> t3 = new ArrayList<String>(Arrays.asList("4", "5", "6"));
		techTypes.put("type 1", t1);
		techTypes.put("type 2", t2);
		techTypes.put("type 3", t3);

		expDesign = new HashMap<String, Map<Pair<String, String>, List<String>>>();
		Map<Pair<String, String>, List<String>> levelsFactor1 = new HashMap<Pair<String, String>, List<String>>();
		Pair<String, String> level1Value1 = new ImmutablePair<String, String>("10", "g");
		levelsFactor1.put(level1Value1, new ArrayList<String>(Arrays.asList("1", "2")));
		Pair<String, String> level1Value2 = new ImmutablePair<String, String>("20", "g");
		levelsFactor1.put(level1Value2, new ArrayList<String>(Arrays.asList("3", "4", "5")));
		Pair<String, String> level1Value3 = new ImmutablePair<String, String>("30", "g");
		levelsFactor1.put(level1Value3, new ArrayList<String>(Arrays.asList("6")));

		expDesign.put("weight", levelsFactor1);

		Map<Pair<String, String>, List<String>> levelsFactor2 = new HashMap<Pair<String, String>, List<String>>();
		Pair<String, String> level2Value1 = new ImmutablePair<String, String>("disease", null);
		levelsFactor2.put(level2Value1, new ArrayList<String>(Arrays.asList("1", "2", "3")));
		Pair<String, String> level2Value2 = new ImmutablePair<String, String>("control", null);
		levelsFactor2.put(level2Value2, new ArrayList<String>(Arrays.asList("4", "5", "6")));

		expDesign.put("phenotype", levelsFactor2);

		otherProps = new HashMap<String, List<Property>>();
		List<Property> props1 = new ArrayList<Property>();
		props1.add(new Property("name", "Cargo Cult", PropertyType.Property));
		props1.add(new Property("length", "50000", Unit.Meter, PropertyType.Property));
		List<Property> props2 = new ArrayList<Property>();
		props2.add(new Property("name", "Prosthetic Conscience", PropertyType.Property));
		props2.add(new Property("length", "56000", Unit.Meter, PropertyType.Property));
		props2.add(new Property("class", "unknown", PropertyType.Property));
		otherProps.put("ship1", props1);
		otherProps.put("ship2", props2);
		
		List<TechnologyType> techs = parser.mapToTechnologyTypes(techTypes);

		fullDesign = parser.createNewDesign(techs, expDesign, otherProps);
		noProps = parser.createNewDesign(techs, expDesign, new HashMap<String, List<Property>>());
		noTypes = parser.createNewDesign(new ArrayList<TechnologyType>(), expDesign, otherProps);
		noPropsNoFactors = parser.createNewDesign(techs,
				new HashMap<String, Map<Pair<String, String>, List<String>>>(), new HashMap<String, List<Property>>());
		noFactors = parser.createNewDesign(techs, new HashMap<String, Map<Pair<String, String>, List<String>>>(),
				otherProps);
	}

	@Test
	public void testGetEmptyXML() {
		Qexperiment empty = this.empty.getValue();
		assertTrue(empty.getTechnologyType().isEmpty());
		assertEquals(empty.getQfactors(), null);
		assertTrue(empty.getQproperty().isEmpty());
	}

	@Test
	public void testCreateNewDesign() {
		Qexperiment fullDesign = this.fullDesign.getValue();
		assertTrue(fullDesign.getQfactors() != null);
		assertFalse(fullDesign.getQproperty().isEmpty());
		assertEquals(fullDesign.getTechnologyType().size(), 3);
		assertEquals(fullDesign.getQfactors().getQcategorical().get(0).getLabel(), "phenotype");
		assertEquals(fullDesign.getQfactors().getQcategorical().get(0).getQcatlevel().size(), 2);
		assertEquals(fullDesign.getQfactors().getQcontinuous().get(0).getLabel(), "weight");
		assertEquals(fullDesign.getQfactors().getQcontinuous().get(0).getQcontlevel().size(), 3);
		int propCount = 0;
		for (List<Property> props : otherProps.values()) {
			propCount += props.size();
		}
		assertEquals(fullDesign.getQproperty().size(), propCount);

		Qexperiment noProps = this.noProps.getValue();
		assertEquals(noProps.getTechnologyType().size(), 3);
		assertTrue(noProps.getQproperty().isEmpty());
		assertEquals(noProps.getQfactors().getQcategorical().get(0).getLabel(), "phenotype");
		assertEquals(noProps.getQfactors().getQcategorical().get(0).getQcatlevel().size(), 2);
		assertEquals(noProps.getQfactors().getQcontinuous().get(0).getLabel(), "weight");
		assertEquals(noProps.getQfactors().getQcontinuous().get(0).getQcontlevel().size(), 3);

		Qexperiment noTypes = this.noTypes.getValue();
		assertTrue(noTypes.getTechnologyType().isEmpty());
		propCount = 0;
		for (List<Property> props : otherProps.values()) {
			propCount += props.size();
		}
		assertEquals(noTypes.getQproperty().size(), propCount);
		assertEquals(noTypes.getQfactors().getQcategorical().get(0).getLabel(), "phenotype");
		assertEquals(noTypes.getQfactors().getQcategorical().get(0).getQcatlevel().size(), 2);
		assertEquals(noTypes.getQfactors().getQcontinuous().get(0).getLabel(), "weight");
		assertEquals(noTypes.getQfactors().getQcontinuous().get(0).getQcontlevel().size(), 3);

		Qexperiment noPropsNoFactors = this.noPropsNoFactors.getValue();
		assertEquals(noPropsNoFactors.getTechnologyType().size(), 3);
		assertTrue(noPropsNoFactors.getQproperty().isEmpty());
		assertTrue(noPropsNoFactors.getQfactors().getQcategorical().isEmpty());
		assertTrue(noPropsNoFactors.getQfactors().getQcontinuous().isEmpty());

		Qexperiment noFactors = this.noFactors.getValue();
		propCount = 0;
		for (List<Property> props : otherProps.values()) {
			propCount += props.size();
		}
		assertEquals(noFactors.getQproperty().size(), propCount);
		assertEquals(noFactors.getTechnologyType().size(), 3);
		assertTrue(noFactors.getQfactors().getQcategorical().isEmpty());
		assertTrue(noFactors.getQfactors().getQcontinuous().isEmpty());
	}

	@Test
	public void testParseXMLString() throws JAXBException {
		String xml1 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<qexperiment>"
				+ "	<technology_type name=\"RNA-SEQ\">"
				+ "<entity_id>QABCD004AF</entity_id>"
				+ "</technology_type>" + "	<technology_type name = \"Microarray Expression Analysis\"><entity_id>QABCD004AF</entity_id></technology_type>"
				+ "    <qfactors>" + "        <qcategorical label=\"phenotype\">"
				+ "        	<qcatlevel value=\"pupper\">" + "    			<entity_id>QABCD003AB</entity_id>"
				+ "    			<entity_id>QABCD004AF</entity_id>" + "    		</qcatlevel>"
				+ "    		<qcatlevel value=\"doggo\">" + "    			<entity_id>QABCD005AB</entity_id>"
				+ "    			<entity_id>QABCD006AF</entity_id>" + "    		</qcatlevel>"
				+ "    		<qcatlevel value=\"cate\">" + "    			<entity_id>QABCD008AB</entity_id>"
				+ "    		</qcatlevel>" + "    	</qcategorical>" + "    	<qcategorical label=\"genotype\">"
				+ "        	<qcatlevel value=\"AA\">" + "    			<entity_id>QABCD003AB</entity_id>"
				+ "    			<entity_id>QABCD004AF</entity_id>" + "    		</qcatlevel>"
				+ "    		<qcatlevel value=\"aa\">" + "    			<entity_id>QABCD005AB</entity_id>"
				+ "    			<entity_id>QABCD006AF</entity_id>" + "    		</qcatlevel>" + "    	</qcategorical>"
				+ "        <qcontinuous label=\"age\" unit=\"years\">" + "        	<qcontlevel value=\"0.5\">"
				+ "    			<entity_id>QABCD003AB</entity_id>" + "    			<entity_id>QABCD004AF</entity_id>"
				+ "    		</qcontlevel>" + "    		<qcontlevel value=\"2\">"
				+ "    			<entity_id>QABCD005AB</entity_id>" + "    			<entity_id>QABCD006AF</entity_id>"
				+ "    		</qcontlevel>" + "    		<qcontlevel value=\"10\">"
				+ "    			<entity_id>QABCD008AB</entity_id>" + "    		</qcontlevel>"
				+ "        </qcontinuous>" + "    </qfactors>"
				+ "    <qproperty entity_id=\"QABCD008AB\" label=\"tail_length\" value=\"0.3\" unit=\"m\"></qproperty>"
				+ "    <qproperty entity_id=\"QABCD008AB\" label=\"name\" value=\"Krishna\"></qproperty>"
				+ "</qexperiment>";

		assertTrue(parser.validate(xml1));
		JAXBElement<Qexperiment> firstParse1 = parser.parseXMLString(xml1);
		JAXBElement<Qexperiment> secondParse1 = parser.parseXMLString(parser.toString(firstParse1));
		assertEquals(secondParse1.getValue().getTechnologyType(), firstParse1.getValue().getTechnologyType());
		List<Qproperty> q2 = secondParse1.getValue().getQproperty();
		List<Qproperty> q1 = firstParse1.getValue().getQproperty();
		for (int i = 0; i < q1.size(); i++) {
			assertEquals(q1.get(i), q2.get(i));
		}
		assertEquals(secondParse1.getValue().getQfactors(), firstParse1.getValue().getQfactors());

		String xml2 = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" + "<qexperiment>"
				+ "	<technology_type name = \"RNA-SEQ\"></technology_type>" + "<qfactors/>"
				+ "    <qproperty entity_id=\"QABCD008AB\" label=\"tail_length\" value=\"0.3\" unit=\"m\"></qproperty>"
				+ "    <qproperty entity_id=\"QABCD008AB\" label=\"name\" value=\"Krishna\"></qproperty>"
				+ "</qexperiment>";

		assertTrue(parser.validate(xml2));
		JAXBElement<Qexperiment> firstParse2 = parser.parseXMLString(xml2);
		JAXBElement<Qexperiment> secondParse2 = parser.parseXMLString(parser.toString(firstParse2));
		q2 = secondParse2.getValue().getQproperty();
		q1 = firstParse2.getValue().getQproperty();
		for (int i = 0; i < q1.size(); i++) {
			assertEquals(q1.get(i), q2.get(i));
		}
		assertEquals(secondParse2.getValue().getQfactors(), firstParse2.getValue().getQfactors());
	}

	@Test
	public void testToString() throws JAXBException {
		assertNotEquals(null, parser.toString(empty));
		assertNotEquals("", parser.toString(empty));

		logger.info("testing empty xml");
		assertFalse(parser.validate(parser.toString(empty)));
		logger.info("testing full design");
		assertTrue(parser.validate(parser.toString(fullDesign)));
		logger.info("testing no factors xml");
		assertTrue(parser.validate(parser.toString(noFactors)));
		logger.info("testing no props xml");
		assertTrue(parser.validate(parser.toString(noProps)));
		logger.info("testing props and no factors xml");
		assertTrue(parser.validate(parser.toString(noPropsNoFactors)));
		logger.info("testing omics types xml");
		assertTrue(parser.validate(parser.toString(noTypes)));
	}

	@Test
	public void testGetFactorLabels() {
		Set<String> labels = new HashSet<String>(Arrays.asList("weight", "phenotype"));
		assertEquals(parser.getFactorLabels(fullDesign), labels);
		assertTrue(parser.getFactorLabels(noFactors).isEmpty());
		assertEquals(parser.getFactorLabels(noProps), labels);
		assertTrue(parser.getFactorLabels(noPropsNoFactors).isEmpty());
		assertEquals(parser.getFactorLabels(noTypes), labels);
		assertTrue(parser.getFactorLabels(empty).isEmpty());
	}

	@Test
	public void testGetFactorsForLabelsAndSamples() {
		Set<Pair<String, String>> expected = new HashSet<Pair<String, String>>();
		expected.add(new ImmutablePair<String, String>("phenotype", "1"));
		expected.add(new ImmutablePair<String, String>("phenotype", "2"));
		expected.add(new ImmutablePair<String, String>("phenotype", "3"));
		expected.add(new ImmutablePair<String, String>("phenotype", "4"));
		expected.add(new ImmutablePair<String, String>("phenotype", "5"));
		expected.add(new ImmutablePair<String, String>("phenotype", "6"));
		expected.add(new ImmutablePair<String, String>("weight", "1"));
		expected.add(new ImmutablePair<String, String>("weight", "2"));
		expected.add(new ImmutablePair<String, String>("weight", "3"));
		expected.add(new ImmutablePair<String, String>("weight", "4"));
		expected.add(new ImmutablePair<String, String>("weight", "5"));
		expected.add(new ImmutablePair<String, String>("weight", "6"));

		assertEquals(parser.getFactorsForLabelsAndSamples(fullDesign).keySet(), expected);
		assertEquals(parser.getFactorsForLabelsAndSamples(fullDesign).size(), 12);
		assertTrue(parser.getFactorsForLabelsAndSamples(noFactors).isEmpty());
		assertTrue(parser.getFactorsForLabelsAndSamples(empty).isEmpty());
	}

	@Test
	public void testGetPropertiesForSampleCode() {
		assertEquals(otherProps, parser.getPropertiesForSampleCode(fullDesign));
		assertEquals(otherProps, parser.getPropertiesForSampleCode(noFactors));
		assertEquals(otherProps, parser.getPropertiesForSampleCode(noTypes));
		assertTrue(parser.getPropertiesForSampleCode(empty).isEmpty());
		assertTrue(parser.getPropertiesForSampleCode(noProps).isEmpty());
	}

}
