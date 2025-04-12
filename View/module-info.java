module View {
    requires javafx.controls;
    requires javafx.fxml;
    requires ModelProject;

    opens view to javafx.fxml;
}