package com.gradecalc.controller;

import com.gradecalc.model.*;
import com.gradecalc.util.*;
import javafx.collections.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.*;
import javafx.stage.FileChooser;
import javafx.beans.property.SimpleStringProperty;
import java.io.File;
import java.util.List;

public class MainController {

    private final GradeCalculator calc = new GradeCalculator();
    private final ObservableList<Subject> tableData =
            FXCollections.observableArrayList();

    private TextField subjectField, scoreField, maxScoreField;
    private TableView<Subject> gradesTable;
    private Label avgLabel, gpaLabel, statusLabel;

    public VBox getView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f0f4f8;");

        // ── TITLE ──────────────────────────────────────────
        Label title = new Label("Student Grade Calculator");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.web("#2c3e50"));
        title.setMaxWidth(Double.MAX_VALUE);
        title.setAlignment(Pos.CENTER);

        // ── INPUT ROW ──────────────────────────────────────
        subjectField = new TextField();
        subjectField.setPromptText("Subject Name");
        subjectField.setPrefWidth(200);

        scoreField = new TextField();
        scoreField.setPromptText("Score e.g. 75");
        scoreField.setPrefWidth(130);

        maxScoreField = new TextField();
        maxScoreField.setPromptText("Max Score e.g. 100");
        maxScoreField.setPrefWidth(150);

        Button addBtn = new Button("+ Add Subject");
        addBtn.setStyle("-fx-background-color: #27ae60;" +
                "-fx-text-fill: white; -fx-font-weight: bold;" +
                "-fx-background-radius: 5; -fx-padding: 8 15 8 15;");
        addBtn.setOnAction(e -> handleAdd());

        HBox inputRow = new HBox(10,
                subjectField, scoreField, maxScoreField, addBtn);
        inputRow.setAlignment(Pos.CENTER_LEFT);

        // ── TABLE ──────────────────────────────────────────
        gradesTable = new TableView<>();
        gradesTable.setItems(tableData);
        gradesTable.setPrefHeight(280);

        TableColumn<Subject, String> nameCol = new TableColumn<>("Subject");
        nameCol.setPrefWidth(200);
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Subject, Double> scoreCol = new TableColumn<>("Score");
        scoreCol.setPrefWidth(120);
        scoreCol.setCellValueFactory(new PropertyValueFactory<>("score"));

        TableColumn<Subject, String> gradeCol = new TableColumn<>("Grade");
        gradeCol.setPrefWidth(100);
        gradeCol.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        calc.getGradeLetter(
                                cell.getValue().getPercentage())));

        TableColumn<Subject, String> statusCol = new TableColumn<>("Status");
        statusCol.setPrefWidth(120);
        statusCol.setCellValueFactory(cell ->
                new SimpleStringProperty(
                        calc.getPassFail(
                                cell.getValue().getPercentage())));

        gradesTable.getColumns().addAll(
                nameCol, scoreCol, gradeCol, statusCol);
        gradesTable.setPlaceholder(
                new Label("No subjects yet. Add one above!"));

        // ── SUMMARY BAR ────────────────────────────────────
        avgLabel = new Label("Average: --");
        avgLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        avgLabel.setTextFill(Color.WHITE);

        gpaLabel = new Label("GPA: --");
        gpaLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        gpaLabel.setTextFill(Color.web("#f1c40f"));

        statusLabel = new Label("Status: --");
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        statusLabel.setTextFill(Color.web("#2ecc71"));

        HBox summaryBar = new HBox(30,
                avgLabel, new Separator(), gpaLabel,
                new Separator(), statusLabel);
        summaryBar.setAlignment(Pos.CENTER);
        summaryBar.setPadding(new Insets(12));
        summaryBar.setStyle("-fx-background-color: #2c3e50;" +
                "-fx-background-radius: 8;");

        // ── ACTION BUTTONS ─────────────────────────────────
        Button deleteSelectedBtn = new Button("Delete Selected");
        deleteSelectedBtn.setStyle(
                "-fx-background-color: #e67e22;" +
                        "-fx-text-fill: white; -fx-font-weight: bold;" +
                        "-fx-background-radius: 5; -fx-padding: 8 15 8 15;");
        deleteSelectedBtn.setOnAction(e -> handleDeleteSelected());

        Button deleteAllBtn = new Button("Delete All");
        deleteAllBtn.setStyle(
                "-fx-background-color: #e74c3c;" +
                        "-fx-text-fill: white; -fx-font-weight: bold;" +
                        "-fx-background-radius: 5; -fx-padding: 8 15 8 15;");
        deleteAllBtn.setOnAction(e -> handleDeleteAll());

        Button pdfBtn = new Button("Download PDF");
        pdfBtn.setStyle(
                "-fx-background-color: #2980b9;" +
                        "-fx-text-fill: white; -fx-font-weight: bold;" +
                        "-fx-background-radius: 5; -fx-padding: 8 15 8 15;");
        pdfBtn.setOnAction(e -> handleDownloadPDF());

        Button excelBtn = new Button("Download Excel");
        excelBtn.setStyle(
                "-fx-background-color: #8e44ad;" +
                        "-fx-text-fill: white; -fx-font-weight: bold;" +
                        "-fx-background-radius: 5; -fx-padding: 8 15 8 15;");
        excelBtn.setOnAction(e -> handleDownloadExcel());

        HBox buttonRow = new HBox(12,
                deleteSelectedBtn, deleteAllBtn, pdfBtn, excelBtn);
        buttonRow.setAlignment(Pos.CENTER);

        // ── FOOTER ─────────────────────────────────────────
        Label footer = new Label(
                "Tip: Click a row to select it before deleting");
        footer.setTextFill(Color.web("#95a5a6"));
        footer.setMaxWidth(Double.MAX_VALUE);
        footer.setAlignment(Pos.CENTER);

        // ── ASSEMBLE ───────────────────────────────────────
        root.getChildren().addAll(
                title,
                new Separator(),
                inputRow,
                gradesTable,
                summaryBar,
                buttonRow,
                new Separator(),
                footer
        );

        return root;
    }

    // ── HANDLERS ───────────────────────────────────────────

    private void handleAdd() {
        try {
            String name = subjectField.getText().trim();
            double score = Double.parseDouble(
                    scoreField.getText().trim());
            double max = Double.parseDouble(
                    maxScoreField.getText().trim());

            if (name.isEmpty()) {
                showAlert("Missing Info", "Enter a subject name.");
                return;
            }
            if (score < 0 || max <= 0 || score > max) {
                showAlert("Invalid Input",
                        "Score must be between 0 and Max Score.");
                return;
            }

            Subject subject = new Subject(name, score, max);
            calc.addSubject(subject);
            tableData.add(subject);
            updateSummary();

            // Lambda clears all fields at once
            List.of(subjectField, scoreField, maxScoreField)
                    .forEach(TextField::clear);

        } catch (NumberFormatException e) {
            showAlert("Invalid Input",
                    "Please enter valid numbers for Score and Max Score.");
        }
    }

    private void handleDeleteSelected() {
        int idx = gradesTable.getSelectionModel().getSelectedIndex();
        if (idx >= 0) {
            calc.deleteSubject(idx);
            tableData.remove(idx);
            updateSummary();
        } else {
            showAlert("No Selection",
                    "Please click a row in the table first.");
        }
    }

    private void handleDeleteAll() {
        calc.deleteAll();
        tableData.clear();
        updateSummary();
    }

    private void handleDownloadPDF() {
        File file = chooseFile("PDF Files", "*.pdf");
        if (file != null) {
            try {
                PDFExporter.export(calc, file.getAbsolutePath());
                showAlert("Success",
                        "PDF saved to:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Error", "Could not save PDF:\n"
                        + e.getMessage());
            }
        }
    }

    private void handleDownloadExcel() {
        File file = chooseFile("Excel Files", "*.xlsx");
        if (file != null) {
            try {
                ExcelExporter.export(calc, file.getAbsolutePath());
                showAlert("Success",
                        "Excel saved to:\n" + file.getAbsolutePath());
            } catch (Exception e) {
                showAlert("Error", "Could not save Excel:\n"
                        + e.getMessage());
            }
        }
    }

    private void updateSummary() {
        if (calc.getSubjects().isEmpty()) {
            avgLabel.setText("Average: --");
            gpaLabel.setText("GPA: --");
            statusLabel.setText("Status: --");
            return;
        }
        double avg = calc.calculate();
        avgLabel.setText(String.format("Average: %.1f%%", avg));
        gpaLabel.setText(String.format("GPA: %.2f",
                calc.calculateGPA()));
        statusLabel.setText("Status: " + calc.getOverallPassFail());
    }

    private File chooseFile(String desc, String ext) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Save File");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter(desc, ext));
        return fc.showSaveDialog(gradesTable.getScene().getWindow());
    }

    private void showAlert(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}