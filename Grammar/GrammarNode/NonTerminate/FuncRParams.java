package Grammar.GrammarNode.NonTerminate;

import Grammar.GrammarNode.ASTNode;
import Grammar.GrammarNode.NonTerminalNode;
import Grammar.NodeType;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class FuncRParams extends NonTerminalNode {
    //FuncRParams -> Exp { ',' Exp }
    public FuncRParams() {
        super();
    }

    public void addChild(ASTNode node) {
        super.addChild(node);
    }

    public int getParamSize() {
        int cnt = 0;
        for (ASTNode node : getChildNodes()) {
            if (node instanceof Exp) {
                cnt++;
            }
        }
        return cnt;
    }

    public void print(FileWriter fileWriter) throws IOException {
        Iterator<ASTNode> iterator = super.getChildNodes().iterator();
        while (iterator.hasNext()) {
            iterator.next().print(fileWriter);
        }
        fileWriter.append("<FuncRParams>\n");
    }

    public ArrayList<Integer> getParamsType() {
        ArrayList<Integer> types = new ArrayList<>();
        for (ASTNode node : getChildNodes()) {
            if (node instanceof Exp) {
                types.add(((Exp)node).getParamType());
            }
        }
        return types;
    }
}
