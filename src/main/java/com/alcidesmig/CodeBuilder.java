package com.alcidesmig;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Map.Entry;

/**
 *
 * @author alcides
 */
public class CodeBuilder {

    ClassManager classesMemory;
    RoutesManager routesMemory;
    PrintWriter pw;

    private int currentIdentation = 0;

    private void writeBreakline() {
        pw.write("\n");
    }

    private void writeLine(String line) {
        for (int i = 0; i < currentIdentation; i++) {
            pw.write("\t");
        }
        pw.write(line + "\n");
    }

    private void writeText(String text) {
        for (int i = 0; i < currentIdentation; i++) {
            pw.write("\t");
        }
        pw.write(text);
    }

    private void writeRawText(String text) {
        pw.write(text);
    }

    private String capitalize(String key) {
        return key.substring(0, 1).toUpperCase() + key.substring(1);
    }

    public CodeBuilder(ClassManager classesMemory, RoutesManager routesMemory) {
        this.classesMemory = classesMemory;
        this.routesMemory = routesMemory;
    }

    public void build(String filename) throws FileNotFoundException {
        pw = new PrintWriter(new File(filename));
        imports();
        dbConnect();
        main();
        models();
        dbGets();
        dbSaves();
        dbDeletes();
        routes();
        pw.close();
    }

    private void imports() {
        writeLine("package main");
        writeBreakline();
        writeLine("import (");
        currentIdentation++;
        writeLine("\"log\"");
        writeLine("\"io/ioutil\"");
        writeLine("\"net/http\"");
        writeLine("\"encoding/json\"");
        writeLine("\"errors\"");
        writeLine("\"strconv\"");
        writeLine("\"gorm.io/driver/sqlite\"");
        writeLine("\"gorm.io/gorm\"");
        writeLine("\"github.com/husobee/vestigo\"");
        currentIdentation--;
        writeLine(")");
        writeBreakline();
    }

    private void dbConnect() {
        writeLine("var db, _ = gorm.Open(sqlite.Open(\"gorm.db\"), &gorm.Config{})");
        writeBreakline();
    }

    private void main() {
        writeLine("func main() {");
        currentIdentation++;
        writeText("db.AutoMigrate(");
        for (int i = 0; i < classesMemory.getClasses().size(); i++) {
            Entry<String, APIClass> cl = (Entry<String, APIClass>) classesMemory.getClasses().toArray()[i];
            writeRawText("&" + capitalize(cl.getKey()) + "{}");
            if (i != classesMemory.getClasses().size() - 1) {
                writeRawText(", ");
            } else {
                writeRawText(")");
            }
        }

        writeBreakline();

        writeLine(
                "router := vestigo.NewRouter()");
        for (Entry<String, RoutesManager.RouteContent> route
                : routesMemory.getRoutes()) {
            writeText("router.");
            switch (route.getValue().getMethod()) {
                case "GET":
                    writeRawText(RoutesManager.GET);
                    break;
                case "POST":
                    writeRawText(RoutesManager.POST);
                    break;
                case "PUT":
                    writeRawText(RoutesManager.PUT);
                    break;
                case "DELETE":
                    writeRawText(RoutesManager.DELETE);
                    break;
            }
            writeRawText("(\"" + route.getValue().getPath() + "\", " + route.getKey() + ")");
            writeBreakline();
        }

        writeLine(
                "log.Fatal(http.ListenAndServe(\":8080\", router))");
        currentIdentation--;
        writeLine(
                "}");
    }

    private void models() {
        writeBreakline();
        for (Entry<String, APIClass> cl : classesMemory.getClasses()) {
            writeLine("type " + capitalize(cl.getKey()) + " struct {");
            currentIdentation++;
            writeLine("ID int `json:\"id\";gorm:\"primaryKey\"`");
            for (Entry<String, String> field : cl.getValue().getFields().entrySet()) {
                if (!field.getValue().equals(APIClass.FLOAT)
                        && !field.getValue().equals(APIClass.INTEGER)
                        && !field.getValue().equals(APIClass.STRING)) {
                    writeLine(capitalize(field.getKey()) + "ID int `json:\"" + capitalize(field.getKey()) + "ID\"`");
                    writeLine(capitalize(field.getKey()) + " *" + field.getValue() + " `json:\"" + capitalize(field.getKey()) + ",omitempty\";gorm:\"references:" + capitalize(field.getKey()) + "ID\"`");
                } else {
                    writeLine(capitalize(field.getKey()) + " " + field.getValue() + " `json:\"" + capitalize(field.getKey()) + "\"`");
                }
            }
            currentIdentation--;
            writeLine("}");
            writeBreakline();
        }
    }

    private void dbGets() {
        writeBreakline();
        for (Entry<String, APIClass> cl : classesMemory.getClasses()) {
            writeLine("func DatabaseGet" + capitalize(cl.getKey()) + "ByID(id int) (" + cl.getKey().toLowerCase() + " *" + capitalize(cl.getKey()) + ", err error) {");
            currentIdentation++;
            writeLine(cl.getKey().toLowerCase() + " = new(" + capitalize(cl.getKey()) + ")");
            writeLine("res := db.Where(\"id = ?\", id).Find(&" + cl.getKey().toLowerCase() + ")");
            writeLine("err = res.Error");
            writeLine("if errors.Is(err, gorm.ErrRecordNotFound) || res.RowsAffected == 0 {");
            currentIdentation++;
            writeLine("return nil, gorm.ErrRecordNotFound");
            currentIdentation--;
            writeLine("}");
            writeLine("return");
            currentIdentation--;
            writeLine("}");
            writeBreakline();
            writeLine("func DatabaseGet" + capitalize(cl.getKey()) + "s() (" + cl.getKey().toLowerCase() + "s []*" + capitalize(cl.getKey()) + ", err error) {");
            currentIdentation++;
            writeLine("res := db.Model(&" + capitalize(cl.getKey()) + "{}).Find(&" + cl.getKey().toLowerCase() + "s)");
            writeLine("err = res.Error");
            writeLine("if errors.Is(err, gorm.ErrRecordNotFound) || res.RowsAffected == 0 {");
            currentIdentation++;
            writeLine("return nil, gorm.ErrRecordNotFound");
            currentIdentation--;
            writeLine("}");
            writeLine("return");
            currentIdentation--;
            writeLine("}");
            writeBreakline();

        }
    }

    private void dbSaves() {
        writeBreakline();
        for (Entry<String, APIClass> cl : classesMemory.getClasses()) {
            writeLine("func (" + cl.getKey().toLowerCase() + " " + capitalize(cl.getKey()) + ") Save() (err error) {");
            currentIdentation++;
            writeLine("res := db.Save(&" + cl.getKey().toLowerCase() + ")");
            writeLine("err = res.Error");
            writeLine("return");
            currentIdentation--;
            writeLine("}");
            writeBreakline();
        }
    }

    private void dbDeletes() {
        writeBreakline();
        for (Entry<String, APIClass> cl : classesMemory.getClasses()) {
            writeLine("func (" + cl.getKey().toLowerCase() + " " + capitalize(cl.getKey()) + ") Delete() (err error) {");
            currentIdentation++;
            writeLine("res := db.Delete(&" + cl.getKey().toLowerCase() + ")");
            writeLine("err = res.Error");
            writeLine("return");
            currentIdentation--;
            writeLine("}");
            writeBreakline();
        }
    }

    private void routes() {
        for (Entry<String, RoutesManager.RouteContent> route : routesMemory.getRoutes()) {
            writeLine("func " + route.getKey() + "(w http.ResponseWriter, r *http.Request) {");
            currentIdentation++;
            if (route.getValue().getParameter() != null) {
                writeLine("str" + route.getValue().getParameter() + " := vestigo.Param(r, \"" + route.getValue().getParameter() + "\")");
                writeLine(route.getValue().getParameter() + ", _ := strconv.Atoi(str" + route.getValue().getParameter() + ")");
            }
            APIClass object = classesMemory.getClass(route.getValue().getObjectType());
            String typeLower = route.getValue().getObjectType().toLowerCase();
            String typeHigher = capitalize(route.getValue().getObjectType());
            switch (route.getValue().getMethod()) {
                case "GET":
                    if (route.getValue().getParameter() != null) {
                        writeLine("response, err := DatabaseGet" + capitalize(route.getValue().getObjectType()) + "ByID(" + route.getValue().getParameter() + ")");
                        writeLine("w.Header().Set(\"Content-Type\", \"application/json\")");
                        writeLine("if err != nil && errors.Is(err, gorm.ErrRecordNotFound) {");
                        currentIdentation++;
                        writeLine("w.WriteHeader(404)");
                        writeLine("w.Write([]byte(\"{}\"))");
                        writeLine("return");
                        currentIdentation--;
                        writeLine("} else if err != nil {");
                        currentIdentation++;
                        writeLine("w.WriteHeader(500)");
                        writeLine("w.Write([]byte(\"\"))");
                        writeLine("return");
                        currentIdentation--;
                        writeLine("}");
                    } else {
                        writeLine("response, err := DatabaseGet" + capitalize(route.getValue().getObjectType()) + "s()");
                        writeLine("w.Header().Set(\"Content-Type\", \"application/json\")");
                        writeLine("if err != nil && errors.Is(err, gorm.ErrRecordNotFound) {");
                        currentIdentation++;
                        writeLine("w.WriteHeader(404)");
                        writeLine("w.Write([]byte(\"{}\"))");
                        writeLine("return");
                        currentIdentation--;
                        writeLine("} else if err != nil {");
                        currentIdentation++;
                        writeLine("w.WriteHeader(500)");
                        writeLine("w.Write([]byte(\"\"))");
                        writeLine("return");
                        currentIdentation--;
                        writeLine("}");
                    }
                    writeLine("jsonResponse, _ := json.Marshal(response)");
                    writeLine("w.WriteHeader(200)");
                    writeLine("w.Write(jsonResponse)");
                    break;
                case "POST":
                    writeLine("var " + typeLower + " " + typeHigher);
                    writeLine("body, err := ioutil.ReadAll(r.Body)");
                    writeLine("if err != nil {");
                    currentIdentation++;
                    writeLine("log.Println(\"Error while parsing body\")");
                    writeLine("w.WriteHeader(400)");
                    writeLine("w.Write([]byte(\"\"))");
                    writeLine("return");
                    currentIdentation--;
                    writeLine("}");
                    writeBreakline();
                    writeLine("err = json.Unmarshal(body, &" + typeLower + ")");
                    writeLine("if err != nil {");
                    currentIdentation++;
                    writeLine("log.Println(\"Error while unmarshalling data\")");
                    writeLine("w.WriteHeader(400)");
                    writeLine("w.Write([]byte(\"\"))");
                    writeLine("return");
                    currentIdentation--;
                    writeLine("}");
                    writeBreakline();
                    writeLine("err = " + typeLower + ".Save()");
                    writeLine("if err != nil {");
                    currentIdentation++;
                    writeLine("log.Println(\"Error while saving to db\")");
                    writeLine("w.WriteHeader(500)");
                    writeLine("w.Write([]byte(\"\"))");
                    writeLine("return");
                    currentIdentation--;
                    writeLine("}");
                    writeLine("w.WriteHeader(200)");
                    writeLine("w.Write([]byte(\"\"))");
                    break;
                case "PUT":
                    writeLine("response, err := DatabaseGet" + capitalize(route.getValue().getObjectType()) + "ByID(" + route.getValue().getParameter() + ")");
                    writeLine("w.Header().Set(\"Content-Type\", \"application/json\")");
                    writeLine("if err != nil && errors.Is(err, gorm.ErrRecordNotFound) {");
                    currentIdentation++;
                    writeLine("w.WriteHeader(404)");
                    writeLine("w.Write([]byte(\"{}\"))");
                    writeLine("return");
                    currentIdentation--;
                    writeLine("} else if err != nil {");
                    currentIdentation++;
                    writeLine("w.WriteHeader(500)");
                    writeLine("w.Write([]byte(\"\"))");
                    writeLine("return");
                    currentIdentation--;
                    writeLine("}");
                    writeLine("body, err := ioutil.ReadAll(r.Body)");
                    writeLine("if err != nil {");
                    currentIdentation++;
                    writeLine("log.Println(\"Error while parsing body\")");
                    writeLine("w.WriteHeader(400)");
                    writeLine("w.Write([]byte(\"\"))");
                    writeLine("return");
                    currentIdentation--;
                    writeLine("}");
                    writeLine("err = json.Unmarshal(body, &response)");
                    writeLine("if err != nil {");
                    currentIdentation++;
                    writeLine("log.Println(\"Error while unmarshalling data\")");
                    writeLine("w.WriteHeader(400)");
                    writeLine("w.Write([]byte(\"\"))");
                    writeLine("return");
                    currentIdentation--;
                    writeLine("}");
                    writeLine("err = response.Save()");
                    writeLine("if err != nil {");
                    currentIdentation++;
                    writeLine("log.Println(\"Error while saving to db\")");
                    writeLine("w.WriteHeader(500)");
                    writeLine("w.Write([]byte(\"\"))");
                    writeLine("return");
                    currentIdentation--;
                    writeLine("}");
                    writeLine("w.WriteHeader(200)");
                    writeLine("w.Write([]byte(\"\\\"{\\\"message\\\":\\\"updated\\\"}\\\"\"))");
                    break;
                case "DELETE":
                    writeLine("var " + typeLower + " " + typeHigher);
                    writeLine(typeLower + ".ID = " + route.getValue().getParameter());
                    writeLine("err := " + typeLower + ".Delete()");
                    writeLine("if err != nil {");
                    currentIdentation++;
                    writeLine("log.Println(\"Error while deleting in db\")");
                    writeLine("w.WriteHeader(500)");
                    writeLine("w.Write([]byte(\"\"))");
                    writeLine("return");
                    currentIdentation--;
                    writeLine("}");
                    writeLine("w.WriteHeader(200)");
                    writeLine("w.Write([]byte(\"\"))");
                    break;
            }
            writeLine("return");
            currentIdentation--;
            writeLine("}");
            writeBreakline();
        }
    }

}

//
//func GetClusterById(id uint) (cluster *Cluster, err error) {
//	cluster = new(Cluster)
//	res := db.Where("id = ?", id).Find(&cluster)
//
//	err = res.Error
//	if errors.Is(err, gorm.ErrRecordNotFound) || res.RowsAffected == 0 {
//		log.Log.Debug("Cluster not found", zap.Error(err), zap.Uint("id", id))
//		return nil, gorm.ErrRecordNotFound
//	} else if err != nil {
//		log.log.Println("Something went wrong while querying for cluster", zap.Error(err))
//	}
//
//	return
//}
