package com.metrobg;

import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.engine.util.JRElementsVisitor;
import net.sf.jasperreports.engine.util.JRVisitorSupport;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class SubreportCompiler {

 //   public static void main(String[] args) throws Exception {
 public static void Initmain(String[] args) throws Exception {

        // Replace with your main report's JRXML path
        InputStream mainReportInputStream = ClassLoader.getSystemResourceAsStream("main_report.jrxml");

        // Load the main report design
        JasperDesign design = JRXmlLoader.load(mainReportInputStream);

        // Compile the main report
        JasperReport compiledReport = JasperCompileManager.compileReport(design);

        // Compile subreports
        compileSubreports(compiledReport);

        // Fill and export the report
        // (Example code for filling and exporting, replace with your logic)
        Map<String, Object> parameters = new HashMap<>();
        JasperPrint jasperPrint = JasperFillManager.fillReport(compiledReport, parameters, new JRBeanCollectionDataSource(new java.util.ArrayList<>()));
        JasperExportManager.exportReportToPdfFile(jasperPrint.toString(), "output.pdf");
    }

    public static void compileSubreports(JasperReport report) throws Exception {

        // Iterate through all subreports in the main report
        JRElementsVisitor.visitReport(report, new JRVisitorSupport() {
            @Override
            public void visitSubreport(JRSubreport subreport) {
                // You might want to check if the subreport is actually a subreport
                // before attempting to compile it.  For example, you could check
                // if the subreport's report expression returns a non-null JasperReport
                // when you call the subreport's getReportExpression() method

                // Replace with your subreport's JRXML path or method to retrieve it
                // (e.g., from a database)
                InputStream subreportInputStream = null; // Get InputStream for subreport

                if (subreport != null) {
                    // Compile the subreport
                    try {
                        JasperReport compiledSubreport = JasperCompileManager.compileReport(subreportInputStream);
                        // You can save the compiled subreport to file or use it as needed
                    } catch (JRException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }
}