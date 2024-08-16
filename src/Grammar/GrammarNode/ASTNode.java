package Grammar.GrammarNode;

import java.io.FileWriter;
import java.io.IOException;

public interface ASTNode {
    void print(FileWriter fileWriter) throws IOException;
}
