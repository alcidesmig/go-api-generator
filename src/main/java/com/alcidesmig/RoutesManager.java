package com.alcidesmig;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author alcides
 */
public class RoutesManager {

    class RouteContent {

        private final String method;
        private final String path;
        private final String objectType;
        private final String parameter;

        public RouteContent(String objectType, String method, String path) {
            this.objectType = objectType;
            this.method = method;
            this.path = path;
            this.parameter = null;
        }

        public RouteContent(String objectType, String method, String path, String parameter) {
            this.objectType = objectType;
            this.method = method;
            this.path = path;
            this.parameter = parameter;
        }

        public String getObjectType() {
            return objectType;
        }

        public String getMethod() {
            return method;
        }

        public String getPath() {
            return path;
        }

        public String getParameter() {
            return parameter;
        }

    }
    private Map<String, RouteContent> routes = new HashMap<String, RouteContent>();

    public static final String GET = "Get";
    public static final String POST = "Post";
    public static final String PUT = "Put";
    public static final String DELETE = "Delete";

    public RoutesManager() {
    }

    public void addRoute(String ident, String objectType, String method, String path) {
        routes.put(ident, new RouteContent(objectType, method, path));
    }

    public void addRoute(String ident, String objectType, String method, String path, String parameter) {
        routes.put(ident, new RouteContent(objectType, method, path, parameter));
    }

    public Set<Entry<String, RoutesManager.RouteContent>> getRoutes() {
        return routes.entrySet();
    }

    public RouteContent getRoute(String ident) {
        return routes.get(ident);
    }

    public boolean exists(String ident) {
        return routes.containsKey(ident);
    }

}
