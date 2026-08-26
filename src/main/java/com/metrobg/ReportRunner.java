package com.metrobg;
/*
 * Created by IntelliJ IDEA.
 * User: ggraves
 * Date: 01/12/2024
 * Time: 5:49 PM
 * To change this template use File | Settings | File Templates.
 */

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.util.JRLoader;
//import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;


@WebServlet("/ReportRunner")
public class ReportRunner extends HttpServlet {
//static final Logger logger = LogManager.getLogger(ReportRunner.class.getName());
    /**
     *
     */
    private static final long serialVersionUID = -3632339249096739601L;
    String TemplateFolder = "/home/public/reports/";
    String os = System.getProperty("os.name");


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        TemplateFolder = getServletConfig().getInitParameter("ReportFolder");
        System.out.println("Template Folder from init is: " + TemplateFolder);
        System.out.flush();
    }

    /**
     * Destroys the Servlet.
     */

    public void destroy() {
    }
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    public void processRequest(HttpServletRequest req,
                               HttpServletResponse res) throws IOException {

        String filename = req.getParameter("template");
        OutputStream out = res.getOutputStream();

        try {

            JasperPrint jasperPrint = returnReportPrint(req);

            if (req.getParameter("output").equalsIgnoreCase("PDF")) {
                res.setContentType("application/pdf");
                res.setHeader("cache-control", "no-cache");
                filename = filename + ".pdf";
                res.setHeader("Content-Disposition", "filename=" + filename);
                res.setDateHeader("Expires", 0);
                JasperExportManager.exportReportToPdfStream(jasperPrint, out);

            }

        } catch (Exception e) {
            System.out.println("Error " + e.getMessage());
        }
        finally {
            if (out != null) {
                out.flush();
            }
            assert out != null;
            out.close();
        }
    }

    public JasperPrint returnReportPrint( HttpServletRequest req) throws SQLException {
        JasperPrint jasperPrint = null;
        HashMap<String, Object> map = new HashMap<String, Object>();
        Connection connection = null;
        String key;
        String value;
        File compiledReport;
        // save all the request parameters to a hash map
        for (Enumeration<?> e = req.getParameterNames(); e.hasMoreElements();) {
            key = (String) e.nextElement();
            value = req.getParameter(key);
            map.put(key, value);
            System.out.println("Key Pairs are: " + key + "/" + value);
            System.out.flush();
        }

        try {
            String dbKey = (String) map.get("dbkey");

            TemplateFolder = ConnectionFactory.getReportFolder(dbKey);
            String Template = TemplateFolder + map.get("template");
            System.out.println("Report template is: " + map.get("template")) ;
            System.out.flush();

            JasperReport jasperReport = null;

            compiledReport = new File(Template + ".jasper");

            if (compiledReport.exists()) {
                jasperReport = (JasperReport) JRLoader.loadObject(compiledReport);
            } else {
                jasperReport = JasperCompileManager.compileReport(Template + ".jrxml");
                System.out.println("compiling the report") ;

            }
            connection = DbConn(dbKey);
            System.out.println("acquired connection");
            System.out.flush();
            if (!connection.isClosed()) {
                System.out.println("connection is open");
            }
            jasperPrint = JasperFillManager.fillReport(jasperReport, map, connection);

            connection.close();

        } catch (Exception ex) {
            String connectMsg = "Could not create the report stream " + ex.getMessage() + " "
                    + ex.getLocalizedMessage();
            System.out.println(connectMsg);
            ex.printStackTrace();

        }
        return jasperPrint;
    }

    public Connection DbConn(String key) throws Exception {
        System.out.println("Getting connection");
        return ConnectionFactory.getConnection(key);

    }

}
