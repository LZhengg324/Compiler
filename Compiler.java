import FileProcess.FileProcessor;
import Grammar.Parser;
import Lexical.Lexer;

import java.io.IOException;

public class Compiler {
    public static void main(String[] args) throws IOException {
        FileProcessor fp = new FileProcessor();
        Lexer lexer = new Lexer(fp.getSource());
        Parser parser = new Parser(lexer.getList());
        //fp.generateLexerOutput(lexer.getList());
        fp.generateParserOutput(parser.getRoot());
    }
}