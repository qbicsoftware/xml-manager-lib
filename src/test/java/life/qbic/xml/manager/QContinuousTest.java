package life.qbic.xml.manager;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import life.qbic.xml.study.Qcontinuous;
import life.qbic.xml.study.Qcontlevel;

public class QContinuousTest {

	private static final Logger logger = LogManager.getLogger(QContinuousTest.class);
	private Qcontinuous empty;
	private Qcontinuous filled;
	private Qcontinuous filled2;


	@Before
	public void setUp() throws JAXBException {
		empty = StudyXMLParser.factory.createQcontinuous();
		filled = StudyXMLParser.factory.createQcontinuous();
		filled.setLabel("filled");
		Qcontlevel c1 = StudyXMLParser.factory.createQcontlevel();
		c1.setValue("1");
		c1.getEntityId().add("a");
		Qcontlevel c2 = StudyXMLParser.factory.createQcontlevel();
		c2.setValue("2");
		c2.getEntityId().addAll(Arrays.asList("b", "c"));
		filled.getQcontlevel().addAll(new ArrayList<Qcontlevel>(Arrays.asList(c1, c2)));
		
		filled2 = StudyXMLParser.factory.createQcontinuous();
		filled2.setLabel("filled");
		Qcontlevel c3 = StudyXMLParser.factory.createQcontlevel();
		c3.setValue("1");
		c3.getEntityId().add("a");
		Qcontlevel c4 = StudyXMLParser.factory.createQcontlevel();
		c4.setValue("4");
		c4.getEntityId().addAll(Arrays.asList("g", "h"));
		filled2.getQcontlevel().addAll(new ArrayList<Qcontlevel>(Arrays.asList(c3, c4)));
	}

	@Test
	public void testCreateLevels() {
		Map<Pair<String, String>, List<String>> levels = new HashMap<Pair<String, String>, List<String>>();
		levels.put(new ImmutablePair<String, String>("3", "T"), Arrays.asList("d"));
		levels.put(new ImmutablePair<String, String>("4", "T"), Arrays.asList("e", "f"));
		empty.createLevels(levels);
		
		assertEquals(empty.getQcontlevel().size(), 2);
		assertEquals(empty.getLevelOrNull("1"), null);
		assertEquals(empty.getLevelOrNull("3").getEntityId(), new HashSet<String>(Arrays.asList("d")));
		assertEquals(empty.getLevelOrNull("4").getEntityId(), new HashSet<String>(Arrays.asList("e","f")));

		filled.createLevels(levels);
		assertEquals(filled.getQcontlevel().size(), 4);
		assertEquals(filled.getLevelOrNull("0"), null);
		assertEquals(filled.getLevelOrNull("1").getEntityId(), new HashSet<String>(Arrays.asList("a")));
		assertEquals(filled.getLevelOrNull("2").getEntityId(), new HashSet<String>(Arrays.asList("b","c")));
		assertEquals(filled.getLevelOrNull("3").getEntityId(), new HashSet<String>(Arrays.asList("d")));
		assertEquals(filled.getLevelOrNull("4").getEntityId(), new HashSet<String>(Arrays.asList("e","f")));
	}

	@Test
	public void testUpdate() {
		Map<Pair<String, String>, List<String>> levels = new HashMap<Pair<String, String>, List<String>>();
		levels.put(new ImmutablePair<String, String>("2", "T"), Arrays.asList("e", "f"));
		levels.put(new ImmutablePair<String, String>("3", "T"), Arrays.asList("g", "h"));
		empty.update(levels);
		
		assertEquals(empty.getQcontlevel().size(), 2);
		assertEquals(empty.getLevelOrNull("1"), null);
		assertEquals(empty.getLevelOrNull("2").getEntityId(), new HashSet<String>(Arrays.asList("e","f")));
		assertEquals(empty.getLevelOrNull("3").getEntityId(), new HashSet<String>(Arrays.asList("g","h")));

		filled.update(levels);
		assertEquals(filled.getQcontlevel().size(), 3);
		assertEquals(filled.getLevelOrNull("0"), null);
		assertEquals(filled.getLevelOrNull("1").getEntityId(), new HashSet<String>(Arrays.asList("a")));
		assertEquals(filled.getLevelOrNull("2").getEntityId(), new HashSet<String>(Arrays.asList("b","c","e","f")));
		assertEquals(filled.getLevelOrNull("3").getEntityId(), new HashSet<String>(Arrays.asList("g","h")));
		

		filled2.update(levels);
		assertEquals(filled2.getQcontlevel().size(), 3);
		assertEquals(filled2.getLevelOrNull("0"), null);
		assertEquals(filled2.getLevelOrNull("1").getEntityId(), new HashSet<String>(Arrays.asList("a")));
		assertEquals(filled2.getLevelOrNull("2").getEntityId(), new HashSet<String>(Arrays.asList("e", "f")));
		assertEquals(filled2.getLevelOrNull("3").getEntityId(), new HashSet<String>(Arrays.asList("g", "h")));
		assertEquals(filled2.getLevelOrNull("4"), null);
	}
	
	@Test
	public void testUpdateOverwrite() {
		Map<Pair<String, String>, List<String>> overwrite = new HashMap<Pair<String, String>, List<String>>();
		overwrite.put(new ImmutablePair<String, String>("1", "T"), Arrays.asList("b", "f"));
		overwrite.put(new ImmutablePair<String, String>("2", "T"), Arrays.asList("a", "h"));
		filled.update(overwrite);
		assertEquals(filled.getQcontlevel().size(), 2);
		assertEquals(filled.getLevelOrNull("0"), null);
		assertEquals(filled.getLevelOrNull("1").getEntityId(), new HashSet<String>(Arrays.asList("b","f")));
		assertEquals(filled.getLevelOrNull("2").getEntityId(), new HashSet<String>(Arrays.asList("a","c","h")));
	}
	
	@Test
	public void testgetLevelOrNull() {
		assertEquals(null, empty.getLevelOrNull("1"));
		assertEquals(null, filled.getLevelOrNull("nonexistent"));
		assertTrue(filled.getLevelOrNull("1") instanceof Qcontlevel);
	}
}
