package org.example.fxtest.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import org.example.fxtest.util.Adapter;
import org.example.fxtest.util.DateUtil;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@XmlRootElement(name = "note")
public class Note {
    private StringProperty name;
    private StringProperty text;
    private LocalDateTime createDate;
    private LocalDateTime expiryDate;

    public Note() {
        this(null, null, String.valueOf(LocalDateTime.now()), String.valueOf(LocalDateTime.now()));
    }

    public Note(String name, String text, String createDate, String expiryDate) {
        this.name = new SimpleStringProperty(name);
        this.text = new SimpleStringProperty(text);
        this.createDate = DateUtil.parse(createDate);
        this.expiryDate = DateUtil.parse(expiryDate);
    }

    public Note(String name, String text, String expiryDate) {
        this.name = new SimpleStringProperty(name);
        this.text = new SimpleStringProperty(text);
        this.createDate = DateUtil.parse(DateUtil.format(LocalDateTime.now()));
        this.expiryDate = DateUtil.parse(expiryDate);
    }

    @XmlElement
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name = new SimpleStringProperty(name);
    }

    public StringProperty nameProperty() {
        return name;
    }

    @XmlElement
    public String getText() {
        return text.get();
    }

    public void setText(String text) {
        this.text = new SimpleStringProperty(text);
    }

    public StringProperty textProperty() {
        return text;
    }

    @XmlElement
    @XmlJavaTypeAdapter(Adapter.class)
    public LocalDateTime getCreateDate() {
        return createDate;
    }

    @XmlElement
    @XmlJavaTypeAdapter(Adapter.class)
    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public void setCreateDate(LocalDateTime createDate) {
        this.createDate = createDate;
    }

    public StringProperty expiryProperty() {
        return new SimpleStringProperty(DateUtil.format(expiryDate));
    }

    @Override
    public String toString() {
        return "Note{" +
                "name=" + name +
                ", text=" + text +
                ", createDate=" + createDate +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
