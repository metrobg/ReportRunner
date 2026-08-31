package com.metrobg;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;

import java.io.File;
import java.io.FilenameFilter;

/**
 * Subreports are referenced by their compiled .jasper filename
 * (e.g. $P{SUBREPORT_DIR} + "Report-P2.jasper"), but report folders on this
 * server often only carry the .jrxml source. Compile any .jrxml in a folder
 * that doesn't already have a matching .jasper, so subreport chains resolve
 * without requiring every subreport to be precompiled and uploaded by hand.
 */
public class SubreportCompiler {

    public static void compileMissingJaspers(File folder) {
        if (folder == null || !folder.isDirectory()) {
            return;
        }

        File[] jrxmlFiles = folder.listFiles((FilenameFilter) (dir, name) -> name.toLowerCase().endsWith(".jrxml"));
        if (jrxmlFiles == null) {
            return;
        }

        for (File jrxmlFile : jrxmlFiles) {
            String baseName = jrxmlFile.getName().substring(0, jrxmlFile.getName().length() - ".jrxml".length());
            File jasperFile = new File(folder, baseName + ".jasper");
            if (jasperFile.exists()) {
                continue;
            }
            try {
                System.out.println("Compiling subreport: " + jrxmlFile.getAbsolutePath());
                JasperCompileManager.compileReportToFile(jrxmlFile.getAbsolutePath(), jasperFile.getAbsolutePath());
            } catch (JRException e) {
                System.out.println("Failed to compile " + jrxmlFile.getAbsolutePath() + ": " + e.getMessage());
            }
        }
    }
}
