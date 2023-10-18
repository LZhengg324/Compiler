import ErrorHandling.ErrorHandler;
import FileProcess.FileProcessor;
import Grammar.Parser;
import Grammar.Tables.Function;
import Grammar.Tables.FunctionsTable;
import Grammar.Tables.SymbolsTable;
import Lexical.Lexer;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Compiler {
    public static void main(String[] args) throws IOException {
        FileProcessor fp = new FileProcessor();
        Lexer lexer = new Lexer(fp.getSource());
        Parser parser = new Parser(lexer.getList());
        //fp.generateLexerOutput(lexer.getList());
        //fp.generateParserOutput(parser.getRoot());
        fp.generateErrorOutput();
    }
}