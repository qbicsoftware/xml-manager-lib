package life.qbic.xml.manager;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import life.qbic.xml.study.Qexperiment;
import life.qbic.xml.study.Qfactors;

public class QfactorsTest {

	private static final Logger logger = LogManager.getLogger(QfactorsTest.class);

	private Qfactors factors = new Qfactors();
	private JAXBElement<Qexperiment> fullDesign;

	private Map<String, Map<Pair<String, String>, List<String>>> expDesign;

	@Before
	public void setUp() throws JAXBException {

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

		factors.createNewFactor("weight", "g", levelsFactor1);
		factors.createNewFactor("phenotype", levelsFactor2);
	}

	@Test
	public void testCreateNewFactor() {
		assertEquals(factors.getQcategorical().size(), 1);
		assertEquals(factors.getQcontinuous().size(), 1);
	}

}
