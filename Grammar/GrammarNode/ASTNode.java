package Grammar.GrammarNode;

import Grammar.NodeType;

import java.io.FileWriter;
import java.io.IOException;

public interface ASTNode {
    void print(FileWriter fileWriter) throws IOException;
}
