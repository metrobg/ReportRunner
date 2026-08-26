package com.metrobg;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.*;

public class XMLHelper {
    // Parse an XML document from a character Reader
    public static Document parse( Reader r, URL baseUrl )
            throws IOException, SAXParseException, SAXException  {
        // Construct an input source from the Reader
        InputSource input = new InputSource(r);
        // Set the base URL if provided
        if (baseUrl != null) input.setSystemId(baseUrl.toString());
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            // Parse in Non-Validating Mode
            dbf.setValidating(false);
            DocumentBuilder builder = dbf.newDocumentBuilder();
            return builder.parse(input);
        } catch (ParserConfigurationException e) {
            throw new IOException(e);
        }
    }
    // Parse XML from an InputStream
    public static Document parse( InputStream is, URL baseURL )
            throws SAXParseException, SAXException, IOException {
        // Construct a Reader and call parse(Reader)
        return parse( new InputStreamReader(is), baseURL );
    }
    // Parse XML From a String
    public static Document parse( String xml, URL baseurl )
            throws MalformedURLException, IOException,
            SAXParseException, SAXException {

        // Construct a reader and call parse(Reader)
        return parse(new StringReader(xml),baseurl);
    }
    // Parse XML from a URL
    public static Document parse( URL url ) throws IOException,
            SAXParseException,
            SAXException  {
        // Construct an InputStream and call parse(InputStream)
        // Use the url passed-in as the baseURL
        return parse( url.openStream(), url);
    }
    // Format information for a parse error
    public static String formatParseError(SAXParseException s) {
        int lineNum = s.getLineNumber();
        int  colNum = s.getColumnNumber();
        String file = s.getSystemId();
        String  err = s.getMessage();
        return "XML parse error " + (file != null ? "in file " + file + "\n" : "")+
                "at line " + lineNum + ", character " + colNum + "\n" + err;
    }
}
