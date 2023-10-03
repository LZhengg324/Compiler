package Grammar.GrammarNode;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class NonTerminalNode implements ASTNode {
    private final ArrayList<ASTNode> childNodes;

    public NonTerminalNode() {
        this.childNodes = new ArrayList<>();
    }

    public void addChild(ASTNode node) {
        childNodes.add(node);
    }

    public ArrayList<ASTNode> getChildNodes() {
        return this.childNodes;
    }

    @Override
    public void print(FileWriter fileWriter) throws IOException {

    }
}
