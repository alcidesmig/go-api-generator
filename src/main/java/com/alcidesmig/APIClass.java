package com.alcidesmig;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author alcides
 */
public class APIClass {

    static final String INTEGER = "int";
    static final String STRING = "string";
    static final String FLOAT = "float64";

    private Map<String, String> fields = new HashMap<String, String>();

    public APIClass() {
    }

    public void addField(String ident, String type) {
        fields.put(ident, type);
    }

    public Map<String, String> getFields() {
        return fields;
    }

}
