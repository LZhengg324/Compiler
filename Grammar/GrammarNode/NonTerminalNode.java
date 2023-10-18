package Grammar.GrammarNode;

import Grammar.NodeType;
import Lexical.LexType;
import Lexical.tokenNode;

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

    public String getIdent() {
        for (ASTNode node : childNodes) {
            if (node instanceof tokenNode) {
                if (((tokenNode) node).getLexType().compareTo(LexType.IDENFR) == 0) {
                    return ((tokenNode) node).getContent();
                }
            }
        }
        return null;
    }

    @Override
    public void print(FileWriter fileWriter) throws IOException {

    }
}
