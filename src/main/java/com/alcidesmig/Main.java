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
        // Inicia o PrintWriter para escrever o output no arquivo passado como parâmetro
        // CharStreams do arquivo de entrada passado como parâmetro
        pw = new PrintWriter(new File(args[1]));

        // Executa lexer e, se tudo estiver correto na parte léxica, executa o parser sintático
        if (lexer(args[0]) && parser(args[0])) {
            semantic(args[0]);
            cb = new CodeBuilder(Semantic.classesMemory, Semantic.routesMemory);
            cb.build(args[2]);
            
        }

        // Fecha arquivo de output
        pw.close();
        
    }
    
    static boolean semantic(String file) throws IOException {
        try {
            // Inicia o CharStream novamente
            cs = CharStreams.fromFileName(file);
            // Instancia o Lexer gerado pelo ANTLR
            lex = new APiAPILexer(cs);
            // Cria o CommonTokenStream
            tokens = new CommonTokenStream(lex);
            // Instancia o Parser gerado pelo ANTLR
            APiAPIParser parser = new APiAPIParser(tokens);
            // Resgata arvore semântica
            APiAPIParser.MainContext tree = parser.main();
            // Instância o visitor
            Semantic main = new Semantic();
            // Visita a árvore de forma recursiva
            main.visitMain(tree);
            // Imprime os erros no arquivo de output
            Semantic.semanticErrors.forEach((s) -> pw.write(s + "\n"));
            // Imprime fim da compilação
            pw.println("End of compilation");
            // Fecha o arquivo de output
            pw.close();
            return true;
        } catch (IOException | RecognitionException e) {
            return false;
        }
    }
    
    static boolean parser(String file) throws IOException {
        // Inicia o CharStream novamente
        cs = CharStreams.fromFileName(file);
        // Instancia o Lexer gerado pelo ANTLR
        lex = new APiAPILexer(cs);
        // Cria o CommonTokenStream
        tokens = new CommonTokenStream(lex);
        // Instancia o Parser gerado pelo ANTLR
        parser = new APiAPIParser(tokens);
        // Instancia o error listener
        ErrorListener mcel = new ErrorListener(pw);
        // Remove o error listener default
        parser.removeErrorListeners();
        // Attach do error listener no parser
        parser.addErrorListener(mcel);
        // Execute o parser (sintático)
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
        // Instancia o Lexer gerado pelo ANTLR
        lex = new APiAPILexer(cs);
        tokens = new CommonTokenStream(lex);
        
        Token token;
        boolean error = false;
        
        OUTER:
        while ((token = lex.nextToken()).getType() != Token.EOF) {
            // System.out.println(token.getText());
            String symbolicName = APiAPILexer.VOCABULARY.getSymbolicName(token.getType());
            if (symbolicName == null) {
                continue;
            }
            switch (symbolicName) {
                case "WRONG_COMMENT":
                    // Erro comentário
                    pw.write(String.format("Line %d: comment not closed\n", token.getLine()));
                    error = true;
                    break OUTER;
                case "WRONG_SYMBOL":
                    // Erro simbolo
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
