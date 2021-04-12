/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.alcidesmig;

import com.alcidesmig.grammar.APiAPIBaseVisitor;
import com.alcidesmig.grammar.APiAPIParser;
import com.alcidesmig.grammar.APiAPIParser.FieldContext;
import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;

/**
 *
 * @author alcides
 */
public class Semantic extends APiAPIBaseVisitor<Void> {

    static ClassManager classesMemory = new ClassManager();
    static RoutesManager routesMemory = new RoutesManager();

    public static List<String> semanticErrors = new ArrayList<>();

    public static void addSemanticError(Token t, String msg) {
        int line = t.getLine();
        semanticErrors.add(String.format("Line %d: %s", line, msg));
    }

    private String getType(String type) {
        switch (type) {
            case "float":
                return APIClass.FLOAT;
            case "int":
                return APIClass.INTEGER;
            case "string":
                return APIClass.STRING;
            default:
                if (classesMemory.exists(type)) {
                    return type;
                }
                return null;
        }
    }

    @Override
    public Void visitModel(APiAPIParser.ModelContext ctx) {
        if (classesMemory.exists(ctx.IDENT().getText())) {
            addSemanticError(ctx.getStart(), "already declared identifier (" + ctx.IDENT().getText() + ")");
            return null;
        }
        APIClass current = new APIClass();
        ctx.fields().field().forEach(field -> {
            String currentType = getType(field.type().getText());
            if (currentType == null) {
                addSemanticError(field.getStart(), "type not declared (" + field.type().getText() + ")");
            } else {
                current.addField(field.IDENT().getText(), currentType);
            }
        });
        classesMemory.addClass(ctx.IDENT().getText(), current);
        return null;
    }

    @Override
    public Void visitRoutes(APiAPIParser.RoutesContext ctx) {
        if (getType(ctx.IDENT().getText()) == null) {
            addSemanticError(ctx.getStart(), "type not declared (" + ctx.IDENT().getText() + ")");
        }

        for (APiAPIParser.RouteContext routeContext : ctx.route()) {
            if (routesMemory.exists(routeContext.IDENT().getText())) {
                addSemanticError(ctx.getStart(), "name of route already used(" + routeContext.IDENT().getText() + ")");
                continue;
            }
            if (routeContext.routeSpecs().path().param() != null) {
                if (!routeContext.routeSpecs().path().param().IDENT().getText().equals("id")) { // change to generic 
                    addSemanticError(ctx.getStart(), "parameter does not exist (" + routeContext.routeSpecs().path().param().IDENT().getText() + ")");
                    continue;
                }
                routesMemory.addRoute(routeContext.IDENT().getText(), ctx.IDENT().getText(),
                        routeContext.routeSpecs().method().getText(), routeContext.routeSpecs().path().getText(),
                        routeContext.routeSpecs().path().param().IDENT().getText());
                continue;
            }
            if (routeContext.routeSpecs().path().param() == null
                    && (routeContext.routeSpecs().method().getText().equals("PUT")
                    || routeContext.routeSpecs().method().getText().equals("DELETE"))) {
                addSemanticError(ctx.getStart(), "parameter needed in url");
                continue;
            }
            routesMemory.addRoute(routeContext.IDENT().getText(), ctx.IDENT().getText(),
                    routeContext.routeSpecs().method().getText(), routeContext.routeSpecs().path().getText());
        }
        return super.visitRoutes(ctx);
    }
}
