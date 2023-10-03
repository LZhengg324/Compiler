package Grammar.GrammarNode;

import java.io.FileWriter;
import java.io.IOException;

public interface ASTNode {
    public void print(FileWriter fileWriter) throws IOException;
}
