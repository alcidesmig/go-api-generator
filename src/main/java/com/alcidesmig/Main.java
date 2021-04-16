package com.alcidesmig;

import com.alcidesmig.grammar.APiAPILexer;
import com.alcidesmig.grammar.APiAPIParser;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Token;

public class Main {

    static CharStream cs;
    static APiAPILexer lex;
    static APiAPIParser parser;
    static FileWriter f;
    static CommonTokenStream tokens;
    static PrintWriter pw;
    static CodeBuilder cb;

    public static void main(String args[]) throws IOException {
        // Init printwriter to be used to write data for output file
        pw = new PrintWriter(new File(args[1]));

        // Execute lexer, parser and semantic. Finally, execute code builder.
        // the approval of the last step is necessary to execute the next
        // Semantic is too used by code builder (povoate classesMemory and
        // routesMemory)
        if (lexer(args[0]) && parser(args[0]) && semantic(args[0])) {
            cb = new CodeBuilder(Semantic.classesMemory, Semantic.routesMemory);
            cb.build(args[2]);
        }
        pw.close();
    }

    static boolean semantic(String file) throws IOException {
        try {
            // Re-init charstream
            cs = CharStreams.fromFileName(file);
            // Init Lexer
            lex = new APiAPILexer(cs);
            // Init CommonTokenStream
            tokens = new CommonTokenStream(lex);
            // Instantiate parser
            APiAPIParser parser = new APiAPIParser(tokens);
            // Get semantic tree
            APiAPIParser.MainContext tree = parser.main();
            // Instantiate semantic visitor
            Semantic main = new Semantic();
            // Visite semantic tree - recursive
            main.visitMain(tree);
            // Print errors
            Semantic.semanticErrors.forEach((s) -> pw.write(s + "\n"));

            pw.println("End of compilation");
            pw.close();
            return true;
        } catch (IOException | RecognitionException e) {
            return false;
        }
    }

    static boolean parser(String file) throws IOException {
        // Re-init charstream
        cs = CharStreams.fromFileName(file);
        // Init Lexer
        lex = new APiAPILexer(cs);
        // Init CommonTokenStream
        tokens = new CommonTokenStream(lex);
        // Instantiate parser
        parser = new APiAPIParser(tokens);
        // Instantiate error listener
        ErrorListener mcel = new ErrorListener(pw);
        // Update used error listener 
        parser.removeErrorListeners();
        parser.addErrorListener(mcel);
        // Execute parser
        try {
            parser.main();
        } catch (Exception e) {
            System.out.println(e.toString());
            pw.println("End of compilation");
            pw.close();
            return false;
        }
        return true;
    }

    static boolean lexer(String file) throws IOException {
        cs = CharStreams.fromFileName(file);
        // Instantiate lexer
        lex = new APiAPILexer(cs);
        tokens = new CommonTokenStream(lex);

        Token token;
        boolean error = false;

        OUTER:
        while ((token = lex.nextToken()).getType() != Token.EOF) {
            String symbolicName = APiAPILexer.VOCABULARY.getSymbolicName(token.getType());
            if (symbolicName == null) {
                continue;
            }
            switch (symbolicName) {
                case "WRONG_COMMENT":
                    // Error: malformed comment
                    pw.write(String.format("Line %d: comment not closed\n", token.getLine()));
                    error = true;
                    break OUTER;
                case "WRONG_SYMBOL":
                    // Error: unknown symbol
                    pw.write(String.format("Line %d: %s - symbol not identified\n", token.getLine(), token.getText()));
                    error = true;
                    break OUTER;
                default:
                    break;
            }
        }
        if (error) {
            pw.println("End of compilation");
            return false;
        }
        return true;
    }
}
