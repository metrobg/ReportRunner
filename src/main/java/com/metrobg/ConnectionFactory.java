package com.metrobg;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;


public class ConnectionFactory {
    private static Document root;

    private static Document loadConnections() throws Exception {
        if (root == null) {
            // Read Connections.xml from the runtime CLASSPATH
            Class<ConnectionFactory> c = ConnectionFactory.class;
            InputStream file = c.getResourceAsStream("/Connections.xml");
            if (file == null) {
                throw new FileNotFoundException("Connections.xml not in CLASSPATH");
            }
            // Parse Connections.xml and cache the Document of config info
            root = XMLHelper.parse(file, null);
        }
        return root;
    }

    private static Node findConnectionNode(String name) throws Exception {
        Document doc = loadConnections();
        // Prepare an XPath expression to find the connectioin named 'name'
        String pattern = "/connections/connection[@name='" + name + "']";
        XPath xpath = XPathFactory.newInstance().newXPath();
        // Find the first connection matching the expression above
        return (Node) xpath.evaluate(pattern, doc, XPathConstants.NODE);
    }

    private static String childText(Node connNode, String childName) throws Exception {
        XPath xpath = XPathFactory.newInstance().newXPath();
        return xpath.evaluate(childName, connNode);
    }

    public static Connection getConnection(String name) throws Exception {
        Node connNode = findConnectionNode(name);
        if (connNode != null) {
            String username = childText(connNode, "username");
            String password = childText(connNode, "password");
            String dburl = childText(connNode, "dburl");
            String driverClass = "oracle.jdbc.driver.OracleDriver";
            Driver d = (Driver) Class.forName(driverClass).getDeclaredConstructor().newInstance();
            System.out.println("Connecting as " + username + " at " + dburl);
            return DriverManager.getConnection(dburl, username, password);
        } else
            return null;
    }
    public static String getReportFolder(String name) throws Exception {
        Node connNode = findConnectionNode(name);
        if (connNode != null) {
            String folder = childText(connNode, "folder");
            System.out.println("report folder is " + folder);
            return folder;
        } else
            return "/tmp";
    }
}
