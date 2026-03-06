module org.example.studentgradecalculator {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.studentgradecalculator to javafx.fxml;
    exports org.example.studentgradecalculator;
}