package com.gradecalc.util;

import com.gradecalc.model.GradeCalculator;
import com.gradecalc.model.Subject;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import java.io.FileOutputStream;

public class PDFExporter {

    public static void export(GradeCalculator calc, String filePath)
            throws Exception {
        Document doc = new Document();
        PdfWriter.getInstance(doc, new FileOutputStream(filePath));
        doc.open();

        // Title
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18,
                Font.BOLD);
        doc.add(new Paragraph("Student Grade Report", titleFont));
        doc.add(new Paragraph(" "));

        // Table
        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        Stream.of("Subject", "Score", "Grade", "Status")
                .forEach(h -> table.addCell(new PdfPCell(
                        new Phrase(h, new Font(Font.FontFamily.HELVETICA,
                                12, Font.BOLD)))));

        // Rows — using lambda
        calc.getSubjects().forEach(s -> {
            double pct = s.getPercentage();
            table.addCell(s.getName());
            table.addCell(String.format("%.1f%%", pct));
            table.addCell(calc.getGradeLetter(pct));
            table.addCell(calc.getPassFail(pct));
        });

        doc.add(table);
        doc.add(new Paragraph(" "));
        doc.add(new Paragraph(String.format(
                "Overall Average: %.1f%%", calc.calculate())));
        doc.add(new Paragraph(String.format(
                "GPA: %.1f", calc.calculateGPA())));
        doc.add(new Paragraph(
                "Overall Status: " + calc.getOverallPassFail()));
        doc.close();
    }
}