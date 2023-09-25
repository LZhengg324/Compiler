import FileProcess.FileProcessor;
import Lexical.Lexer;

import java.io.IOException;

public class Compiler {
    public static void main(String[] args) throws IOException {
        FileProcessor fp = new FileProcessor();
        Lexer lexer = new Lexer(fp.getSource());
        fp.generateOutput(lexer.getList());
    }
}