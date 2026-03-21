package com.joelfah.gradecalculator.services

import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.properties.UnitValue
import com.joelfah.gradecalculator.models.Student
import java.io.ByteArrayOutputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class PdfService {

    private val colorPassBg  = DeviceRgb(0xD8, 0xF3, 0xDC)
    private val colorFailBg  = DeviceRgb(0xFF, 0xE8, 0xE8)
    private val colorHeader  = DeviceRgb(0xEE, 0xEE, 0xEE)
    private val colorText    = DeviceRgb(0x0D, 0x0D, 0x0D)

    fun generateReport(
        students: List<Student>,
        subjectHeaders: List<String>,
        sessionLabel: String,
    ): ByteArray {
        val out      = ByteArrayOutputStream()
        val writer   = PdfWriter(out)
        val pdfDoc   = PdfDocument(writer)
        val document = Document(pdfDoc)

        // Title
        document.add(
            Paragraph("Grade Report — $sessionLabel")
                .setFontSize(18f)
                .setBold()
                .setMarginBottom(4f)
        )

        // Date
        val timestamp = LocalDateTime.now()
            .format(DateTimeFormatter.ofPattern("MMM d, yyyy · HH:mm"))
        document.add(
            Paragraph("Generated on $timestamp")
                .setFontSize(10f)
                .setFontColor(DeviceRgb(0x6B, 0x6B, 0x6B))
                .setMarginBottom(16f)
        )

        // Table
        val columns = listOf("ID", "Name") + subjectHeaders +
                      listOf("Average", "Grade", "GPA", "Status")

        val table = Table(UnitValue.createPercentArray(columns.size)).useAllAvailableWidth()

        // Header
        columns.forEach { title ->
            table.addHeaderCell(
                Cell().add(Paragraph(title).setBold().setFontSize(9f))
                    .setBackgroundColor(colorHeader)
                    .setPadding(4f)
            )
        }

        // Rows
        students.forEach { s ->
            val rowBg = if (s.status == "PASS") colorPassBg else colorFailBg

            val cells = listOf(s.studentId, s.name) +
                        s.scores.map { it.toInt().toString() } +
                        listOf(
                            "%.2f".format(s.average),
                            s.grade,
                            "%.1f".format(s.gpa),
                            s.status,
                        )

            cells.forEach { value ->
                table.addCell(
                    Cell().add(Paragraph(value).setFontSize(9f))
                        .setBackgroundColor(rowBg)
                        .setPadding(3f)
                )
            }
        }

        document.add(table)

        // Summary
        val passCount = students.count { it.status == "PASS" }
        document.add(
            Paragraph(
                "\nTotal: ${students.size} students  |  " +
                "Pass: $passCount  |  Fail: ${students.size - passCount}"
            ).setFontSize(10f).setMarginTop(12f)
        )

        document.close()
        return out.toByteArray()
    }
}
