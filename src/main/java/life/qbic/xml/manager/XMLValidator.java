package life.qbic.xml.manager;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

public class XMLValidator {

	private static final Logger logger = LogManager.getLogger(XMLValidator.class);

	private boolean validate(Source xml, File schemaFile) throws IOException, SAXException {
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		Schema schema = schemaFactory.newSchema(schemaFile);
		Validator validator = schema.newValidator();
		try {
			validator.validate(xml);
			logger.info("Tested xml is valid.");
			return true;
		} catch (SAXException e) {
			logger.warn("Tested xml is NOT valid");
			logger.warn("Reason: " + e.getLocalizedMessage());
			return false;
		}
	}

	public boolean validate(String xml, File schemaFile) throws SAXException, IOException {
		return validate(new StreamSource(new StringReader(xml)), schemaFile);
	}

	public boolean validate(File xmlFile, File schemaFile) throws IOException, SAXException {
		return validate(new StreamSource(xmlFile), schemaFile);
	}
}
