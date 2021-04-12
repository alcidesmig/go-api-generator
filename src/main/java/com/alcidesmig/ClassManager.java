package com.alcidesmig;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author alcides
 */
public class ClassManager {

    private static Map<String, APIClass> classes = new HashMap<String, APIClass>();

    public void addClass(String ident, APIClass _class) {
        classes.put(ident, _class);
    }

    public APIClass getClass(String ident) {
        return classes.get(ident);
    }

    public Set<Entry<String, APIClass>> getClasses() {
        return classes.entrySet();
    }

    public boolean exists(String ident) {
        return classes.containsKey(ident);
    }

}
