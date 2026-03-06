package com.gradecalc.util;

import com.gradecalc.model.GradeCalculator;
import com.gradecalc.model.Subject;
import org.apache.poi.xssf.usermodel.*;
import java.io.FileOutputStream;

public class ExcelExporter {

    public static void export(GradeCalculator calc, String filePath)
            throws Exception {
        XSSFWorkbook workbook = new XSSFWorkbook();
        XSSFSheet sheet = workbook.createSheet("Grade Report");

        // Header row
        String[] headers = {"Subject", "Score", "Max",
                "Percentage", "Grade", "Status"};
        XSSFRow header = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            header.createCell(i).setCellValue(headers[i]);
        }

        // Data rows — lambda with index
        var subjects = calc.getSubjects();
        for (int i = 0; i < subjects.size(); i++) {
            Subject s = subjects.get(i);
            double pct = s.getPercentage();
            XSSFRow row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(s.getName());
            row.createCell(1).setCellValue(s.getScore());
            row.createCell(2).setCellValue(s.getMaxScore());
            row.createCell(3).setCellValue(
                    String.format("%.1f%%", pct));
            row.createCell(4).setCellValue(
                    calc.getGradeLetter(pct));
            row.createCell(5).setCellValue(
                    calc.getPassFail(pct));
        }

        // Summary rows
        int sumRow = subjects.size() + 2;
        sheet.createRow(sumRow).createCell(0)
                .setCellValue("Average: " +
                        String.format("%.1f%%", calc.calculate()));
        sheet.createRow(sumRow + 1).createCell(0)
                .setCellValue("GPA: " + calc.calculateGPA());
        sheet.createRow(sumRow + 2).createCell(0)
                .setCellValue("Status: " +
                        calc.getOverallPassFail());

        try (FileOutputStream out = new FileOutputStream(filePath)) {
            workbook.write(out);
        }
        workbook.close();
    }
}
```

        ---

        ## STEP 7 — Design the GUI in Scene Builder

Open **Scene Builder** and build this layout, then save it as `main-view.fxml`:
        ```
        ┌─────────────────────────────────────────┐
        │         🎓 Student Grade Calculator      │
        ├──────────┬──────────┬────────────────────┤
        │ Subject  │  Score   │  Max Score  [Add]  │
        ├──────────┴──────────┴────────────────────┤
        │  TableView (Subject | Score | Grade |    │
        │             Status | Actions[Delete])    │
        ├─────────────────────────────────────────┤
        │  Average: __  GPA: __  Status: __       │
        ├─────────────────────────────────────────┤
        │  [Delete All]  [Download PDF] [Excel]   │
        └─────────────────────────────────────────┘