module org.example.fxtest {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.prefs;
    requires jakarta.xml.bind;
    requires org.glassfish.jaxb.runtime;

    opens org.example.fxtest to javafx.fxml, jakarta.xml.bind;
    exports org.example.fxtest;
    exports org.example.fxtest.controller;
    opens org.example.fxtest.controller to javafx.fxml, jakarta.xml.bind;
    exports org.example.fxtest.model;
    opens org.example.fxtest.model to javafx.fxml, jakarta.xml.bind;
    exports org.example.fxtest.util;
    opens org.example.fxtest.util to javafx.fxml, jakarta.xml.bind;
}
