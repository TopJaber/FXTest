package org.example.fxtest.util;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;
import java.time.LocalDateTime;

public class Adapter extends XmlAdapter<String, LocalDateTime> {
    @Override
    public LocalDateTime unmarshal(String v) throws Exception {
        return DateUtil.parse(v);
    }

    @Override
    public String marshal(LocalDateTime v) throws Exception {
        return DateUtil.format(v);
    }
}